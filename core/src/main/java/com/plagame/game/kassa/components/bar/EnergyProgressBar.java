package com.plagame.game.kassa.components.bar;

import static com.plagame.game.kassa.Resources.HP_BAR_ENERGY_BG_TEXTURE_REGION;
import static com.plagame.game.kassa.Resources.HP_BAR_ENERGY_FRAME_TEXTURE_REGION;
import static com.plagame.game.kassa.Resources.HP_BAR_ENERGY_GREEN_TEXTURE_REGION;
import static com.plagame.game.kassa.Resources.HP_BAR_ENERGY_RED_TEXTURE_REGION;
import static com.plagame.game.kassa.Resources.HP_BAR_ENERGY_YELLOW_TEXTURE_REGION;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 23.06.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public class EnergyProgressBar extends AbstractProgressBar {

    private final static int COLOR_GREEN = 1;
    private final static int COLOR_YELLOW = 2;
    private final static int COLOR_RED = 3;
    private int COLOR = COLOR_GREEN;

    public EnergyProgressBar(float width) {
        init(width);
    }

    private void init(float width) {
        clearChildren();
        clearActions();
        setSize(width, width / 4);
        bgImage = new Image(HP_BAR_ENERGY_BG_TEXTURE_REGION);
        float startX = getWidth() * 0.0475f;
//        float startX = getWidth() * 0.086f;
        bgImage.setSize(getActualProgressWidth(), getHeight() * 0.95f);
        bgImage.setPosition(startX, 1);
        addActor(bgImage);

        barImage = new Image(getCurrentColorTextureRegion());
        barImage.setSize(getActualProgressWidth() * getPercent(), getHeight() * 0.95f);
        barImage.setPosition(startX, 1);
        addActor(barImage);

        Image frameImage = new Image(HP_BAR_ENERGY_FRAME_TEXTURE_REGION);
        frameImage.setSize(getWidth(), getHeight());
        addActor(frameImage);

    }

    @Override
    public void act(float delta) {
        float startX = getWidth() * 0.0473f;
//        float startX = getWidth() * 0.086f;
        float w = getActualProgressWidth();
        barImage.setSize(w * getPercent(), getHeight() * 0.95f);
        barImage.setPosition(startX, 1);
        if(getPercent() < 0.25f) {
            if(COLOR != COLOR_RED) {
                COLOR = COLOR_RED;
                init(getWidth());
            }
        } else if(getPercent() >= 0.25f && getPercent() < 0.5f) {
            if(COLOR != COLOR_YELLOW) {
                COLOR = COLOR_YELLOW;
                init(getWidth());
            }
        } else if(getPercent() >= 0.5f) {
            if(COLOR != COLOR_GREEN) {
                COLOR = COLOR_GREEN;
                init(getWidth());
            }
        }
    }

    public float getPercent() {
        return 0.5f;
    }

    private TextureRegion getCurrentColorTextureRegion() {
        if(COLOR == COLOR_GREEN) {
            return HP_BAR_ENERGY_GREEN_TEXTURE_REGION;
        } else if(COLOR == COLOR_YELLOW) {
            return HP_BAR_ENERGY_YELLOW_TEXTURE_REGION;
        } else {
            return HP_BAR_ENERGY_RED_TEXTURE_REGION;
        }
    }

    private float getActualProgressWidth() {
        float startX = getWidth() * 0.0475f;
        float rightPad = getWidth() * 0.0777f;
//        float startX = getWidth() * 0.086f;
//        float rightPad = getWidth() * 0.158f;
        return getWidth() - startX - rightPad;
    }
}
