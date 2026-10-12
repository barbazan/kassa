(function (window, document) {
    'use strict';
    var ready = null;
    var pending = null;
    var callbackInstalled = false;

    function withTimeout(promise, message) {
        return new Promise(function (resolve, reject) {
            var timer = window.setTimeout(function () { reject(new Error(message)); }, 15000);
            promise.then(function (value) { window.clearTimeout(timer); resolve(value); },
                function (error) { window.clearTimeout(timer); reject(error); });
        });
    }

    function focusGame() {
        var canvas = document.querySelector('#embed-html canvas');
        if (canvas) canvas.focus();
    }

    function installCallback() {
        if (callbackInstalled) return;
        callbackInstalled = true;
        var previous = window.API_callback;
        window.API_callback = function (method, result, data) {
            if (method !== 'showPayment' || !pending) {
                if (typeof previous === 'function') previous(method, result, data);
                return;
            }
            var purchase = pending;
            pending = null;
            focusGame();
            if (result === 'cancel' || data === 'cancel') purchase.resolve({status: 'cancel'});
            else if (result === 'ok') purchase.resolve({status: 'success'});
            else purchase.reject(new Error('OK payment was rejected'));
        };
    }

    function loadFAPI() {
        if (window.FAPI) return Promise.resolve(window.FAPI);
        return withTimeout(new Promise(function (resolve, reject) {
            var script = document.createElement('script');
            script.src = 'https://api.ok.ru/js/fapi5.js';
            script.onload = function () {
                if (window.FAPI) resolve(window.FAPI);
                else reject(new Error('OK SDK is unavailable'));
            };
            script.onerror = function () { reject(new Error('Cannot load OK SDK')); };
            document.head.appendChild(script);
        }), 'OK SDK loading timed out');
    }

    function init() {
        if (!ready) {
            installCallback();
            var params = new URLSearchParams(window.location.search);
            if (params.has('api_server')) {
                ready = loadFAPI().then(function (api) {
                    return withTimeout(new Promise(function (resolve, reject) {
                        api.init(params.get('api_server'), params.get('apiconnection'), function () {
                            resolve({mode: 'fapi', api: api});
                        }, function () { reject(new Error('OK initialization failed')); });
                    }), 'OK initialization timed out');
                });
            } else if (params.get('vk_client') === 'ok' && window.kassaVKBilling) {
                // Use the unified payment API only when this OK client advertises support.
                ready = window.kassaVKBilling.init().then(function () { return {mode: 'bridge'}; });
            } else {
                ready = Promise.reject(new Error('OK launch parameters are missing'));
            }
            ready = ready.catch(function (error) { ready = null; throw error; });
        }
        return ready;
    }

    window.kassaOKBilling = {
        init: init,
        loaded: function () {
            init().then(function (sdk) {
                if (sdk.mode === 'fapi' && sdk.api.UI.loaded) sdk.api.UI.loaded();
            }).catch(function () { /* Billing initialization can be retried by the buy button. */ });
        },
        purchase: function (orderJson) {
            var order;
            try { order = JSON.parse(orderJson); } catch (error) { return Promise.reject(new Error('Invalid OK order')); }
            if (!order || typeof order.item !== 'string' || !/^ok1_[A-Za-z0-9_-]{54}$/.test(order.item)
                || !Number.isSafeInteger(order.price) || order.price <= 0 || order.currency !== 'ok'
                || !/^[a-f0-9]{64}$/.test(order.productHash) || !/^(fapi|bridge)$/.test(order.mode)) {
                return Promise.reject(new Error('Invalid OK order'));
            }
            if (pending) return Promise.reject(new Error('OK purchase is already pending'));
            return new Promise(function (resolve, reject) {
                pending = {resolve: resolve, reject: reject};
                init().then(function (sdk) {
                    if (order.mode !== sdk.mode) throw new Error('OK payment mode mismatch');
                    if (sdk.mode === 'bridge') {
                        window.kassaVKBilling.purchase(order.item).then(function (result) {
                            var purchase = pending;
                            pending = null;
                            focusGame();
                            purchase.resolve(result);
                        }, fail);
                    } else {
                        sdk.api.UI.showPayment('Полная версия «Кассы»', 'Открывает полную версию игры',
                            order.item, order.price, null, null, 'ok', 'true',
                            JSON.stringify({'popLayer.payment.productHash': order.productHash}));
                    }
                }).catch(fail);
            });
        }
    };

    function fail(error) {
        var purchase = pending;
        pending = null;
        focusGame();
        if (purchase) purchase.reject(error);
    }
})(window, document);
