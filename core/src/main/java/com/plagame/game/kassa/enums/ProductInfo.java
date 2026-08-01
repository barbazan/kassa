package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_PRODUCTS;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    PRODUCT_INFO_21(21, 11.5f),
    PRODUCT_INFO_22(22, 4.5f),
    PRODUCT_INFO_23(23, 6.5f),
    PRODUCT_INFO_24(24, 5.5f),
    PRODUCT_INFO_25(25, 15),
    PRODUCT_INFO_26(26, 35),
    PRODUCT_INFO_27(27, 25),
    PRODUCT_INFO_28(28, 6.75f),
    PRODUCT_INFO_29(29, 12.5f),
    PRODUCT_INFO_30(30, 22.75f),
    PRODUCT_INFO_31(31, 11.5f),
    PRODUCT_INFO_32(32, 4.5f),
    PRODUCT_INFO_33(33, 6.5f),
    PRODUCT_INFO_34(34, 5.5f),
    PRODUCT_INFO_35(35, 15),
    PRODUCT_INFO_36(36, 35),
    PRODUCT_INFO_37(37, 25),
    PRODUCT_INFO_38(38, 6.75f),
    PRODUCT_INFO_39(39, 12.5f),
    PRODUCT_INFO_40(40, 22.75f),
    PRODUCT_INFO_41(41, 11.5f),
    PRODUCT_INFO_42(42, 4.5f),
    PRODUCT_INFO_43(43, 6.5f),
    PRODUCT_INFO_44(44, 5.5f),
    PRODUCT_INFO_45(45, 15),
    PRODUCT_INFO_46(46, 35),
    PRODUCT_INFO_47(47, 25),
    PRODUCT_INFO_48(48, 6.75f),
    PRODUCT_INFO_49(49, 12.5f),
    PRODUCT_INFO_50(50, 22.75f),
    PRODUCT_INFO_51(51, 11.5f),
    PRODUCT_INFO_52(52, 4.5f),
    PRODUCT_INFO_53(53, 6.5f),
    PRODUCT_INFO_54(54, 5.5f),
    PRODUCT_INFO_55(55, 15),
    PRODUCT_INFO_56(56, 35),
    PRODUCT_INFO_57(57, 25),
    PRODUCT_INFO_58(58, 6.75f),
    PRODUCT_INFO_59(59, 12.5f),
    PRODUCT_INFO_60(60, 22.75f),
    PRODUCT_INFO_61(61, 11.5f),
    PRODUCT_INFO_62(62, 4.5f),
    PRODUCT_INFO_63(63, 6.5f),
    PRODUCT_INFO_64(64, 5.5f),
    PRODUCT_INFO_65(65, 15),
    PRODUCT_INFO_66(66, 35),
    PRODUCT_INFO_67(67, 25),
    PRODUCT_INFO_68(68, 6.75f),
    PRODUCT_INFO_69(69, 12.5f),
    PRODUCT_INFO_70(70, 22.75f),
    PRODUCT_INFO_71(71, 11.5f),
    PRODUCT_INFO_72(72, 4.5f),
    PRODUCT_INFO_73(73, 6.5f),
    PRODUCT_INFO_74(74, 5.5f),
    PRODUCT_INFO_75(75, 15),
    PRODUCT_INFO_76(76, 35),
    PRODUCT_INFO_77(77, 25),
    PRODUCT_INFO_78(78, 6.75f),
    PRODUCT_INFO_79(79, 12.5f),
    PRODUCT_INFO_80(80, 22.75f),
    PRODUCT_INFO_81(81, 11.5f),
    PRODUCT_INFO_82(82, 4.5f),
    PRODUCT_INFO_83(83, 6.5f),
    PRODUCT_INFO_84(84, 5.5f),
    PRODUCT_INFO_85(85, 15),
    PRODUCT_INFO_86(86, 35),
    PRODUCT_INFO_87(87, 25),
    PRODUCT_INFO_88(88, 6.75f),
    PRODUCT_INFO_89(89, 12.5f),
    PRODUCT_INFO_90(90, 22.75f),
    PRODUCT_INFO_91(91, 11.5f),
    PRODUCT_INFO_92(92, 4.5f),
    PRODUCT_INFO_93(93, 6.5f),
    PRODUCT_INFO_94(94, 5.5f),
    PRODUCT_INFO_95(95, 15),
    PRODUCT_INFO_96(96, 35),
    PRODUCT_INFO_97(97, 25),
    PRODUCT_INFO_98(98, 6.75f),
    PRODUCT_INFO_99(99, 12.5f),
    PRODUCT_INFO_100(100, 22.75f),
    PRODUCT_INFO_101(101, 22.75f),
    PRODUCT_INFO_102(102, 22.75f),
    PRODUCT_INFO_103(103, 22.75f),
    PRODUCT_INFO_104(104, 22.75f),
    PRODUCT_INFO_105(105, 22.75f),
    PRODUCT_INFO_106(106, 22.75f),
    PRODUCT_INFO_107(107, 22.75f),
    PRODUCT_INFO_108(108, 22.75f),
    PRODUCT_INFO_109(109, 22.75f),
    PRODUCT_INFO_110(110, 22.75f),
    PRODUCT_INFO_111(111, 22.75f),
    PRODUCT_INFO_112(112, 22.75f),
    PRODUCT_INFO_113(113, 22.75f),
    PRODUCT_INFO_114(114, 22.75f),
    PRODUCT_INFO_115(115, 22.75f),
    PRODUCT_INFO_116(116, 22.75f),
    PRODUCT_INFO_117(117, 22.75f),
    PRODUCT_INFO_118(118, 22.75f),
    PRODUCT_INFO_119(119, 22.75f),
    PRODUCT_INFO_120(120, 22.75f),
    PRODUCT_INFO_121(121, 22.75f),
    PRODUCT_INFO_122(122, 22.75f),
    PRODUCT_INFO_123(123, 22.75f),
    PRODUCT_INFO_124(124, 22.75f),
    PRODUCT_INFO_125(125, 22.75f),
    PRODUCT_INFO_126(126, 22.75f),
    PRODUCT_INFO_127(127, 22.75f),
    PRODUCT_INFO_128(128, 22.75f),
    PRODUCT_INFO_129(129, 220),
    PRODUCT_INFO_130(130, 275),
    PRODUCT_INFO_131(131, 425),
    PRODUCT_INFO_132(132, 2275),
    ;

    public static final List<Integer> START_PRODUCT_LIST = Arrays.asList(
        PRODUCT_INFO_1.type, PRODUCT_INFO_2.type, PRODUCT_INFO_3.type, PRODUCT_INFO_4.type,
        PRODUCT_INFO_5.type, PRODUCT_INFO_6.type, PRODUCT_INFO_7.type, PRODUCT_INFO_8.type
    );
    public static final List<ProductInfo> UNLOCK_PRODUCT_LIST = Arrays.asList(
        PRODUCT_INFO_1, PRODUCT_INFO_2, PRODUCT_INFO_3, PRODUCT_INFO_4,
        PRODUCT_INFO_5, PRODUCT_INFO_6, PRODUCT_INFO_7, PRODUCT_INFO_8,
        PRODUCT_INFO_9, PRODUCT_INFO_10, PRODUCT_INFO_11, PRODUCT_INFO_12,
        PRODUCT_INFO_13, PRODUCT_INFO_14, PRODUCT_INFO_15, PRODUCT_INFO_16
    );

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
        if(User.get().isFullVersionBuyed) {
            return getRandom(Arrays.asList(values()));
        } else {
            return getRandom(User.get().buyedProducts);
        }
    }

    private static ProductInfo getRandom(Set<Integer> productList) {
        return getRandom(productList.stream()
            .map(ProductInfo::getByType)
            .collect(Collectors.toList())
        );
    }

    private static ProductInfo getRandom(List<ProductInfo> productList) {
        int rndType = GameConfig.random.nextInt(productList.size());
        return productList.get(rndType);
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
            textureRegion = ATLAS_PRODUCTS.findRegion("product_" + type);
            System.out.println("--------textureRegion = " + textureRegion);
        }
        return textureRegion;
    }
}
