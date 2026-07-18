package com.plagame.game.kassa.components.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.utils.SoundUtil;

/**
 * Created by Дмитрий Малышев on 17.07.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class LabelButton extends Group {

    private static final float BASE_MULT = 1.5f; // все кто больше этого значения, значит это рекламная кнопка и центровка текста сделана на четверть правее

    public LabelButton(TextureRegion buttonTextureRegion, String text, BitmapFont font, Color color) {
        this(buttonTextureRegion, text, font, color, BASE_MULT);
    }

    public LabelButton(TextureRegion buttonTextureRegion, String text, BitmapFont bitmapFont, Color color, float widthMult) {
        super();
        Image image = new Image(buttonTextureRegion);
        addActor(image);
        if(text != null) {
            Label label = new Label(text, new Label.LabelStyle(bitmapFont, color));
            float pad = label.getWidth() / 10;
            float w = Math.max(label.getWidth() + pad * 2, GameApplication.get().getWorldMinSize() / 5) * widthMult;
            float h = Math.max(label.getHeight() + pad * 2, w / 3);
            setSize(w, h);
            image.setSize(w, h);
            if(widthMult > BASE_MULT) {
                label.setPosition(getX() + getWidth() / 2 - label.getWidth() / 4, getY() + getHeight() / 2 - label.getHeight() / 2);
            } else {
                label.setPosition(getX() + getWidth() / 2 - label.getWidth() / 2, getY() + getHeight() / 2 - label.getHeight() / 2);
            }
            addActor(label);
        }
        addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                onClick();
            }
        });
    }

    public LabelButton(TextureRegion buttonUpTextureRegion, TextureRegion buttonDownTextureRegion, String text, BitmapFont font, Color color, float width, float height) {
        super();
        setSize(width, height);
        Image image = new Image(buttonUpTextureRegion);
        image.setSize(width, height);
//        YandexSDK.alert("width=" + width + ", height=" + height);
        addActor(image);
        if(text != null) {
            Label label = new Label(text, new Label.LabelStyle(font, color));
            label.setPosition(getX() + getWidth() / 2 - label.getWidth() / 2, getY() + getHeight() / 2 - label.getHeight() / 2);
            addActor(label);
        }
        addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                onClick();
            }
        });
    }

    public abstract void onClick();
}
