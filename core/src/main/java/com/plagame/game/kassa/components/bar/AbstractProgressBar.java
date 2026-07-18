package com.plagame.game.kassa.components.bar;


import static com.plagame.game.kassa.Resources.HP_BAR_BG_TEXTURE_REGION;
import static com.plagame.game.kassa.Resources.HP_BAR_FRAME_TEXTURE_REGION;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by Дмитрий Малышев on 24.06.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class AbstractProgressBar extends Group {

    protected Image bgImage;
    protected Image barImage;

    public AbstractProgressBar() {
    }

    AbstractProgressBar(float width, TextureRegion barTexture) {
        this(width, barTexture, HP_BAR_BG_TEXTURE_REGION, HP_BAR_FRAME_TEXTURE_REGION);
    }

    AbstractProgressBar(float width, TextureRegion barTexture, TextureRegion bgTexture, TextureRegion frameTexture) {
        this(width, width / 6, barTexture, bgTexture, frameTexture);
    }

    AbstractProgressBar(float width, float height, TextureRegion barTexture, TextureRegion bgTexture, TextureRegion frameTexture) {
        setSize(width, height);
        Image bgImage = new Image(bgTexture);
        bgImage.setSize(getWidth(), getHeight());
        addActor(bgImage);

        barImage = new Image(barTexture);
        barImage.setSize(getWidth() * getPercent(), getHeight());
        addActor(barImage);

        Image frameImage = new Image(frameTexture);
        frameImage.setSize(getWidth(), getHeight());
        addActor(frameImage);
    }

    @Override
    public void act(float delta) {
        barImage.setSize(getWidth() * getPercent(), getHeight());
        super.act(delta);
    }

    public abstract float getPercent();

}
