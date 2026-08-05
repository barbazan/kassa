package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_BIG;
import static com.plagame.game.kassa.GameApplication.FONT_BIG_TOYZ;
import static com.plagame.game.kassa.GameApplication.FONT_DEFAULT;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_BIG_TOYZ;
import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.kassa.enums.ColorInfo.LOADING_SCREEN_BG_3_COLOR;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.components.HeaderPanel;
import com.plagame.game.kassa.enums.AchievementInfo;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.SoundUtil;

/**
 * Created by Дмитрий Малышев on 27.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class AchievementsScreen extends BaseScreen {

    public AchievementsScreen() {
        init();
    }

    private void init() {
        stage.clear();
        float width = GameApplication.get().minScreenSize * 0.99f;
        float height = GameApplication.get().screenHeight * 0.99f;
        float pad = width / 20;
        Table dialogTable = new Table();
//        dialogTable.setDebug(true);
        dialogTable.setSize(width, height);
        dialogTable.padBottom(pad * 4);

        Label totalLabel = new Label("УГОЛОК \nДОСТИЖЕНИЙ", new Label.LabelStyle(FONT_VERY_BIG_TOYZ, Color.WHITE)); //todo I18N
        totalLabel.setAlignment(Align.center);
        dialogTable.add(totalLabel).align(Align.left).pad(pad / 2).padTop(pad).fill();
        dialogTable.row();

        for(AchievementInfo achievementInfo : AchievementInfo.values()) {
            Table avhievTable = createAchievmentTable(achievementInfo, width * 0.96f);
            dialogTable.add(avhievTable).size(avhievTable.getWidth(), avhievTable.getHeight()).align(Align.center).expandX().fill();
            dialogTable.row();
        }

        dialogTable.setPosition(GameApplication.get().screenWidth / 2 - width / 2, GameApplication.get().screenHeight / 2 - height / 2);

        ScrollPane scrollPane = new ScrollPane(dialogTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setSize(width, height);

        scrollPane.setPosition(GameApplication.get().screenWidth / 2 - scrollPane.getWidth() / 2, GameApplication.get().screenHeight / 2 - scrollPane.getHeight() / 2);

        stage.addActor(scrollPane);

        Button btnStart = createStartBtn();
        if(GameApplication.get().isPortrait()) {
            btnStart.setPosition(GameApplication.get().screenWidth / 2 - btnStart.getWidth() / 2, pad / 2);
        } else {
            btnStart.setPosition(GameApplication.get().screenWidth  - btnStart.getWidth() - pad / 2, pad / 2);
        }
        stage.addActor(btnStart);
    }

    @Override
    public void render(float delta) {
        AssetUtil.clearScreen(LOADING_SCREEN_BG_3_COLOR.color);
        super.render(delta);
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Table createAchievmentTable(AchievementInfo achievementInfo, float tableWidth) {
        float tableHeight = tableWidth / 2.90f;
        float imageHeight = tableHeight * 0.9f;
        float pad = imageHeight / 10;
        Table achievmentTable = new Table();
        achievmentTable.setSize(tableWidth, tableHeight);
//        achievmentTable.setDebug(true);
        achievmentTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_goods_bg_4")));
        achievmentTable.pad(pad / 2);

        Label nameLabel = new Label(achievementInfo.name, new Label.LabelStyle(FONT_DEFAULT, Color.DARK_GRAY)); //todo I18N
        nameLabel.setAlignment(Align.center);
        achievmentTable.add(nameLabel).align(Align.center).padBottom(pad).expandX().fill().colspan(2);
        achievmentTable.row();

        Label descLabel = new Label(achievementInfo.description, new Label.LabelStyle(FONT_DEFAULT, Color.DARK_GRAY)); //todo I18N
        descLabel.setAlignment(Align.topLeft);
        achievmentTable.add(descLabel).align(Align.topLeft).pad(pad).expand().fill();

        boolean hasAvhiev = User.get().hasAchievment(achievementInfo.type);
        Image achievImage = new Image(ATLAS_1.findRegion(hasAvhiev ? "icon_achiev" : "icon_achiev_disable"));
        float imageSize = tableHeight * 0.55f;
        achievImage.setSize(imageSize, imageSize);
        achievImage.setPosition(achievmentTable.getWidth() - achievImage.getWidth() - pad, achievmentTable.getHeight() * 0.44f - achievImage.getHeight() / 2);
        achievmentTable.addActor(achievImage);

        return achievmentTable;
    }

    private Button createStartBtn() {
        TextureRegion textureRegion = ATLAS_1.findRegion("button_green");
        final Button startBtn = new Button(
            new TextureRegionDrawable(textureRegion),
            new TextureRegionDrawable(textureRegion)
        );
        Label label = new Label("НАЗАД", new Label.LabelStyle(FONT_BIG_TOYZ, Color.DARK_GRAY)); //todo I18N
        label.setAlignment(Align.center);
        float w = label.getWidth() * 1.5f;
        float h = w * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
        startBtn.setSize(w, h);
        startBtn.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().setGameScreen();
                super.tap(event, x, y, count, button);
            }
        });
        label.setPosition(startBtn.getWidth() / 2 - label.getWidth() / 2, startBtn.getHeight() / 2 - label.getHeight() / 2);
        startBtn.add(label);

        return startBtn;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }
}
