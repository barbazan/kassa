package com.plagame.game.kassa.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Created by Дмитрий Малышев on 05.08.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ActionsUtil {

    public static void addForeverScaleAction(Actor actor) {
        float scaleDelta = 0.15f;
        float duration = 0.4f;
        actor.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleBy(scaleDelta, scaleDelta, duration, Interpolation.sine),
                Actions.scaleBy(-scaleDelta, -scaleDelta, duration, Interpolation.sine)
            )
        ));
    }


}
