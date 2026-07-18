package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameConfig.SHOW_FPS;

import com.badlogic.gdx.InputProcessor;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.GameScene;
import com.plagame.game.kassa.beans.User;
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
//        uiStage.showYandexGoldDialog(); //todo remove
//        uiStage.showYandexInAppDialog(); //todo remove
//        uiStage.showYandexActionsDialog(); //todo remove
//        uiStage.showShopBoostersDialog(); //todo remove
//        uiStage.showYandexRatingDialog(); //todo remove
//        User.get().location = LocationInfo.DUBAI.type;
//        User.get().saveUser();
//        uiStage.showPereezdDialog(); //todo remove
//        uiStage.showGiftDialog(); //todo remove
//        uiStage.showAbsenceDialog(); //todo remove
//        uiStage.showShopGoldDialog(); //todo remove
//        uiStage.showShopDialog(ShopInfo.THINGS); //todo remove
    }

    @Override
    public void render(float delta) {
        User.get().refresh();
        AssetUtil.clearScreen();
        super.render(delta); // тут сцена отрисовывается на стейдже экторами

        checkAbsenceDialog(); // проверяем нужна ли награда за долгоу отсутвие
        uiStage.render(delta);

        GameApplication.get().batchBegin();
        drawParticles(delta);
        if(fpsRate != null) {
            fpsRate.render();
        }
        GameApplication.get().batchEnd();
        checkConnect();
    }

    private void checkAbsenceDialog() {
        if(User.get().absenceMinutes > 0 && uiStage.absenceDialog == null) {
            uiStage.showAbsenceDialog();
        }
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return uiStage;
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        if(fpsRate != null) {
            fpsRate.dispose();
        }
    }

    private void drawParticles(float delta) {
        GameApplication.get().particlePool.update(delta);
        GameApplication.get().particlePool.draw(GameApplication.get().batch);
    }

    private void checkConnect() {
        if(GameApplication.get().networkWebSocketClient.isDisconnected()) {
            GameApplication.get().networkWebSocketClient.tryReconnect();
        }
    }
}
