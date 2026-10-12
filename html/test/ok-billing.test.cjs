const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const source = fs.readFileSync(path.join(__dirname, '../webapp/ok-billing.js'), 'utf8');
const order = (mode = 'fapi') => JSON.stringify({item: 'ok1_' + 'a'.repeat(54), price: 299,
    currency: 'ok', productHash: 'b'.repeat(64), mode});

function setup(options = {}) {
    const calls = [], other = [];
    let focused = 0, initialized = 0;
    const window = {setTimeout, clearTimeout,
        location: {search: options.search || '?api_server=https%3A%2F%2Fapi.ok.ru%2F&apiconnection=test'},
        API_callback: (...args) => other.push(args)};
    window.FAPI = {init: (server, connection, success, fail) => {
        initialized++;
        calls.push({method: 'init', server, connection});
        if (options.failInit) fail('failed'); else success();
    }, UI: {loaded: () => calls.push({method: 'loaded'}),
        showPayment: (...args) => {
            calls.push({method: 'payment', args});
            if (options.throwPayment) throw new Error('SDK failed');
        }}};
    const document = {querySelector: () => ({focus: () => focused++})};
    vm.runInNewContext(source, {window, document, Promise, Error, URLSearchParams});
    return {api: window.kassaOKBilling, window, document, calls, other, options,
        focused: () => focused, initialized: () => initialized};
}

const tick = () => new Promise(resolve => setImmediate(resolve));

test('initializes once, preserves callbacks and notifies the portal that loading completed', async () => {
    const env = setup();
    await Promise.all([env.api.init(), env.api.init()]);
    env.api.loaded();
    await tick();
    assert.equal(env.initialized(), 1);
    assert.equal(env.calls[0].server, 'https://api.ok.ru/');
    assert.equal(env.calls[0].connection, 'test');
    assert.equal(env.calls.filter(c => c.method === 'loaded').length, 1);
    env.window.API_callback('getPageInfo', 'ok', '{}');
    assert.deepEqual(env.other, [['getPageInfo', 'ok', '{}']]);
});

test('passes the server quote and price hash and handles the global FAPI payment callback', async () => {
    const env = setup();
    const pending = env.api.purchase(order());
    await tick();
    const args = env.calls.find(c => c.method === 'payment').args;
    assert.equal(args[2], JSON.parse(order()).item);
    assert.equal(args[3], 299);
    assert.equal(args[6], 'ok');
    assert.equal(args[7], 'true');
    assert.equal(JSON.parse(args[8])['popLayer.payment.productHash'], 'b'.repeat(64));
    env.window.API_callback('showPayment', 'ok', '{"amount":"299"}');
    assert.equal((await pending).status, 'success');
    assert.equal(env.focused(), 1);
});

for (const [result, data] of [['cancel', ''], ['error', 'cancel']]) {
    test('normalizes cancellation: ' + result + '/' + data, async () => {
        const env = setup();
        const pending = env.api.purchase(order());
        await tick();
        env.window.API_callback('showPayment', result, data);
        assert.equal((await pending).status, 'cancel');
        const retry = env.api.purchase(order());
        await tick();
        env.window.API_callback('showPayment', 'ok', 'ok');
        assert.equal((await retry).status, 'success');
    });
}

test('rejects duplicate clicks and releases the lock after a failure', async () => {
    const env = setup();
    const pending = env.api.purchase(order());
    await tick();
    await assert.rejects(env.api.purchase(order()), /already pending/);
    env.window.API_callback('showPayment', 'error', 'failed');
    await assert.rejects(pending, /rejected/);
    env.options.throwPayment = true;
    await assert.rejects(env.api.purchase(order()), /SDK failed/);
    env.options.throwPayment = false;
    const retry = env.api.purchase(order());
    await tick();
    env.window.API_callback('showPayment', 'ok', 'phone');
    assert.equal((await retry).status, 'success');
});

test('invalid orders cannot open the payment UI', async () => {
    const env = setup();
    for (const value of ['invalid', 'null', '{}', JSON.stringify({...JSON.parse(order()), price: 0}),
        JSON.stringify({...JSON.parse(order()), currency: 'RUR'}),
        JSON.stringify({...JSON.parse(order()), productHash: ''})]) {
        await assert.rejects(env.api.purchase(value), /Invalid OK order/);
    }
    assert.equal(env.calls.length, 0);
});

test('initialization failure can be retried', async () => {
    const env = setup({failInit: true});
    await assert.rejects(env.api.init(), /initialization failed/);
    env.options.failInit = false;
    await env.api.init();
    assert.equal(env.initialized(), 2);
});

test('script loading failure can be retried with a loaded SDK', async () => {
    const env = setup();
    const api = env.window.FAPI;
    delete env.window.FAPI;
    let script;
    env.document.createElement = () => ({});
    env.document.head = {appendChild: value => {script = value;}};
    const first = env.api.init();
    assert.equal(script.src, 'https://api.ok.ru/js/fapi5.js');
    script.onerror();
    await assert.rejects(first, /Cannot load/);
    env.window.FAPI = api;
    await env.api.init();
});

test('initialization timeout releases state for retry', async () => {
    const env = setup();
    let expire;
    env.window.setTimeout = callback => {expire = callback; return 1;};
    env.window.clearTimeout = () => {};
    env.window.FAPI.init = () => {};
    const first = env.api.init();
    await tick();
    expire();
    await assert.rejects(first, /timed out/);
    env.window.FAPI.init = (server, connection, success) => success();
    await env.api.init();
});

test('new OK launches use the unified bridge only when initialization confirms support', async () => {
    const env = setup({search: '?vk_client=ok&vk_app_id=123&vk_user_id=456'});
    const calls = [];
    env.window.kassaVKBilling = {init: () => {calls.push('init'); return Promise.resolve();},
        purchase: item => {calls.push(item); return Promise.resolve({status: 'success', order_id: 123});}};
    const result = await env.api.purchase(order('bridge'));
    assert.equal(result.status, 'success');
    assert.deepEqual(calls, ['init', JSON.parse(order()).item]);
    assert.equal(env.calls.length, 0);
});

test('unsupported new clients fail without opening an unrelated payment UI', async () => {
    const env = setup({search: '?vk_client=ok&vk_app_id=123&vk_user_id=456'});
    env.window.kassaVKBilling = {init: () => Promise.reject(new Error('unsupported'))};
    await assert.rejects(env.api.purchase(order('bridge')), /unsupported/);
    assert.equal(env.calls.length, 0);
});

test('rejects an order prepared for another SDK mode', async () => {
    const env = setup();
    await assert.rejects(env.api.purchase(order('bridge')), /mode mismatch/);
    assert.equal(env.calls.filter(c => c.method === 'payment').length, 0);
});

test('Yandex SDK is skipped for both old and new OK launch formats', () => {
    const html = fs.readFileSync(path.join(__dirname, '../webapp/index.html'), 'utf8');
    const loader = html.match(/<script>(\s*\/\/ Keep synchronous Yandex SDK loading[\s\S]*?)<\/script>/)[1];
    for (const search of ['?api_server=https%3A%2F%2Fapi.ok.ru%2F&logged_user_id=456', '?vk_app_id=123&vk_client=ok']) {
        const writes = [];
        vm.runInNewContext(loader, {window: {location: {search}}, document: {write: value => writes.push(value)}});
        assert.equal(writes.length, 0);
    }
});
