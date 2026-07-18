package com.plagame.game.kassa.pools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Дмитрий Малышев on 31.05.2025.
 * Email: dmitry.malyshev@gmail.com
 * -------------------------------------------------------------------------------------------------
 * ParticleManager particles = new ParticleManager();          // или new ParticleManager(800);
 * particles.loadDefaults("particles");                       // путь к папке с .p и атласами
 *
 * // ── в момент спавна
 * PooledEffect boom = particles.obtain("particle_explosion.p", new Vector2(x, y));
 *
 * // ── в render
 * particles.update(delta);
 * batch.begin();
 * particles.draw(batch);
 * batch.end();
 * -------------------------------------------------------------------------------------------------
 */
public class ParticlePool {

    // Партиклы короткие из пула
    public static String PARTICLE_DOLLARS_FILENAME = "particles/particle_dollars.p";
    public static String PARTICLE_DOLLAR_SINGLE_FILENAME = "particles/particle_dollar_single.p";


    /** Container for a single effect type. */
    private static class ParticleData {
        final ParticleEffectPool pool;
        final Array<ParticleEffectPool.PooledEffect> active = new Array<>();
        final int maxCount;

        ParticleData(ParticleEffectPool pool, int maxCount) {
            this.pool = pool;
            this.maxCount = maxCount;
        }
    }

    private final ObjectMap<String, ParticleData> effects = new ObjectMap<>();
    private int globalMaxActive = -1; // ‑1 disables global cap
    public int globalActive = 0;

    /** Creates a manager with no global cap. */
    public ParticlePool() {}

    /** Creates a manager that caps the total active effects across <b>all</b> types. */
    public ParticlePool(int globalMaxActive) {
        this.globalMaxActive = globalMaxActive;
    }

    /** Adds an effect template to the manager. */
    public void addEffectTemplate(String id,
                                  ParticleEffect template,
                                  int initialCapacity,
                                  int maxCapacity,
                                  int maxActiveCount) {
        ParticleEffectPool pool = new ParticleEffectPool(template, initialCapacity, maxCapacity);
        effects.put(id, new ParticleData(pool, maxActiveCount));
    }

    // -------------------------------------------------------------------------
    // Bulk loading helpers
    // -------------------------------------------------------------------------

