package com.plagame.game.kassa.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.android.play.GooglePlayPlatformServices;
import com.plagame.game.kassa.android.rustore.RuStorePlatformServices;
import com.plagame.game.kassa.android.xsolla.XsollaPlatformServices;
import com.plagame.game.kassa.platform.service.api.PlatformServices;
import com.plagame.game.kassa.platform.service.api.model.TargetPlatform;
import com.plagame.game.kassa.platform.service.local.LocalPlatformServices;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        CommonWebSockets.initiate();
        System.out.println("--------------- CommonWebSockets.initiate() -----------------------");

        PlatformServices platformServices = createPlatformServices(GameConfig.TARGET_PLATFORM);
        GameApplication gameApplication = new GameApplication(platformServices);
        initialize(gameApplication, configuration);
    }

    private PlatformServices createPlatformServices(TargetPlatform targetPlatform) {
        switch (targetPlatform) {
            case ANDROID_GOOGLE_PLAY:
                return new GooglePlayPlatformServices(this);
            case ANDROID_XSOLLA:
                return new XsollaPlatformServices(this);
            case ANDROID_RUSTORE:
                return new RuStorePlatformServices(this);
            case LOCAL:
            default:
                return new LocalPlatformServices();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        System.out.println("----------------onNewIntent: " + intent);
    }
}
