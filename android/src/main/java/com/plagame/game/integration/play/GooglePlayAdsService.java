package com.plagame.game.integration.play;

import static com.plagame.game.integration.play.GoogleAdsConfig.GOOGLE_ADS_INTERSTITIAL_ID;
import static com.plagame.game.integration.play.GoogleAdsConfig.GOOGLE_ADS_REWARDED_VIDEO_ID;

import android.app.Activity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.plagame.game.integration.platform.service.api.BaseAdsService;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

/**
 * Created by Дмитрий Малышев on 23.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GooglePlayAdsService extends BaseAdsService {
    private final Activity activity;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private boolean initialized = false;

    public GooglePlayAdsService(Activity activity) {
        this.activity = activity;
    }

    // =========================================================
    // INIT
    // =========================================================

    public void init() {
        RequestConfiguration configuration =
            new RequestConfiguration.Builder()
//                .setTestDeviceIds(Arrays.asList(TEST_DEVICE_ID))
                .build();

        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(activity, status -> {
            System.out.println("======================= MobileAds initialized =======================");
            initialized = true;
            loadInterstitial();
            loadRewarded();
        });
    }

    // =========================================================
    // COOLDOWN
    // =========================================================

    @Override
    public boolean isFullscreenAdReady() {
        return interstitialAd != null && !isFullscreenAdCooldown();
    }

    @Override
    public boolean isVideoAdReady() {
        return rewardedAd != null && !isVideoAdCooldown();
    }

    // =========================================================
    // INTERSTITIAL
    // =========================================================

    @Override
    public void showFullscreenAdv() {
        System.out.println("initialized = " + initialized);
        System.out.println("interstitialAd = " + interstitialAd);
        if (!initialized)
            return;

        if (isFullscreenAdCooldown())
            return;

        activity.runOnUiThread(() -> {
            if (interstitialAd != null) {
                interstitialAd.show(activity);
                lastFullscreenAdTime = System.currentTimeMillis();
            } else {
                System.out.println("Interstitial not loaded");
                loadInterstitial();
            }
        });
    }

    private void loadInterstitial() {
        System.out.println("--------------Interstitial ID = " + GOOGLE_ADS_INTERSTITIAL_ID);
        activity.runOnUiThread(() -> {
            AdRequest request = new AdRequest.Builder().build();
            InterstitialAd.load(
                activity,
                GOOGLE_ADS_INTERSTITIAL_ID,
                request,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd ad) {
                        System.out.println("----------------onAdLoaded--------ad = " + ad);
                        onInterstitialLoaded(ad);
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError error) {
                        interstitialAd = null;
                        System.out.println("code = " + error.getCode());
                        System.out.println( "message = " + error.getMessage());
                        System.out.println("domain = " + error.getDomain());
                        System.out.println("response = " + error.getResponseInfo());
                    }
                });
        });
    }

    // =========================================================
    // REWARDED
    // =========================================================

    @Override
    public void showRewardedVideo(PlatformCallback<String> callback) {
        if (!initialized) return;
        if (isVideoAdCooldown()) return;
        if (rewardedAd == null) return;

        activity.runOnUiThread(() -> {
            rewardedAd.show(activity, rewardItem -> {
                lastRewardedVideoTime = System.currentTimeMillis();
                callback.onSuccess("reward");
                loadRewarded(); // preload next
            });
        });

    }

    private void loadRewarded() {
        AdRequest request = new AdRequest.Builder().build();
        RewardedAd.load(
            activity,
            GOOGLE_ADS_REWARDED_VIDEO_ID,
            request,
            new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(RewardedAd ad) {
                    System.out.println("----------------loadRewarded-------video-ad = " + ad);
                    onRewardedLoaded(ad);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError error) {
                    System.out.println("----------------FailedToLoad-------video-ad = " + null);
                    System.out.println("code = " + error.getCode());
                    System.out.println( "message = " + error.getMessage());
                    System.out.println("domain = " + error.getDomain());
                    System.out.println("response = " + error.getResponseInfo());
                    rewardedAd = null;
                }
            }
        );
    }

    private void onInterstitialLoaded(InterstitialAd ad) {
        interstitialAd = ad;
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {

            @Override
            public void onAdShowedFullScreenContent() {
                System.out.println("Interstitial shown");
                lastFullscreenAdTime = System.currentTimeMillis();
                interstitialAd = null;
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                System.out.println("Interstitial dismissed");
                loadInterstitial();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                System.out.println("Interstitial failed: " + adError.getMessage());
                interstitialAd = null;
                loadInterstitial();
            }
        });
    }

    private void onRewardedLoaded(RewardedAd ad) {
        rewardedAd = ad;
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {

            @Override
            public void onAdShowedFullScreenContent() {
                lastRewardedVideoTime = System.currentTimeMillis();
                rewardedAd = null;
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                loadRewarded();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                rewardedAd = null;
                loadRewarded();
            }
        });
    }
}
