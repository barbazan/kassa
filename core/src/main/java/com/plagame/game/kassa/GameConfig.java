package com.plagame.game.kassa;

import com.badlogic.gdx.utils.Json;
import com.plagame.game.integration.platform.service.api.model.TargetPlatform;

import java.util.Random;

/**
 * Created by Дмитрий Малышев on 22.03.2023.
 * Email: dmitry.malyshev@gmail.com
 * ================== ВАЖНО!!!! Этот конфиг должен быть одинаковый на клиенте и на сервере ==================
 */
public class GameConfig {
    public static final String VERSION = "1.10";
    public static final float DEFAULT_MUSIC_VOLUME = 0.10f;
    public static final float DEFAULT_SOUND_VOLUME = 0.2f;
    public static final float DEFAULT_CLICK_VOLUME = 0.2f;

    public static final boolean GUI_DEBUG = false;
    public static final boolean SHOW_FPS = false;
    public static final String LEADERBOARD_MAX_DOLLARS_NAME = "maxDollars";

    public static TargetPlatform TARGET_PLATFORM = TargetPlatform.HTML_YANDEX;

    public static final Json JSON = new Json();
    public static final Random random = new Random();

    public static String randomId() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()))
            + Long.toHexString(System.currentTimeMillis());
    }
}
