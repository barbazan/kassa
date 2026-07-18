package com.plagame.game.kassa.utils;

import static com.plagame.game.kassa.GameApplication.FONT_VERY_SMALL;
import static com.plagame.game.kassa.GameConfig.VERSION;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.plagame.game.kassa.GameApplication;

/**
 * Created by Дмитрий Малышев on 29.06.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public class FPSRate implements Disposable {
    private long lastTimeCounted;
    private float sinceChange;
    private float frameRate;
    private int particlesCount;

    public FPSRate() {
        lastTimeCounted = TimeUtils.millis();
        sinceChange = 0;
        frameRate = Gdx.graphics.getFramesPerSecond();
    }

    public void render() {
        update();
        String stringFps = "V:" + VERSION + " FPS: " + (int)frameRate + " RC: " + GameApplication.get().batch.renderCalls + " P: " + particlesCount;
        FONT_VERY_SMALL.draw(GameApplication.get().batch, stringFps, 0, Gdx.graphics.getHeight());
    }

    private void update() {
        long delta = TimeUtils.timeSinceMillis(lastTimeCounted);
        lastTimeCounted = TimeUtils.millis();
        sinceChange += delta;
        if(sinceChange >= 2000) {
            sinceChange = 0;
            frameRate = Gdx.graphics.getFramesPerSecond();
            if(GameApplication.get().particlePool != null) {
                particlesCount = GameApplication.get().particlePool.globalActive;
            }
        }
    }

    public void dispose() {
    }
}
