package com.plagame.game.kassa.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.Resources;

/**
 * Created by Дмитрий Малышев on 02.05.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public class LoadingScreen extends BaseScreen {
    public static String IMAGE_LOGO_FILENAME = "images/logo.jpg";
//    public static String IMAGE_LOGO_2_FILENAME = "images/logo_3.jpg";

    private final Label loading;
    private final ProgressBar progressBar;
    private final long startTime;
    private int progress;

    public LoadingScreen() {
        super();
        background = createBgSprite(IMAGE_LOGO_FILENAME);
//        background = createBgSprite(IMAGE_LOGO_2_FILENAME);
        progressBar = new ProgressBar(0, 100, 1, false, skin);
        progressBar.setPosition(0, 0);
        progressBar.setSize(Gdx.graphics.getWidth(), progressBar.getHeight());
        stage.addActor(progressBar);
        loading = new Label("Loading...", skin);
        loading.setFontScale(2);
        loading.setPosition(Gdx.graphics.getWidth() / 2f - loading.getWidth() * loading.getFontScaleX() / 2, progressBar.getHeight() * 3);
        stage.addActor(loading);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void render(float delta) {
        clearScreen();
        GameApplication.get().batchBegin();
        background.draw(GameApplication.get().batch);
        GameApplication.get().batchEnd();
        if (GameApplication.get().assetManager.update() && Resources.isLoadingFinished()) {
            loading.setText("Loading... 100%");
            progressBar.setValue(100);
            GameApplication.get().finishLoading();
        } else {
            int newProgress = progress;
            while(newProgress <= progress) {
                GameApplication.get().assetManager.update();
                newProgress = (int) (GameApplication.get().assetManager.getProgress() * 100);
            }
            progress = newProgress;
            loading.setText("Loading... " + newProgress + "% " + (System.currentTimeMillis() - startTime) + "ms");
            progressBar.setValue(newProgress);
        }
        super.render(delta);
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return null;
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

}