    /**
     * Convenience helper for the common case of <b>short‑lived</b> effects.
     * Uses (10, 120, 100) for (initial, maxCapacity, maxCount).
     */
    private void addShort(String fileName, TextureAtlas atlas) {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal(fileName), atlas);
        addEffectTemplate(fileName, effect, 10, 120, 100);
    }

    /**
     * Loads all default effects stored in {@code directory} (relative to assets root).
     * Call once from {@code create()}.
     */
    public void loadDefaults(TextureAtlas atlas) {
        addShort(PARTICLE_DOLLARS_FILENAME, atlas);
        addShort(PARTICLE_DOLLAR_SINGLE_FILENAME, atlas);
    }

    // -------------------------------------------------------------------------
    // Runtime API
    // -------------------------------------------------------------------------

    /**
     * Obtains (spawns) a particle effect at the given position.
     * Returns {@code null} if per‑effect or global limits are exceeded.
     */
    public ParticleEffectPool.PooledEffect obtain(String id, Vector2 position) {
        ParticleData data = effects.get(id);
        if (data == null) {
            System.out.println("----------------UNKNOWN EFFECT for " + id);
            return null;
        };

        if (globalMaxActive >= 0 && globalActive >= globalMaxActive) {
            System.out.println("----------------GLOBAL LIMIT for " + id);
            return null;
        };

        if (data.active.size >= data.maxCount) {
            System.out.println("----------------LIMIT REACHED for " + id);
            return null;
        }

        ParticleEffectPool.PooledEffect e = data.pool.obtain();
        e.reset();
        e.setPosition(position.x, position.y);
        e.start();
        return e;
    }


    public ParticleEffectPool.PooledEffect obtainEffectDollars(float x, float y, float scale) {
        return obtainEffect(PARTICLE_DOLLAR_SINGLE_FILENAME, x, y, scale);
//        return obtainEffect(PARTICLE_DOLLARS_FILENAME, x, y, scale);
    }

    private ParticleEffectPool.PooledEffect obtainEffect(String particleFilename, Vector2 position, float scale) {
        return obtainEffect(particleFilename, position.x, position.y, scale);
    }

    private ParticleEffectPool.PooledEffect obtainEffect(String particleFilename, float x, float y, float scale) {
        ParticleData data = getParticleData(particleFilename);
        if(data == null) {
            return null;
        }
        ParticleEffectPool.PooledEffect e = data.pool.obtain();
        e.reset();
        e.setPosition(x, y);
        e.scaleEffect(scale);
        e.start();

        data.active.add(e);
        globalActive++;
        return e;
    }

    private ParticleData getParticleData(String id) {
        ParticleData data = effects.get(id);
        if (data == null) {
            System.out.println("----------------UNKNOWN EFFECT for " + id);
            return null;
        }; // unknown effect

        if (globalMaxActive >= 0 && globalActive >= globalMaxActive) {
            System.out.println("----------------GLOBAL LIMIT for " + id);
            return null;
        };

        // per‑effect limit
        if (data.active.size >= data.maxCount) {
            System.out.println("----------------LIMIT REACHED for " + id);
            return null;
        }
        return data;
    }

    /** Advances all active effects and frees those that have completed. */
    public void update(float delta) {
        for (ObjectMap.Entry<String, ParticleData> entry : effects.entries()) {
            ParticleData d = entry.value;
            for (int i = d.active.size - 1; i >= 0; i--) {
                ParticleEffectPool.PooledEffect e = d.active.get(i);
                e.update(delta);
                if (e.isComplete()) {
                    e.free();
                    d.active.removeIndex(i);
                    globalActive--;
                }
            }
        }
    }

    /** Draws all active effects. Call between {@code batch.begin()} / {@code batch.end()}. */
    public void draw(Batch batch) {
        // Отрисовываем сначала обычные эффекты (не additive)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Array<String> keys = effects.keys().toArray();
        int keyCount = keys.size;
        for (int i = 0; i < keyCount; i++) {
            ParticleData d = effects.get(keys.get(i));
            Array<ParticleEffectPool.PooledEffect> active = d.active;
            int size = active.size;
            for (int j = 0; j < size; j++) {
                ParticleEffectPool.PooledEffect e = active.get(j);
                if (!hasAdditiveEmitters(e)) {
                    e.draw(batch);
                }
            }
        }
        // Затем additive-эффекты
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < keyCount; i++) {
            ParticleData d = effects.get(keys.get(i));
            Array<ParticleEffectPool.PooledEffect> active = d.active;
            int size = active.size;
            for (int j = 0; j < size; j++) {
                ParticleEffectPool.PooledEffect e = active.get(j);
                if (hasAdditiveEmitters(e)) {
                    e.draw(batch);
                }
            }
        }
        // Восстанавливаем стандартный blend
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /** Frees all effects currently in flight and resets counters. */
    public void freeAll() {
        for (ParticleData d : effects.values()) {
            for (ParticleEffectPool.PooledEffect e : d.active) {
                e.free();
            }
            d.active.clear();
        }
        globalActive = 0;
    }

    /** Releases all resources retained by the manager. */
    public void dispose() {
        freeAll();
        effects.clear();
    }

    private boolean hasAdditiveEmitters(ParticleEffectPool.PooledEffect effect) {
        Array<ParticleEmitter> emitters = effect.getEmitters();
        int n = emitters.size;
        for (int i = 0; i < n; i++) {
            if (emitters.get(i).isAdditive()) return true;
        }
        return false;
    }
}
