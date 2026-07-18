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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.plagame.game.kassa.net.kassa.KassaNetworkWebSocketClient;
import com.plagame.game.kassa.platform.service.api.PlatformServices;
import com.plagame.game.kassa.platform.service.api.model.DefaultPurchaseListener;
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
//    public static long VIDEO_AD_COOLDOWN = 2 * Time.MINUTE_MILLIS;
//    public static long FULLSCREEN_AD_COOLDOWN = 2 * Time.MINUTE_MILLIS;
    private static GameApplication instance;
//    public static YandexPlayer yandexPlayer = new YandexPlayer();
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
//    public long lastRewardedVideoTime = System.currentTimeMillis() - VIDEO_AD_COOLDOWN;
//    public long lastFullscreenAdTime = System.currentTimeMillis() - FULLSCREEN_AD_COOLDOWN;

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
//        if(isWebGLAndYandex()) { //todo remove это уехало в  platform.init()
//            YandexBridge.exportMethods();
//        }
        initWebSocketClient();
        this.platform.init(new DefaultPurchaseListener()); // Инициализация всех сервисов в том числе сервиса покупок. Например загрузка каталога товаров. //todo попробовать перенести в finishLoading
    }

    public void finishLoading() {
        this.platform.notifyLoadingReady();
//        if(isWebGLAndYandex()) {
//            YandexSDK.notifyLoadingReady();
//            initYandexProducts();
//            tryLoadCloudUser();
//        } else {
//            initYandexProducts();
//        }
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
        setSettingsScreen();
//        setGameScreen();
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
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
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
//        FONT_HEADER = fontGenerator.generateHeaderBitmapFont(1, Color.WHITE);
//        FONT_DIALOG_HEADER = fontGenerator.generateDialogHeaderBitmapFont(1, Color.WHITE);
//        FONT_BIG = FONT_DIALOG_HEADER;
//        FONT_DIALOG_BUTTON = fontGenerator.generateDialogButtonBitmapFont(1, Color.WHITE);
//        FONT_RATING = fontGenerator.generateRatingBitmapFont(1, Color.WHITE);
//        FONT_VERY_BIG = fontGenerator.generateVeryBigBitmapFont(1, Color.WHITE);
//        FONT_SMALL = fontGenerator.generateSmallBitmapFont(1, Color.WHITE);
//        FONT_VERY_SMALL = fontGenerator.generateVerySmallBitmapFont(1, Color.WHITE);
    }

    public void initCam() {
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.position.set(screenWidth / 2f, screenHeight / 2f, 0);
        camera.zoom = 1.0f;
        camera.update();
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

//    public boolean isYandex() {
//		return YandexSDK.isYandexPlatform();
//    }
//
//    public boolean isWebGLAndYandex() {
//        return isWebGL() && isYandex();
//    }

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

//    private void initYandexProducts() {
//        if(GameApplication.get().isWebGLAndYandex()) {
//            YandexSDK.getCatalog(new YandexSDK.JsonCallback() {
//                @Override
//                public void onResult(String json) {
//                    YandexSDK.PRODUCTS = YandexParser.parseProducts(json);
////                    System.out.println("Каталог: " + json);
////                    YandexSDK.alert("Каталог: " + json);
////                     2. Проверяем все pending purchases
//                    YandexSDK.checkPendingPurchases();
//                }
//
//                @Override
//                public void onError(String error) {
////                    System.out.println("Ошибка каталога: " + error);
////                    YandexSDK.alert("Ошибка каталога: " + error);
//                }
//            });
//        } else {
////            String json = "[{\"id\":\"coin_1\",\"title\":\"100\",\"description\":\"100 монет\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/1881364/2a00000198b399c36fb660fa422c19aef464//default256x256\",\"price\":\"10 TST\",\"priceValue\":\"10\",\"priceCurrencyCode\":\"TST\"},{\"id\":\"coin_2\",\"title\":\"500\",\"description\":\"500 монет\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/3006389/2a00000198b39ac070d99b24548886a669fd//default256x256\",\"price\":\"50 TST\",\"priceValue\":\"50\",\"priceCurrencyCode\":\"TST\"},{\"id\":\"coin_3\",\"title\":\"1000\",\"description\":\"1000 монет\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/6238841/2a00000198b95f17191f0f807434c1e62458//default256x256\",\"price\":\"100 TST\",\"priceValue\":\"100\",\"priceCurrencyCode\":\"TST\"},{\"id\":\"coin_4\",\"title\":\"2500\",\"description\":\"2500 монет\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/6238841/2a00000198b95f50aac2b75a1dccca0e494e//default256x256\",\"price\":\"220 TST\",\"priceValue\":\"220\",\"priceCurrencyCode\":\"TST\"},{\"id\":\"coin_5\",\"title\":\"5000\",\"description\":\"5000 монет\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/1892995/2a00000198b95f71e32b03365476314e02de//default256x256\",\"price\":\"420 TST\",\"priceValue\":\"420\",\"priceCurrencyCode\":\"TST\"},{\"id\":\"coin_6\",\"title\":\"25000\",\"description\":\"25000 монет\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/1892995/2a00000198b9544318688cd8d73592708820//default256x256\",\"price\":\"2000 TST\",\"priceValue\":\"2000\",\"priceCurrencyCode\":\"TST\"}]";
//            String json = "[{\"id\":\"shop_gold_1\",\"title\":\"100\",\"description\":\"100 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17955142/2a0000019b06e923e3e6a92a646231e9fb1b//default256x256\",\"price\":\"10 RUB\",\"priceValue\":\"10\",\"priceCurrencyCode\":\"RUB\"},{\"id\":\"shop_gold_2\",\"title\":\"500\",\"description\":\"500 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17696815/2a0000019b06e951c0cc04cfeca92e8cd598//default256x256\",\"price\":\"50 RUB\",\"priceValue\":\"50\",\"priceCurrencyCode\":\"RUB\"},{\"id\":\"shop_gold_3\",\"title\":\"1000\",\"description\":\"1000 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17696815/2a0000019b06e97e2c1952eee9f50ec6b738//default256x256\",\"price\":\"100 RUB\",\"priceValue\":\"100\",\"priceCurrencyCode\":\"RUB\"}," +
//                "{\"id\":\"shop_gold_4\",\"title\":\"2500\",\"description\":\"2500 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/18762293/2a0000019b06d3a07392318d72a0653c1b3c//default256x256\",\"price\":\"220 RUB\",\"priceValue\":\"220\",\"priceCurrencyCode\":\"RUB\"}," +
//                "{\"id\":\"shop_gold_5\",\"title\":\"5000\",\"description\":\"5000 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17955142/2a0000019b06ebb2a4838a57de754f57cdd5//default256x256\",\"price\":\"420 RUB\",\"priceValue\":\"420\",\"priceCurrencyCode\":\"RUB\"}," +
//                "{\"id\":\"shop_gold_6\",\"title\":\"25000\",\"description\":\"25000 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17782796/2a0000019b06ebd659d9fb575d9a6a339833//default256x256\",\"price\":\"2000 RUB\",\"priceValue\":\"2000\",\"priceCurrencyCode\":\"RUB\"}," +
//                "{\"id\":\"shop_action_1\",\"title\":\"100\",\"description\":\"100 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17782796/2a0000019b06ebd659d9fb575d9a6a339833//default256x256\",\"price\":\"25 RUB\",\"priceValue\":\"25\",\"priceCurrencyCode\":\"RUB\"}," +
//                "{\"id\":\"shop_action_2\",\"title\":\"500\",\"description\":\"500 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17782796/2a0000019b06ebd659d9fb575d9a6a339833//default256x256\",\"price\":\"150 RUB\",\"priceValue\":\"150\",\"priceCurrencyCode\":\"RUB\"}," +
//                "{\"id\":\"shop_action_3\",\"title\":\"1000\",\"description\":\"1000 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17782796/2a0000019b06ebd659d9fb575d9a6a339833//default256x256\",\"price\":\"350 RUB\",\"priceValue\":\"350\",\"priceCurrencyCode\":\"RUB\"}," +
//                "{\"id\":\"shop_action_4\",\"title\":\"2000\",\"description\":\"2000 рубинов\",\"imageURI\":\"https://avatars.mds.yandex.net/get-games/17782796/2a0000019b06ebd659d9fb575d9a6a339833//default256x256\",\"price\":\"700 RUB\",\"priceValue\":\"700\",\"priceCurrencyCode\":\"RUB\"}]";
//            System.out.println("--------------------local---json = " + json);
//            YandexSDK.PRODUCTS = YandexParser.parseProducts(json);
//        }
//    }

//    public void tryLoadCloudUser() {
//        YandexBridge.checkAuth(data -> {
////            YandexSDK.alert("tryLoadCloudUser: data = " + data);
//            YandexPlayer player = new YandexPlayer(data);
//            if (player.id != null && !player.id.isEmpty()) {
////                YandexSDK.alert("Авторизован: " + player.id + " (" + player.login + ")");
//                yandexPlayer.id = player.id;
//                yandexPlayer.login = player.login;
//                YandexBridge.loadUserFromCloud();
//            } else {
//                System.out.println("Игрок не авторизован");
////                YandexSDK.alert("Игрок не авторизован");
//                // todo предложить авторизваться
//            }
//        });
//    }

//    public boolean isFullscreenAdCooldown() {
//        return System.currentTimeMillis() < lastFullscreenAdTime + FULLSCREEN_AD_COOLDOWN;
//    }
//
//    public boolean isVideoAdCooldown() {
//        return false; // видеорекламу с ревардами без кулдаунов показываем
////        return System.currentTimeMillis() < lastRewardedVideoTime + VIDEO_AD_COOLDOWN;
//    }

}
