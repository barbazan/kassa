package com.plagame.game.integration.platform.service.api;

import com.plagame.game.kassa.utils.Time;

/**
 * Created by Дмитрий Малышев on 21.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class BaseAdsService implements AdsService {
    public static long VIDEO_AD_COOLDOWN = 2 * Time.MINUTE_MILLIS;
    public static long FULLSCREEN_AD_COOLDOWN = 2 * Time.MINUTE_MILLIS;
    public long lastRewardedVideoTime = System.currentTimeMillis() - VIDEO_AD_COOLDOWN;
//    public long lastFullscreenAdTime = System.currentTimeMillis() - FULLSCREEN_AD_COOLDOWN;
    public long lastFullscreenAdTime = System.currentTimeMillis() - FULLSCREEN_AD_COOLDOWN / 2; // чтобы первая реклама через некоторое время была а не сразу

    public boolean isFullscreenAdCooldown() {
        return System.currentTimeMillis() < lastFullscreenAdTime + FULLSCREEN_AD_COOLDOWN;
    }

    public boolean isVideoAdCooldown() {
        return false; // видеорекламу с ревардами без кулдаунов показываем
//        return System.currentTimeMillis() < lastRewardedVideoTime + VIDEO_AD_COOLDOWN;
    }

}
