package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameConfig.SHOW_FPS;

import com.badlogic.gdx.InputProcessor;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.GameScene;
import com.plagame.game.kassa.stages.UIStage;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.FPSRate;

/**
 * Created by Дмитрий Малышев on 02.05.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameScreen extends BaseScreen {

    public final UIStage uiStage;
    private FPSRate fpsRate;
    public GameScene gameScene;

    public GameScreen() {
        super();
        uiStage = new UIStage();
        gameScene = new GameScene();
        stage.addActor(gameScene);
        if(SHOW_FPS && GameApplication.get().FONT_VERY_SMALL != null) {
            fpsRate = new FPSRate();
        }
    }

    @Override
    public void render(float delta) {
        AssetUtil.clearScreen();
        super.render(delta); // тут сцена отрисовывается на стейдже экторами

        uiStage.render(delta);

        GameApplication.get().batchBegin();
        if(fpsRate != null) {
            fpsRate.render();
        }
        GameApplication.get().batchEnd();
//        checkConnect();
    }


    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        if(fpsRate != null) {
            fpsRate.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gameScene.resize();
//        gameScene.productCortege.moveCameraSlowly(true);
    }

//    private void checkConnect() {
//        if(GameApplication.get().networkWebSocketClient.isDisconnected()) {
//            GameApplication.get().networkWebSocketClient.tryReconnect();
//        }
//    }
}
