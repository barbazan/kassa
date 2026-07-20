package com.plagame.game.integration.platform.service.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Created by Дмитрий Малышев on 22.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class BaseI18nService implements I18nService {

    private static String CUR_LANG = "";
    private static I18NBundle GAME_I18N_BUNDLE;

    public String get(String key) {
        if(!CUR_LANG.equals(getLang())) {
            CUR_LANG = getLang();
            GAME_I18N_BUNDLE = I18NBundle.createBundle(Gdx.files.internal("i18n/strings"), getGameLocale());
        }
        return GAME_I18N_BUNDLE.get(key);
    }

}
