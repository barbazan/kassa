package com.plagame.game.kassa.yandex;

import com.badlogic.gdx.utils.Base64Coder;
import com.plagame.game.kassa.beans.User;

import java.util.function.Consumer;

/**
 * Created by Дмитрий Малышев on 20.08.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexBridge {
    // Интерфейс для обратного вызова
    public interface PlayerCallback {
        void onResult(String data);
    }

    public static void exportMethods() {
        exportOnUserLoaded();
        registerCallbacks();
    }

    private static native void exportOnUserLoaded() /*-{
        $wnd.YandexBridge_onUserLoaded = $entry(@com.plagame.game.kassa.yandex.YandexBridge::onUserLoaded(Ljava/lang/String;));
    }-*/;

    public static native void registerCallbacks() /*-{
        $wnd.YandexBridge_onUserLoaded = $entry(@com.plagame.game.kassa.yandex.YandexBridge::onUserLoaded(Ljava/lang/String;));
    }-*/;

    // --- JS методы ---
    public static native void saveUserToCloudNative(String data) /*-{
        $wnd.saveUserToCloud(data);
    }-*/;

    public static native void loadUserFromCloud() /*-{
        $wnd.loadUserFromCloud();
    }-*/;

    // --- Удобные методы для игры ---
    public static void saveUserToCloud(User user) {
//        YandexSDK.alert("saveUserToCloud user = " + user);
        saveUserToCloudNative(serializeUser(user));
    }

    // Проверка (без диалога)
    public static native void checkAuth(PlayerCallback callback) /*-{
        $wnd.getPlayerString(function(data) {
            callback.@com.plagame.game.kassa.yandex.YandexBridge.PlayerCallback::onResult(Ljava/lang/String;)(data);
        });
    }-*/;

    // Авторизация (с диалогом)
    public static native void authorizePlayer(PlayerCallback callback) /*-{
        $wnd.authorizePlayer(function(data) {
            callback.@com.plagame.game.kassa.yandex.YandexBridge.PlayerCallback::onResult(Ljava/lang/String;)(data);
        });
    }-*/;

    // --- JS вызовет этот метод когда данные придут ---
    // Колбэк из JS
    public static void onUserLoaded(String data) {
        try {
//            YandexSDK.alert("onUserLoaded data = " + data);

            if (data == null || data.isEmpty()) {
                return;
            }

            User user = deserializeUser(data);

//            YandexSDK.alert("user = " + user);

            if (user != null) {
                User.get().applyCloudUser(user);
            }

            YandexSDK.checkPendingPurchases();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // --- Сериализация User в строку ---
    public static String serializeUser(User user) {
        try {
            byte[] bytes = user.serialize();

            return Base64Coder.encodeLines(bytes);

        } catch (Throwable t) {
            t.printStackTrace();
//            YandexSDK.alert("----error---serializeUser--- " + t.getMessage());
            return "";
        }
    }

    // --- Десериализация строки обратно в User ---
    public static User deserializeUser(String data) {
        try {
            if (data == null || data.isEmpty()) {
                return null;
            }

            byte[] bytes = Base64Coder.decodeLines(data);

            return User.deserialize(bytes);

        } catch (Throwable t) {
            t.printStackTrace();
//            YandexSDK.alert("----error---deserializeUser--- " + t.getMessage());
            return null;
        }
    }

    public static native void getYandexPlayer(Consumer<YandexPlayer> onPlayer) /*-{
        if ($wnd.ysdk) {
            $wnd.ysdk.getPlayer().then(function(player) {
                var javaPlayer = @com.plagame.game.kassa.yandex.YandexPlayer::new()();
                javaPlayer.id = player.id;
                javaPlayer.login = player.login;
                onPlayer.@java.util.function.Consumer::accept(Ljava/lang/Object;)(javaPlayer);
            });
        }
    }-*/;

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return 0; }
    }

    private static long parseLongSafe(String s) {
        try { return Long.parseLong(s); }
        catch (NumberFormatException e) { return 0; }
    }

    private static boolean parseBooleanSafe(String s) {
        try { return Boolean.parseBoolean(s); }
        catch (Exception e) { return false; }
    }

}
