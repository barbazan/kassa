package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_BIG_TOYZ;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.integration.platform.service.api.model.TargetPlatform;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.utils.SoundUtil;

/**
 * Created by Дмитрий Малышев on 01.08.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class BuyFullVersionScreen extends BaseScreen {

    public BuyFullVersionScreen() {
        init();
    }

    private void init() {
        addBackground();
        initButtons();
    }

    private void addBackground() {
        TextureRegion textureRegion;
        if(GameApplication.get().isPortrait()) {
            int num = 1 + GameConfig.random.nextInt(1);
            textureRegion = new TextureRegion(new Texture("images/full_version_bg_v_" + num + ".jpg"));
        } else {
            int num = 1 + GameConfig.random.nextInt(1);
            textureRegion = new TextureRegion(new Texture("images/full_version_bg_h_" + num + ".jpg"));
        }
        Image bgImage = getImage(textureRegion);
        stage.addActor(bgImage);
    }

    private static Image getImage(TextureRegion textureRegion) {
        Image bgImage = new Image(textureRegion);
        float scaleX = GameApplication.get().screenWidth / textureRegion.getRegionWidth();
        float scaleY = GameApplication.get().screenHeight / textureRegion.getRegionHeight();
        float scale = Math.max(scaleX, scaleY);
        bgImage.setSize(textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
        bgImage.setPosition(GameApplication.get().screenWidth / 2 - bgImage.getWidth() / 2, GameApplication.get().screenHeight / 2 - bgImage.getHeight() / 2);
        return bgImage;
    }

    private void initButtons() {
        float pad = GameApplication.get().minScreenSize / 25;
        TextureRegionDrawable buttonCloseTexture = new TextureRegionDrawable(ATLAS_1.findRegion("button_close"));
        Button buttonClose = new Button(new TextureRegionDrawable(ATLAS_1.findRegion("button_close")));
        buttonClose.setSize(pad * 3, pad * 3);
        buttonClose.setPosition(pad, GameApplication.get().screenHeight - buttonClose.getHeight() - pad);
        buttonClose.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().setGameScreen();
                super.tap(event, x, y, count, button);
            }
        });
        stage.addActor(buttonClose);

        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_gray"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        TextureRegionDrawable buttonGreenDown = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.down = buttonGreenDown;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button button = new Button(style);
        button.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                if(GameApplication.get().platform.getPlatform() == TargetPlatform.HTML_VK || User.get().isAuthorized()) {
                    isPurchasesChecked = false;
                    GameApplication.get().platform.billing().buyFullVersion();
                } else if(GameConfig.TARGET_PLATFORM == TargetPlatform.LOCAL) {
                    User.get().setFullVersionBuyed(true);
                    User.get().saveUser();
                    GameApplication.get().setBuyFullVersionScreen();
                }
                super.tap(event, x, y, count, button);
            }
        });

        if(User.get().isFullVersionBuyed()) {
            Label label = new Label("Вы уже купили " +
                "\nполную версию", new Label.LabelStyle(FONT_BIG_TOYZ, Color.DARK_GRAY));
            label.setAlignment(Align.center);
            label.setPosition(GameApplication.get().screenWidth / 2 - label.getWidth() / 2, pad);
            stage.addActor(label);
        } else {
            String priceText = GameConfig.TARGET_PLATFORM == TargetPlatform.HTML_VK
                ? " \n всего за 50 VK" : " \n всего за 399 руб";
            Label label = new Label("Купить полную версию" + priceText,
                new Label.LabelStyle(FONT_BIG_TOYZ, Color.DARK_GRAY));
            label.setAlignment(Align.center);
            button.add(label).align(Align.center).pad(pad * 1.1f).fill();
            button.setSize(label.getWidth() * 1.11f, label.getHeight() * 1.9f);
            button.setPosition(GameApplication.get().screenWidth / 2 - button.getWidth() / 2, pad * 2);
            stage.addActor(button);
        }
    }

    @Override
    public void render(float delta) {
        checkPurchases();
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
