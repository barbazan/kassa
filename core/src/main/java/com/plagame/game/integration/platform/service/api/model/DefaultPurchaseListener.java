package com.plagame.game.integration.platform.service.api.model;

import static com.plagame.game.integration.platform.service.api.model.BillingCatalog.PRODUCT_HIDE_ADV;

import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 19.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class DefaultPurchaseListener implements PurchaseListener {

    @Override
    public void onPurchased(BillingProduct product) {
        if(product.id.equals(PRODUCT_HIDE_ADV)) { // это отключение рекламы
            User.get().isAdHide = true;
        } else {
            User.get().changeGold(product.gold);
        }
        User.get().saveUser();
    }

}
