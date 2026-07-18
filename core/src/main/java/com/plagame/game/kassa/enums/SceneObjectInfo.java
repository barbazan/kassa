package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.IMAGE_UNKNOWN_TEXTURE_REGION;
import static com.plagame.game.kassa.enums.UpgradeInfo.getUpgradeInfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 05.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum SceneObjectInfo {
    GROUND(1, 0.0f, 0.0f, 1.0f), // земля или газон вместе с дорогой
//    SKY(2, 0.0f, 0.70f, 1.0f), // небо
    BACKGROUND(3,0.0f, 0.70f, 1.0f), // задний фон района: заводы или свалка, а потом яхты моря, горы в районах побогаче
    TREE(4, 0.0f, 0.65f, 1.0f), // деревя или кусты, которые частично заслоняют задний фон района
//    BACKYARD(5, 0.0f, 2 / 3f, 1.0f), // задний двор(пространсво слева от дома) там может быть барбекю, бассайн, теннисный корт и т.д.
    HOUSE(6, 3 / 8f, 0.62f, 0.48f), // дом
    STATUE(7, 0.012f, 0.535f, 0.4f), // статуя-фонтан
    CAR(8, 0.01f, 0.38f, 0.52f), // машина
    CAMERA(9, 0.21f, 0.225f, 0.15f),
    MICRO(10, 0.25f, 0.15f, 0.15f),
    LIGHT(11,0.025f, 0.17f, 0.22f),
    GIRL(12, 0.49f, 0.20f, 0.39f),
    ;

    public int type;
    private float percentX; // x в процентах от ширины экрана
    private float percentY; // y в процентах от высоты экрана
    private float percentWidth; // width в процентах от ширины экрана
    public Action actionMoveIn;
    public Action actionMoveOut;

    SceneObjectInfo(int type, float percentX, float percentY, float percentWidth) {
        this.type = type;
        this.percentX = percentX;
        this.percentY = percentY;
        this.percentWidth = percentWidth;
        if(type > 2) { // если не земля и небо
            actionMoveIn = Actions.moveTo(getX(), getY(), 0.35f, Interpolation.elasticOut);
            actionMoveOut = Actions.moveTo(getStartX(), getStartY(), 0.35f, Interpolation.circleIn);
        }
        ENUM_MAPS.SceneObjectInfoMap.put(type, this);
    }

    public static SceneObjectInfo getByType(int type) {
        return ENUM_MAPS.SceneObjectInfoMap.get(type);
    }

    public boolean canAction() {
        return type > BACKGROUND.type;
    }

    public boolean isNeedMoveAction() {
        return type >= CAR.type;
    }

    public float getStartX() {
        if(type % 2 == 0) {
            return -getWidth() * 2;
        } else {
            return GameApplication.get().screenWidth + getWidth();
        }
    }

    public float getStartY() {
        return getY();
    }

    public float getX() {
        return GameApplication.get().screenWidth * getPercentX();
    }

    public float getY() {
        return GameApplication.get().screenHeight * getPercentY();
    }

    public float getWidth() {
        return GameApplication.get().screenWidth * getPercentWidth();
    }

    public float getHeight() {
        if(type == GROUND.type) {
            return GameApplication.get().screenHeight;
        } else {
            float baseWidth = getTexture().getRegionWidth();
            float baseHeight = getTexture().getRegionHeight();
            return baseHeight * getWidth() / baseWidth;
        }
    }

    public TextureRegion getTexture() {
        String regionName = getTextureRegionName();
        if(regionName != null) {
            return User.get().getLocationInfo().getAtlas().findRegion(regionName);
        } else {
            return IMAGE_UNKNOWN_TEXTURE_REGION;
        }
    }

    private String getTextureRegionName() {
        UpgradeInfo upgradeInfo = getUpgradeInfo(this);
        int num = User.get().getUpgradeLevelSceneObjectNum(upgradeInfo);
//        int num = 3;
        switch (this) {
            case GROUND: return "ground_1";
//            case GROUND: return "ground_" + num;
//            case SKY: return "sky_" + num;
            case BACKGROUND:
                num = User.get().getUpgradeLevelSceneObjectNum(UpgradeInfo.GRASS_ROAD);
                return "background_" + num;
            case TREE: return "tree_" + num;
//            case BACKYARD: return "backyard_" + num;
            case HOUSE: return "house_" + num;
            case STATUE: return "statue_" + num;
            case CAR: return "car_" + num;
            case CAMERA: return "camera_" + num;
            case MICRO: return "micro_" + num;
            case LIGHT: return "sofit_" + num;
            case GIRL: return "girl_" + num;
        }
        return null;
    }

    public float getPercentX() {
        int location = User.get().location;
        if(location == LocationInfo.START_HOUSE.type) {
            switch (this) {
                case HOUSE: return 0.28f;
                default: return percentX;
            }
        } else if(location == LocationInfo.EUROPE.type) {
            switch (this) {
                case CAMERA: return 0.21f;
                case MICRO: return 0.30f;
                default: return percentX;
            }

        } else if(location == LocationInfo.DUBAI.type) {
            switch (this) {
                case HOUSE: return 0.25f;
                case GIRL: return 0.36f;
                default: return percentX;
            }
        }
        return percentX;
    }

    public float getPercentY() {
        int location = User.get().location;
        if(location == LocationInfo.START_HOUSE.type) {
            switch (this) {
                case STATUE: return 0.555f;
                case CAR: return 0.42f;
                default: return percentY;
            }
        } else if(location == LocationInfo.EUROPE.type) {
            switch (this) {
//                case BACKGROUND: return 0.65f;
                case CAR: return 0.43f;
                case CAMERA: return 0.275f;
                case MICRO: return 0.12f;
                default: return percentY;
            }
        } else if(location == LocationInfo.DUBAI.type) {
            switch (this) {
                case BACKGROUND: return 0.69f;
                case TREE: return 0.62f;
                case GIRL: return 0.15f;
                default: return percentY;
            }
        }
        return percentY;
    }

    public float getPercentWidth() {
        int location = User.get().location;
        if(location == LocationInfo.START_HOUSE.type) {
            switch (this) {
                case HOUSE: return 0.62f;
                case STATUE: return 0.32f;
                default: return percentWidth;
            }
        } else if(location == LocationInfo.EUROPE.type) {

        } else if(location == LocationInfo.DUBAI.type) {
            switch (this) {
                case HOUSE: return 0.72f;
                default: return percentWidth;
            }
        }
        return percentWidth;
    }
}
