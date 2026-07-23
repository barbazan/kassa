package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_CUSTOMERS;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.GameConfig;

/**
 * Created by Дмитрий Малышев on 23.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum CustomerInfo {

    CUSTOMER_INFO_1(1),
    CUSTOMER_INFO_2(2),
    CUSTOMER_INFO_3(3),
    CUSTOMER_INFO_4(4),
    CUSTOMER_INFO_5(5),
    CUSTOMER_INFO_6(6),
    CUSTOMER_INFO_7(7),
    CUSTOMER_INFO_8(8),
    CUSTOMER_INFO_9(9),
    CUSTOMER_INFO_10(10),
    CUSTOMER_INFO_11(11),
//    CUSTOMER_INFO_12(12),
//    CUSTOMER_INFO_13(13),
//    CUSTOMER_INFO_14(14),
//    CUSTOMER_INFO_15(15),
//    CUSTOMER_INFO_16(16),
//    CUSTOMER_INFO_17(17),
//    CUSTOMER_INFO_18(18),
    ;

    public final int type;
    private TextureRegion textureRegion;

    CustomerInfo(int type) {
        this.type = type;
        ENUM_MAPS.CUSTOMER_INFO_MAP.put(type, this);
    }

    public static CustomerInfo getByType(int type) {
        return ENUM_MAPS.CUSTOMER_INFO_MAP.get(type);
    }

    public static CustomerInfo getRandom() {
        int rndType = 1 + GameConfig.random.nextInt(values().length);
        return ENUM_MAPS.CUSTOMER_INFO_MAP.get(rndType);
    }

    public TextureRegion getTextureRegion() {
        if(textureRegion == null) {
            System.out.println("\"customer_\" + type = " + "customer_" + type);
            textureRegion = ATLAS_CUSTOMERS.findRegion("customer_" + type);
            System.out.println("--------textureRegion = " + textureRegion);
        }
        return textureRegion;
    }

}
