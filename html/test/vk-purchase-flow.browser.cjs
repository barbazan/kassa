// Run after :html:dist. Requires Playwright and a Chromium browser.
// Network requests are intercepted: no server accounts or real payments are used.
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const {chromium} = require(process.env.PLAYWRIGHT_MODULE || 'playwright');
const html = path.resolve(__dirname, '..');
const root = path.join(html, 'build/dist');
const caches = fs.readdirSync(path.join(root, 'html')).filter(n => n.endsWith('.cache.js'));
assert.equal(caches.length, 1, 'Build one GWT permutation before running this test');
const symbols = fs.readFileSync(path.join(html, 'build/gwt/extra/html/symbolMaps',
    caches[0].replace('.cache.js', '.symbolMap')), 'utf8').split(/\r?\n/).map(l => l.split(','));
function symbol(clazz, member) {
    const row = symbols.find(r => r[2] === clazz && r[3] === member && r[1]);
    assert(row, `Missing GWT symbol: ${clazz}::${member}`);
    return row[0];
}
const app = symbol('com.plagame.game.kassa.GameApplication', 'instance');
const screen = symbol('com.badlogic.gdx.Game', 'screen');
const buy = symbol('com.plagame.game.kassa.screens.BuyFullVersionScreen', 'BuyFullVersionScreen');
const play = symbol('com.plagame.game.kassa.screens.GameScreen', 'GameScreen');
const hook = symbol('com.plagame.game.kassa.GameApplication', 'GameApplication');
const print = symbol('java.lang.Throwable', '$printStackTraceImpl');
const message = symbol('java.lang.Throwable', 'detailMessage');
const dialog = symbol('com.plagame.game.kassa.screens.BuyFullVersionScreen', 'purchaseSuccessDialog');
const content = symbol('com.badlogic.gdx.scenes.scene2d.ui.Dialog', 'contentTable');
const children = symbol('com.badlogic.gdx.scenes.scene2d.Group', 'children');
const items = symbol('com.badlogic.gdx.utils.Array', 'items');
const coords = Object.fromEntries(['x', 'y', 'width', 'height'].map(name =>
    [name, symbol('com.badlogic.gdx.scenes.scene2d.Actor', name)]));
// Observe screens, dialog bounds and Java exceptions without production-only test hooks.
const observe = `$wnd.purchaseTestScreen=function(){return ${app}&&${app}.${screen} instanceof ${buy}?1:${app}&&${app}.${screen} instanceof ${play}?2:0};
$wnd.purchaseTestDialog=function(){
    var d=${app}&&${app}.${screen} instanceof ${buy}&&${app}.${screen}.${dialog};
    if(!d)return null;
    var c=d.${content}, b=c.${children}.${items}[2];
    return {x:d.${coords.x},y:d.${coords.y},width:d.${coords.width},height:d.${coords.height},
        playX:d.${coords.x}+c.${coords.x}+b.${coords.x}+b.${coords.width}/2,
        playY:d.${coords.y}+c.${coords.y}+b.${coords.y}+b.${coords.height}/2};
};`.replace(/\r?\n/g, '');

