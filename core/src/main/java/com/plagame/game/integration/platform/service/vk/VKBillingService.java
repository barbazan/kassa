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
        VKSDK.init(new PlatformCallback<String>() {
            @Override
            public void onSuccess(String value) {
                // VK obtains the actual price from the server's get_item handler.
                BillingProduct fullVersion = BillingCatalog.get(BillingCatalog.PRODUCT_FULL_VERSION);
                if (fullVersion != null) {
                    products.put(fullVersion.id, fullVersion.withPrice("Цена в окне VK"));
                }
                catalogLoaded = !products.isEmpty();
                restorePurchases();
            }

            @Override
            public void onError(String error) {
                System.err.println("VK billing initialization failed: " + error);
            }
        });
    }

    @Override
    public void purchase(BillingProduct product) {
        if (!catalogLoaded || product == null || !products.containsKey(product.id)) {
            System.err.println("VK product is not loaded");
            return;
        }
        if (purchasePending) return;
        if (purchaseListener == null) {
            System.err.println("VK purchase listener is not set");
            return;
        }
        GameApplication game = GameApplication.get();
        if (game == null || game.networkWebSocketClient == null) return;
        purchasePending = true;
        // The server signs an item bound to both the game account and the VK buyer.
        game.networkWebSocketClient.prepareVKPurchase(product.id, VKSDK.getLaunchParams(), new PlatformCallback<String>() {
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

    private void purchaseFailed(String error) {
        purchasePending = false;
        System.err.println("VK purchase failed: " + error);
        VKSDK.alert("Не удалось начать оплату VK. Попробуйте позже.");
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