package com.plagame.game.kassa.platform.service.api.model;

import static com.plagame.game.kassa.platform.service.api.model.BillingCatalog.PRODUCT_HIDE_ADV;

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
            long dollars = getActionDollars(product); // если акция дает доллары то начислить
            if (dollars > 0) {
                User.get().changeDollars(dollars);
            }
            long folowers = getActionFolowers(product); // если акция дает подписчиков то начислить
            if (folowers > 0) {
                User.get().changeFolowers(folowers);
            }
        }
        User.get().saveUser();
    }

    private static long getActionDollars(BillingProduct product) {
        int goldCount = product.gold;
        if(product.id.startsWith("shop_action_1")) {
            return (long)(User.get().dollars + goldCount * User.get().pasIncome);
        } else if(product.id.startsWith("shop_action_3")) {
            return (long)(2 * User.get().dollars + 2 * goldCount * User.get().pasIncome);
        }
        return 0;
    }

    private static long getActionFolowers(BillingProduct product) {
        int goldCount = product.gold;
        if(product.id.startsWith("shop_action_2")) {
            return (long)(User.get().folowers + goldCount * User.get().folowersIncome);
        } else if(product.id.startsWith("shop_action_4")) {
            return (long)(2 * User.get().folowers + 2 * goldCount * User.get().folowersIncome);
        }
        return 0;
    }

}
