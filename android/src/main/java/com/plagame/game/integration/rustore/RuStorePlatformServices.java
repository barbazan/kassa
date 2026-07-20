package com.plagame.game.integration.rustore;

import android.app.Activity;

import com.plagame.game.integration.platform.service.api.AdsService;
import com.plagame.game.integration.platform.service.api.BillingService;
import com.plagame.game.integration.platform.service.api.CloudSaveService;
import com.plagame.game.integration.platform.service.api.I18nService;
import com.plagame.game.integration.platform.service.api.LeaderboardService;
import com.plagame.game.integration.platform.service.api.PlatformServices;
import com.plagame.game.integration.platform.service.api.model.PurchaseListener;
import com.plagame.game.integration.platform.service.api.model.TargetPlatform;
import com.plagame.game.integration.platform.service.local.LocalCloudSaveService;
import com.plagame.game.integration.platform.service.local.LocalI18nService;
import com.plagame.game.integration.platform.service.local.LocalLeaderboardService;
import com.plagame.game.integration.platform.service.yookassa.YookassaBillingService;

public class RuStorePlatformServices implements PlatformServices {
    private final BillingService billingService;
    private final CloudSaveService cloudSaveService;
    private final I18nService i18nService;
    private final LeaderboardService leaderboardService;

    public RuStorePlatformServices(Activity activity) {
        this.billingService = new YookassaBillingService();
        this.cloudSaveService = new LocalCloudSaveService();
        this.i18nService = new LocalI18nService();
        this.leaderboardService = new LocalLeaderboardService();

    }

    @Override
    public TargetPlatform getPlatform() {
        return TargetPlatform.ANDROID_RUSTORE;
    }

    public void init(PurchaseListener purchaseListener) {
        billingService.setPurchaseListener(purchaseListener);
        billingService.init();
        leaderboardService.init();
    }

    @Override
    public void notifyLoadingReady() {

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

    @Override
    public AdsService ads() {
        return null;
    }

    @Override
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

    }
}
