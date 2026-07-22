package com.plagame.game.kassa.enums;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.utils.AssetUtil;

/**
 * Created by Дмитрий Малышев on 05.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum SceneObjectInfo {
    BACKGROUND(1, 0.0f, 0.0f, 0f), // задний фон - полки с продуктами
    ;

    public int type;
    private float percentX; // x в процентах от ширины экрана
    private float percentY; // y в процентах от высоты экрана
    private float percentWidth; // width в процентах от ширины экрана
    public Action actionMoveIn;
    public Action actionMoveOut;
    private TextureRegion textureRegion;

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

    public float getStartX() {
        return getX();
    }

    public float getStartY() {
        return getY();
    }

    public float getX() {
        switch (this) {
            case BACKGROUND: return GameApplication.get().screenWidth / 2 - getWidth() / 2;
        }
        return GameApplication.get().screenWidth * getPercentX();
    }

    public float getY() {
        switch (this) {
            case BACKGROUND:  return GameApplication.get().screenHeight / 2 - getHeight() / 2;
        }
        return GameApplication.get().screenHeight * getPercentY();
    }

    public float getWidth() {
        float baseWidth = getTexture().getRegionWidth();
        float baseHeight = getTexture().getRegionHeight();
        switch (this) {
            case BACKGROUND:
                float width = baseWidth * getHeight() / baseHeight;
                System.out.println("------------------BACKGROUND---------------width = " + width);
                return width;
        }
        return GameApplication.get().screenWidth * getPercentWidth();
    }

    public float getHeight() {
        switch (this) {
            case BACKGROUND:
                System.out.println("------------------BACKGROUND---------------height = " + GameApplication.get().screenHeight);
                return GameApplication.get().screenHeight;
        }
        float baseWidth = getTexture().getRegionWidth();
        float baseHeight = getTexture().getRegionHeight();
        return baseHeight * getWidth() / baseWidth;
    }

    public TextureRegion getTexture() {
        if(textureRegion == null) {
            switch (this) {
                case BACKGROUND:
                    textureRegion = new TextureRegion(AssetUtil.getTexture("images/game_bg.jpg"));
                    break;
            }
        }
        return textureRegion;
    }

    public float getPercentX() {
        return percentX;
    }

    public float getPercentY() {
        return percentY;
    }

    public float getPercentWidth() {
        return percentWidth;
    }
}
