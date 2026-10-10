(function (window, document) {
    'use strict';
    var ready = null;
    var purchasePending = false;

    function withTimeout(promise, message) {
        return new Promise(function (resolve, reject) {
            var timer = window.setTimeout(function () { reject(new Error(message)); }, 15000);
            promise.then(function (value) {
                window.clearTimeout(timer);
                resolve(value);
            }, function (error) {
                window.clearTimeout(timer);
                reject(error);
            });
        });
    }

    function loadBridge() {
        if (window.vkBridge) return Promise.resolve(window.vkBridge);
        return withTimeout(new Promise(function (resolve, reject) {
            var script = document.createElement('script');
            script.src = 'https://unpkg.com/@vkontakte/vk-bridge@2.15.0/dist/browser.min.js';
            script.onload = function () {
                if (window.vkBridge) resolve(window.vkBridge);
                else reject(new Error('VK Bridge is unavailable'));
            };
            script.onerror = function () { reject(new Error('Cannot load VK Bridge')); };
            document.head.appendChild(script);
        }), 'VK Bridge loading timed out');
    }

    function init() {
        if (!ready) {
            ready = loadBridge().then(function (bridge) {
                return withTimeout(bridge.send('VKWebAppInit', {}), 'VK initialization timed out').then(function () {
                    return withTimeout(bridge.supportsAsync('VKWebAppShowOrderBox'),
                        'VK payment support check timed out').then(function (supported) {
                        if (!supported) throw new Error('VK purchases are unavailable on this platform');
                        return bridge;
                    });
                });
            }).catch(function (error) {
                ready = null;
                throw error;
            });
        }
        return ready;
    }

    function focusGame() {
        var canvas = document.querySelector('#embed-html canvas');
        if (canvas) canvas.focus();
    }

    window.kassaVKBilling = {
        init: init,
        purchase: function (item) {
            // VK's payment API limits item identifiers to 64 characters.
            if (typeof item !== 'string' || !item.length || item.length > 64) {
                return Promise.reject(new Error('Invalid VK item: expected 1 to 64 characters'));
            }
            if (purchasePending) return Promise.reject(new Error('VK purchase is already pending'));
            purchasePending = true;
            return init().then(function (bridge) {
                return bridge.send('VKWebAppShowOrderBox', { type: 'item', item: item });
            }).then(function (result) {
                purchasePending = false;
                focusGame();
                if (result && result.status === 'cancel') return result;
                // Current VK Bridge returns success: true; older clients use status: 'success'.
                var successful = result && (result.success === true
                    || (typeof result.success === 'undefined' && result.status === 'success'));
                if (!successful || !result.order_id) {
                    throw new Error('VK purchase failed');
                }
                return { status: 'success', order_id: result.order_id };
            }, function (error) {
                purchasePending = false;
                focusGame();
                throw error;
            });
        }
    };
})(window, document);