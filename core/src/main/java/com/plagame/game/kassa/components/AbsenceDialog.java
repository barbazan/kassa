package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_BIG;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.platform.service.api.model.PlatformCallback;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;
import com.plagame.game.kassa.utils.TimeFormat;

/**
 * Created by Дмитрий Малышев on 09.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class AbsenceDialog extends Table {

    private float dialogWidth, dialogHeight, imageSize, pad;
    private final Table headerTable;
    private final Table bodyTable;

    public AbsenceDialog() {
        this.dialogWidth = GameApplication.get().screenWidth * 0.9f;
        this.dialogHeight = GameApplication.get().screenHeight * 0.5f;
        this.imageSize = dialogWidth / 10;
        this.pad = imageSize / 5;
        setSize(dialogWidth, dialogHeight);
        setPosition((GameApplication.get().screenWidth - dialogWidth) / 2, (GameApplication.get().screenHeight - dialogHeight) / 2);
//        setDebug(true);

        headerTable = new Table();
//        headerTable.setDebug(true);
        headerTable.setWidth(dialogWidth);
        headerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_header_green")));
        Label headerLabel = new Label(getAbsenceText(), new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        headerLabel.setAlignment(Align.center);
        headerTable.add(headerLabel).align(Align.center).pad(pad).expandX().fill();

        add(headerTable).align(Align.top).expandX().fill();
        row();

        bodyTable = new Table();
//        bodyTable.setDebug(true);
        bodyTable.setSize(dialogWidth, dialogHeight - headerTable.getHeight());
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_body")));
        Table innerTable = new Table();
        innerTable.setSize(bodyTable.getWidth(), bodyTable.getHeight());

        Group group = new Group();
        group.setSize(bodyTable.getWidth(), bodyTable.getHeight());
        Image bgImage = new Image(ATLAS_1.findRegion("dialog_bg"));
        bgImage.setSize(group.getWidth(), group.getHeight());
        group.addActor(bgImage);
        Image safeImage = new Image(ATLAS_1.findRegion("icon_safe"));
        safeImage.setSize(group.getWidth() * 0.6f, group.getWidth() * 0.6f);
        safeImage.setPosition((group.getWidth() - safeImage.getWidth()) / 2, (group.getHeight() - safeImage.getHeight()) / 2);
        group.addActor(safeImage);
        Label dollarsLabel =  new Label("+" + NumberFormat.format(User.get().absenceDollars), new Label.LabelStyle(FONT_VERY_BIG, Color.WHITE));
        dollarsLabel.setAlignment(Align.center);
        dollarsLabel.setPosition((group.getWidth() - dollarsLabel.getWidth()) / 2, dollarsLabel.getHeight() / 2);
        group.addActor(dollarsLabel);
        innerTable.add(group).pad(pad / 2).expandX().fill();

        innerTable.row();

        Table t = new Table();
        Button redButton = createRedButton("СОБРАТЬ");
        t.add(redButton).align(Align.right).pad(pad).fill();

        Button greenButton = createGreenButton("СОБРАТЬ"); //  X2
        t.add(greenButton).align(Align.left).pad(pad).fill();
        innerTable.add(t).expandX().fill();

        bodyTable.add(innerTable).pad(pad / 2).expandX().fill();
        add(bodyTable).expandX().fill();


    }

    private String getAbsenceText() {
        return GameApplication.get().platform.i18n().get("YOU_WERE_ABANDONED") + " "
            + TimeFormat.formatAbsenceDuration(User.get().absenceMinutes)
            + ",\n " + GameApplication.get().platform.i18n().get("YOU_EARNED");
//        return "ТЕБЯ НЕ БЫЛО " + TimeFormat.formatAbsenceDuration(User.get().absenceMinutes)
//            + ",\n ТЫ ЗАРАБОТАЛ";
    }

    private Button createRedButton(String text) {
        TextureRegionDrawable buttonImage = new TextureRegionDrawable(ATLAS_1.findRegion("button_white"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonImage;     // по умолчанию
        style.disabled = buttonImage;  // при нажатии (опционально)
        Button redButton = new Button(style);
        Label label = new Label(text, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        label.setAlignment(Align.center);
        redButton.add(label).align(Align.center).pad(pad * 2.3f).fill();
        redButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                gotReward(false);
            }
        });
        return redButton;
    }

    private Button createGreenButton(String text) {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_video_reward_disable"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_video_reward_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button redButton = new Button(style) {
            @Override
            public void act(float delta) {
                if(GameApplication.get().platform.isAdsAvailable()) {
                    setDisabled(!GameApplication.get().platform.ads().isVideoAdReady());
                } else {
                    setDisabled(true);
                }
                super.act(delta);
            }
        };
        Label label = new Label(text, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        label.setAlignment(Align.center);
        redButton.add(label).align(Align.left).pad(pad * 2.3f).padRight(pad * 8).fill();
        redButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(GameApplication.get().platform.isAdsAvailable() && GameApplication.get().platform.ads().isVideoAdReady()) {
                    SoundUtil.playClickSound();
                    onClickWathAd();
                }
            }
        });
        redButton.setWidth(label.getWidth() * 2);
        return redButton;
    }

    private void gotReward(boolean isDouble) {
        long absenceDollars = isDouble ? User.get().absenceDollars * 2 : User.get().absenceDollars;
        User.get().changeDollars(absenceDollars);
        User.get().absenceMinutes = 0;
        User.get().absenceDollars = 0;
        User.get().energy = User.MAX_ENERGY;
        User.get().saveUser();
        GameApplication.get().getGameScreen().uiStage.hideAbsenceDialog();
    }

    private void onClickWathAd() {
        GameApplication.get().platform.ads().showRewardedVideo(new PlatformCallback<String>() {
            @Override
            public void onSuccess(String value) {
                // ✅ Досмотрел или локальный запуск — выдаём двойную награду
                gotReward(true);
            }

            @Override
            public void onError(String error) {
                // ❌ Закрыл до конца или реклама не загрузилась — выдаём одинарную награду
                System.out.println("Ad skipped or failed — doing fallback action, error: " + error);
                gotReward(false);
            }
        });
//        if(GameApplication.get().isWebGLAndYandex() && !GameApplication.get().isVideoAdCooldown()) {
//            YandexSDK.showRewardedVideo(
//                () -> {
//                    // ✅ Досмотрел или локальный запуск — выдаём награду
//                    gotReward(true);
//                    GameApplication.get().lastRewardedVideoTime = System.currentTimeMillis();
//                },
//                () -> {
//                    // ❌ Закрыл до конца или реклама не загрузилась
//                    System.out.println("Ad skipped or failed — doing fallback action");
//                    gotReward(false);
//                }
//            );
//        } else {
//            gotReward(false);
//        }
    }

}
