package com.plagame.game.kassa.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.plagame.game.kassa.utils.NumberFormat;

/**
 * Created by Дмитрий Малышев on 30.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class NumberFormatLabel extends ModelLabel {

    public NumberFormatLabel(LabelStyle labelStyle) {
        super(labelStyle);
    }

    public NumberFormatLabel(String value, BitmapFont font, Color color) {
        super(value, font, color);
    }

    public NumberFormatLabel(String value, LabelStyle labelStyle) {
        super(value, labelStyle);
    }

    @Override
    protected String getValue() {
        return NumberFormat.format(getLongValue());
    }

    public abstract long getLongValue();
}
