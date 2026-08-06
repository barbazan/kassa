package com.plagame.game.integration.play;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.BaseBillingService;
import com.plagame.game.integration.platform.service.api.model.BillingCatalog;
import com.plagame.game.integration.platform.service.api.model.BillingProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GooglePlayBillingService extends BaseBillingService implements PurchasesUpdatedListener {

    private final Activity activity;
    private BillingClient billingClient;
    private boolean connected;
    private final Map<String, ProductDetails> productDetailsMap = new HashMap<>();

    public GooglePlayBillingService(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void init() {
        connect();
    }

    private void connect() {
        if (billingClient != null) { // это на случай множественных дисконнектов
            billingClient.endConnection();
            billingClient = null;
        }

        billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connected = true;
                    Log.i("GoogleBilling","BillingClient.BillingResponseCode.OK");
                    System.out.println("---------------BillingClient.BillingResponseCode.OK");
                    loadCatalog();
                    restorePurchases();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.e("GoogleBilling","onBillingServiceDisconnected");
                System.out.println("---------------onBillingServiceDisconnected");
                connected = false;
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (!connected) {
                        connect();
                    }
                }, 3000);
            }
        });
    }

    private void loadCatalog() {
        Log.i("GoogleBilling","loadCatalog");
        System.out.println("---------------loadCatalog");
        List<QueryProductDetailsParams.Product> list = new ArrayList<>();
        for (BillingProduct p : BillingCatalog.values()) {
            list.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(p.id)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            );
        }
        QueryProductDetailsParams params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(list)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, result) -> {
                Log.d("GoogleBilling", "code = " + billingResult.getResponseCode());
                Log.d("GoogleBilling", "message = " + billingResult.getDebugMessage());

                if (result != null) {
                    Log.d("GoogleBilling", "count = " + result.getProductDetailsList().size());
                }
                List<ProductDetails> productDetailsList = result.getProductDetailsList();
                System.out.println("-------billingResult.getResponseCode() = " + billingResult.getResponseCode());
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || productDetailsList == null) {
                    Log.i("GoogleBilling","queryProductDetails: code=" + billingResult.getResponseCode() + ", debug=" + billingResult.getDebugMessage());
                    return;
                }
                Log.i("GoogleBilling","queryProductDetailsAsync: productDetailsList.size() = " + productDetailsList.size());
                System.out.println("-------queryProductDetailsAsync: productDetailsList.size() = " + productDetailsList.size());
                products.clear();
                productDetailsMap.clear();
                for (ProductDetails details : productDetailsList) {
                    Log.i("GoogleBilling", "FOUND: " + details.getProductId());
                    System.out.println("------------FOUND: " + details.getProductId());
                    productDetailsMap.put(
                        details.getProductId(),
                        details
                    );
                    BillingProduct base = BillingCatalog.get(details.getProductId());
                    System.out.println("-----------base = " + base);
                    if (base != null) {
                        String price = "";
                        ProductDetails.OneTimePurchaseOfferDetails offer = details.getOneTimePurchaseOfferDetails();
                        System.out.println("------------offer = " + offer);
                        if (offer != null) {
                            price = offer.getFormattedPrice();
                        }
                        System.out.println("------------price = " + price);
                        products.put(base.id, base.withPrice(price));
                    }
                }
                catalogLoaded = true;
            });
    }

    @Override
    public List<BillingProduct> getProducts() {
        return new ArrayList<>(products.values());
    }

    @Override
    public void restorePurchases() {
        if (billingClient == null || !connected) {
            return;
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            (billingResult, purchases) -> {

                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || purchases == null) {
                    Log.e("GoogleBilling", billingResult.getDebugMessage());
                    return;
                }

                for (Purchase p : purchases) {
                    if (p.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        handlePurchase(p);
                    }
                }
            }
        );
    }

    @Override
    public void purchase(BillingProduct product) {
        if (billingClient == null || !connected || product == null) {
            return;
        }
        if (productDetailsMap.isEmpty()) {
            Log.e("GoogleBilling", "Catalog not loaded");
            return;
        }
        ProductDetails details = productDetailsMap.get(product.id);
        if (details == null) {
            return;
        }

        BillingFlowParams.ProductDetailsParams productParams =
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build();

        BillingFlowParams flowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(List.of(productParams))
                .build();

        BillingResult result = billingClient.launchBillingFlow(activity, flowParams);
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e("GoogleBilling","-------Billing launchBillingFlow error: " + result.getDebugMessage());
        }
    }

    @Override
    public void buyHideAdv() {
        BillingProduct p = BillingCatalog.get(BillingCatalog.PRODUCT_HIDE_ADV);
        if (p != null) {
            purchase(p);
        }
    }

    @Override
    public void buyFullVersion() {
        BillingProduct p = BillingCatalog.get(BillingCatalog.PRODUCT_FULL_VERSION);
        if (p != null) {
            purchase(p);
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("GoogleBilling", "Purchase canceled");
            return;
        }
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || purchases == null) {
            Log.e("GoogleBilling", billingResult.getDebugMessage());
            return;
        }
        for (Purchase purchase : purchases) {
            handlePurchase(purchase);
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
            return;
        }
        for (String id : purchase.getProducts()) {
            BillingProduct product = BillingCatalog.get(id);
            if (product != null) {
                processPurchase(purchase, product);
            }
        }
    }

    private void processPurchase(Purchase purchase, BillingProduct product) {
        if (billingClient == null || !connected || purchaseListener == null) {
            return;
        }
        consume(purchase, product);
    }

    private void consume(Purchase purchase, BillingProduct product) {
        if (billingClient == null || !connected) {
            return;
        }
        if (User.get().isPurchasedToken(purchase.getPurchaseToken())) {
            Log.d("GoogleBilling","Token already processed: " + purchase.getPurchaseToken());
            return;
        }
        ConsumeParams params =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.consumeAsync(params, (billingResult, token) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchaseListener != null) {
                    User.get().markPurchasedToken(token);
                    purchaseListener.onPurchased(product); // CONSUMABLE (gold)
                }
            } else {
                Log.e("GoogleBilling","Consume error: " + billingResult.getDebugMessage());
            }
        });
    }

}


//public void dispose() {
//    connected = false;
//    if (billingClient != null) {
//        billingClient.endConnection();
//        billingClient = null;
//        productDetailsMap.clear();
//        products.clear();
//    }
//}

//    private void handlePurchase(Purchase purchase) {
//        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
//            return;
//        }
//
//        String token = purchase.getPurchaseToken();
//
//        // защита от дубля
//        if (User.get().isPurchasedToken(token)) {
//            return;
//        }
//
//        // ACK ОДИН РАЗ НА ВЕСЬ PURCHASE
//        acknowledge(purchase);
//
//        List<String> ids = purchase.getProducts();
//
//        for (String id : ids) {
//            BillingProduct product = BillingCatalog.get(id);
//            if (product == null) continue;
//            processPurchase(purchase, product);
//        }
//    }

// =========================================================
// ACKNOWLEDGE
// =========================================================

//    private void acknowledge(Purchase purchase) {
//        if (purchase.isAcknowledged()) {
//            return;
//        }
//        AcknowledgePurchaseParams params =
//            AcknowledgePurchaseParams.newBuilder()
//                .setPurchaseToken(purchase.getPurchaseToken())
//                .build();
//        billingClient.acknowledgePurchase(params,billingResult -> {
//            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
//                Log.e("GoogleBilling","-------Billing acknowledgePurchase error: " + billingResult.getDebugMessage());
//            }
//        });
//    }
