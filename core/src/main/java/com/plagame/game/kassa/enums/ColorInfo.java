package com.plagame.game.kassa.enums;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Дмитрий Малышев on 12.09.2022.
 * Email: dmitry.malyshev@gmail.com
 */
public enum ColorInfo {
    ACTION_GREEN(new Color(0x00862cff)),
    ACTION_ORANGE(new Color(0xff530eff)),

    //    EXP(new Color(0xcb13bcff)), // фиолетовый
    EXP(new Color(0x0fbbecff)), // голубой
    HEAL(Color.GREEN),
    DAMAGE(Color.YELLOW),
    DAMAGE_TO_PLAYER(Color.RED),
    POWER_UP_DIALOG_TITLE(new Color(0x2f2f68ff)),
    SLOT_TITLE(new Color(0x009acfff)),

    NEW(new Color(0x7fcdffff)),
    DESC(new Color(0xffcd5eff)),
    LEVELUP_TITLE(new Color(0xb4ff94ff)),
    POOR(new Color(0x9d9d9dff)),
    COMMON(new Color(0xffffffff)),
    UNCOMMON(new Color(0x1eff00ff)),
    RARE(new Color(0x0070ddff)),
    EPIC(new Color(0x9345ffff)),
    LEGENDARY(new Color(0xff8000ff)),
    ARTIFACT(new Color(0x0fbbecff)),
    BUTTON_BACK(new Color(0xffd497ff)),
    DAILY_REWARD(new Color(0xc7b1fcff)),
    DAILY_REWARD_SELECTED(new Color(0xffbe00ff)),
    RUBY(new Color(0xbd0ebeff)),
    ;

    public Color color;

    ColorInfo(Color color) {
        this.color = color;
    }

    public static Color getRandomColor() {
        return new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.8f);
    }
}
