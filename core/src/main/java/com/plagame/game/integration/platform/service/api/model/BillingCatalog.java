package com.plagame.game.integration.platform.service.api.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Дмитрий Малышев on 19.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public final class BillingCatalog {

    private static final Map<String, BillingProduct> PRODUCTS = new LinkedHashMap<>();
    public static final String PRODUCT_HIDE_ADV = "shop_hide_adv";
    public static final String PRODUCT_FULL_VERSION = "full_version";

    static {

//        add(new BillingProduct(
//            "shop_gold_1",
//            100,
//            "100",
//            "100 gold",
//            "10 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_gold_2",
//            500,
//            "500",
//            "500 gold",
//            "50 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_gold_3",
//            1000,
//            "1000",
//            "1000 gold",
//            "100 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_gold_4",
//            2500,
//            "2500",
//            "2500 gold",
//            "220 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_gold_5",
//            5000,
//            "5000",
//            "5000 gold",
//            "420 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_gold_6",
//            25000,
//            "25000",
//            "25000 gold",
//            "2000 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_action_1",
//            200,
//            "200",
//            "200 gold",
//            "50 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_action_2",
//            1500,
//            "1500",
//            "1500 gold",
//            "125 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_action_3",
//            3000,
//            "3000",
//            "3000 gold",
//            "350 TST"
//        ));
//
//        add(new BillingProduct(
//            "shop_action_4",
//            6000,
//            "6000",
//            "6000 gold",
//            "700 TST"
//        ));

//        add(new BillingProduct(
//            PRODUCT_HIDE_ADV,
//            0,
//            "Отключить рекламу",
//            "Отключить рекламу",
//            "99 TST"
//        ));

        add(new BillingProduct(
            PRODUCT_FULL_VERSION,
            1,
            "Полная версия игры",
            "Полная версия игры: 132 товара и 42 персонажа",
            "399 TST"
        ));
    }

    private static void add(BillingProduct product) {
        PRODUCTS.put(product.id, product);
    }

    public static BillingProduct get(String id) {
        return PRODUCTS.get(id);
    }

    public static BillingProduct getByGold(int gold) { // возвращает BillingProduct по количеству золота (Ярик не присылает sku(productId), поэтому определяем BillingProduct по кол-ву игровой валюты)
        for(BillingProduct billingProduct : PRODUCTS.values()) {
            if(billingProduct.id.equals(PRODUCT_FULL_VERSION)) { // только один продукт у нас PRODUCT_FULL_VERSION
                return billingProduct;
            }
        }
        return null;
    }

    public static Collection<BillingProduct> values() {
        return PRODUCTS.values();
    }

}
