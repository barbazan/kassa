package com.plagame.game.kassa;

import com.plagame.game.kassa.platform.service.api.AdsService;
import com.plagame.game.kassa.platform.service.api.BillingService;
import com.plagame.game.kassa.platform.service.api.CloudSaveService;
import com.plagame.game.kassa.platform.service.api.I18nService;
import com.plagame.game.kassa.platform.service.api.LeaderboardService;
import com.plagame.game.kassa.platform.service.api.PlatformServices;
import com.plagame.game.kassa.platform.service.api.model.PurchaseListener;
import com.plagame.game.kassa.platform.service.api.model.TargetPlatform;

/**
 * Created by Дмитрий Малышев on 25.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class IOSPlatformServices implements PlatformServices {
    @Override
    public TargetPlatform getPlatform() {
        return null;
    }

    @Override
    public void init(PurchaseListener purchaseListener) {

    }

    @Override
    public void notifyLoadingReady() {

    }

    @Override
    public BillingService billing() {
        return null;
    }

    @Override
    public CloudSaveService cloud() {
        return null;
    }

    @Override
    public LeaderboardService leaderboard() {
        return null;
    }

    @Override
    public AdsService ads() {
        return null;
    }

    @Override
    public I18nService i18n() {
        return null;
    }

    @Override
    public boolean isLeaderboardAvailable() {
        return false;
    }

    @Override
    public boolean isAdsAvailable() {
        return false;
    }

    @Override
    public void alert(String msg) {

    }
}
