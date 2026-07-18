package com.plagame.game.kassa.components;


import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_SMALL;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.enums.ShopInfo;
import com.plagame.game.kassa.platform.service.api.model.PlatformCallback;
import com.plagame.game.kassa.utils.SoundUtil;

/**
 * Created by Дмитрий Малышев on 08.09.2022.
 * Email: dmitry.malyshev@gmail.com
 */
public class FooterPanel extends Group {

    public FooterPanel() {
        TextureRegion textureRegion = ShopInfo.THINGS.getButtonTexture();
        float buttonWidth = GameApplication.get().minScreenSize / 5f;
        float buttonHeight = textureRegion.getRegionHeight() * buttonWidth / textureRegion.getRegionWidth();
        float pad = buttonHeight / 5;
        float panelWidth = GameApplication.get().minScreenSize;
        float panelHeight = User.get().isAuthorized() ? buttonHeight + 4 * pad : buttonHeight + 9 * pad;
//        float panelHeight = buttonHeight + 4 * pad;
        setSize(panelWidth, panelHeight);
        Table table = new Table();
//        table.setDebug(true);
        table.setSize(panelWidth, panelHeight);
        table.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("header_bg")));

        for(ShopInfo shopInfo: ShopInfo.values()) {
            Group buttonGroup = new Group();
            buttonGroup.setSize(buttonWidth, buttonHeight);

            Image buttonImage = new Image(shopInfo.getButtonTexture());
            buttonImage.setSize(buttonWidth, buttonHeight);
            buttonImage.addListener(new ActorGestureListener() {
                @Override
                public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    SoundUtil.playClickSound();
                    GameApplication.get().getGameScreen().uiStage.hideShopDialog();
                    GameApplication.get().getGameScreen().uiStage.showShopDialog(shopInfo);
                    GameApplication.get().getGameScreen().uiStage.checkFullscreenAdv();
                }
            });
            buttonGroup.addActor(buttonImage);

            Group countGroup = new Group() {
                @Override
                public void act(float delta) {
                    int count = shopInfo.getUpgradeCountAvailable();
                    if(count > 0) {
                        setVisible(true);
                    } else {
                        setVisible(false);
                    }
                    super.act(delta);
                }
            };
            float size = buttonGroup.getHeight() * 0.45f;
            countGroup.setSize(size, size);
            Image redCircleImage = new Image(ATLAS_1.findRegion("icon_circle_red"));
            redCircleImage.setSize(size, size);
            countGroup.addActor(redCircleImage);
            Label countLabel = new ModelLabel(String.valueOf(Math.min(shopInfo.getUpgradeCountAvailable(), 99)), new Label.LabelStyle(FONT_VERY_SMALL, Color.WHITE)) {
                @Override
                protected String getValue() {
                    return String.valueOf(Math.min(shopInfo.getUpgradeCountAvailable(), 99));
                }
            };
            countLabel.setAlignment(Align.center);
            countLabel.setPosition((countGroup.getWidth() - countLabel.getWidth()) / 2, (countGroup.getHeight() - countLabel.getHeight()) / 2);
            countGroup.addActor(countLabel);
            countGroup.setPosition(buttonGroup.getWidth() - countGroup.getWidth(), buttonGroup.getHeight() - countGroup.getHeight());
//            countGroup.setOrigin(size / 2, size / 2);
            countGroup.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleTo(1.08f, 1.08f, 0.4f, Interpolation.sine),
                        Actions.scaleTo(1f, 1f, 0.4f, Interpolation.sine)
                    )
                )
            );

            buttonGroup.addActor(countGroup);

            table.add(buttonGroup).size(buttonWidth, buttonHeight).pad(pad).align(Align.center).expandX().fill();
        }

        table.row();

        if(User.get().isAuthorized()) {
            Label loginLabel = new Label(GameApplication.get().platform.cloud().getLogin(), new Label.LabelStyle(FONT_VERY_SMALL, Color.LIGHT_GRAY));
            loginLabel.setAlignment(Align.center);
            table.add(loginLabel).align(Align.center).expandX().fill().colspan(4);
        } else  {
            Label loginDescLabel = new ModelLabel("", new Label.LabelStyle(FONT_VERY_SMALL, Color.LIGHT_GRAY)) {
                @Override
                protected String getValue() {
                    if(GameApplication.get().platform.cloud().isConnected()) {
                        return GameApplication.get().platform.i18n().get("LOGIN_DESC");
                    } else {
                        return "Сервер временно недоступен"; //todo i18n
                    }
                }
            };
            loginDescLabel.setAlignment(Align.center);
            table.add(loginDescLabel).align(Align.center).expandX().fill().colspan(4);

            table.row();
            TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy_disable"));
            TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy"));
            Button.ButtonStyle style = new Button.ButtonStyle();
            style.up = buttonGreen;     // по умолчанию
            style.disabled = buttonGray;  // при нажатии (опционально)
            Button loginButton = new Button(style) {
                @Override
                public void act(float delta) {
                    if(User.get().isAuthorized() && isVisible()) {
                        setVisible(false);
                        GameApplication.get().setGameScreen();
                    }
                    setDisabled(!GameApplication.get().platform.cloud().isConnected());
                }
            };
            loginButton.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    if(!User.get().isAuthorized()) {
                        GameApplication.get().platform.cloud().doLogin(new PlatformCallback<String>() {
                            @Override
                            public void onSuccess(String value) {
                                User.get().saveUser();
                                GameApplication.get().setGameScreen();
                            }

                            @Override
                            public void onError(String error) {
                                System.out.println("doLogin failed, error: " + error);
                            }
                        });
                    }
                    super.tap(event, x, y, count, button);
                }
            });
            Label label = new Label("Войти", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)); //todo i18n
            label.setAlignment(Align.center);
            loginButton.add(label).align(Align.center).pad(pad);

            table.add(loginButton).align(Align.center).colspan(4).pad(10);
        }

        addActor(table);

        Label versionLabel = new Label("v." + GameConfig.VERSION + " " + GameConfig.TARGET_PLATFORM.getShortName(), new Label.LabelStyle(FONT_VERY_SMALL, Color.LIGHT_GRAY));
        versionLabel.setAlignment(Align.center);
        versionLabel.setPosition(pad, 0);
        addActor(versionLabel);

    }
}
