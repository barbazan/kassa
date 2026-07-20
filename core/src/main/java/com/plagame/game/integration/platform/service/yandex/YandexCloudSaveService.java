package com.plagame.game.integration.platform.service.yandex;

import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.BaseCloudSaveService;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;
import com.plagame.game.kassa.utils.FileUtilHtml;
import com.plagame.game.integration.yandex.YandexBridge;
import com.plagame.game.integration.yandex.YandexPlayer;

/**
 * Created by Дмитрий Малышев on 21.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexCloudSaveService extends BaseCloudSaveService {
    public YandexPlayer yandexPlayer = new YandexPlayer();

    @Override
    public void init() {
        tryLoadCloudUser();
    }

    @Override
    public void loadUser(PlatformCallback<User> callback) {
        try {
            tryLoadCloudUser();
        } catch (Exception e) {
//            YandexSDK.alert("---loadUser e.getMessage()" + e.getMessage());
            e.printStackTrace();
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void saveUser(User user) {
        saveUserLocal(user);
//        YandexSDK.alert("---------------------- isAuthorized()  " + isAuthorized());
        if (isAuthorized()) {
//            YandexSDK.alert("---------------------- YandexBridge.saveUserToCloud  ");
            YandexBridge.saveUserToCloud(user);
        }
    }

    public User loadUserLocal() {
        return FileUtilHtml.loadUserLocalAsBytes();
    }

    public void saveUserLocal(User user) {
        FileUtilHtml.saveUserLocalAsBytes(user);
        user.lastSaveTime = System.currentTimeMillis();
    }

    public boolean isAuthorized() {
        return yandexPlayer.isAuthorized();
    }

    public String getLogin() {
        return User.get().login;
    }

    public void doLogin(PlatformCallback<String> callback) {
        if(!isAuthorized()) {
            YandexBridge.authorizePlayer(data -> {
                YandexPlayer player = new YandexPlayer(data);
                yandexPlayer = player;
                if (player.id != null && !player.id.isEmpty()) {
                    User.get().login = player.login;
//                    YandexSDK.alert("Теперь авторизован: " + player.id + " (" + player.login + ")");
                    callback.onSuccess("Теперь авторизован: " + player.id + " (" + player.login + ")");
                } else {
//                    YandexSDK.alert("Авторизация отменена");
                    callback.onError("Авторизация отменена");
                }
            });
        }
    }

    public void tryLoadCloudUser() {
        YandexBridge.checkAuth(data -> {
//            YandexSDK.alert("tryLoadCloudUser: data = " + data);
            YandexPlayer player = new YandexPlayer(data);
            if (player.id != null && !player.id.isEmpty()) {
//                YandexSDK.alert("Авторизован: " + player.id + " (" + player.login + ")");
                yandexPlayer.id = player.id;
                yandexPlayer.login = player.login;
                User.get().login = player.login;
//                YandexSDK.alert("Авторизован: getLogin() = " + GameApplication.get().platform.cloud().getLogin());
                YandexBridge.loadUserFromCloud();
            } else {
                System.out.println("Игрок не авторизован");
//                YandexSDK.alert("Игрок не авторизован");
                // todo предложить авторизваться
            }
        });
    }

}
