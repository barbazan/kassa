package com.plagame.game.kassa.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.integration.platform.service.api.PlatformServices;
import com.plagame.game.integration.platform.service.api.model.TargetPlatform;
import com.plagame.game.integration.platform.service.local.LocalPlatformServices;
import com.plagame.game.integration.platform.service.yandex.YandexPlatformServices;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {

    @Override
        public GwtApplicationConfiguration getConfig () {
            // Resizable application, uses available space in browser with no padding:
            GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
            cfg.padVertical = 0;
            cfg.padHorizontal = 0;
            return cfg;
            // If you want a fixed size application, comment out the above resizable section,
            // and uncomment below:
            //return new GwtApplicationConfiguration(640, 480);
        }

        @Override
        public ApplicationListener createApplicationListener() {
            PlatformServices platformServices = createPlatformServices(GameConfig.TARGET_PLATFORM);
            return new GameApplication(platformServices);
        }

        private PlatformServices createPlatformServices(TargetPlatform targetPlatform) {
            switch (targetPlatform) {
                case HTML_YANDEX:
                    return new YandexPlatformServices();
//                case HTML_XSOLLA:
//                    return new XsollaPlatformServices(this);
                case LOCAL:
                default:
                    return new LocalPlatformServices();
            }
        }

}
