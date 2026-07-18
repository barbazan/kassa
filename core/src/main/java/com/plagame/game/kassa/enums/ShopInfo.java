package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.kassa.enums.UpgradeInfo.BACKYARD;
import static com.plagame.game.kassa.enums.UpgradeInfo.CAMERA;
import static com.plagame.game.kassa.enums.UpgradeInfo.CAR;
import static com.plagame.game.kassa.enums.UpgradeInfo.CHARISMA;
import static com.plagame.game.kassa.enums.UpgradeInfo.CLOTH;
import static com.plagame.game.kassa.enums.UpgradeInfo.EMOTIONALITY;
import static com.plagame.game.kassa.enums.UpgradeInfo.ERUDITION;
import static com.plagame.game.kassa.enums.UpgradeInfo.GLASSES;
import static com.plagame.game.kassa.enums.UpgradeInfo.GRASS_ROAD;
import static com.plagame.game.kassa.enums.UpgradeInfo.HAIRSTYLE;
import static com.plagame.game.kassa.enums.UpgradeInfo.HOUSE;
import static com.plagame.game.kassa.enums.UpgradeInfo.IMPROVISATION;
import static com.plagame.game.kassa.enums.UpgradeInfo.LIGHT;
import static com.plagame.game.kassa.enums.UpgradeInfo.MICRO;
import static com.plagame.game.kassa.enums.UpgradeInfo.SMARTNESS;
import static com.plagame.game.kassa.enums.UpgradeInfo.STATUE;
import static com.plagame.game.kassa.enums.UpgradeInfo.TRIPOD;
import static com.plagame.game.kassa.enums.UpgradeInfo.WATCH;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.beans.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 29.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum ShopInfo {
    THINGS(1, "ИМУЩЕСТВО"), // ------ Имущество ------ tapIncome: +2% * nextLevel, pasIncome: +4% * nextLevel - (+4% * nextLevel) / 100
    EQUIPMENT(2, "ТЕХНИКА"), // ------ Техника ------ tapIncome: +4% * nextLevel, pasIncome: +2% * nextLevel
    STYLE(3, "СТИЛЬ"), // ------ Стиль ------ tapIncome: +2% * nextLevel, pasIncome: +4% * nextLevel
    SKILLS(4, "НАВЫКИ"), // ------ Навыки ------ tapIncome: +3% * nextLevel, pasIncome: +3% * nextLevel
    ;

//    private static final List<UpgradeInfo> THINGS_LIST = Arrays.asList(HOUSE, CAR, GRASS_ROAD, BACKYARD, STATUE);
//    private static final List<UpgradeInfo> EQUIPMENT_LIST = Arrays.asList(CAMERA, TRIPOD, MICRO, LIGHT);
//    private static final List<UpgradeInfo> STYLE_LIST = Arrays.asList(CLOTH, HAIRSTYLE, GLASSES, WATCH);
//    private static final List<UpgradeInfo> SKILLS_LIST = Arrays.asList(CHARISMA, IMPROVISATION, ERUDITION, SMARTNESS, EMOTIONALITY);
    public final int type;
    public final String name;
//    private List<UpgradeInfo> upgradesList = new ArrayList<>();

    ShopInfo(int type, String name) {
        this.type = type;
        this.name = name;
        ENUM_MAPS.ShopInfoMap.put(type, this);
    }

    public static ShopInfo getByType(int type) {
        return ENUM_MAPS.ShopInfoMap.get(type);
    }

    public TextureRegion getIconTexture() {
        return ATLAS_1.findRegion("icon_shop_" + type);
    }

    public TextureRegion getButtonTexture() {
        return ATLAS_1.findRegion("button_shop_" + type);
    }

    public List<UpgradeInfo> getUpgradesList() {
//        switch (this) {
//            case THINGS: return THINGS_LIST;
//            case EQUIPMENT: return EQUIPMENT_LIST;
//            case STYLE: return STYLE_LIST;
//            case SKILLS: return SKILLS_LIST;
//            default: return THINGS_LIST;
//        }
        switch (this) {
            case THINGS: return Arrays.asList(HOUSE, CAR, GRASS_ROAD, BACKYARD, STATUE);
            case EQUIPMENT: return Arrays.asList(CAMERA, TRIPOD, MICRO, LIGHT);
            case STYLE: return Arrays.asList(CLOTH, HAIRSTYLE, GLASSES, WATCH);
            case SKILLS: return Arrays.asList(CHARISMA, IMPROVISATION, ERUDITION, SMARTNESS, EMOTIONALITY);
            default: return Arrays.asList(HOUSE, CAR, GRASS_ROAD, BACKYARD, STATUE);
        }
    }

    public int getUpgradeCountAvailable() {
        int maxValue = 0;
        for(int i = 0; i < getUpgradesList().size(); i++) {
            UpgradeInfo upgradeInfo = getUpgradesList().get(i);
            int curLevel = User.get().getUpgradeLevel(upgradeInfo);
            int count = upgradeInfo.getUpgradeCountMax(curLevel + 1);
            if(count > maxValue) {
                maxValue = count;
            }
        }
        return maxValue;
    }
}
