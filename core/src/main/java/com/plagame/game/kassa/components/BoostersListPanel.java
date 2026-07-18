package com.plagame.game.kassa.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.enums.BoosterInfo;

/**
 * Created by Дмитрий Малышев on 29.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class BoostersListPanel extends Table {

    public BoostersListPanel() {
//        setDebug(true);
        float panelHeight = 0;
        for(BoosterInfo boosterInfo : BoosterInfo.values()) {
            if(User.get().hasBooster(boosterInfo)) {
                BoosterTimePanel boosterTimePanel = new BoosterTimePanel(boosterInfo);
                float pad = boosterTimePanel.getHeight() / 8;
                panelHeight = boosterTimePanel.getHeight() + pad * 2;
                add(boosterTimePanel).size(boosterTimePanel.getWidth(), boosterTimePanel.getHeight()).align(Align.left).pad(pad).fill();
                row();
            }
        }
        align(Align.topLeft);
        setSize(GameApplication.get().screenWidth / 2, panelHeight);
    }
}
