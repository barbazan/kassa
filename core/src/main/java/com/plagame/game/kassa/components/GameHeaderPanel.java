package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_HEADER;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.components.bar.CircularProgressActor;
import com.plagame.game.kassa.components.bar.EnergyProgressBar;
import com.plagame.game.kassa.platform.service.api.model.TargetPlatform;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.function.Supplier;


/**
 * Created by Дмитрий Малышев on 21.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameHeaderPanel extends Table {

    private static final float HEADER_PLASHKA_WIDTH_MULT = 0.26f;
    private float headerWidth, headerHeight, pad, iconSize;
    private CircularProgressActor locationProgressBar;
    private Image folowersBarImage;
    private Group folowersGroup;

    public GameHeaderPanel() {
        headerWidth = GameApplication.get().screenWidth;
        headerHeight = GameApplication.get().screenHeight * 0.13f;
        iconSize = headerHeight * 0.33f;
        pad = (headerHeight - iconSize * 2) / 3;

        setSize(headerWidth, headerHeight);
        setPosition(0, GameApplication.get().screenHeight - headerHeight);
//        setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("header_bg")));
        padTop(pad / 2);
//        setDebug(true);

        Group mapGroup = new Group();
        mapGroup.setSize(iconSize * 1.8f, iconSize * 1.8f);
        mapGroup.setOrigin(mapGroup.getWidth() / 2, mapGroup.getHeight() / 2);
        locationProgressBar = new CircularProgressActor() {
            @Override
            public void act(float delta) {
                locationProgressBar.setProgress(User.get().getLocationProgress());
                if(User.get().getLocationProgress() == 1) {
                    if(!mapGroup.hasActions()) {
                        mapGroup.addAction(
                            Actions.forever(
                                Actions.sequence(
                                    Actions.scaleTo(1.08f, 1.08f, 0.4f, Interpolation.sine),
                                    Actions.scaleTo(1f, 1f, 0.4f, Interpolation.sine)
                                )
                            )
                        );
                    }
                }

            }
        };
        locationProgressBar.setProgress(User.get().getLocationProgress());
        locationProgressBar.setSize(mapGroup.getWidth(), mapGroup.getHeight());
        mapGroup.addActor(locationProgressBar);

        Image mapImage = new Image(ATLAS_1.findRegion("icon_map"));
        mapImage.setSize(mapGroup.getWidth(), mapGroup.getHeight());
        mapImage.setPosition((mapGroup.getWidth() - mapImage.getWidth()) / 2, (mapGroup.getHeight() - mapImage.getHeight()) / 2);
        mapImage.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) { //todo remove
                User.get().setDollar(0);
                User.get().setGold(0);
                User.get().isAdHide = false;
                User.get().saveUser();
                return super.longPress(actor, x, y);
            }

            @Override
            public void tap(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.showPereezdDialog();
            }
        });
        mapGroup.addActor(mapImage);
        add(mapGroup).size(mapGroup.getWidth(), mapGroup.getHeight()).align(Align.left).pad(pad / 2).padLeft(pad);

        Image imageRed = new Image(ATLAS_1.findRegion("icon_circle_red"));
        imageRed.setSize(10, 10);
        imageRed.setPosition(mapGroup.getWidth() / 2 - imageRed.getWidth() / 2, mapGroup.getHeight() / 2 - imageRed.getHeight() / 2);
        mapGroup.addActor(imageRed);

        Image imageGreen = new Image(ATLAS_1.findRegion("icon_circle_green")) {
            @Override
            public void act(float delta) {
                setVisible(GameApplication.get().networkWebSocketClient.isConnected());
                super.act(delta);
            }
        };
        imageGreen.setSize(imageRed.getWidth(), imageRed.getHeight());
        imageGreen.setPosition(imageRed.getX(), imageRed.getY());
        mapGroup.addActor(imageGreen);


        Table innerTable = new Table();
//        innerTable.setSize(headerWidth - mapGroup.getWidth() - pad * 2, headerHeight);
//        innerTable.setDebug(true);

        Group dollarsGroup = createStatItem("icon_dollars", () -> User.get().getDollars());
        dollarsGroup.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.showShopActionsDialog();
                super.touchDown(event, x, y, pointer, button);
            }
        });
        innerTable.add(dollarsGroup).size(dollarsGroup.getWidth(), dollarsGroup.getHeight()).align(Align.center).pad(pad / 2, pad * 2, pad, pad * 3).fill();

//        Group goldGroup = createStatItem("icon_gold", () -> User.get().gold);
        Group goldGroup = createGoldPanel();
        goldGroup.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.showShopGoldDialog();
                super.touchDown(event, x, y, pointer, button);
            }
        });
        innerTable.add(goldGroup).size(goldGroup.getWidth(), goldGroup.getHeight()).align(Align.center).pad(pad / 2, 0, pad, pad).fill();

        innerTable.row();

        Group dollarGroup = createStringStatItem("icon_dollar", () -> NumberFormat.format(User.get().getTotalPasIncome()) + " / с");
        innerTable.add(dollarGroup).size(dollarGroup.getWidth(), dollarGroup.getHeight()).align(Align.center).pad(0, pad * 2, 0, pad * 3).fill();

        Group folowersGroup = createFolowersPanel("icon_folowers");
        innerTable.add(folowersGroup).size(folowersGroup.getWidth(), folowersGroup.getHeight()).align(Align.center).pad(0, 0, 0, pad).fill();

        add(innerTable).align(Align.top).expand();

        if(GameConfig.TARGET_PLATFORM != TargetPlatform.HTML_YANDEX) {
            Image settingsImage = new Image(ATLAS_1.findRegion("icon_settings"));
            settingsImage.setSize(iconSize * 1.1f, iconSize * 1.1f);
            settingsImage.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    SoundUtil.playClickSound();
                    GameApplication.get().setSettingsScreen();
                }
            });
            add(settingsImage).size(settingsImage.getWidth(), settingsImage.getHeight()).align(Align.top).padRight(pad).padTop(pad / 2).expandX().fill();
        } else {
            add().expandX();
        }
        row();

        EnergyProgressBar energyProgressBar = new EnergyProgressBar(headerWidth * 0.25f);
        add(energyProgressBar).height(energyProgressBar.getHeight()).padTop(pad / 2).colspan(3);
    }

    private Group createStatItem(String iconName, Supplier<Long> valueSupplier) {
        Group group = new Group();
        group.setSize(headerWidth * HEADER_PLASHKA_WIDTH_MULT, iconSize * 0.8f);
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
        group.setSize(headerWidth * HEADER_PLASHKA_WIDTH_MULT, iconSize * 0.8f);
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

    private Group createStringStatItem(String iconName, Supplier<String> valueSupplier) {
        Group group = new Group();
        group.setSize(headerWidth * HEADER_PLASHKA_WIDTH_MULT, iconSize * 0.8f);
        Image bgImage = new Image(ATLAS_1.findRegion("title_bg"));
        bgImage.setSize(group.getWidth(), group.getHeight());
        group.addActor(bgImage);
        Label label = new ModelLabel(new Label.LabelStyle(FONT_HEADER, Color.BLACK)) {
            @Override
            protected String getValue() {
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

    private Group createFolowersPanel(String iconName) {
        folowersGroup = new Group() {
            @Override
            public void act(float delta) {
                folowersBarImage.setWidth(getActualFolowersProgressWidth() * User.get().getFolowersPercent());
                if(User.get().getFolowersPercent() == 1) {
                    if(!hasActions()) {
                        addAction(
                            Actions.forever(
                                Actions.sequence(
                                    Actions.scaleTo(1.08f, 1.08f, 0.4f, Interpolation.sine),
                                    Actions.scaleTo(1f, 1f, 0.4f, Interpolation.sine)
                                )
                            )
                        );
                    }
                }
                super.act(delta);
            }
        };
//        folowersGroup.setDebug(true);
        folowersGroup.setSize(headerWidth * HEADER_PLASHKA_WIDTH_MULT, iconSize * 0.8f);

        Image bgImage = new Image(ATLAS_1.findRegion("bar_folowers_bg"));
        float startX = folowersGroup.getWidth() * 0.019f;
        bgImage.setSize(getActualFolowersProgressWidth(), folowersGroup.getHeight() * 0.95f);
        bgImage.setPosition(startX, 1);

        folowersGroup.addActor(bgImage);

        folowersBarImage = new Image(ATLAS_1.findRegion("bar_folowers_pink"));
        folowersBarImage.setSize(getActualFolowersProgressWidth() * User.get().getFolowersPercent(), folowersGroup.getHeight());
        folowersBarImage.setPosition(startX, 0);
        folowersGroup.addActor(folowersBarImage);

        Image frameImage = new Image(ATLAS_1.findRegion("bar_folowers_frame"));
        frameImage.setSize(folowersGroup.getWidth(), folowersGroup.getHeight());
        folowersGroup.addActor(frameImage);

        Table labelTable = new Table();
//        labelTable.setDebug(true);
        labelTable.setSize(folowersGroup.getWidth(), folowersGroup.getHeight());
        labelTable.setPosition(0, 0);
        Label labelFolowers = new NumberFormatLabel(new Label.LabelStyle(FONT_HEADER, Color.BLACK)) {
            @Override
            public long getLongValue() {
                return User.get().getFolowers();
            }
        };
        labelFolowers.setAlignment(Align.center);
        labelTable.add(labelFolowers).align(Align.center).expandX();

//        Label labelMaxFolowers = new ModelLabel(new Label.LabelStyle(FONT_VERY_SMALL, Color.GRAY)) {
//            @Override
//            public void act(float delta) {
//                setVisible(User.get().getFolowers() < User.get().maxFolowers);
//                super.act(delta);
//            }
//
//            @Override
//            protected String getValue() {
//                if(User.get().getFolowers() < User.get().maxFolowers) {
//                    return " / " + NumberFormat.format(User.get().maxFolowers);
//                } else {
//                    return "";
//                }
//            }
//        };
//        labelMaxFolowers.setAlignment(Align.topLeft);
//        labelTable.add(labelMaxFolowers).padRight(pad).align(Align.topLeft).expandX();

        folowersGroup.addActor(labelTable);

        Image iconImage = new Image(ATLAS_1.findRegion(iconName));
        iconImage.setSize(iconSize * 1.0f, iconSize * 1.0f);
        iconImage.setPosition(- iconSize * 0.8f, (folowersGroup.getHeight() - iconImage.getHeight()) / 2);
        folowersGroup.addActor(iconImage);
        folowersGroup.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                if(User.get().getFolowersPercent() >= 1) {
                    System.out.println("-------------------User.get().getFolowersPercent() = " + User.get().getFolowersPercent());
                    folowersGroup.clearActions();
                    folowersGroup.setScale(1);//todo проверить
                    long MAX_FOLOWERS_TIME = 10 * 60; //  // увеличиваем maxFolowers так, чтобы игрок за 10 минут набирал
                    float addFolowers = User.get().folowersIncome * MAX_FOLOWERS_TIME;
                    System.out.println("---------------------addFolowers = " + addFolowers);
                    float donateMult = 1.0f + GameConfig.random.nextFloat() / 2;
//                    long donate = (long)(User.get().folowers / 10 * donateMult);
                    long donate = (long)(User.get().folowers * 2 * donateMult);
                    User.get().lastMaxFolowers = User.get().folowers;
                    User.get().maxFolowers = (long)(User.get().folowers + addFolowers); // увеличиваем maxFolowers так, чтобы игрок за MAX_FOLOWERS_TIME секунд набирал (10 минут)
                    System.out.println("-------------------User.get().maxFolowers = " + User.get().maxFolowers);
                    User.get().saveUser();
//                    GameApplication.get().setGameScreen();
                    GameApplication.get().getGameScreen().uiStage.showDonateDialog(donate);
                } else {
                    GameApplication.get().getGameScreen().uiStage.showShopActionsDialog();
                }
            }
        });
        return folowersGroup;
    }

    private float getActualFolowersProgressWidth() { // визуальный прогресс, чтобы толстая рамка не закрывала начальный прогресси чтобы конечный прогресс не выходил за пределы закругленой рамка
        float startX = folowersGroup.getWidth() * 0.019f;
        float rightPad = folowersGroup.getWidth() * 0.0429f;
        return folowersGroup.getWidth() - startX - rightPad;
    }

}
