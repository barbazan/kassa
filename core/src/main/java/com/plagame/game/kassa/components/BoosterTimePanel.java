package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_VERY_SMALL;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.enums.BoosterInfo;
import com.plagame.game.kassa.utils.Time;

/**
 * Created by Дмитрий Малышев on 29.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class BoosterTimePanel extends Group {

    private BoosterInfo boosterInfo;
    private Label timeLabel;

    public BoosterTimePanel(BoosterInfo boosterInfo) {
        this.boosterInfo = boosterInfo;
        float imageSize = GameApplication.get().minScreenSize / 6;
        setSize(imageSize, imageSize);

        Image image = new Image(boosterInfo.getTextureRegion());
        image.setSize(imageSize, imageSize);
        addActor(image);


        Group timeGroup = new Group();
        timeGroup.setSize(imageSize, imageSize / 4);
        Image timeBgImage = new Image(ATLAS_1.findRegion("title_bg"));
        timeBgImage.setSize(timeGroup.getWidth(), timeGroup.getHeight());
        timeGroup.addActor(timeBgImage);

        timeLabel = new ModelLabel("", new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK)) {
            @Override
            public void act(float delta) {
                setPosition((timeGroup.getWidth() - timeLabel.getWidth()) / 2, (timeGroup.getHeight() - timeLabel.getHeight()) / 2);
                super.act(delta);
            }

            @Override
            protected String getValue() {
                if(User.get().getBoosterTimeleft(boosterInfo) > 0) {
                    return new Time(User.get().getBoosterTimeleft(boosterInfo)).toStringInHumanFormat();
                }
                return "00:00:00";
            }
        };
        timeLabel.setAlignment(Align.center);
        timeLabel.setPosition((timeGroup.getWidth() - timeLabel.getWidth()) / 2, (timeGroup.getHeight() - timeLabel.getHeight()) / 2);
        timeGroup.addActor(timeLabel);

        addActor(timeGroup);

        addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameApplication.get().getGameScreen().uiStage.showShopBoostersDialog();
            }
        });
    }

    @Override
    public void act(float delta) {
        setVisible(User.get().hasBooster(boosterInfo));
        super.act(delta);
    }
}
