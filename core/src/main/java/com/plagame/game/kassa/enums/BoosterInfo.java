package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.utils.Time;

/**
 * Created by Дмитрий Малышев on 25.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum BoosterInfo { // Эти бустеры на время
    BOOSTER_ENERGY(1, "Энергетик", "Безлимитная энергия \nна 10 минут", 18, 10 * Time.MINUTE_MILLIS),// Энергетик
    BOOSTER_KOFE(2, "Кофе", "Увеличение дохода за тап \nв 4 раза на 15 минут", 90, 15 * Time.MINUTE_MILLIS),
    BOOSTER_PIZZA(3, "Пончик", "Увеличение пасс. дохода \nв 4 раза на 15 минут", 140, 15 * Time.MINUTE_MILLIS), //Пицца
    ;

    public int type;
    public String name;
    public String description;
    public int costGold;
    public long duration;
    private TextureRegion textureRegion;

    BoosterInfo(int type, String name, String description, int costGold, long duration) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.costGold = costGold;
        this.duration = duration;
        ENUM_MAPS.BuffInfoMap.put(type, this);
    }

    public static BoosterInfo getByType(int type) {
        return ENUM_MAPS.BuffInfoMap.get(type);
    }

    public TextureRegion getTextureRegion() {
        if(textureRegion == null) {
            textureRegion = ATLAS_1.findRegion("icon_buff_" + type);
        }
        return textureRegion;
    }
}
