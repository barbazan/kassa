package com.plagame.game.kassa.platform.service.yandex;

import com.plagame.game.kassa.platform.service.api.BaseAdsService;
import com.plagame.game.kassa.platform.service.api.model.PlatformCallback;
import com.plagame.game.kassa.yandex.YandexSDK;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexAdsService extends BaseAdsService {

    @Override
    public void init() {

    }

    @Override
    public boolean isFullscreenAdReady() {
        return !isFullscreenAdCooldown();
    }

    @Override
    public boolean isVideoAdReady() {
        return !isVideoAdCooldown();
    }

    @Override
    public void showFullscreenAdv() {
        if(!isFullscreenAdCooldown()) {
            YandexSDK.showFullscreenAdv();
            lastFullscreenAdTime = System.currentTimeMillis();
        }
    }

    @Override
    public void showRewardedVideo(PlatformCallback<String> callback) {
        YandexSDK.showRewardedVideo(
            () -> {
                // ✅ Досмотрел или локальный запуск — выдаём награду
                callback.onSuccess("OK");
                lastRewardedVideoTime = System.currentTimeMillis();
            },
            () -> {
                // ❌ Закрыл до конца или реклама не загрузилась
                System.out.println("Ad skipped or failed — doing fallback action");
                callback.onError("Ad skipped or failed — doing fallback action, Закрыл до конца или реклама не загрузилась");
            }
        );
    }
}
