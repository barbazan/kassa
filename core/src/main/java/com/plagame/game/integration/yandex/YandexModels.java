package com.plagame.game.integration.yandex;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 17.08.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexModels {

    public static class LeaderboardResponse {
        public List<YandexLeaderboardEntry> entries = new ArrayList<>();
        public Leaderboard leaderboard = new Leaderboard();
        public int userRank;
    }

    public static class YandexLeaderboardEntry {
        public String extraData;
        public long score;
        public int rank;
        public String formattedScore;
        public YandexPlayer yandexPlayer = new YandexPlayer();
    }

    public static class YandexPlayer {
        public String lang;
        public String publicName;
        public String uniqueID;
        public String avatarPermission;
        public String publicNamePermission;
    }

    public static class Leaderboard {
        public String name;
        public int appID;
        public String titleRu;
    }

    // Элемент каталога
    public static class Product {
        public String id; // строка вида "shop_gold_1"
        public String title; // строка вида "50"
        public String description; // строка вида "50 gold"
        public String price; // строка вида "99 RUB"
        public String priceValue; // строка вида "0.99"
        public String priceCurrencyCode; // строка вида "USD"

        public TextureRegion getTextureRegion() {
            return ATLAS_1.findRegion(id);
        }

        public static int parseTitle(YandexModels.Product p) {
            try {
                return Integer.parseInt(p.title);
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", price='" + price + '\'' +
                    ", priceValue='" + priceValue + '\'' +
                    ", priceCurrencyCode='" + priceCurrencyCode + '\'' +
                    '}';
        }
    }

    // Купленный предмет
    public static class Purchase {
        public String productID;
        public String purchaseToken;

        @Override
        public String toString() {
            return "Purchase{" +
                    "productID='" + productID + '\'' +
                    ", purchaseToken='" + purchaseToken + '\'' +
                    '}';
        }
    }
}
