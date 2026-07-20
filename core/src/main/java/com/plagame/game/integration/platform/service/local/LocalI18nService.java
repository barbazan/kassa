package com.plagame.game.integration.platform.service.local;

import java.util.Locale;
import com.plagame.game.integration.platform.service.api.BaseI18nService;

/**
 * Created by Дмитрий Малышев on 22.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class LocalI18nService extends BaseI18nService {

    public String getLang() {
        return "ru";
    }
    public Locale getGameLocale() {
        return new Locale(getLang());
    }

}
