package com.plagame.game.kassa.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by Дмитрий Малышев on 29.07.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class ModelLabel extends Label {

    private static final long REFRESH_INTERVAL = 300;
    private String lastValue;
    private long lastRefreshTime = System.currentTimeMillis();

    public ModelLabel(LabelStyle labelStyle) {
        this("", labelStyle);
    }

    public ModelLabel(String value, BitmapFont font, Color color) {
        this(value, new LabelStyle(font, color));
    }

    public ModelLabel(String value, LabelStyle labelStyle) {
        super(value, labelStyle);
    }

    @Override
    public void act(float delta) {
        refreshLabel();
        super.act(delta);
    }

    public void refreshLabel() {
        if(System.currentTimeMillis() - lastRefreshTime > REFRESH_INTERVAL) {
            String value = getValue();
            if(!value.equals(lastValue)) {
                lastValue = value;
                setText(value);
            }
            lastRefreshTime = System.currentTimeMillis();
        }
    }

    protected abstract String getValue();

}
