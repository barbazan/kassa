package com.plagame.game.integration.platform.service.api;

import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.plagame.game.integration.platform.service.api.model.PurchaseListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Дмитрий Малышев on 20.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class BaseBillingService implements BillingService {

    protected PurchaseListener purchaseListener;
    protected Map<String, BillingProduct> products = new LinkedHashMap<>();
    protected volatile boolean catalogLoaded = false;

    @Override
    public void setPurchaseListener(PurchaseListener listener) {
        this.purchaseListener = listener;
    }

    @Override
    public void purchase(String productId) {
        BillingProduct product = products.get(productId);
        if (product == null) {
            System.err.println("Product not found: " + productId);
            return;
        }
        purchase(product);
    }

    public boolean isCatalogLoaded() {
        return catalogLoaded;
    }

    @Override
    public void consume(BillingProduct product) {
        purchaseListener.onPurchased(product);
    }
}
