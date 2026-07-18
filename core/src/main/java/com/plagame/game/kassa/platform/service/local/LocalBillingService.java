package com.plagame.game.kassa.platform.service.local;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.platform.service.api.BaseBillingService;
import com.plagame.game.kassa.platform.service.api.model.BillingCatalog;
import com.plagame.game.kassa.platform.service.api.model.BillingProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class LocalBillingService extends BaseBillingService {

    @Override
    public void init() {
        loadCatalog();
    }

    @Override
    public void purchase(BillingProduct product) {
        // Локальная покупка всегда успешна и сразу
        purchaseListener.onPurchased(product);
    }

    @Override
    public void buyHideAdv() {
        User.get().isAdHide = true;
        User.get().saveUser();
    }

    @Override
    public void restorePurchases() {
        GameApplication.get().networkWebSocketClient.sendCheckPurchasesPacket();
    }

    @Override
    public List<BillingProduct> getProducts() {
        return new ArrayList<>(products.values());
    }

    private void loadCatalog() {
        products.clear();
        for(BillingProduct billingProduct : BillingCatalog.values()) {
            products.put(billingProduct.id, billingProduct);
        }
        catalogLoaded = true;
    }

}
