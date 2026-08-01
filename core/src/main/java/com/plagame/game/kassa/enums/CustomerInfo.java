package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_CUSTOMERS;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;

import java.util.Arrays;
import java.util.List;

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
    CUSTOMER_INFO_12(12),
    CUSTOMER_INFO_13(13),
    CUSTOMER_INFO_14(14),
    CUSTOMER_INFO_15(15),
    CUSTOMER_INFO_16(16),
    CUSTOMER_INFO_17(17),
    CUSTOMER_INFO_18(18),
    CUSTOMER_INFO_19(19),
    CUSTOMER_INFO_20(20),
    CUSTOMER_INFO_31(31),
    CUSTOMER_INFO_32(32),
    CUSTOMER_INFO_33(33),
    CUSTOMER_INFO_34(34),
    CUSTOMER_INFO_35(35),
    CUSTOMER_INFO_36(36),
    CUSTOMER_INFO_37(37),
    CUSTOMER_INFO_38(38),
    CUSTOMER_INFO_39(39),
    CUSTOMER_INFO_40(40),
    CUSTOMER_INFO_41(41),
    CUSTOMER_INFO_42(42),
    CUSTOMER_INFO_43(43),
    CUSTOMER_INFO_44(44),
    CUSTOMER_INFO_45(45),
    ;

    public static final List<CustomerInfo> UNLOCK_CUSTOMER_LIST = Arrays.asList(
        CUSTOMER_INFO_1, CUSTOMER_INFO_2, CUSTOMER_INFO_3,
        CUSTOMER_INFO_4, CUSTOMER_INFO_5, CUSTOMER_INFO_6
    );

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
        if(User.get().isFullVersionBuyed) {
            return getRandom(Arrays.asList(values()));
        } else {
            return getRandom(UNLOCK_CUSTOMER_LIST);
        }
    }

    private static CustomerInfo getRandom(List<CustomerInfo> customerList) {
        int rndType = GameConfig.random.nextInt(customerList.size());
        return customerList.get(rndType);
    }

    public TextureRegion getTextureRegion() {
        if(textureRegion == null) {
            textureRegion = ATLAS_CUSTOMERS.findRegion("customer_" + type);
        }
        return textureRegion;
    }

    public boolean isLegless() {
        return false;
    }
}
