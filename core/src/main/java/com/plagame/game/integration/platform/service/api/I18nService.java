package com.plagame.game.integration.platform.service.api;

import java.util.Locale;

/**
 * Created by Дмитрий Малышев on 22.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface I18nService {

    String getLang();

    Locale getGameLocale();

    String get(String key);

}
