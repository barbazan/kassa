package com.plagame.game.kassa;

import com.badlogic.gdx.utils.Json;
import com.plagame.game.kassa.platform.service.api.model.TargetPlatform;

import java.util.Random;

/**
 * Created by Дмитрий Малышев on 22.03.2023.
 * Email: dmitry.malyshev@gmail.com
 * ================== ВАЖНО!!!! Этот конфиг должен быть одинаковый на клиенте и на сервере ==================
 */
public class GameConfig {
    public static final String VERSION = "1.26";
//    public static final boolean HTML_BUILD = false; // Сборка под html, если нет, то значит под андройд. Это влияет пока только на локальное сохранение.
    public static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    public static final float DEFAULT_SOUND_VOLUME = 0.2f;
    public static final float DEFAULT_CLICK_VOLUME = 0.2f;

    public static final boolean GUI_DEBUG = false;
    public static final boolean SHOW_FPS = false;
    public static final boolean RELEASE_BUILD = true;
    public static final String LEADERBOARD_MAX_DOLLARS_NAME = "maxDollars5";

    public static TargetPlatform TARGET_PLATFORM = TargetPlatform.HTML_YANDEX;

    public static final Json JSON = new Json();
    public static final Random random = new Random();

    public static String randomId() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()))
            + Long.toHexString(System.currentTimeMillis());
    }
}
