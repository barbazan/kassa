package com.plagame.game.kassa.components;


import static com.plagame.game.kassa.GameApplication.FONT_HEADER;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.utils.ActionsUtil;
import com.plagame.game.kassa.utils.SoundUtil;


/**
 * Created by Дмитрий Малышев on 21.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class HeaderPanel extends Table {

    public Image achieveImage;
    private float headerWidth, headerHeight, pad, iconSize;

    public HeaderPanel() {
        init();
    }

    private void init() {
        clear();
        headerWidth = GameApplication.get().screenWidth;
        iconSize = GameApplication.get().minScreenSize * 0.10f;
        pad = iconSize / 4;
        headerHeight = iconSize + 2 * pad;

        setSize(headerWidth, headerHeight);
        setPosition(0, GameApplication.get().screenHeight - headerHeight);
//        setDebug(true);

        Table innerTable = new Table();
//        innerTable.setDebug(true);

        innerTable.add().expandX().fill();

        Group dayGroup = createDayStat();
        innerTable.add(dayGroup).size(dayGroup.getWidth(), dayGroup.getHeight()).align(Align.left).pad(pad).padRight(0).fill();

        Group dollarsGroup = createStatItem("icon_dollars");
        dollarsGroup.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
//                GameApplication.get().getGameScreen().uiStage.showShopActionsDialog(); //todo
                super.touchDown(event, x, y, pointer, button);
            }
        });
        innerTable.add(dollarsGroup).size(dollarsGroup.getWidth(), dollarsGroup.getHeight()).align(Align.left).pad(pad).fill();

        achieveImage = new Image(ATLAS_1.findRegion("icon_v"));
        achieveImage.setSize(iconSize, iconSize);
        achieveImage.setOrigin(achieveImage.getWidth() / 2, achieveImage.getHeight() / 2);
        achieveImage.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().setAchievementsScreen();
            }
        });
        innerTable.add(achieveImage).size(achieveImage.getWidth(), achieveImage.getHeight()).align(Align.center).padRight(pad).fill();

        Image settingsImage = new Image(ATLAS_1.findRegion("icon_settings"));
        settingsImage.setSize(iconSize, iconSize);
        settingsImage.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().setSettingsScreen();
            }
        });
        innerTable.add(settingsImage).size(settingsImage.getWidth(), settingsImage.getHeight()).align(Align.center).fill();

        add(innerTable).align(Align.topRight).pad(pad).padTop(0).expandX().fill();

        row();
    }

    private Group createDayStat() {
        Group group = new Group();
        float width = GameApplication.get().minScreenSize * 0.28f;
        group.setSize(width, iconSize * 0.8f);
        Image bgImage = new Image(ATLAS_1.findRegion("title_bg"));
        bgImage.setSize(group.getWidth(), group.getHeight());
        group.addActor(bgImage);
        Label label = new ModelLabel(new Label.LabelStyle(FONT_HEADER, Color.BLACK)) {
            @Override
            protected String getValue() {
                return "ДЕНЬ " + User.get().day;
            }

        };
        label.setAlignment(Align.center);
        label.setPosition(group.getWidth() / 2 - label.getWidth() / 2, group.getHeight() / 2 - label.getHeight() / 2);
        group.addActor(label);
        return group;
    }

    private Group createStatItem(String iconName) {
        Group group = new Group();
        float width = GameApplication.get().minScreenSize * 0.28f;
        group.setSize(width, iconSize * 0.8f);
        Image bgImage = new Image(ATLAS_1.findRegion("title_bg"));
        bgImage.setSize(group.getWidth(), group.getHeight());
        group.addActor(bgImage);
        Label label = new ModelLabel(new Label.LabelStyle(FONT_HEADER, Color.BLACK)) {
            @Override
            protected String getValue() {
                return "$" + User.get().getDollarsAsString();
            }

        };
        label.setAlignment(Align.center);
        label.setPosition(group.getWidth() / 2 - label.getWidth() / 2, group.getHeight() / 2 - label.getHeight() / 2);
        group.addActor(label);
//        Image iconImage = new Image(ATLAS_1.findRegion(iconName));
//        iconImage.setSize(iconSize * 1.0f, iconSize * 1.0f);
//        iconImage.setPosition(- iconSize * 0.8f, (group.getHeight() - iconImage.getHeight()) / 2);
//        group.addActor(iconImage);
        return group;
    }


}
