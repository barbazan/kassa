package com.plagame.game.kassa.platform.service.local;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.platform.service.api.AdsService;
import com.plagame.game.kassa.platform.service.api.BillingService;
import com.plagame.game.kassa.platform.service.api.CloudSaveService;
import com.plagame.game.kassa.platform.service.api.LeaderboardService;
import com.plagame.game.kassa.platform.service.api.I18nService;
import com.plagame.game.kassa.platform.service.api.PlatformServices;
import com.plagame.game.kassa.platform.service.api.model.PurchaseListener;
import com.plagame.game.kassa.platform.service.api.model.TargetPlatform;
import com.plagame.game.kassa.yandex.YandexSDK;

/**
 * Created by Дмитрий Малышев on 19.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class LocalPlatformServices implements PlatformServices {

    private final BillingService billingService = new LocalBillingService();
    private final CloudSaveService cloudService = new LocalCloudSaveService();
    private final LeaderboardService leaderboardService = new LocalLeaderboardService();
    private final I18nService i18nService = new LocalI18nService();

    @Override
    public TargetPlatform getPlatform() {
        return TargetPlatform.LOCAL;
    }

    @Override
    public void init(PurchaseListener purchaseListener) {
        billingService.setPurchaseListener(purchaseListener);
        billingService.init();
        cloudService.init();
        leaderboardService.init();
    }

    @Override
    public void notifyLoadingReady() {
        // локально не надо
    }

    @Override
    public BillingService billing() {
        return billingService;
    }

    @Override
    public CloudSaveService cloud() {
        return cloudService;
    }

    @Override
    public LeaderboardService leaderboard() {
        return leaderboardService;
    }

    @Override
    public AdsService ads() {
        return null;
    }

    public I18nService i18n() {
        return i18nService;
    }

    @Override
    public boolean isLeaderboardAvailable() {
        return true;
    }

    @Override
    public boolean isAdsAvailable() {
        return false;
    }

    @Override
    public void alert(String msg) {
        if(GameApplication.get().isWebGL()) {
            YandexSDK.alert(msg);
        } else {
            System.out.println("Alert msg = " + msg);
        }
    }
}
