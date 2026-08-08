package com.plagame.game.integration.platform.service.yandex;

import com.plagame.game.integration.platform.service.api.AdsService;
import com.plagame.game.integration.platform.service.api.BillingService;
import com.plagame.game.integration.platform.service.api.CloudSaveService;
import com.plagame.game.integration.platform.service.api.I18nService;
import com.plagame.game.integration.platform.service.api.LeaderboardService;
import com.plagame.game.integration.platform.service.api.PlatformServices;
import com.plagame.game.integration.platform.service.api.model.PurchaseListener;
import com.plagame.game.integration.platform.service.api.model.TargetPlatform;
import com.plagame.game.integration.platform.service.local.LocalLeaderboardService;
import com.plagame.game.integration.yandex.YandexBridge;
import com.plagame.game.integration.yandex.YandexSDK;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexPlatformServices implements PlatformServices {

    private final BillingService billingService = new YandexBillingService();
    private final CloudSaveService cloudSaveService = new YandexCloudSaveService();
    private final LeaderboardService leaderboardService = new LocalLeaderboardService();
    private final AdsService adsService = new YandexAdsService();
    private final I18nService i18nService = new YandexI18nService();

    @Override
    public TargetPlatform getPlatform() {
        return TargetPlatform.HTML_YANDEX;
    }

    @Override
    public void init(PurchaseListener purchaseListener) {
        YandexBridge.exportMethods();
        billingService.setPurchaseListener(purchaseListener);
        billingService.init();
        cloudSaveService.init();
        leaderboardService.init();
        adsService.init();
    }

    @Override
    public void notifyLoadingReady() {
        YandexSDK.notifyLoadingReady();
    }

    @Override
    public BillingService billing() {
        return billingService;
    }

    @Override
    public CloudSaveService cloud() {
        return cloudSaveService;
    }

    @Override
    public LeaderboardService leaderboard() {
        return leaderboardService;
    }
    public I18nService i18n() {
        return i18nService;
    }

    @Override
    public AdsService ads() {
        return adsService;
    }

    @Override
    public boolean isLeaderboardAvailable() {
        return false;
    }

    @Override
    public boolean isAdsAvailable() {
        return true;
    }

    @Override
    public void alert(String msg) {
        YandexSDK.alert(msg);
    }
}
