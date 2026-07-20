package com.plagame.game.integration.xsolla;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.BaseBillingService;
import com.plagame.game.integration.platform.service.api.model.BillingCatalog;
import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.xsolla.android.mobile.BillingClient;
import com.xsolla.android.mobile.BillingClientStateListener;
import com.xsolla.android.mobile.BillingFlowParams;
import com.xsolla.android.mobile.BillingResult;
import com.xsolla.android.mobile.Config;
import com.xsolla.android.mobile.ConsumeParams;
import com.xsolla.android.mobile.LogLevel;
import com.xsolla.android.mobile.LoginUuid;
import com.xsolla.android.mobile.ProductDetails;
import com.xsolla.android.mobile.ProductDetailsResponseListener;
import com.xsolla.android.mobile.ProjectId;
import com.xsolla.android.mobile.Purchase;
import com.xsolla.android.mobile.PurchasesUpdatedListener;
import com.xsolla.android.mobile.QueryProductDetailsParams;
import com.xsolla.android.mobile.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Дмитрий Малышев on 25.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
class XsollaBillingService extends BaseBillingService {
//    private static final int PROJECT_ID = 301871; // Test project
//    private static final String LOGIN_ID = "dfcb133b-6d0b-4937-b8d2-c4f4d58fb53a"; // Test login ID

//    private static final int PROJECT_ID = 309320; // kassa project dmitry.malyshev@gmail.com
//    private static final String LOGIN_ID = "fd39b71e-a363-4e4b-b31e-fdc6685b8f37";
    private static final int PROJECT_ID = 309903; // kassa project plagame.ru@gmail.com
    private static final String LOGIN_ID = "99c5c8fa-345b-434e-9ac1-bf25e4e35b1e";

    private final Activity activity;
    private BillingClient mBillingClient;
    private boolean ready = false;
    private final Map<String, ProductDetails> productDetailsMap = new HashMap<>();

