package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_VERY_BIG;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;

/**
 * Created by Дмитрий Малышев on 27.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class DayCompleteScreen extends BaseScreen {

    public DayCompleteScreen() {
        super();
        init();
    }

    private void init() {
        stage.clear();
        Label label = new Label("DAY COMPLETE", new Label.LabelStyle(FONT_VERY_BIG, Color.WHITE)); //todo I18N
        System.out.println("label = " + label);
        label.setAlignment(Align.center);
        label.setPosition(stage.getWidth() / 2 - label.getWidth() / 2, stage.getHeight() / 2 - label.getHeight() / 2);
        label.addAction(
            Actions.color(Color.GREEN, 2.35f, Interpolation.linear)
        );
        stage.addActor(label);

        stage.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                GameApplication.get().setGameScreen();
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
