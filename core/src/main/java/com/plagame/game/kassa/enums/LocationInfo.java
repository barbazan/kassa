package com.plagame.game.kassa.enums;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.NumberFormat;

/**
 * Created by Дмитрий Малышев on 28.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum LocationInfo {
    // всего 8 локаций
    // каждая локация дает +50 уровней прокачки
    //
    // 1 локация от 1 до 74 доход x 1
    // 2 локация от 74 до 124 доход x 4
    // 3 локация от 124 до 174 доход x 100
    // 4 локация от 174 до 224 доход x 2.5K // Las Vegas
    // 5 локация от 224 до 274 доход x 80K
    // 6 локация от 274 до 374 доход x 2.5M
    // 7 локация от 374 до 474 доход x 67.5M
    // 8 локация от 474 до 599 доход x 2B // Los Angeles
    //

    START_HOUSE(1, "Гетто"), // 🏚️ Стартовый дом (гетто / район) ;
    EUROPE(2, "Европа"), // 🏠 Улучшенный дом
    DUBAI(3, "Дубай"), // 🏔️ Aspen
    LAS_VEGAS(4, "Лас Вегас"), // 🎰 Las Vegas
//    MANSION(5, "Особняк"), //🏡 Особняк (Mansion)
//    MIAMI(6, "Miami"), // 🌴 Miami
//    LOS_ANGELES(7, "Los Angeles"), // 🌆 Los Angeles (LA)
//    SINGAPORE(8, "Singapore"), // 🌏 Singapore
    ;

    public static final LocationInfo MAX_LOCATION = LAS_VEGAS;
//    public static final LocationInfo MAX_LOCATION = SINGAPORE;
    public final int type;
    public final String name;
    public TextureAtlas atlas;

    LocationInfo(int type, String name) {
        this.type = type;
        this.name = name;
        ENUM_MAPS.LocationInfoMap.put(type, this);
    }

    public TextureAtlas getAtlas() {
        if(atlas == null) {
            atlas = AssetUtil.getTextureAtlas(getAtlasName());
        }
        return atlas;
    }

    public String getAtlasName() {
        return "images/kassa_atlas_location_" + type + ".atlas";
    }

    public TextureRegion getNextLocationTexture() {
        return getAtlas().findRegion("next_location");
    }

    public static LocationInfo getByType(int type) {
        return ENUM_MAPS.LocationInfoMap.get(type);
    }

    public int getStartUpgradeLevel() { // уровень с которого стартует на данной локации
        if (type <= 1) {
            return 0;
        }
        int prevType = type - 1;
        int base = 74;
        if (prevType <= 5) {
            return base + (prevType - 1) * 50 + 1;
        }
        if (prevType <= 7) {
            return 274 + (prevType - 5) * 100 + 1;
        }
        return 600;
    }

    public int getLockUpgradeLevel() { // уровень на котором лочится прокачта и требует других вещей
        if (type <= 5) {
            return getMaxUpgradeLevel() - 25;
        }
        if (type <= 7) {
            return getMaxUpgradeLevel() - 50;
        }
        return getMaxUpgradeLevel();
    }

    public int getMaxUpgradeLevel() { // макс уровень прокачки на этой локации, на этом уровне требует переезд
        int base = 74;
        if (type <= 5) {
            return base + (type - 1) * 50;
        }
        if (type <= 7) {
            return 274 + (type - 5) * 100;
        }
        return 599;
    }

    public String getMultiplierAsString() {
        return NumberFormat.format(getMultiplier());
    }

    public long getMultiplier() {
        switch (type) {
            case 1: return 1;
            case 2: return 4;
            case 3: return 100;
            case 4: return 2500;
            case 5: return 80000;
            case 6: return 2_500_000;
            case 7: return 67_500_000;
            case 8: return 2_000_000_000;
        }
        return 1;
    }
}
