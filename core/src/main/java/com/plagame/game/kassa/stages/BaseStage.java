package com.plagame.game.kassa.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Дмитрий Малышев on 25.03.2023.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class BaseStage extends Stage {

    public void render(float delta) {
        act(delta);
        draw();
    }

}
