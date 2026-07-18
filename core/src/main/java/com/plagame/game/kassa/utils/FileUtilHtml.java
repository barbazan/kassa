package com.plagame.game.kassa.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 14.02.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public class FileUtilHtml {

    private static final String USER_PREFERENCES_FILENAME = "kassa_yandex_user_preferences";
    private static final String USER_KEY = "kassa_yandex_user_data";

    public static void saveUserLocal(User user) {
        try {
            Preferences prefs = Gdx.app.getPreferences(USER_PREFERENCES_FILENAME);

            String json = user.toJson();

            prefs.putString(USER_KEY, json);
            prefs.flush();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static User loadUserLocal() {
        try {
            Preferences prefs = Gdx.app.getPreferences(USER_PREFERENCES_FILENAME);

            String json = prefs.getString(USER_KEY, "");

            if (json == null || json.isEmpty()) {
                return null;
            }

            return User.fromJson(json);

        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    // todo это вернуть под яндекс иначе не сохраняется
    public static void saveUserLocalAsBytes(User user) {
        try {
            Preferences prefs = Gdx.app.getPreferences(USER_PREFERENCES_FILENAME);

            byte[] data = user.serialize();

            String encoded = Base64Coder.encodeLines(data);

            prefs.putString(USER_KEY, encoded);
            prefs.flush();

        } catch (Throwable t) {
//            YandexSDK.alert("---- saveUserLocalAsBytes()    t.getMessage()" + t.getMessage());
            t.printStackTrace();
        }
    }

    public static User loadUserLocalAsBytes() {
        try {
            Preferences prefs = Gdx.app.getPreferences(USER_PREFERENCES_FILENAME);

            String encoded = prefs.getString(USER_KEY, "");

            if (encoded == null || encoded.isEmpty()) {
                return null;
            }

            byte[] data = Base64Coder.decodeLines(encoded);

            return User.deserialize(data);

        } catch (Throwable t) {
//            YandexSDK.alert("---- loadUserLocalAsBytes()    t.getMessage()" + t.getMessage());
            t.printStackTrace();
            return null;
        }
    }

    public static void clearUser() {
        try {
            Preferences prefs = Gdx.app.getPreferences(USER_PREFERENCES_FILENAME);

            prefs.clear();
            prefs.flush();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
