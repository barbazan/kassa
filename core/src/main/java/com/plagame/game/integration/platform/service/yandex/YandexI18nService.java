package com.plagame.game.integration.platform.service.yandex;

import java.util.Locale;
import com.plagame.game.integration.platform.service.api.BaseI18nService;

/**
 * Created by Дмитрий Малышев on 22.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexI18nService extends BaseI18nService {

    public String getLang() {
        try {
            return getLangInternal();
        } catch (Throwable t) {
//            t.printStackTrace();
            return "ru";
        }
    }
    public Locale getGameLocale() {
        String lang = getLang();
        if(lang.equals("ru") || lang.equals("en") || lang.equals("es") || lang.equals("tr")) {
            return new Locale(lang);
        } else {
            return new Locale("ru");
        }
    }

    // Вызов JavaScript напрямую через JSNI
    private native String getLangInternal() /*-{
        return $wnd.ysdk_lang || "ru";
    }-*/;


}
