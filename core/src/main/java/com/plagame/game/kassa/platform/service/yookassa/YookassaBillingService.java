package com.plagame.game.kassa.platform.service.yookassa;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.platform.service.api.BaseBillingService;
import com.plagame.game.kassa.platform.service.api.model.BillingCatalog;
import com.plagame.game.kassa.platform.service.api.model.BillingProduct;
import com.plagame.game.kassa.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class YookassaBillingService extends BaseBillingService {

    @Override
    public void init() {
        loadCatalog();
    }

    @Override
    public void purchase(BillingProduct product) {
        try {
            HttpUtil.sendYookassaRedirectHttpRequest(User.get().id, product.gold);
        } catch (Exception e) {
            System.err.println("Yookassa Purchase failed");
            e.printStackTrace();
        }
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
            String price = Math.round(billingProduct.gold * 0.08f) + " RUB";
            billingProduct = billingProduct.withPrice(price);
            products.put(billingProduct.id, billingProduct);
        }
        catalogLoaded = true;
    }

}
