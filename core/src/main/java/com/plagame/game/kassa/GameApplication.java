package com.plagame.game.kassa;

import static com.plagame.game.kassa.Resources.resourcesAssigned;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.plagame.game.net.kassa.KassaNetworkWebSocketClient;
import com.plagame.game.integration.platform.service.api.PlatformServices;
import com.plagame.game.integration.platform.service.api.model.DefaultPurchaseListener;
import com.plagame.game.kassa.pools.ParticlePool;
import com.plagame.game.kassa.screens.GameScreen;
import com.plagame.game.kassa.screens.LoadingScreen;
import com.plagame.game.kassa.screens.SettingsScreen;
import com.plagame.game.kassa.utils.FontGenerator;

/**
 * Created by Дмитрий Малышев on 09.04.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameApplication extends Game {
    public final static float WORLD_WIDTH = 1080;
    public final static float WORLD_HEIGHT = 1920;
    private static GameApplication instance;
    public final PlatformServices platform;
    public float screenWidth, screenHeight, minScreenSize;
    public final AssetManager assetManager = new AssetManager();
    public KassaNetworkWebSocketClient networkWebSocketClient;
    public ParticlePool particlePool; // это не надо уносить в tiledMapLevel, потому что particleManager инициализируется до tiledMapLevel, чтобы загрузить партиклы
    public Viewport viewport;
    public OrthographicCamera camera;
    public PolygonSpriteBatch batch;
    public LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;
    public static BitmapFont FONT_DEFAULT, FONT_HEADER, FONT_DIALOG_HEADER, FONT_DIALOG_BUTTON, FONT_RATING, FONT_BIG, FONT_VERY_BIG, FONT_SMALL, FONT_VERY_SMALL;
    public int exceptionCount;

    public GameApplication(PlatformServices platformServices) {
        this.platform = platformServices;
    }

    public static GameApplication get() {
        return instance;
    }

    @Override
    public void create() {
        instance = this;
        initScreenSize();
        initCam();
        Resources.loadResources();
        batch = new PolygonSpriteBatch();

        loadingScreen = new LoadingScreen();
        setScreen(loadingScreen);
//        printTextureMaxSize();
        initFonts();
        initWebSocketClient();
        this.platform.init(new DefaultPurchaseListener()); // Инициализация всех сервисов в том числе сервиса покупок. Например загрузка каталога товаров. //todo попробовать перенести в finishLoading
    }

    public void finishLoading() {
        this.platform.notifyLoadingReady();
        if(!resourcesAssigned) {
            System.out.println("------------- FINISH_LOADING -------------");
            Resources.assignResources();
            System.out.println("------------- ASSIGN_RESOURCES -------------");
            goToFirstScreen();
            loadingScreen.dispose();
        }
    }

    @Override
    public void render () {
        try {
            super.render();
        } catch (Exception e) {
            exceptionCount++;
            if(exceptionCount >= 3) {
                //todo send to server
                throw e;
            }
        }
    }

    private void goToFirstScreen() {
//        setSettingsScreen();
        setGameScreen();
    }

    public void setGameScreen() {
        if(gameScreen != null) {
            gameScreen.dispose();
        }
        gameScreen = new GameScreen();
        setScreen(gameScreen);
        setInputProcessor(gameScreen.getInputProcessor());
    }

    public void setSettingsScreen() {
        if(settingsScreen != null) {
            settingsScreen.dispose();
        }
        settingsScreen = new SettingsScreen();
        setScreen(settingsScreen);
        setInputProcessor(settingsScreen.getInputProcessor());
    }

    public void initWebSocketClient() {
        networkWebSocketClient = new KassaNetworkWebSocketClient();
    }

    public void initScreenSize() {
        initScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void initScreenSize(float width, float height) {
        screenWidth = width;
        screenHeight = height;
        minScreenSize = Math.min(screenWidth, screenHeight);
    }

    public void initFonts() {
        FontGenerator fontGenerator = new FontGenerator();
        FONT_DEFAULT = fontGenerator.generateDefaultBitmapFont(1, Color.WHITE);
        FONT_HEADER = FONT_DEFAULT;
        FONT_DIALOG_HEADER = FONT_DEFAULT;
        FONT_BIG = FONT_DIALOG_HEADER;
        FONT_DIALOG_BUTTON = FONT_DEFAULT;
        FONT_RATING = FONT_DEFAULT;
        FONT_VERY_BIG = FONT_DEFAULT;
        FONT_SMALL = FONT_DEFAULT;
        FONT_VERY_SMALL = FONT_DEFAULT;
    }

    public void initCam() {
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.position.set(screenWidth / 2f, screenHeight / 2f, 0);
        camera.zoom = 1.0f;
        camera.update();
        viewport = new StretchViewport(screenWidth, screenHeight, camera);
    }

    public void onResize(int width, int height) {
        initScreenSize(width, height);
        viewport.setWorldSize(width, height);
        viewport.update(width, height,true);
        initFonts();
    }

    public void batchBegin() {
        if(!batch.isDrawing()) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
        }
    }

    public void batchEnd() {
        if(batch.isDrawing()) {
            batch.setProjectionMatrix(camera.combined);
            batch.end();
        }
    }

    public void setInputProcessor(InputProcessor inputProcessor) {
        Gdx.input.setInputProcessor(inputProcessor);
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
        if(gameScreen != null) {
            gameScreen.dispose();
        }
        super.dispose();
    }

    public boolean isWebGL() {
        return Gdx.app.getType() == Application.ApplicationType.WebGL;
    }

    public boolean isPortrait() {
        return getWorldHeight() >= getWorldWidth();
    }

    public float getWorldWidth() {
        return camera.viewportWidth * camera.zoom;
    }

    public float getWorldHeight() {
        return camera.viewportHeight * camera.zoom;
    }

    public float getWorldMinSize() {
        return Math.min(getWorldWidth(), getWorldHeight());
    }

}
