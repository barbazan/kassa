const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const source = fs.readFileSync(require('node:path').join(__dirname, '../webapp/vk-billing.js'), 'utf8');

function setup(send, supports = true) {
    const calls = [];
    let focused = 0;
    const window = { setTimeout, clearTimeout, vkBridge: {
        supportsAsync: () => Promise.resolve(supports),
        send: (method, params) => {
            calls.push({method, params});
            return method === 'VKWebAppInit' ? Promise.resolve({result: true}) : send(method, params);
        }
    }};
    const document = {querySelector: () => ({focus: () => focused++})};
    vm.runInNewContext(source, {window, document, Promise, Error});
    return {api: window.kassaVKBilling, bridge: window.vkBridge, calls, focused: () => focused};
}

test('initializes once and sends the signed item unchanged', async () => {
    const env = setup(() => Promise.resolve({status: 'success', order_id: '12345'}));
    await Promise.all([env.api.init(), env.api.init()]);
    const result = await env.api.purchase('signed.item');
    assert.equal(result.order_id, '12345');
    assert.equal(env.calls.filter(call => call.method === 'VKWebAppInit').length, 1);
    assert.equal(env.calls[1].params.type, 'item');
    assert.equal(env.calls[1].params.item, 'signed.item');
    assert.equal(env.focused(), 1);
});

test('cancel and failure release the purchase lock for retry', async () => {
    let outcome = {status: 'cancel'};
    const env = setup(() => Promise.resolve(outcome));
    assert.equal((await env.api.purchase('item')).status, 'cancel');
    outcome = {status: 'fail'};
    await assert.rejects(env.api.purchase('item'), /failed/);
    outcome = {status: 'success'};
    await assert.rejects(env.api.purchase('item'), /failed/);
    outcome = {status: 'success', order_id: 12};
    assert.equal((await env.api.purchase('item')).order_id, 12);
});

test('rejects concurrent purchases and recovers from bridge errors', async () => {
    let rejectOrder;
    const env = setup(() => new Promise((resolve, reject) => {rejectOrder = reject;}));
    await env.api.init();
    const pending = env.api.purchase('item');
    await Promise.resolve();
    await assert.rejects(env.api.purchase('second'), /already pending/);
    rejectOrder(new Error('network failure'));
    await assert.rejects(pending, /network failure/);
    const retry = env.api.purchase('retry');
    await Promise.resolve();
    rejectOrder(new Error('second failure'));
    await assert.rejects(retry, /second failure/);
});

test('unsupported clients cannot open payments', async () => {
    const env = setup(() => assert.fail('payment must not open'), false);
    await assert.rejects(env.api.init(), /unavailable/);
});

test('script loading failure is reported and initialization can retry', async () => {
    let script;
    const window = {setTimeout, clearTimeout};
    const document = {createElement: () => ({}), head: {appendChild: value => {script = value;}}};
    vm.runInNewContext(source, {window, document, Promise, Error});
    const first = window.kassaVKBilling.init();
    script.onerror();
    await assert.rejects(first, /Cannot load/);
    window.vkBridge = {supportsAsync: () => Promise.resolve(true), send: () => Promise.resolve({result: true})};
    await window.kassaVKBilling.init();
});
test('initialization timeout releases state so the SDK can retry', async () => {
    let expire;
    let stuck = true;
    const window = {
        setTimeout: callback => {expire = callback; return 1;},
        clearTimeout: () => {},
        vkBridge: {supportsAsync: () => Promise.resolve(true), send: () => stuck ? new Promise(() => {}) : Promise.resolve({result: true})}
    };
    vm.runInNewContext(source, {window, document: {}, Promise, Error});
    const first = window.kassaVKBilling.init();
    await Promise.resolve();
    expire();
    await assert.rejects(first, /timed out/);
    stuck = false;
    await window.kassaVKBilling.init();
});

test('failed async support check can be retried', async () => {
    const env = setup(() => Promise.resolve({status: 'success', order_id: 1}));
    let attempts = 0;
    env.bridge.supportsAsync = method => {
        assert.equal(method, 'VKWebAppShowOrderBox');
        attempts++;
        return attempts === 1 ? Promise.reject(new Error('support check failed')) : Promise.resolve(true);
    };
    await assert.rejects(env.api.init(), /support check failed/);
    await env.api.init();
    assert.equal(attempts, 2);
});

test('async support timeout releases initialization for retry', async () => {
    let expire;
    let stuck = true;
    const window = {
        setTimeout: callback => {expire = callback; return 1;},
        clearTimeout: () => {},
        vkBridge: {
            send: () => Promise.resolve({result: true}),
            supportsAsync: () => stuck ? new Promise(() => {}) : Promise.resolve(true)
        }
    };
    vm.runInNewContext(source, {window, document: {}, Promise, Error});
    const first = window.kassaVKBilling.init();
    await new Promise(resolve => setImmediate(resolve));
    expire();
    await assert.rejects(first, /support check timed out/);
    stuck = false;
    await window.kassaVKBilling.init();
});

test('Yandex SDK is skipped inside VK and stays synchronous on other platforms', () => {
    const html = fs.readFileSync(require('node:path').join(__dirname, '../webapp/index.html'), 'utf8');
    const loader = html.match(/<script>(\s*\/\/ Keep synchronous Yandex SDK loading[\s\S]*?)<\/script>/)[1];
    for (const [search, expected] of [['?vk_app_id=123&vk_user_id=456', 0], ['?lang=ru', 1]]) {
        const writes = [];
        vm.runInNewContext(loader, {window: {location: {search}}, document: {write: html => writes.push(html)}});
        assert.equal(writes.length, expected);
        if (expected) assert.equal(writes[0], '<script src="/sdk.js"></script>');
    }
});


test('enforces the VK item limit before opening payment and allows a valid retry', async () => {
    const env = setup(() => Promise.resolve({status: 'cancel'}));
    for (const item of [null, undefined, 123, '', 'x'.repeat(65), 'vk1.' + 'x'.repeat(102)]) {
        await assert.rejects(env.api.purchase(item), /1 to 64 characters/);
    }
    assert.equal(env.calls.length, 0);
    assert.equal((await env.api.purchase('x'.repeat(64))).status, 'cancel');
    assert.equal(env.calls.filter(call => call.method === 'VKWebAppShowOrderBox').length, 1);
    assert.equal(env.calls[1].params.item.length, 64);
});