    public XsollaBillingService(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void init() {
        createBillingClient(activity);
        startConnection();
    }

    @Override
    public void purchase(BillingProduct product) {
        if(ready && mBillingClient != null) {
            ProductDetails details = productDetailsMap.get(product.id);
            if(details == null) {
                System.err.println("ProductDetails not found: " + product.id);
                return;
            }
            launchBillingFlow(details);
        }
    }

    @Override
    public void buyHideAdv() {
        if (ready && mBillingClient != null) {
            purchase(BillingCatalog.PRODUCT_HIDE_ADV);
        }
    }

    @Override
    public void restorePurchases() {
        if (ready && mBillingClient != null) {
            checkPendingPurchases();
        }
    }

    @Override
    public List<BillingProduct> getProducts() {
        if(products == null || products.isEmpty()) {
            products = parseProductsInternal(productDetailsMap);
        }
        return new ArrayList<>(products.values());
    }

    private Map<String, BillingProduct> parseProductsInternal(Map<String, ProductDetails> productDetailsMap) {
        Map<String, BillingProduct> map = new LinkedHashMap<>();
        for (ProductDetails p : productDetailsMap.values()) {
            String id = p.getProductId();
            ProductDetails.OneTimePurchaseOfferDetails offer = p.getOneTimePurchaseOfferDetails();
            if(offer != null) {
                String formattedPrice = offer.getFormattedPrice().trim(); // "$0.99"
//                if(formattedPrice.contains(".00") || formattedPrice.contains(",00")) {
//                    formattedPrice = formattedPrice.substring(0, formattedPrice.length() - 5);
//                }
                long micros = offer.getPriceAmountMicros();        // 990000
                String currency = offer.getPriceCurrencyCode();    // "USD"
                int gold = BillingCatalog.get(id).gold;
                String desc = gold + " gold";
                BillingProduct base = BillingCatalog.get(p.getProductId());
                map.put(base.id, base.withPrice(formattedPrice));
//                map.put(id, new BillingProduct(id, gold, String.valueOf(gold), desc, formattedPrice + " " + currency));
            } else {
                System.out.println("parseProductsInternal offer is null, ProductId = " + id);
            }
        }
        return map;
    }

    private void createBillingClient(Activity activity) {
        if (mBillingClient == null) {
            //  Перед запуском в рабочую среду отключите режим песочницы и отладочное логирование .
            final Config.Common configCommon = Config.Common.getDefault()
                .withDebugEnabled(true) // отладочное логирование //todo false
                .withLogLevel(LogLevel.VERBOSE)  // отладочное логирование //todo INFO
                .withSandboxEnabled(false) // режим песочницы //todo false
                ;

            final Config.Integration configIntegration = Config.Integration.forXsolla(
                Config.Integration.Xsolla.Authentication.forAutoJWT(
                    ProjectId.parse(PROJECT_ID).getRightOrThrow(),
                    LoginUuid.parse(LOGIN_ID).getRightOrThrow()
                )
            );

            final Config config = new Config(
                configCommon,
                configIntegration,
                Config.Payments.getDefault(),
                Config.Analytics.getDefault()
            );

            // Inside onCreate(), after creating Config:
            mBillingClient = BillingClient.newBuilder(activity)
                .setConfig(config)
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                        if (purchases != null) {
                            for (Purchase p : purchases) {
                                consumePurchase(p);
                            }
                        }
                    }
                })
                .build();
        }
    }

    private void startConnection() {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(final BillingResult billingResult) {
                System.out.println("------onBillingSetupFinished------------billingResult.isSuccessful() = " + billingResult.isSuccessful());
                if (billingResult.isSuccessful()) {
                    ready = true;
                    loadCatalog();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Connection dropped — release the client so it's recreated on next use.
                mBillingClient.endConnection();
                mBillingClient = null;
                ready = false;
            }
        });
    }

    private void loadCatalog() {
        List<QueryProductDetailsParams.Product> list = new ArrayList<>();
        for (BillingProduct billingProduct : BillingCatalog.values()) {
            list.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(billingProduct.id)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            );
        }

        QueryProductDetailsParams params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(list)
                .build();

        mBillingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<ProductDetails> list) {
                System.out.println("-------queryProductDetailsAsync-------billingResult.isSuccessful() = " + billingResult.isSuccessful());
                if (billingResult.isSuccessful() && list != null) {
                    System.out.println("-------list.size() = " + list.size());
                    productDetailsMap.clear();
                    for (ProductDetails p : list) {
                        productDetailsMap.put(p.getProductId(), p);
                    }
                    products = parseProductsInternal(productDetailsMap);
                    catalogLoaded = true;
                }
            }
        });
    }

    private void launchBillingFlow(ProductDetails productDetails) {
        BillingFlowParams params =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    Collections.singletonList(
                        BillingFlowParams.ProductDetailsParams
                            .newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                )
                .build();
        mBillingClient.launchBillingFlow(activity, params);
    }

    private void consumePurchase(Purchase purchase) {
        String token = purchase.getPurchaseToken();

        if (User.get().isPurchasedToken(token)) {
            return;
        }

        ConsumeParams params =
            ConsumeParams.newBuilder()
                .setPurchaseToken(token)
                .build();

        mBillingClient.consumeAsync(params, (billingResult, purchaseToken) -> {
            if (!billingResult.isSuccessful()) {
                System.err.println("Consume failed: " + billingResult.getDebugMessage());
                return;
            }

            User.get().markPurchasedToken(token);

            for (String productId : purchase.getProducts()) {
                BillingProduct product = products.get(productId);
                if (product != null && purchaseListener != null) {
                    purchaseListener.onPurchased(product);
                }
            }
        });
    }

    private void checkPendingPurchases() {
        mBillingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            (billingResult, purchases) -> {

                if (!billingResult.isSuccessful() || purchases == null)
                    return;

                for (Purchase purchase : purchases) {
                    consumePurchase(purchase);
                }
            }
        );
    }

}
