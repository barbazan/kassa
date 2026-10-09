package com.plagame.game.integration.platform.service.vk;

import com.plagame.game.integration.platform.service.api.BaseBillingService;
import com.plagame.game.integration.platform.service.api.model.BillingCatalog;
import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;
import com.plagame.game.integration.vk.VKSDK;
import com.plagame.game.kassa.GameApplication;

import java.util.ArrayList;
import java.util.List;

public class VKBillingService extends BaseBillingService {
    private boolean purchasePending;

    @Override
    public void init() {
        catalogLoaded = false;
        products.clear();
        initializeVK(new PlatformCallback<String>() {
            @Override
            public void onSuccess(String value) {
                loadProducts();
                restorePurchases();
            }

            @Override
            public void onError(String error) {
                System.err.println("VK billing initialization failed: " + error);
            }
        });
    }

    private void loadProducts() {
        // VK obtains the actual price from the server's get_item handler.
        BillingProduct fullVersion = BillingCatalog.get(BillingCatalog.PRODUCT_FULL_VERSION);
        if (fullVersion != null) {
            products.put(fullVersion.id, fullVersion.withPrice("Цена в окне VK"));
        }
        catalogLoaded = !products.isEmpty();
    }

    @Override
    public void purchase(final String productId) {
        if (purchasePending) return;
        if (!BillingCatalog.PRODUCT_FULL_VERSION.equals(productId)) {
            purchaseFailed("Unsupported VK product");
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
            initializeVK(new PlatformCallback<String>() {
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
            purchaseFailed("VK product is not loaded");
            return;
        }
        if (purchasePending) return;
        if (purchaseListener == null) {
            purchaseFailed("VK purchase listener is not set");
            return;
        }
        GameApplication game = GameApplication.get();
        if (game == null || game.networkWebSocketClient == null) {
            purchaseFailed("Game server is unavailable");
            return;
        }
        purchasePending = true;
        // The server signs an item bound to both the game account and the VK buyer.
        game.networkWebSocketClient.prepareVKPurchase(product.id, getVKLaunchParams(), new PlatformCallback<String>() {
            @Override
            public void onSuccess(String item) {
                VKSDK.purchase(item, new PlatformCallback<String>() {
                    @Override
                    public void onSuccess(String orderId) {
                        purchasePending = false;
                        // Grant only purchases saved after VK's signed server notification.
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

    protected void initializeVK(PlatformCallback<String> callback) {
        VKSDK.init(callback);
    }

    protected String getVKLaunchParams() {
        return VKSDK.getLaunchParams();
    }

    protected void showPurchaseError(String message) {
        VKSDK.alert(message);
    }

    private void purchaseFailed(String error) {
        purchasePending = false;
        System.err.println("VK purchase failed: " + error);
        showPurchaseError("Не удалось начать оплату VK. Попробуйте ещё раз или перезагрузите игру.");
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
