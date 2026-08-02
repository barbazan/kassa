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
    PRODUCT_INFO_1(1, 1.29f),
    PRODUCT_INFO_2(2, 1.49f),
    PRODUCT_INFO_3(3, 1.79f),
    PRODUCT_INFO_4(4, 1.99f),
    PRODUCT_INFO_5(5, 2.29f),
    PRODUCT_INFO_6(6, 2.59f),
    PRODUCT_INFO_7(7, 2.99f),
    PRODUCT_INFO_8(8, 3.49f),
    PRODUCT_INFO_9(9, 3.99f),
    PRODUCT_INFO_10(10, 4.49f),

    PRODUCT_INFO_11(11, 4.99f),
    PRODUCT_INFO_12(12, 5.49f),
    PRODUCT_INFO_13(13, 5.99f),
    PRODUCT_INFO_14(14, 6.49f),
    PRODUCT_INFO_15(15, 6.99f),
    PRODUCT_INFO_16(16, 7.99f),
    PRODUCT_INFO_17(17, 8.99f),
    PRODUCT_INFO_18(18, 9.49f),
    PRODUCT_INFO_19(19, 9.99f),
    PRODUCT_INFO_20(20, 10.99f),

    PRODUCT_INFO_21(21, 11.99f),
    PRODUCT_INFO_22(22, 12.99f),
    PRODUCT_INFO_23(23, 13.99f),
    PRODUCT_INFO_24(24, 14.99f),
    PRODUCT_INFO_25(25, 15.99f),
    PRODUCT_INFO_26(26, 16.99f),
    PRODUCT_INFO_27(27, 17.99f),
    PRODUCT_INFO_28(28, 18.99f),
    PRODUCT_INFO_29(29, 19.99f),
    PRODUCT_INFO_30(30, 21.99f),

    PRODUCT_INFO_31(31, 23.99f),
    PRODUCT_INFO_32(32, 25.99f),
    PRODUCT_INFO_33(33, 27.99f),
    PRODUCT_INFO_34(34, 29.99f),
    PRODUCT_INFO_35(35, 31.99f),
    PRODUCT_INFO_36(36, 34.99f),
    PRODUCT_INFO_37(37, 37.99f),
    PRODUCT_INFO_38(38, 40.99f),
    PRODUCT_INFO_39(39, 43.99f),
    PRODUCT_INFO_40(40, 46.99f),

    PRODUCT_INFO_41(41, 49.99f),
    PRODUCT_INFO_42(42, 53.99f),
    PRODUCT_INFO_43(43, 57.99f),
    PRODUCT_INFO_44(44, 61.99f),
    PRODUCT_INFO_45(45, 66.99f),
    PRODUCT_INFO_46(46, 71.99f),
    PRODUCT_INFO_47(47, 77.99f),
    PRODUCT_INFO_48(48, 83.99f),
    PRODUCT_INFO_49(49, 89.99f),
    PRODUCT_INFO_50(50, 96.99f),

    PRODUCT_INFO_51(51, 104.99f),
    PRODUCT_INFO_52(52, 112.99f),
    PRODUCT_INFO_53(53, 121.99f),
    PRODUCT_INFO_54(54, 131.99f),
    PRODUCT_INFO_55(55, 142.99f),
    PRODUCT_INFO_56(56, 154.99f),
    PRODUCT_INFO_57(57, 167.99f),
    PRODUCT_INFO_58(58, 181.99f),
    PRODUCT_INFO_59(59, 196.99f),
    PRODUCT_INFO_60(60, 212.99f),

    PRODUCT_INFO_61(61, 229.99f),
    PRODUCT_INFO_62(62, 247.99f),
    PRODUCT_INFO_63(63, 266.99f),
    PRODUCT_INFO_64(64, 286.99f),
    PRODUCT_INFO_65(65, 307.99f),
    PRODUCT_INFO_66(66, 329.99f),
    PRODUCT_INFO_67(67, 352.99f),
    PRODUCT_INFO_68(68, 376.99f),
    PRODUCT_INFO_69(69, 401.99f),
    PRODUCT_INFO_70(70, 427.99f),

    PRODUCT_INFO_71(71, 454.99f),
    PRODUCT_INFO_72(72, 482.99f),
    PRODUCT_INFO_73(73, 511.99f),
    PRODUCT_INFO_74(74, 541.99f),
    PRODUCT_INFO_75(75, 572.99f),
    PRODUCT_INFO_76(76, 604.99f),
    PRODUCT_INFO_77(77, 637.99f),
    PRODUCT_INFO_78(78, 671.99f),
    PRODUCT_INFO_79(79, 706.99f),
    PRODUCT_INFO_80(80, 742.99f),

    PRODUCT_INFO_81(81, 779.99f),
    PRODUCT_INFO_82(82, 817.99f),
    PRODUCT_INFO_83(83, 856.99f),
    PRODUCT_INFO_84(84, 896.99f),
    PRODUCT_INFO_85(85, 937.99f),
    PRODUCT_INFO_86(86, 979.99f),
    PRODUCT_INFO_87(87, 1022.99f),
    PRODUCT_INFO_88(88, 1066.99f),
    PRODUCT_INFO_89(89, 1111.99f),
    PRODUCT_INFO_90(90, 1157.99f),

    PRODUCT_INFO_91(91, 1204.99f),
    PRODUCT_INFO_92(92, 1252.99f),
    PRODUCT_INFO_93(93, 1301.99f),
    PRODUCT_INFO_94(94, 1351.99f),
    PRODUCT_INFO_95(95, 1402.99f),
    PRODUCT_INFO_96(96, 1454.99f),
    PRODUCT_INFO_97(97, 1507.99f),
    PRODUCT_INFO_98(98, 1561.99f),
    PRODUCT_INFO_99(99, 1616.99f),
    PRODUCT_INFO_100(100, 1672.99f),

    PRODUCT_INFO_101(101, 1729.99f),
    PRODUCT_INFO_102(102, 1787.99f),
    PRODUCT_INFO_103(103, 1846.99f),
    PRODUCT_INFO_104(104, 1906.99f),
    PRODUCT_INFO_105(105, 1967.99f),
    PRODUCT_INFO_106(106, 2029.99f),
    PRODUCT_INFO_107(107, 2092.99f),
    PRODUCT_INFO_108(108, 2156.99f),
    PRODUCT_INFO_109(109, 2221.99f),
    PRODUCT_INFO_110(110, 2287.99f),
    PRODUCT_INFO_111(111, 2354.99f),
    PRODUCT_INFO_112(112, 2422.99f),
    PRODUCT_INFO_113(113, 2491.99f),
    PRODUCT_INFO_114(114, 2561.99f),
    PRODUCT_INFO_115(115, 2632.99f),
    PRODUCT_INFO_116(116, 2705.99f),
    PRODUCT_INFO_117(117, 2779.99f),
    PRODUCT_INFO_118(118, 2854.99f),
    PRODUCT_INFO_119(119, 2930.99f),
    PRODUCT_INFO_120(120, 3007.99f),
    PRODUCT_INFO_121(121, 3085.99f),
    PRODUCT_INFO_122(122, 3164.99f),
    PRODUCT_INFO_123(123, 3244.99f),
    PRODUCT_INFO_124(124, 3325.99f),
    PRODUCT_INFO_125(125, 3407.99f),
    PRODUCT_INFO_126(126, 3491.99f),
    PRODUCT_INFO_127(127, 3576.99f),
    PRODUCT_INFO_128(128, 3662.99f),

    // Золотые украшения
    PRODUCT_INFO_129(129, 2149.95f),
    PRODUCT_INFO_130(130, 3187.50f),
    PRODUCT_INFO_131(131, 4276.75f),
    PRODUCT_INFO_132(132, 5189.90f),
    ;

    public static final List<Integer> START_PRODUCT_LIST = Arrays.asList(
        PRODUCT_INFO_1.type, PRODUCT_INFO_2.type, PRODUCT_INFO_3.type, PRODUCT_INFO_4.type,
        PRODUCT_INFO_5.type, PRODUCT_INFO_6.type, PRODUCT_INFO_7.type, PRODUCT_INFO_8.type
    );
    public static final List<ProductInfo> UNLOCK_PRODUCT_LIST = Arrays.asList(
        PRODUCT_INFO_1, PRODUCT_INFO_2, PRODUCT_INFO_3, PRODUCT_INFO_4,
        PRODUCT_INFO_5, PRODUCT_INFO_6, PRODUCT_INFO_7, PRODUCT_INFO_8,
        PRODUCT_INFO_9, PRODUCT_INFO_10, PRODUCT_INFO_11, PRODUCT_INFO_12,
        PRODUCT_INFO_13, PRODUCT_INFO_14, PRODUCT_INFO_15, PRODUCT_INFO_16,
        PRODUCT_INFO_17, PRODUCT_INFO_18, PRODUCT_INFO_19, PRODUCT_INFO_20
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
