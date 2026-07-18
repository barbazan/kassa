package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Дмитрий Малышев on 25.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum PerkInfo { // Эти бустеры навсегда

    PERK_VIDEO_LESSONS(1, "Видеоуроки", "Деньги зарабатываются на 10% быстрее", 4, 0.1f),
    PERK_VIDEO_MONTAJ(2, "Курсы видеомонтажа", "Доход за тап больше на 10%", 3, 0.1f),
    PERK_CREATIVITY(3, "Креативность", "Подарки больше на 20%", 5, 0.2f),
    ;

    public int type;
    public String name;
    public String description;
    public int costGold;
    public float multiplier;
    private TextureRegion textureRegion;


    PerkInfo(int type, String name, String description, int costGold, float multiplier) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.costGold = costGold;
        this.multiplier = multiplier;
        ENUM_MAPS.BoosterInfoMap.put(type, this);
    }

    public static PerkInfo getByType(int type) {
        return ENUM_MAPS.BoosterInfoMap.get(type);
    }

    public TextureRegion getTextureRegion() {
        if(textureRegion == null) {
            textureRegion = ATLAS_1.findRegion("icon_booster_" + type);
        }
        return textureRegion;
    }

    public long getUpgradeCost(int nextLevel) {
        return costGold * nextLevel;
    }

}
