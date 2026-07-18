package com.plagame.game.kassa.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.plagame.game.kassa.GameApplication;

/**
 * Created by Дмитрий Малышев on 08.02.2019.
 * Email: dmitry.malyshev@gmail.com
 */
public class AssetUtil {

    public static void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void loadAtlas(String filename) {
        GameApplication.get().assetManager.load(filename, TextureAtlas.class);
    }

    public static void loadTexture(String filename) {
        GameApplication.get().assetManager.load(filename, Texture.class);
    }

    public static void loadSound(String filename) {
        GameApplication.get().assetManager.load(filename, Sound.class);
    }

    public static void loadMusic(String filename) {
        GameApplication.get().assetManager.load(filename, Music.class);
    }

    public static TextureAtlas getTextureAtlas(String filename) {
        return GameApplication.get().assetManager.get(filename, TextureAtlas.class);
    }

    public static Texture getTexture(String filename) {
        Texture texture = GameApplication.get().assetManager.get(filename, Texture.class);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    public static Sprite createSprite(String filename) {
        Texture texture = getTexture(filename);
        return new Sprite(texture);
    }

    public static Sprite createSprite(String filename, Vector3 startPosition, float size) {
        Sprite sprite = createSprite(filename);
        sprite.setPosition(startPosition.x, startPosition.y);
        sprite.setSize(size, size);
        return sprite;
    }

    public static Sprite createBgSprite(OrthographicCamera cam, String bgFileName) {
        Sprite bgSprite = createSprite(bgFileName);
        float xScale = cam.viewportWidth * cam.zoom / bgSprite.getWidth();
        float yScale = cam.viewportHeight * cam.zoom / bgSprite.getHeight();
        float scale = Math.max(xScale, yScale);
        bgSprite.setScale(scale);
        bgSprite.setPosition(-bgSprite.getWidth() / 2, -bgSprite.getHeight() / 2);
        return bgSprite;
    }

    public static ImageButton createButton(String imageUpFilename, String imageDownFilename, Vector3 position, float size) {
        Drawable imageUp = new TextureRegionDrawable(new TextureRegion(GameApplication.get().assetManager.get(imageUpFilename, Texture.class)));
        Drawable imageDown = new TextureRegionDrawable(new TextureRegion(GameApplication.get().assetManager.get(imageDownFilename, Texture.class)));
        ImageButton button = new ImageButton(imageUp, imageDown);
        button.setPosition(position.x, position.y);
        button.setSize(size, size);
        return button;
    }

    public static Sound getSound(String soundFilepath) {
        return GameApplication.get().assetManager.get(soundFilepath, Sound.class);
    }

    public static Music getMusic(String musicFilepath) {
        return GameApplication.get().assetManager.get(musicFilepath, Music.class);
    }

}
