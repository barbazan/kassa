package com.plagame.game.integration.xsolla;

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

/**
 * Created by Дмитрий Малышев on 25.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class XsollaPlatformServices implements PlatformServices {

    private final BillingService billingService;
    private final CloudSaveService cloudSaveService;
    private final I18nService i18nService;
//    private final AdsService adsService;
    private final LeaderboardService leaderboardService;

    public XsollaPlatformServices(Activity activity) {
        this.billingService = new XsollaBillingService(activity);
        this.cloudSaveService = new LocalCloudSaveService();
//        this.adsService = new GooglePlayAdsService(activity);
        this.i18nService = new LocalI18nService();
        this.leaderboardService = new LocalLeaderboardService();
    }

    @Override
    public void init(PurchaseListener purchaseListener) {
        billingService.setPurchaseListener(purchaseListener);
        billingService.init();
        leaderboardService.init();
//        adsService.init();
    }

    @Override
    public TargetPlatform getPlatform() {
        return TargetPlatform.ANDROID_XSOLLA;
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
//        return adsService;
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
