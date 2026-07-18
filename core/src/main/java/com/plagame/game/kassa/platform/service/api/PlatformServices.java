package com.plagame.game.kassa.platform.service.api;

import com.plagame.game.kassa.platform.service.api.model.PurchaseListener;
import com.plagame.game.kassa.platform.service.api.model.TargetPlatform;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface PlatformServices {

    TargetPlatform getPlatform();
    void init(PurchaseListener purchaseListener);
    void notifyLoadingReady(); // репортим статус "игра загризила все ресурсы и готова", пока это только у яндекса требуется, но у других тоже может понадобится
    BillingService billing();
    CloudSaveService cloud();
    LeaderboardService leaderboard();
    AdsService ads();
    I18nService i18n();
    boolean isLeaderboardAvailable();
    boolean isAdsAvailable();
    void alert(String msg);

}
