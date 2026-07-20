package com.plagame.game.integration.platform.service.api.model;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class BillingProduct {

    public final String id; // строка вида "shop_gold_1"
    public final int gold; // число, например 50
    public final String title; // строка вида "50"
    public final String description; // строка вида "50 gold"

    /** Цена, полученная от платформы */
    public final String price; // строка вида "99 RUB"

    public BillingProduct(String id, int gold, String title, String description, String price) {
        this.id = id;
        this.gold = gold;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public TextureRegion getTextureRegion() {
        return ATLAS_1.findRegion(id);
    }

    public BillingProduct withPrice(String price) {
        return new BillingProduct(
            id,
            gold,
            title,
            description,
            price
        );
    }

    @Override
    public String toString() {
        return "BillingProduct{" +
            "id='" + id + '\'' +
            ", gold=" + gold +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", price='" + price + '\'' +
            '}';
    }
}
