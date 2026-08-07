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
    PRODUCT_INFO_1(1, 1_29),
    PRODUCT_INFO_2(2, 1_49),
    PRODUCT_INFO_3(3, 1_79),
    PRODUCT_INFO_4(4, 1_99),
    PRODUCT_INFO_5(5, 2_29),
    PRODUCT_INFO_6(6, 2_59),
    PRODUCT_INFO_7(7, 2_99),
    PRODUCT_INFO_8(8, 3_49),
    PRODUCT_INFO_9(9, 3_99),
    PRODUCT_INFO_10(10, 4_49),

    PRODUCT_INFO_11(11, 4_99),
    PRODUCT_INFO_12(12, 5_49),
    PRODUCT_INFO_13(13, 5_99),
    PRODUCT_INFO_14(14, 6_49),
    PRODUCT_INFO_15(15, 6_99),
    PRODUCT_INFO_16(16, 7_99),
    PRODUCT_INFO_17(17, 8_99),
    PRODUCT_INFO_18(18, 9_49),
    PRODUCT_INFO_19(19, 9_99),
    PRODUCT_INFO_20(20, 10_99),

    PRODUCT_INFO_21(21, 11_99),
    PRODUCT_INFO_22(22, 12_99),
    PRODUCT_INFO_23(23, 13_99),
    PRODUCT_INFO_24(24, 14_99),
    PRODUCT_INFO_25(25, 15_99),
    PRODUCT_INFO_26(26, 16_99),
    PRODUCT_INFO_27(27, 17_99),
    PRODUCT_INFO_28(28, 18_99),
    PRODUCT_INFO_29(29, 19_99),
    PRODUCT_INFO_30(30, 21_99),

    PRODUCT_INFO_31(31, 23_99),
    PRODUCT_INFO_32(32, 25_99),
    PRODUCT_INFO_33(33, 27_99),
    PRODUCT_INFO_34(34, 29_99),
    PRODUCT_INFO_35(35, 31_99),
    PRODUCT_INFO_36(36, 34_99),
    PRODUCT_INFO_37(37, 37_99),
    PRODUCT_INFO_38(38, 40_99),
    PRODUCT_INFO_39(39, 43_99),
    PRODUCT_INFO_40(40, 46_99),

    PRODUCT_INFO_41(41, 49_99),
    PRODUCT_INFO_42(42, 53_99),
    PRODUCT_INFO_43(43, 57_99),
    PRODUCT_INFO_44(44, 61_99),
    PRODUCT_INFO_45(45, 66_99),
    PRODUCT_INFO_46(46, 71_99),
    PRODUCT_INFO_47(47, 77_99),
    PRODUCT_INFO_48(48, 83_99),
    PRODUCT_INFO_49(49, 89_99),
    PRODUCT_INFO_50(50, 96_99),

    PRODUCT_INFO_51(51, 104_99),
    PRODUCT_INFO_52(52, 112_99),
    PRODUCT_INFO_53(53, 121_99),
    PRODUCT_INFO_54(54, 131_99),
    PRODUCT_INFO_55(55, 142_99),
    PRODUCT_INFO_56(56, 154_99),
    PRODUCT_INFO_57(57, 167_99),
    PRODUCT_INFO_58(58, 181_99),
    PRODUCT_INFO_59(59, 196_99),
    PRODUCT_INFO_60(60, 212_99),

    PRODUCT_INFO_61(61, 229_99),
    PRODUCT_INFO_62(62, 247_99),
    PRODUCT_INFO_63(63, 266_99),
    PRODUCT_INFO_64(64, 286_99),
    PRODUCT_INFO_65(65, 307_99),
    PRODUCT_INFO_66(66, 329_99),
    PRODUCT_INFO_67(67, 352_99),
    PRODUCT_INFO_68(68, 376_99),
    PRODUCT_INFO_69(69, 401_99),
    PRODUCT_INFO_70(70, 427_99),

    PRODUCT_INFO_71(71, 454_99),
    PRODUCT_INFO_72(72, 482_99),
    PRODUCT_INFO_73(73, 511_99),
    PRODUCT_INFO_74(74, 541_99),
    PRODUCT_INFO_75(75, 572_99),
    PRODUCT_INFO_76(76, 604_99),
    PRODUCT_INFO_77(77, 637_99),
    PRODUCT_INFO_78(78, 671_99),
    PRODUCT_INFO_79(79, 706_99),
    PRODUCT_INFO_80(80, 742_99),

    PRODUCT_INFO_81(81, 779_99),
    PRODUCT_INFO_82(82, 817_99),
    PRODUCT_INFO_83(83, 856_99),
    PRODUCT_INFO_84(84, 896_99),
    PRODUCT_INFO_85(85, 937_99),
    PRODUCT_INFO_86(86, 979_99),
    PRODUCT_INFO_87(87, 1022_99),
    PRODUCT_INFO_88(88, 1066_99),
    PRODUCT_INFO_89(89, 1111_99),
    PRODUCT_INFO_90(90, 1157_99),

    PRODUCT_INFO_91(91, 1204_99),
    PRODUCT_INFO_92(92, 1252_99),
    PRODUCT_INFO_93(93, 1301_99),
    PRODUCT_INFO_94(94, 1351_99),
    PRODUCT_INFO_95(95, 1402_99),
    PRODUCT_INFO_96(96, 1454_99),
    PRODUCT_INFO_97(97, 1507_99),
    PRODUCT_INFO_98(98, 1561_99),
    PRODUCT_INFO_99(99, 1616_99),
    PRODUCT_INFO_100(100, 1672_99),

    PRODUCT_INFO_101(101, 1729_99),
    PRODUCT_INFO_102(102, 1787_99),
    PRODUCT_INFO_103(103, 1846_99),
    PRODUCT_INFO_104(104, 1906_99),
    PRODUCT_INFO_105(105, 1967_99),
    PRODUCT_INFO_106(106, 2029_99),
    PRODUCT_INFO_107(107, 2092_99),
    PRODUCT_INFO_108(108, 2156_99),
    PRODUCT_INFO_109(109, 2221_99),
    PRODUCT_INFO_110(110, 2287_99),
    PRODUCT_INFO_111(111, 2354_99),
    PRODUCT_INFO_112(112, 2422_99),
    PRODUCT_INFO_113(113, 2491_99),
    PRODUCT_INFO_114(114, 2561_99),
    PRODUCT_INFO_115(115, 2632_99),
    PRODUCT_INFO_116(116, 2705_99),
    PRODUCT_INFO_117(117, 2779_99),
    PRODUCT_INFO_118(118, 2854_99),
    PRODUCT_INFO_119(119, 2930_99),
    PRODUCT_INFO_120(120, 3007_99),
    PRODUCT_INFO_121(121, 3085_99),
    PRODUCT_INFO_122(122, 3164_99),
    PRODUCT_INFO_123(123, 3244_99),
    PRODUCT_INFO_124(124, 3325_99),
    PRODUCT_INFO_125(125, 3407_99),
    PRODUCT_INFO_126(126, 3491_99),
    PRODUCT_INFO_127(127, 3576_99),
    PRODUCT_INFO_128(128, 3662_99),

    // Золотые украшения
    PRODUCT_INFO_129(129, 2149_95),
    PRODUCT_INFO_130(130, 3187_50),
    PRODUCT_INFO_131(131, 4276_75),
    PRODUCT_INFO_132(132, 5189_90),
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
    public static final List<ProductInfo> RARE_PRODUCT_LIST = Arrays.asList( // редкие товары  (всё золото, дубайский шоколад, карандаш, механическая точилка, нить для зубов, лобстер и морской ёж, зелёная бутылка вина, миндальное и кокосовое молоко)
        PRODUCT_INFO_9  ,
        PRODUCT_INFO_10 , PRODUCT_INFO_11 , PRODUCT_INFO_12 , PRODUCT_INFO_34 ,
        PRODUCT_INFO_36 , PRODUCT_INFO_97 , PRODUCT_INFO_105, PRODUCT_INFO_108,
        PRODUCT_INFO_118, PRODUCT_INFO_121, PRODUCT_INFO_127, PRODUCT_INFO_128,
        PRODUCT_INFO_129, PRODUCT_INFO_130, PRODUCT_INFO_131, PRODUCT_INFO_132
    );

    public final int type;
    public final int cost;
    private TextureRegion textureRegion;

    ProductInfo(int type, int cost) {
        this.type = type;
        this.cost = cost;
        ENUM_MAPS.PRODUCT_INFO_MAP.put(type, this);
    }

    public static ProductInfo getByType(int type) {
        return ENUM_MAPS.PRODUCT_INFO_MAP.get(type);
    }

    public static ProductInfo getRandom() {
        HashSet<Integer> alowedProducts = new HashSet<>(User.get().buyedProducts);
        if(User.get().hasAdvGoods()) {
            Set<Integer> adSet = new HashSet<>();
            for(int i = User.get().advGoodIndex; i < User.get().advGoodIndex + 4; i++) {
                adSet.add(i);
            }
            alowedProducts.addAll(adSet);
        }
        return getRandom(alowedProducts);
    }

    public static int getRandomAdvProductType() {
        int count = User.get().buyedProducts.size();
        int rnd = 1 + GameConfig.random.nextInt(3); // не больше чем на 3 товара вперед можно открывать рекламой
        int result = count / 4 + rnd;
        return 1 + result * 4;
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

    public boolean isRare() {
        return ProductInfo.RARE_PRODUCT_LIST.contains(this);
    }
}
