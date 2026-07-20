package com.plagame.game.integration.yandex;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Дмитрий Малышев on 17.08.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexParser {
    public static List<YandexModels.Product> parseProducts(String json) {
        List<YandexModels.Product> products = new ArrayList<>();

        // Убираем все лишние пробелы и переносы
        json = json.trim();

        // Считаем, что список продуктов приходит в виде массива: [ {..}, {..}, ... ]
        if (json.startsWith("[")) {
            json = json.substring(1);
        }
        if (json.endsWith("]")) {
            json = json.substring(0, json.length() - 1);
        }

        // Разбиваем на объекты по "}," (конец одного объекта)
        StringTokenizer objectTokens = new StringTokenizer(json, "}");
        while (objectTokens.hasMoreTokens()) {
            String obj = objectTokens.nextToken().trim();
            if (obj.isEmpty() || obj.equals(",")) continue;

            // убираем { если есть
            if (obj.startsWith(",")) obj = obj.substring(1).trim();
            if (obj.startsWith("{")) obj = obj.substring(1).trim();

            YandexModels.Product product = new YandexModels.Product();

            // Разбираем поля по запятым
            StringTokenizer fieldTokens = new StringTokenizer(obj, ",");
            while (fieldTokens.hasMoreTokens()) {
                String field = fieldTokens.nextToken().trim();
                String[] parts = field.split(":", 2);
                if (parts.length < 2) continue;

                String key = clean(parts[0]);
                String value = clean(parts[1]);

                if (key.equals("id")) {
                    product.id = value;
                } else if (key.equals("title")) {
                    product.title = value;
                } else if (key.equals("description")) {
                    product.description = value;
                } else if (key.equals("price")) {
                    product.price = value;
                } else if (key.equals("priceValue")) {
                    product.priceValue = value;
                } else if (key.equals("priceCurrencyCode")) {
                    product.priceCurrencyCode = value;
                }
            }

            products.add(product);
        }

        return products;
    }

    /** Новый метод для парсинга покупок */
    public static List<YandexModels.Purchase> parsePurchases(String json) {
        List<YandexModels.Purchase> purchases = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        StringTokenizer objectTokens = new StringTokenizer(json, "}");
        while (objectTokens.hasMoreTokens()) {
            String obj = objectTokens.nextToken().trim();
            if (obj.isEmpty() || obj.equals(",")) continue;
            if (obj.startsWith(",")) obj = obj.substring(1).trim();
            if (obj.startsWith("{")) obj = obj.substring(1).trim();

            YandexModels.Purchase purchase = new YandexModels.Purchase();
            StringTokenizer fieldTokens = new StringTokenizer(obj, ",");
            while (fieldTokens.hasMoreTokens()) {
                String field = fieldTokens.nextToken().trim();
                String[] parts = field.split(":", 2);
                if (parts.length < 2) continue;

                String key = clean(parts[0]);
                String value = clean(parts[1]);

                switch (key) {
                    case "productID": purchase.productID = value; break;
                    case "purchaseToken": purchase.purchaseToken = value; break;
                }
            }
            purchases.add(purchase);
        }

        return purchases;
    }

    private static String clean(String s) {
        return s.trim().replaceAll("^\"|\"$", ""); // убираем кавычки в начале/конце
    }
}
