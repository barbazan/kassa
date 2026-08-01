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

    // без ног
    CUSTOMER_INFO_100(100),
    CUSTOMER_INFO_101(101),
    CUSTOMER_INFO_102(102),
    CUSTOMER_INFO_103(103),
    CUSTOMER_INFO_104(104),
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
        return type >= CUSTOMER_INFO_100.type;
    }
}
