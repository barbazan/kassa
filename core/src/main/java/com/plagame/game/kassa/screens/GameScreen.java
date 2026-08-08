package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameConfig.SHOW_FPS;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.GameScene;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.components.HeaderPanel;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.FPSRate;

/**
 * Created by Дмитрий Малышев on 02.05.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameScreen extends BaseScreen {

    private FPSRate fpsRate;
    public GameScene gameScene;
    public HeaderPanel headerPanel;

    public GameScreen() {
        super();
        init();
        addNetIndicator();
    }

    private void init() {
        User.get().fillCompleteAchievements();
        if(gameScene == null) {
            gameScene = new GameScene();
        }
        gameScene.resize();
        stage.addActor(gameScene);
        if(SHOW_FPS && GameApplication.get().FONT_VERY_SMALL != null) {
            fpsRate = new FPSRate();
        }
        initHeaderPanel();
    }

    @Override
    public void render(float delta) {
        AssetUtil.clearScreen();
        super.render(delta); // тут сцена отрисовывается на стейдже экторами

        GameApplication.get().batchBegin();
        if(fpsRate != null) {
            fpsRate.render();
        }
        GameApplication.get().batchEnd();
        checkConnect();
        checkPurchases();
    }


    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
//        uiStage.dispose();
        stage.dispose();
        if(fpsRate != null) {
            fpsRate.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }

    private void checkConnect() {
        if(GameApplication.get().networkWebSocketClient.isDisconnected()) {
            GameApplication.get().networkWebSocketClient.tryReconnect();
        }
    }

    private void initHeaderPanel() {
        if(headerPanel != null) {
            headerPanel.remove();
        }
        headerPanel = new HeaderPanel();
        stage.addActor(headerPanel);
    }

    private void addNetIndicator() {
        Image imageRed = new Image(ATLAS_1.findRegion("icon_circle_red"));
        imageRed.setSize(10, 10);
        imageRed.setPosition(0, GameApplication.get().screenHeight - imageRed.getHeight());
        stage.addActor(imageRed);

        Image imageGreen = new Image(ATLAS_1.findRegion("icon_circle_green")) {
            @Override
            public void act(float delta) {
                setVisible(GameApplication.get().networkWebSocketClient.isConnected());
                super.act(delta);
            }
        };
        imageGreen.setSize(imageRed.getWidth(), imageRed.getHeight());
        imageGreen.setPosition(imageRed.getX(), imageRed.getY());
        stage.addActor(imageGreen);

    }

}
