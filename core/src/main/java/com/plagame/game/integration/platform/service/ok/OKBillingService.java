package com.plagame.game.integration.platform.service.ok;

import com.plagame.game.integration.platform.service.api.BaseBillingService;
import com.plagame.game.integration.platform.service.api.model.BillingCatalog;
import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;
import com.plagame.game.integration.ok.OKSDK;
import com.plagame.game.kassa.GameApplication;

import java.util.ArrayList;
import java.util.List;

public class OKBillingService extends BaseBillingService {
    private boolean purchasePending;

    @Override
    public void init() {
        catalogLoaded = false;
        products.clear();
        initializeOK(new PlatformCallback<String>() {
            @Override
            public void onSuccess(String value) {
                loadProducts();
                restorePurchases();
            }

            @Override
            public void onError(String error) {
                System.err.println("OK billing initialization failed: " + error);
            }
        });
    }

    private void loadProducts() {
        // The signed server order supplies the actual price to the payment dialog.
        BillingProduct fullVersion = BillingCatalog.get(BillingCatalog.PRODUCT_FULL_VERSION);
        if (fullVersion != null) {
            products.put(fullVersion.id, fullVersion.withPrice("Цена в окне ОК"));
        }
        catalogLoaded = !products.isEmpty();
    }

    @Override
    public void purchase(final String productId) {
        if (purchasePending) return;
        if (!BillingCatalog.PRODUCT_FULL_VERSION.equals(productId)) {
            purchaseFailed("Unsupported OK product");
            return;
        }
        GameApplication game = GameApplication.get();
        if (game == null || game.networkWebSocketClient == null
            || !game.networkWebSocketClient.isConnected() || !game.networkWebSocketClient.authorized) {
            showPurchaseError("Нет соединения с игровым сервером. Дождитесь подключения и нажмите «Купить» ещё раз.");
            return;
        }
        if (!catalogLoaded) {
            // A failed startup initialization must not permanently disable the buy button.
            purchasePending = true;
            initializeOK(new PlatformCallback<String>() {
                @Override
                public void onSuccess(String value) {
                    loadProducts();
                    purchasePending = false;
                    purchase(productId);
                }

                @Override
                public void onError(String error) {
                    purchaseFailed(error);
                }
            });
            return;
        }
        purchase(products.get(productId));
    }

    @Override
    public void purchase(BillingProduct product) {
        if (!catalogLoaded || product == null || !products.containsKey(product.id)) {
            purchaseFailed("OK product is not loaded");
            return;
        }
        if (purchasePending) return;
        if (purchaseListener == null) {
            purchaseFailed("OK purchase listener is not set");
            return;
        }
        GameApplication game = GameApplication.get();
        if (game == null || game.networkWebSocketClient == null) {
            purchaseFailed("Game server is unavailable");
            return;
        }
        purchasePending = true;
        // The server signs an item bound to both the game account and the OK buyer.
        game.networkWebSocketClient.prepareOKPurchase(product.id, getOKLaunchParams(), new PlatformCallback<String>() {
            @Override
            public void onSuccess(String item) {
                OKSDK.purchase(item, new PlatformCallback<String>() {
                    @Override
                    public void onSuccess(String orderId) {
                        purchasePending = false;
                        // Grant only purchases saved after OK's signed server notification.
                        restorePurchases();
                    }

                    @Override
                    public void onError(String error) {
                        purchaseFailed(error);
                    }
                }, () -> purchasePending = false);
            }

            @Override
            public void onError(String error) {
                purchaseFailed(error);
            }
        });
    }

    protected void initializeOK(PlatformCallback<String> callback) {
        OKSDK.init(callback);
    }

    protected String getOKLaunchParams() {
        return OKSDK.getLaunchParams();
    }

    protected void showPurchaseError(String message) {
        OKSDK.alert(message);
    }

    private void purchaseFailed(String error) {
        purchasePending = false;
        System.err.println("OK purchase failed: " + error);
        showPurchaseError("Не удалось начать оплату в ОК. Попробуйте ещё раз или перезагрузите игру.");
    }

    @Override
    public void buyHideAdv() {
        purchase(BillingCatalog.PRODUCT_HIDE_ADV);
    }

    @Override
    public void buyFullVersion() {
        purchase(BillingCatalog.PRODUCT_FULL_VERSION);
    }

    @Override
    public void restorePurchases() {
        if (!catalogLoaded || purchaseListener == null) return;
        GameApplication game = GameApplication.get();
        if (game != null && game.networkWebSocketClient != null) {
            game.networkWebSocketClient.sendCheckPurchasesPacket();
        }
    }

    @Override
    public List<BillingProduct> getProducts() {
        return new ArrayList<>(products.values());
    }
}
