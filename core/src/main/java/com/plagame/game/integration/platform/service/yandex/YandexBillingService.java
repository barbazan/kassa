package com.plagame.game.integration.platform.service.yandex;

import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.BaseBillingService;
import com.plagame.game.integration.platform.service.api.model.BillingCatalog;
import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.plagame.game.integration.yandex.YandexModels;
import com.plagame.game.integration.yandex.YandexParser;
import com.plagame.game.integration.yandex.YandexSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexBillingService extends BaseBillingService {

    @Override
    public void init() {
        loadCatalog();
    }

    @Override
    public void purchase(BillingProduct product) {
        if (!catalogLoaded) {
            System.err.println("Catalog not loaded yet");
            return;
        }
        YandexSDK.checkAuthorized(
            () -> purchaseInternal(product),
            () -> {
                YandexSDK.authorize(
                    () -> {
                        purchaseInternal(product);
                    },
                    () -> {}
                );
            }
        );
    }

    @Override
    public void buyHideAdv() {
        BillingProduct product = products.get(BillingCatalog.PRODUCT_HIDE_ADV);
        if (product == null) {
            System.err.println("Hide adv product not loaded yet");
            return;
        }
        purchase(product);
    }

    @Override
    public void buyFullVersion() {
        BillingProduct product = products.get(BillingCatalog.PRODUCT_FULL_VERSION);
        if (product == null) {
            System.err.println("Full version product not loaded yet");
            return;
        }
        purchase(product);

    }

    private void purchaseInternal(BillingProduct product) {
        YandexSDK.purchase(
            product.id,
            purchase -> restorePurchases(),
            () -> System.err.println("Purchase failed")
        );
    }

    @Override
    public void restorePurchases() {
        checkPendingPurchases();
    }

    @Override
    public List<BillingProduct> getProducts() {
        return new ArrayList<>(products.values());
    }

    private void loadCatalog() {
        YandexSDK.getCatalog(new YandexSDK.JsonCallback() {
            @Override
            public void onResult(String json) {
                List<YandexModels.Product> list = YandexParser.parseProducts(json);
                if(list != null && !list.isEmpty()) {
                    products.clear();
                    for (YandexModels.Product p : list) {
                        BillingProduct product = convert(p);
                        if(product != null) {
                            products.put(product.id, product);
                        }
                    }
                } else {
                    System.err.println("catalog is null or empty");
//                    YandexSDK.alert("catalog is null or empty");
                }
                catalogLoaded = true;
            }

            @Override
            public void onError(String error) {
//                YandexSDK.alert("loadCatalog error: " + error);
                catalogLoaded = false;
            }

        });
    }

    private void checkPendingPurchases() {
        YandexSDK.getPurchases(new YandexSDK.JsonCallback() {
            @Override
            public void onResult(String json) {
                try {
                    List<YandexModels.Purchase> purchases = YandexParser.parsePurchases(json);
                    if(purchases != null) {
                        for (YandexModels.Purchase p : purchases) {
                            String token = p.purchaseToken;
                            if (token == null || token.isEmpty()) continue;
                            if (User.get().isPurchasedToken(token)) continue;
                            BillingProduct product = products.get(p.productID);
                            if (product == null) {
                                System.err.println("Unknown purchased product: " + p.productID);
                                continue;
                            }
                            YandexSDK.consumePurchase(
                                token,
                                () -> {
                                    User.get().markPurchasedToken(token);
                                    purchaseListener.onPurchased(product);
                                },
                                () -> System.err.println("Consume failed " + token)
                            );
                        }
                    } else {
                        System.err.println("purchases is null");
//                        YandexSDK.alert("purchases is null");
                    }

                } catch (Exception e) {
//                    System.err.println("checkPendingPurchases error: " + e);
                    YandexSDK.alert("checkPendingPurchases error: " + e);
                }
            }

            @Override
            public void onError(String error) {
                System.err.println("getPurchases error: " + error);
//                YandexSDK.alert("getPurchases error: " + error);
            }
        });
    }

    private BillingProduct convert(YandexModels.Product p) {
        BillingProduct product = BillingCatalog.get(p.id);
        if (product == null) {
//            YandexSDK.alert("Unknown product from Yandex: " + p.id);
            return null;
        }
        return product.withPrice(p.price);
    }

}
