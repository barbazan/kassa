package com.plagame.game.kassa.beans;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

/**
 * Created by Дмитрий Малышев on 24.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class CameraAction extends TemporalAction {

    private final OrthographicCamera camera;

    private float startX, startY;
    private float startZoom;

    private float targetX, targetY;
    private float targetZoom;

    public CameraAction(OrthographicCamera camera,
                        float targetX,
                        float targetY,
                        float targetZoom,
                        float duration) {
        this.camera = camera;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZoom = targetZoom;
        setDuration(duration);
    }

    @Override
    protected void begin() {
        startX = camera.position.x;
        startY = camera.position.y;
        startZoom = camera.zoom;
    }

    @Override
    protected void update(float percent) {
        camera.position.x = startX + (targetX - startX) * percent;
        camera.position.y = startY + (targetY - startY) * percent;
        camera.zoom = startZoom + (targetZoom - startZoom) * percent;
        camera.update();
    }
}
