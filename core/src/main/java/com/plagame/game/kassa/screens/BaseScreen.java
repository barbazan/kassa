package com.plagame.game.kassa.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.utils.AssetUtil;

/**
 * Created by Дмитрий Малышев on 02.05.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class BaseScreen implements Screen {
    public static final int PAD = Gdx.graphics.getHeight() / 100;
    private InputProcessor inputProcessor;
    public Stage stage;
    public Skin skin;
    public Sprite background;
    public float screenWidth;
    public float screenHeight;

    public BaseScreen() {
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        stage = new Stage(GameApplication.get().viewport, GameApplication.get().batch);
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    public InputProcessor getInputProcessor() {
        if(inputProcessor == null) {
            inputProcessor = initInputProcessor();
        }
        return inputProcessor;
    }

    protected Sprite createBgSprite(String filename) {
        Texture texture = new Texture(filename); //todo dispose
        Sprite bgSprite = new Sprite(texture);

        float worldWidth = GameApplication.get().camera.viewportWidth * GameApplication.get().camera.zoom;
        float worldHeight = GameApplication.get().camera.viewportHeight * GameApplication.get().camera.zoom;

        float scale = Math.max(
            worldWidth / bgSprite.getTexture().getWidth(),
            worldHeight / bgSprite.getTexture().getHeight()
        );

        float bgWidth = bgSprite.getTexture().getWidth() * scale;
        float bgHeight = bgSprite.getTexture().getHeight() * scale;

        bgSprite.setSize(bgWidth, bgHeight);

        bgSprite.setPosition(
            GameApplication.get().camera.position.x - bgWidth / 2f,
            GameApplication.get().camera.position.y - bgHeight / 2f
        );
        return bgSprite;
    }

    protected void clearScreen() {
        AssetUtil.clearScreen();
    }

    protected abstract InputProcessor initInputProcessor();

    @Override
    public void show() {
        System.out.println("------------- BASE_SCREEN SHOW() -------------");
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("------------- BASE_SCREEN RESIZE() -------------");
        GameApplication.get().initScreenSize(width, height);
        GameApplication.get().viewport.setWorldSize(width, height);
        GameApplication.get().viewport.update(width, height,true);
    }

    @Override
    public void pause() {
        System.out.println("------------- BASE_SCREEN PAUSE() -------------");
    }

    @Override
    public void resume() {
        System.out.println("------------- BASE_SCREEN RESUME() -------------");
    }

    @Override
    public void hide() {
        System.out.println("------------- BASE_SCREEN HIDE() -------------");
    }
}
