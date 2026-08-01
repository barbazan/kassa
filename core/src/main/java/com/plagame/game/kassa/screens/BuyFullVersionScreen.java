package com.plagame.game.kassa.screens;

import com.badlogic.gdx.InputProcessor;

/**
 * Created by Дмитрий Малышев on 01.08.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class BuyFullVersionScreen extends BaseScreen {

    public BuyFullVersionScreen() {
        init();
    }

    private void init() {

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
