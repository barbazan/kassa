package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_BIG_TOYZ;
import static com.plagame.game.kassa.GameApplication.FONT_DEFAULT;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private boolean purchaseRequested;
    private Dialog purchaseSuccessDialog;
    private Texture backgroundTexture;

    public BuyFullVersionScreen() {
        init();
    }

    private void init() {
        stage.clear();
        purchaseSuccessDialog = null;
        if (backgroundTexture != null) backgroundTexture.dispose();
        addBackground();
        if (purchaseRequested && User.get().isFullVersionBuyed()) {
            showPurchaseSuccess();
        } else {
            initButtons();
        }
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
        backgroundTexture = textureRegion.getTexture();
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
                    purchaseRequested = true;
                    isPurchasesChecked = false;
                    GameApplication.get().platform.billing().buyFullVersion();
                } else if(GameConfig.TARGET_PLATFORM == TargetPlatform.LOCAL) {
                    purchaseRequested = true;
                    User.get().setFullVersionBuyed(true);
                    User.get().saveUser();
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

    private void showPurchaseSuccess() {
        float dialogWidth = Math.min(GameApplication.get().screenWidth * 0.9f,
            Math.min(680, GameApplication.get().screenHeight * 1.4f));
        float pad = dialogWidth * 0.065f;
        float contentWidth = dialogWidth - pad * 2;
        TextureRegionDrawable panel = new TextureRegionDrawable(ATLAS_1.findRegion("dialog_bg"));
        panel.setMinWidth(0);
        panel.setMinHeight(0);
        Window.WindowStyle dialogStyle = new Window.WindowStyle(FONT_DEFAULT, Color.WHITE, panel);
        dialogStyle.stageBackground = skin.getDrawable("dialogDim");
        purchaseSuccessDialog = new Dialog("", dialogStyle);
        purchaseSuccessDialog.setMovable(false);
        purchaseSuccessDialog.getTitleTable().setVisible(false);
        purchaseSuccessDialog.pad(pad);

        Label title = new Label("Покупка прошла\nуспешно!", new Label.LabelStyle(FONT_BIG_TOYZ, Color.WHITE));
        title.setAlignment(Align.center);
        title.setFontScale(Math.min(1, contentWidth / 570));
        Label description = new Label("Полная версия «Кассы» открыта.\nСпасибо за поддержку!",
            new Label.LabelStyle(FONT_DEFAULT, Color.WHITE));
        description.setAlignment(Align.center);
        description.setWrap(true);
        description.setFontScale(Math.min(1, contentWidth / 510));

        Button playButton = new Button(new TextureRegionDrawable(ATLAS_1.findRegion("button_green")),
            new TextureRegionDrawable(ATLAS_1.findRegion("button_green_down")));
        Label playLabel = new Label("Играть", new Label.LabelStyle(FONT_BIG_TOYZ, Color.DARK_GRAY));
        playLabel.setFontScale(Math.min(1, contentWidth / 570));
        playButton.add(playLabel);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundUtil.playClickSound();
                GameApplication.get().setGameScreen();
            }
        });

        purchaseSuccessDialog.getContentTable().pad(0);
        purchaseSuccessDialog.getContentTable().add(title).width(contentWidth).padBottom(pad * 0.7f).row();
        purchaseSuccessDialog.getContentTable().add(description).width(contentWidth).padBottom(pad).row();
        purchaseSuccessDialog.getContentTable().add(playButton).width(contentWidth * 0.62f).height(dialogWidth * 0.17f);
        purchaseSuccessDialog.show(stage, null);
        purchaseSuccessDialog.setPosition(
            (GameApplication.get().screenWidth - purchaseSuccessDialog.getWidth()) / 2,
            (GameApplication.get().screenHeight - purchaseSuccessDialog.getHeight()) / 2);
    }

    @Override
    public void render(float delta) {
        if (User.get().isFullVersionBuyed()) {
            if (!purchaseRequested) {
                GameApplication.get().setGameScreen();
                return;
            }
            // Only the confirmed entitlement opens this dialog, never the payment SDK response.
            if (purchaseSuccessDialog == null) init();
        } else {
            checkPurchases();
        }
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
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
