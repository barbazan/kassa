package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.GameConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Дмитрий Малышев on 22.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum ProductInfo {
    PRODUCT_INFO_1(1, 4),
    PRODUCT_INFO_2(2, 4.5f),
    PRODUCT_INFO_3(3, 5),
    PRODUCT_INFO_4(4, 6),
    PRODUCT_INFO_5(5, 1.75f),
    PRODUCT_INFO_6(6, 2),
    PRODUCT_INFO_7(7, 2.5f),
    PRODUCT_INFO_8(8, 3),
    PRODUCT_INFO_9(9, 4.5f),
    PRODUCT_INFO_10(10, 6.5f),
    PRODUCT_INFO_11(11, 11.5f),
    PRODUCT_INFO_12(12, 4.5f),
    PRODUCT_INFO_13(13, 6.5f),
    PRODUCT_INFO_14(14, 5.5f),
    PRODUCT_INFO_15(15, 15),
    PRODUCT_INFO_16(16, 35),
    PRODUCT_INFO_17(17, 25),
    PRODUCT_INFO_18(18, 6.75f),
    PRODUCT_INFO_19(19, 12.5f),
    PRODUCT_INFO_20(20, 22.75f),
    ;

    public final int type;
    public final float cost;
    private TextureRegion textureRegion;

    ProductInfo(int type, float cost) {
        this.type = type;
        this.cost = cost;
        ENUM_MAPS.PRODUCT_INFO_MAP.put(type, this);
    }

    public static ProductInfo getByType(int type) {
        return ENUM_MAPS.PRODUCT_INFO_MAP.get(type);
    }

    public static ProductInfo getRandom() {
        int rndType = 1 + GameConfig.random.nextInt(values().length);
        return ENUM_MAPS.PRODUCT_INFO_MAP.get(rndType);
    }

    public static Set<ProductInfo> getRandomSet(int count) {
        count = Math.min(count, values().length);
        Set<ProductInfo> set = new HashSet<>();
        while (set.size() < count) {
            set.add(getRandom());
        }
        return set;
    }

    public TextureRegion getTextureRegion() {
        if(textureRegion == null) {
            System.out.println("\"product_\" + type = " + "product_" + type);
            textureRegion = ATLAS_1.findRegion("product_" + type);
            System.out.println("--------textureRegion = " + textureRegion);
        }
        return textureRegion;
    }
}
