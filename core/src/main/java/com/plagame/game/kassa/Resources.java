package com.plagame.game.kassa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;
import com.plagame.game.kassa.pools.ParticlePool;
import com.plagame.game.kassa.utils.AssetUtil;

import java.util.Locale;

/**
 * Created by Дмитрий Малышев on 02.05.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public class Resources {
    public static I18NBundle GAME_I18N_BUNDLE;
    private static final String ATLAS_1_FILENAME = "images/kassa_atlas_1.atlas";
    private static final String ATLAS_CUSTOMERS_FILENAME = "images/kassa_atlas_customers.atlas";
    private static final String ATLAS_PRODUCTS_FILENAME = "images/kassa_atlas_products.atlas";

    public static final String GAME_BG_FILENAME = "images/game_bg.jpg";
    public static final String MUSIC_BG_FILENAME = "sounds/music_bg.mp3";
    public static final String SOUND_CLICK_FILENAME = "sounds/click.mp3";
    public static final String SOUND_CUSTOMER_CLICK_FILENAME = "sounds/customer_click.mp3";
    public static final String SOUND_KASSA_CLICK_FILENAME = "sounds/kassa_click.mp3";
    public static final String SOUND_KASSA_CASH_FILENAME = "sounds/kassa_cash.mp3";
    public static final String SOUND_KASSA_COIN_FILENAME = "sounds/kassa_coin.mp3";
    public static final String SOUND_PRODUCT_CLICK_FILENAME = "sounds/product_click.mp3";
    public static final String SOUND_TERMINAL_CLICK_FILENAME = "sounds/terminal_click.mp3";
    public static final String SOUND_TERMINAL_OK_CLICK_FILENAME = "sounds/terminal_ok_click.mp3";
    public static final String SOUND_WRONG_CLICK_FILENAME = "sounds/wrong_click.mp3";

    public static TextureAtlas ATLAS_1;
    public static TextureAtlas ATLAS_CUSTOMERS;
    public static TextureAtlas ATLAS_PRODUCTS;
    public static TextureRegion IMAGE_UNKNOWN_TEXTURE_REGION;
    public static TextureRegion HP_BAR_BG_TEXTURE_REGION, HP_BAR_FRAME_TEXTURE_REGION;
    public static TextureRegion HP_BAR_ENERGY_GREEN_TEXTURE_REGION, HP_BAR_ENERGY_YELLOW_TEXTURE_REGION, HP_BAR_ENERGY_RED_TEXTURE_REGION,
        HP_BAR_ENERGY_BG_TEXTURE_REGION, HP_BAR_ENERGY_FRAME_TEXTURE_REGION;
    public static TextureRegion BUTTON_BUY_UP_TEXTURE_REGION, BUTTON_BUY_DOWN_TEXTURE_REGION, BUTTON_BUY_DISABLE_TEXTURE_REGION;
    public static boolean loadedAtlas, loadedImages, loadedSounds, resourcesAssigned;

    public static void loadResources() {
        GAME_I18N_BUNDLE = I18NBundle.createBundle(Gdx.files.internal("i18n/strings"), new Locale("ru")); //todo брать реальную локаль
        loadAtlases();
        loadImages();
        loadSounds();
    }

    public static void assignResources() {
        if(!resourcesAssigned) {
            ATLAS_1 = AssetUtil.getTextureAtlas(ATLAS_1_FILENAME);
            applyFilter(ATLAS_1);
            ATLAS_CUSTOMERS = AssetUtil.getTextureAtlas(ATLAS_CUSTOMERS_FILENAME);
            applyFilter(ATLAS_CUSTOMERS);
            ATLAS_PRODUCTS = AssetUtil.getTextureAtlas(ATLAS_PRODUCTS_FILENAME);
            applyFilter(ATLAS_PRODUCTS);

            GameApplication.get().particlePool = new ParticlePool(80);    // new ParticleManager() или new ParticleManager(800);
            GameApplication.get().particlePool.loadDefaults(ATLAS_1);                   // атлас где хранятся картинки для партикла

            HP_BAR_BG_TEXTURE_REGION = ATLAS_1.findRegion("bar_bg");
            HP_BAR_FRAME_TEXTURE_REGION = ATLAS_1.findRegion("bar_frame");
            HP_BAR_ENERGY_GREEN_TEXTURE_REGION = ATLAS_1.findRegion("bar_energy_green");
            HP_BAR_ENERGY_YELLOW_TEXTURE_REGION = ATLAS_1.findRegion("bar_energy_yellow");
            HP_BAR_ENERGY_RED_TEXTURE_REGION = ATLAS_1.findRegion("bar_energy_red");
            HP_BAR_ENERGY_BG_TEXTURE_REGION = ATLAS_1.findRegion("bar_energy_bg");
            HP_BAR_ENERGY_FRAME_TEXTURE_REGION = ATLAS_1.findRegion("bar_energy_frame");
            IMAGE_UNKNOWN_TEXTURE_REGION = ATLAS_1.findRegion("image_unknown");
            BUTTON_BUY_UP_TEXTURE_REGION = ATLAS_1.findRegion("button_green");
            BUTTON_BUY_DOWN_TEXTURE_REGION = ATLAS_1.findRegion("button_gray");
            BUTTON_BUY_DISABLE_TEXTURE_REGION = ATLAS_1.findRegion("button_gray");


            resourcesAssigned = true;
        }
    }

    private static void applyFilter(TextureAtlas textureAtlas) {
        for (Texture texture : textureAtlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    private static void loadAtlases() {
        AssetUtil.loadAtlas(ATLAS_1_FILENAME);
        AssetUtil.loadAtlas(ATLAS_CUSTOMERS_FILENAME);
        AssetUtil.loadAtlas(ATLAS_PRODUCTS_FILENAME);
        loadedAtlas = true;
    }

    private static void loadImages() {
        AssetUtil.loadTexture(GAME_BG_FILENAME);
        loadedImages = true;
    }

    private static void loadSounds() {
//        AssetUtil.loadSound(SOUND_CLICK_FILENAME);
//        AssetUtil.loadSound(SOUND_SPEND_MONEY_FILENAME);
//        AssetUtil.loadSound(SOUND_GOT_MONEY_FILENAME);
        AssetUtil.loadMusic(MUSIC_BG_FILENAME);
        AssetUtil.loadSound(SOUND_CLICK_FILENAME);
        AssetUtil.loadSound(SOUND_CUSTOMER_CLICK_FILENAME);
        AssetUtil.loadSound(SOUND_KASSA_CLICK_FILENAME);
        AssetUtil.loadSound(SOUND_KASSA_CASH_FILENAME);
        AssetUtil.loadSound(SOUND_KASSA_COIN_FILENAME);
        AssetUtil.loadSound(SOUND_PRODUCT_CLICK_FILENAME);
        AssetUtil.loadSound(SOUND_TERMINAL_CLICK_FILENAME);
        AssetUtil.loadSound(SOUND_TERMINAL_OK_CLICK_FILENAME);
        AssetUtil.loadSound(SOUND_WRONG_CLICK_FILENAME);
        loadedSounds = true;
    }

    public static boolean isLoadingFinished() {
        return loadedAtlas && loadedImages && loadedSounds;
    }

}
