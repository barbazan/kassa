package com.plagame.game.kassa.components;


import static com.plagame.game.kassa.GameApplication.FONT_HEADER;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.function.Supplier;


/**
 * Created by Дмитрий Малышев on 21.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class HeaderPanel extends Table {

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

        Group dollarsGroup = createStatItem("icon_dollars", () -> User.get().getDollars());
        dollarsGroup.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
//                GameApplication.get().getGameScreen().uiStage.showShopActionsDialog(); //todo
                super.touchDown(event, x, y, pointer, button);
            }
        });
        innerTable.add(dollarsGroup).size(dollarsGroup.getWidth(), dollarsGroup.getHeight()).align(Align.right).padLeft(pad * 3).fill();

        Group goldGroup = createGoldPanel();
        goldGroup.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.showShopGoldDialog();
                super.touchDown(event, x, y, pointer, button);
            }
        });
        innerTable.add(goldGroup).size(dollarsGroup.getWidth(), dollarsGroup.getHeight()).align(Align.right).padLeft(iconSize);

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

        add(innerTable).align(Align.topRight).pad(pad).expandX().fill();

        row();
    }

    private Group createStatItem(String iconName, Supplier<Long> valueSupplier) {
        Group group = new Group();
        float width = GameApplication.get().minScreenSize * 0.28f;
        group.setSize(width, iconSize * 0.8f);
        Image bgImage = new Image(ATLAS_1.findRegion("title_bg"));
        bgImage.setSize(group.getWidth(), group.getHeight());
        group.addActor(bgImage);
        Label label = new NumberFormatLabel(new Label.LabelStyle(FONT_HEADER, Color.BLACK)) {
            @Override
            public long getLongValue() {
                return valueSupplier.get();
            }
        };
        label.setAlignment(Align.center);
        label.setPosition(group.getWidth() / 2 - label.getWidth() / 2, group.getHeight() / 2 - label.getHeight() / 2);
        group.addActor(label);
        Image iconImage = new Image(ATLAS_1.findRegion(iconName));
        iconImage.setSize(iconSize * 1.0f, iconSize * 1.0f);
        iconImage.setPosition(- iconSize * 0.8f, (group.getHeight() - iconImage.getHeight()) / 2);
        group.addActor(iconImage);
        return group;
    }

    private Group createGoldPanel() {
        Group group = new Group();
        float width = GameApplication.get().minScreenSize * 0.25f;
        group.setSize(width, iconSize * 0.8f);
        Image bgImage = new Image(ATLAS_1.findRegion("title_bg"));
        bgImage.setSize(group.getWidth(), group.getHeight());
        group.addActor(bgImage);
        Label label = new ModelLabel(new Label.LabelStyle(FONT_HEADER, Color.BLACK)) {
            @Override
            protected String getValue() {
                return String.valueOf(User.get().gold);
            }
        };
        label.setAlignment(Align.center);
        label.setPosition(group.getWidth() * 0.85f / 2 - label.getWidth() / 2, group.getHeight() / 2 - label.getHeight() / 2);
        group.addActor(label);
        Image iconImage = new Image(ATLAS_1.findRegion("icon_gold"));
        iconImage.setSize(iconSize * 1.0f, iconSize * 1.0f);
        iconImage.setPosition(- iconSize * 0.8f, (group.getHeight() - iconImage.getHeight()) / 2);
        group.addActor(iconImage);

        Image plusImage = new Image(ATLAS_1.findRegion("icon_plus_green"));
        plusImage.setSize(iconImage.getWidth() * 0.7f, iconImage.getHeight() * 0.7f);
        plusImage.setPosition(group.getWidth() - plusImage.getWidth() * 1.1f, (group.getHeight() - plusImage.getHeight()) / 2);
        group.addActor(plusImage);

        return group;
    }

}