(async () => {
    const browser = await chromium.launch({headless: true,
        ...(process.env.BROWSER_PATH ? {executablePath: process.env.BROWSER_PATH} : {}),
        args: ['--use-angle=swiftshader', '--enable-unsafe-swiftshader', '--autoplay-policy=no-user-gesture-required']});
    try {
        for (const platform of ['vk', 'ok-fapi', 'ok-bridge']) for (const mode of ['success', 'early-confirmation', 'restore', 'cancel']) {
            const context = await browser.newContext({viewport: {width: 800, height: 1000}});
            const page = await context.newPage();
            const events = [], errors = [], alerts = [];
            await page.exposeFunction('purchaseTestEvent', e => events.push(e));
            page.on('pageerror', e => { errors.push(e.message); console.error(platform + '/' + mode + ': ' + e.message); });
            page.on('dialog', async d => { alerts.push(d.message()); await d.dismiss(); });
            await page.addInitScript(({mode, platform, message}) => {
                let confirmed = mode === 'restore';
                const emit = event => window.top.purchaseTestEvent(event);
                window.purchaseTestException = error => emit({event: 'javaError', message: error[message]});
                window.vkBridge = {
                    supportsAsync: () => Promise.resolve(true),
                    send: method => {
                        if (method === 'VKWebAppShowOrderBox') {
                            window.top.purchaseTestBridgeCalls = (window.top.purchaseTestBridgeCalls || 0) + 1;
                            emit({event: 'bridge'});
                            if (mode === 'early-confirmation') return new Promise(resolve => {
                                window.top.purchaseTestFinishPayment = () => resolve({success: true, order_id: 'synthetic-order'});
                            });
                            return Promise.resolve(mode === 'cancel' ? {status: 'cancel'} : {success: true, order_id: 'synthetic-order'});
                        }
                        return Promise.resolve({result: true});
                    }
                };
                window.FAPI = {
                    init: (_server, _connection, ready) => ready(),
                    UI: {
                        loaded: () => {},
                        showPayment: (_name, _description, item, price, _options, _attributes, currency, callback, uiConf) => {
                            emit({event: 'fapi', item, price, currency, callback, uiConf});
                            window.top.purchaseTestBridgeCalls = (window.top.purchaseTestBridgeCalls || 0) + 1;
                            emit({event: 'bridge'});
                            const finish = () => window.API_callback('showPayment', mode === 'cancel' ? 'error' : 'ok',
                                mode === 'cancel' ? 'cancel' : '299');
                            if (mode === 'early-confirmation') window.top.purchaseTestFinishPayment = finish;
                            else setTimeout(finish, 20);
                        }
                    }
                };
                const userData = () => ({lastLoginTime: 1700000000123, advGoodsEndTime: 1700000000456,
                    purchasedProducts: {class: 'java.util.HashSet', items: []},
                    purchaseList: confirmed ? [{id: 2, amount: 1, token: (platform === 'vk' ? 'vk' : 'ok') + ':synthetic:full'}] : [],
                    buyedProducts: [1, 4], achievments: {'3': 11},
                    ratingMaxDollars: [{id: 17, login: 'test', value: 1700000000123, rank: 1}]});
                class FakeSocket {
                    constructor() {
                        window.top.purchaseTestConfirm = () => { confirmed = true; this.reply({action: 4, userId: 17, kassaUserData: userData()}); };
                        this.readyState = 0;
                        setTimeout(() => { this.readyState = 1; this.onopen?.({}); }, 20);
                    }
                    send(text) {
                        const action = Number(text.match(/"?action"?\s*:\s*(\d+)/)?.[1]);
                        const accountRestored = action === 1 && /"?secret"?\s*:\s*"?synthetic-account/.test(text);
                        emit({event: 'send', action, accountRestored, full: action === 2 && /"?isBuyFull"?\s*:\s*true/.test(text)});
                        if (action === 1) this.reply({action: 1, userId: 17, secret: 'synthetic-account'});
                        if (action === 4 || action === 5) this.reply({action, userId: 17, kassaUserData: userData()});
                        if (action === 6) this.reply({action: 6,
                            vkRequestId: text.match(/"?vkRequestId"?\s*:\s*"?([^,}"\s]+)/)?.[1], vkItem: 'vk2_' + 'a'.repeat(54)});
                        if (action === 7) this.reply({action: 7,
                            okRequestId: text.match(/"?okRequestId"?\s*:\s*"?([^,}"\s]+)/)?.[1],
                            okItem: JSON.stringify({item: 'ok1_' + 'a'.repeat(54), price: 299, currency: 'ok',
                                productHash: 'b'.repeat(64), mode: platform === 'ok-fapi' ? 'fapi' : 'bridge'})});
                    }
                    reply(packet) { setTimeout(() => this.onmessage?.({data: JSON.stringify(packet)}), 20); }
                    close() { this.readyState = 3; this.onclose?.({code: 1000, reason: ''}); }
                }
                Object.assign(FakeSocket, {CONNECTING: 0, OPEN: 1, CLOSING: 2, CLOSED: 3});
                window.WebSocket = FakeSocket;
            }, {mode, platform, message});
            await page.route('**/*', async route => {
                const url = new URL(route.request().url());
                if (url.hostname !== 'mir-game.ru' || !url.pathname.startsWith('/kassa/')) return route.abort();
                const file = path.resolve(root, decodeURIComponent(url.pathname.slice(7)) || 'index.html');
                if (!file.startsWith(root + path.sep) || !fs.existsSync(file) || !fs.statSync(file).isFile())
                    return route.fulfill({status: 404, body: ''});
                let body = fs.readFileSync(file);
                if (file.endsWith('.cache.js')) {
                    const marker = 'function ' + hook + '(';
                    let source = body.toString();
                    assert(source.includes(marker));
                    source = source.replace(marker, observe + marker);
                    // The generated identifiers contain only letters and optional '$'.
                    const safePrintMarker = new RegExp('function ' + print.replaceAll('$', '\\$') + '\\(([^)]*)\\)\\{');
                    assert(safePrintMarker.test(source), 'Throwable observer must be installed');
                    source = source.replace(safePrintMarker, (match, args) => match + '$wnd.purchaseTestException(' + args.split(',')[0] + ');');
                    body = Buffer.from(source);
                }
                const contentType = {'.html': 'text/html; charset=utf-8', '.js': 'application/javascript',
                    '.json': 'application/json', '.png': 'image/png', '.jpg': 'image/jpeg', '.mp3': 'audio/mpeg',
                    '.css': 'text/css'}[path.extname(file)] || 'application/octet-stream';
                await route.fulfill({status: 200, body, contentType});
            });
            const waitForGame = () => page.waitForFunction(() => window.purchaseTestScreen?.() === 2, null, {timeout: 15000});
            const launch = platform === 'vk' ? 'vk_app_id=54675409'
                : platform === 'ok-bridge' ? 'vk_app_id=123456789&vk_client=ok&vk_user_id=456'
                : 'api_server=https%3A%2F%2Fapi.ok.ru%2F&apiconnection=synthetic&logged_user_id=456&application_key=public-test-key';
            await page.goto('https://mir-game.ru/kassa/?' + launch);
            await page.waitForFunction(expected => window.purchaseTestScreen?.() === expected,
                mode === 'restore' ? 2 : 1, {timeout: 30000});
            if (mode === 'restore') {
                assert.equal(events.filter(e => e.event === 'bridge').length, 0);
                assert.equal(await page.evaluate(() => window.purchaseTestDialog()), null,
                    'Restoring a previous purchase must skip the success dialog');
            } else {
                await page.mouse.click(400, 875);
                await page.waitForFunction(() => window.purchaseTestBridgeCalls === 1);
                const prepared = events.filter(e => e.event === 'send' && (e.action === 6 || e.action === 7));
                assert.equal(prepared.length, 1);
                assert.equal(prepared[0].action, platform === 'vk' ? 6 : 7, 'Use the matching platform billing');
                if (platform === 'ok-fapi') {
                    const payment = events.find(e => e.event === 'fapi');
                    assert.equal(payment.price, 299);
                    assert.equal(payment.currency, 'ok');
                    assert.equal(payment.callback, 'true');
                    assert.equal(JSON.parse(payment.uiConf)['popLayer.payment.productHash'], 'b'.repeat(64));
                }
                await page.waitForTimeout(300);
                assert.equal(await page.evaluate(() => window.purchaseTestScreen()), 1);
                assert(!events.some(e => e.full), 'SDK response alone must not grant the product');
                assert.equal(await page.evaluate(() => window.purchaseTestDialog()), null,
                    'SDK response alone must not show the success dialog');
                if (mode === 'success' || mode === 'early-confirmation') {
                    await page.evaluate(() => window.purchaseTestConfirm());
                    await page.waitForFunction(() => window.purchaseTestDialog?.());
                    assert.equal(await page.evaluate(() => window.purchaseTestScreen()), 1,
                        'Confirmed purchase must wait for the Play button');
                    assert(events.some(e => e.full), 'Server confirmation must save full version');
                    if (mode === 'early-confirmation') {
                        await page.evaluate(() => window.purchaseTestFinishPayment());
                    }
                    await page.evaluate(() => window.purchaseTestConfirm());
                    await page.waitForTimeout(200);
                    assert(await page.evaluate(() => window.purchaseTestDialog()),
                        'Repeated confirmations must keep the same success step');
                    // A modal must block taps on the old purchase/close controls.
                    await page.mouse.click(80, 80);
                    await page.mouse.click(400, 875);
                    assert.equal(await page.evaluate(() => window.purchaseTestScreen()), 1);
                    assert.equal(events.filter(e => e.event === 'bridge').length, 1);
                    for (const viewport of [{width: 800, height: 1000}, {width: 390, height: 844},
                        {width: 1000, height: 600}, {width: 844, height: 390}]) {
                        await page.setViewportSize(viewport);
                        await page.waitForTimeout(400);
                        const bounds = await page.evaluate(() => window.purchaseTestDialog());
                        assert(bounds, 'Resize must preserve the success step');
                        assert(bounds.x >= 0 && bounds.y >= 0 && bounds.x + bounds.width <= viewport.width + 1
                            && bounds.y + bounds.height <= viewport.height + 1, 'Dialog must fit the viewport');
                        if (mode === 'success' && platform === 'vk') {
                            await page.screenshot({path: path.join(html, '..', 'build',
                                `purchase-success-${viewport.width}x${viewport.height}.png`)});
                        }
                    }
                    const bounds = await page.evaluate(() => window.purchaseTestDialog());
                    await page.mouse.click(bounds.playX, page.viewportSize().height - bounds.playY);
                    await waitForGame();
                    await page.evaluate(() => window.purchaseTestConfirm());
                    await page.waitForTimeout(200);
                    assert.equal(await page.evaluate(() => window.purchaseTestScreen()), 2);
                    await page.reload();
                    await waitForGame();
                    assert.equal(await page.evaluate(() => window.purchaseTestDialog()), null,
                        'Reload must not repeat the success dialog');
                    await page.waitForFunction(() => window.purchaseTestConfirm);
                    assert.equal(events.filter(e => e.event === 'send' && e.action === 1).at(-1).accountRestored, true,
                        'Reload must retain the game account');
                    assert.equal(events.filter(e => e.event === 'bridge').length, 1);
                } else {
                    await page.mouse.click(80, 80);
                    await waitForGame();
                    assert(!events.some(e => e.full));
                }
            }
            assert.deepEqual(alerts, []);
            assert.deepEqual(errors, []);
            assert.deepEqual(events.filter(e => e.event === 'javaError'), [], 'No swallowed Java exceptions');
            console.log('PASS ' + platform + '/' + mode + ': screen, purchase state, saves, collections and timestamps');
            await context.close();
        }
    } finally {
        await browser.close();
    }
})().catch(error => { console.error(error); process.exitCode = 1; });
