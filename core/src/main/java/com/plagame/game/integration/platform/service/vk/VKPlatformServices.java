package com.plagame.game.integration.platform.service.vk;

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
import com.plagame.game.integration.vk.VKSDK;

public class VKPlatformServices implements PlatformServices {
    private final BillingService billingService = new VKBillingService();
    private final CloudSaveService cloudService = new LocalCloudSaveService();
    private final LeaderboardService leaderboardService = new LocalLeaderboardService();
    private final I18nService i18nService = new LocalI18nService();

    @Override
    public TargetPlatform getPlatform() { return TargetPlatform.HTML_VK; }

    @Override
    public void init(PurchaseListener purchaseListener) {
        billingService.setPurchaseListener(purchaseListener);
        billingService.init();
        cloudService.init();
        leaderboardService.init();
    }

    @Override
    public void notifyLoadingReady() {
        // VKWebAppInit is sent by VKSDK when billing is initialized.
    }

    @Override
    public BillingService billing() { return billingService; }
    @Override
    public CloudSaveService cloud() { return cloudService; }
    @Override
    public LeaderboardService leaderboard() { return leaderboardService; }
    @Override
    public AdsService ads() { return null; }
    @Override
    public I18nService i18n() { return i18nService; }
    @Override
    public boolean isLeaderboardAvailable() { return true; }
    @Override
    public boolean isAdsAvailable() { return false; }
    @Override
    public void alert(String msg) { VKSDK.alert(msg); }
}