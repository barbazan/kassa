package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.GameConfig;

/**
 * Created by Дмитрий Малышев on 22.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum ProductInfo {
    PRODUCT_INFO_1(1),
    PRODUCT_INFO_2(2),
    PRODUCT_INFO_3(3),
    PRODUCT_INFO_4(4),
    PRODUCT_INFO_5(5),
    PRODUCT_INFO_6(6),
    PRODUCT_INFO_7(7),
    PRODUCT_INFO_8(8),
    PRODUCT_INFO_9(9),
    PRODUCT_INFO_10(10),
    PRODUCT_INFO_11(11),
    PRODUCT_INFO_12(12),
    PRODUCT_INFO_13(13),
    PRODUCT_INFO_14(14),
    PRODUCT_INFO_15(15),
    PRODUCT_INFO_16(16),
    PRODUCT_INFO_17(17),
    PRODUCT_INFO_18(18),
    PRODUCT_INFO_19(19),
    PRODUCT_INFO_20(20),
    ;

    public final int type;
    private TextureRegion textureRegion;

    ProductInfo(int type) {
        this.type = type;
        ENUM_MAPS.PRODUCT_INFO_MAP.put(type, this);
    }

    public static ProductInfo getByType(int type) {
        return ENUM_MAPS.PRODUCT_INFO_MAP.get(type);
    }

    public static ProductInfo getRandom() {
        int rndType = 1 + GameConfig.random.nextInt(values().length);
        return ENUM_MAPS.PRODUCT_INFO_MAP.get(rndType);
    }

    public TextureRegion getTextureRegion() {
        if(textureRegion == null) {
            textureRegion = ATLAS_1.findRegion("product_" + type);
        }
        return textureRegion;
    }
}
