package com.plagame.game.kassa.components.bar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Дмитрий Малышев on 17.05.2026.
 * Email: dmitry.malyshev@gmail.com
 *
 * Круглый прогрессбар, толщена кольца задается через thickness, если thickness == 1 - полный радиус
 */
public class CircularProgressActor extends Actor {

    private static final ShapeRenderer SHAPE_RENDERER = new ShapeRenderer();

    private float progress;

    private float thickness = 0.2f; // толщена кольца в процентах от радиуса

    public void setProgress(float progress) {
        this.progress = MathUtils.clamp(progress, 0f, 1f);
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);

        SHAPE_RENDERER.setProjectionMatrix(getStage().getCamera().combined);
        SHAPE_RENDERER.setTransformMatrix(batch.getTransformMatrix());

        SHAPE_RENDERER.begin(ShapeRenderer.ShapeType.Filled);

        SHAPE_RENDERER.setColor(Color.GREEN);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float radius = Math.min(getWidth(), getHeight()) / 2f;

        drawRingSector(
            SHAPE_RENDERER,
            centerX,
            centerY,
            radius,
            radius - (thickness * radius),
            90f,
            -360f * progress,
            100
        );

        SHAPE_RENDERER.end();

        batch.begin();
    }

    private void drawRingSector(
        ShapeRenderer renderer,
        float x,
        float y,
        float outerRadius,
        float innerRadius,
        float startAngle,
        float degrees,
        int segments
    ) {

        float step = degrees / segments;

        for (int i = 0; i < segments; i++) {

            float angle1 = startAngle + step * i;
            float angle2 = startAngle + step * (i + 1);

            float cos1 = MathUtils.cosDeg(angle1);
            float sin1 = MathUtils.sinDeg(angle1);

            float cos2 = MathUtils.cosDeg(angle2);
            float sin2 = MathUtils.sinDeg(angle2);

            float ox1 = x + outerRadius * cos1;
            float oy1 = y + outerRadius * sin1;

            float ox2 = x + outerRadius * cos2;
            float oy2 = y + outerRadius * sin2;

            float ix1 = x + innerRadius * cos1;
            float iy1 = y + innerRadius * sin1;

            float ix2 = x + innerRadius * cos2;
            float iy2 = y + innerRadius * sin2;

            renderer.triangle(ox1, oy1, ox2, oy2, ix1, iy1);
            renderer.triangle(ix1, iy1, ox2, oy2, ix2, iy2);
        }
    }
}
