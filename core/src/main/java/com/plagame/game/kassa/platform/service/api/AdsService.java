package com.plagame.game.kassa.platform.service.api;

import com.plagame.game.kassa.platform.service.api.model.PlatformCallback;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface AdsService {
    void init();
    public boolean isFullscreenAdCooldown();
    public boolean isVideoAdCooldown();
    public boolean isFullscreenAdReady();
    public boolean isVideoAdReady();

    public void showFullscreenAdv();
    public void showRewardedVideo(PlatformCallback<String> callback);

}
