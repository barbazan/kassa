package com.plagame.game.kassa.beans;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.GameApplication;

/**
 * Created by Дмитрий Малышев on 22.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ConveerLenta extends Group {

    private Image background;

    public ConveerLenta() {
        init();
    }

    private void init() {
        clear();
        addBackground();
        addBackground2();

        float height = background.getHeight();
        float width = background.getWidth();
        setSize(width, height);

    }

    public void resize() {
        init();
    }

    private void addBackground() {
        float lentaHeight = GameApplication.get().screenHeight * 0.30f;
        TextureRegion textureRegion = new TextureRegion(ATLAS_1.findRegion("lenta"));
        background = new Image(textureRegion);
        float scaleX = GameApplication.get().screenWidth / textureRegion.getRegionWidth();
        float scaleY = lentaHeight / textureRegion.getRegionHeight();
        float scale = Math.max(scaleX, scaleY);
        background.setSize(textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
        background.setPosition(0, 0);
        addActor(background);
    }

    private void addBackground2() {
        float lentaHeight = GameApplication.get().screenHeight * 0.30f;
        TextureRegion textureRegion = new TextureRegion(ATLAS_1.findRegion("lenta"));
        background = new Image(textureRegion);
        float scaleX = GameApplication.get().screenWidth / textureRegion.getRegionWidth();
        float scaleY = lentaHeight / textureRegion.getRegionHeight();
        float scale = Math.max(scaleX, scaleY);
        float w = textureRegion.getRegionWidth() * scale;
        float h = textureRegion.getRegionHeight() * scale;
        background.setSize(w, h);
        background.setPosition(0, -h);
        addActor(background);
    }

}
