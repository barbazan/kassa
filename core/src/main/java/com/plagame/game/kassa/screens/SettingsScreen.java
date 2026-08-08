package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_SMALL;
import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.kassa.enums.ColorInfo.LOADING_SCREEN_BG_COLOR;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.SoundUtil;


/**
 * Created by Дмитрий Малышев on 08.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class SettingsScreen extends BaseScreen {
    private static final float MIN_NAME_LENGTH = 2;
    private static final float MAX_NAME_LENGTH = 32;

    private TextField loginEditField;
    private float pad;

    public SettingsScreen() {
        super();
        init();
    }

    private void init() {
        stage.clear();
        float tableWidth = GameApplication.get().minScreenSize;
        float tableHeight = GameApplication.get().screenHeight;
        pad = tableWidth / 40;
        Table table = new Table();
//        table.setDebug(true);
        table.setSize(tableWidth, tableHeight);
//        table.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("bar_bg")));
        table.align(Align.top);

        Label titleLabel = new Label("Настройки", new Label.LabelStyle(FONT_DIALOG_HEADER, Color.YELLOW)); // todo i18n
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).expandX().pad(pad).padTop(pad * 2);
        table.row();

        Label versionLabel = new Label("версия: " + GameConfig.VERSION + " " + GameConfig.TARGET_PLATFORM.getShortName(), new Label.LabelStyle(FONT_VERY_SMALL, Color.LIGHT_GRAY));
        versionLabel.setAlignment(Align.center);
        table.add(versionLabel);
        table.row();

//        Label idLabel = new Label("ID игрока: " + User.get().id, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)); // todo i18n
//        idLabel.setAlignment(Align.center);
//        table.add(idLabel).expandX().pad(pad);
//        table.row();
//
//        Table t2 = new Table();
//        t2.add().expandX();
//
//        Label nickLabel1 = new Label("Ник: ", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)); // todo i18n
//        nickLabel1.setAlignment(Align.right);
//        t2.add(nickLabel1).align(Align.right).fill();
//
//        Label nickLabel2 = new Label(User.get().login, new Label.LabelStyle(FONT_RATING, Color.YELLOW)); // todo i18n
//        nickLabel2.setAlignment(Align.left);
//        t2.add(nickLabel2).align(Align.left).fill();
//
//        t2.add().expandX();
//
//        table.add(t2).align(Align.center).fill();
//        table.row();
//
//        Table t1 = new Table();
//        t1.setWidth(tableWidth);
//
//        Label newNickLabel = new Label("Новый ник:", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)); // todo i18n
//        newNickLabel.setAlignment(Align.center);
//        t1.add(newNickLabel).align(Align.center).fill().padLeft(pad * 2);
//
//        TextureRegionDrawable bgTexture = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy_disable"));
//        bgTexture.setMinWidth(tableWidth / 2);
//        bgTexture.setMinHeight(bgTexture.getMinWidth() / 6);
//        loginEditField = new TextField(User.get().login, new TextField.TextFieldStyle(FONT_RATING, Color.WHITE,
//            new TextureRegionDrawable(ATLAS_1.findRegion("cursor")),
//            new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy")),
//            bgTexture));
//        loginEditField.setMaxLength(30);
//        loginEditField.setAlignment(Align.center);
//        t1.add(loginEditField).align(Align.left).pad(pad).padTop(0).expandX().fill();
//        stage.setKeyboardFocus(loginEditField);
//
//        table.add(t1).expandX().fill().pad(pad);
//        table.row();
//
//        Button nickButton = createChangeNickButton("Сменить ник"); // todo i18n
//        table.add(nickButton).size(nickButton.getWidth(), nickButton.getHeight()).expandX().pad(pad).padTop(0);
//        table.row();

        float imageSize = tableWidth / 5;
        Table t = new Table();

        Group musicBtn = createMusicBtn(imageSize);
        t.add(musicBtn).size(musicBtn.getWidth()).align(Align.right).pad(pad);
        Group soundBtn = createSoundBtn(imageSize);
        t.add(soundBtn).size(soundBtn.getWidth()).align(Align.left).pad(pad);

        table.add(t).align(Align.center).expandX();

        table.row();

        table.add().expand().fill();
        table.row();

        Button closeButton = createCloseButton("Закрыть"); // todo i18n
        table.add(closeButton).size(closeButton.getWidth(), closeButton.getHeight()).expandX().pad(pad);
        table.row();

        table.setSize(GameApplication.get().minScreenSize, GameApplication.get().screenHeight);
        table.setPosition(GameApplication.get().screenWidth / 2 - tableWidth / 2, GameApplication.get().screenHeight / 2 - tableHeight / 2);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        AssetUtil.clearScreen(LOADING_SCREEN_BG_COLOR.color);
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

    private Button createCloseButton(String text) {
        TextureRegionDrawable buttonImage = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonImage;     // по умолчанию
        style.disabled = buttonImage;  // при нажатии (опционально)
        Button redButton = new Button(style);
        Label label = new Label(text, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        label.setAlignment(Align.center);
        redButton.add(label).align(Align.center).pad(pad * 1.3f).fill();
        redButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().setOldGameScreen();
            }
        });
        redButton.setSize(label.getWidth() * 1.5f, label.getHeight() * 2.5f);
        return redButton;
    }

    private Button createChangeNickButton(String text) {
        TextureRegionDrawable buttonImage = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonImage;     // по умолчанию
        style.disabled = buttonImage;  // при нажатии (опционально)
        Button redButton = new Button(style);
        Label label = new Label(text, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        label.setAlignment(Align.center);
        redButton.add(label).align(Align.center).pad(pad * 1.1f).fill();
        redButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                String text = loginEditField.getText();
                if (text == null || text.length() < MIN_NAME_LENGTH || text.length() > MAX_NAME_LENGTH) {
                    loginEditField.setColor(Color.RED);
                } else {
                    User.get().login = text;
                    User.get().saveUser();
                    GameApplication.get().setSettingsScreen();
                }
            }
        });
        redButton.setSize(label.getWidth() * 1.5f, label.getHeight() * 2.5f);
        return redButton;
    }

    private Group createMusicBtn(float imageSize) {
        Group musicGroup = new Group();
        musicGroup.setSize(imageSize, imageSize);
        Image musicOnImage = new Image(ATLAS_1.findRegion("music_on"));
        musicOnImage.setSize(imageSize, imageSize);
        musicGroup.addActor(musicOnImage);
        Image musicOffImage = new Image(ATLAS_1.findRegion("music_off"));
        musicOffImage.setSize(imageSize, imageSize);
        musicGroup.addActor(musicOffImage);
        musicGroup.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                User.get().musicOn = !User.get().musicOn;
                User.get().saveUser();
                if(User.get().musicOn) {
                    GameApplication.get().bgMusic.play();
                } else {
                    GameApplication.get().bgMusic.pause();
                }
                musicOnImage.setVisible(User.get().musicOn);
                musicOffImage.setVisible(!User.get().musicOn);
            }
        });
        musicOnImage.setVisible(User.get().musicOn);
        musicOffImage.setVisible(!User.get().musicOn);
        return musicGroup;
    }

    private Group createSoundBtn(float imageSize) {
        Group musicGroup = new Group();
        musicGroup.setSize(imageSize, imageSize);
        Image musicOnImage = new Image(ATLAS_1.findRegion("sound_on"));
        musicOnImage.setSize(imageSize, imageSize);
        musicGroup.addActor(musicOnImage);
        Image musicOffImage = new Image(ATLAS_1.findRegion("sound_off"));
        musicOffImage.setSize(imageSize, imageSize);
        musicGroup.addActor(musicOffImage);
        musicGroup.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                User.get().soundOn = !User.get().soundOn;
                User.get().saveUser();
                musicOnImage.setVisible(User.get().soundOn);
                musicOffImage.setVisible(!User.get().soundOn);
            }
        });
        musicOnImage.setVisible(User.get().soundOn);
        musicOffImage.setVisible(!User.get().soundOn);
        return musicGroup;
    }
}
