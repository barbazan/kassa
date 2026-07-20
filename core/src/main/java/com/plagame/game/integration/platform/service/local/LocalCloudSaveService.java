package com.plagame.game.integration.platform.service.local;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.BaseCloudSaveService;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

/**
 * Created by Дмитрий Малышев on 21.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class LocalCloudSaveService extends BaseCloudSaveService {

    @Override
    public void init() {
        doLogin(new PlatformCallback<String>() {
            @Override
            public void onSuccess(String value) {
                GameApplication.get().setGameScreen();
            }

            @Override
            public void onError(String error) {
                System.out.println("--------------error = " + error);
            }
        });
    }

    @Override
    public void loadUser(PlatformCallback<User> callback) {
        try {
            User localUser = loadUserLocal();
            if(localUser != null) {
                callback.onSuccess(localUser);
            } else {
                loadUserFromServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void saveUser(User user) {
        saveUserLocal(user);
        if(isConnected()) {
            saveUserToServer();
        }
    }

    @Override
    public boolean isAuthorized() {
        return GameApplication.get().networkWebSocketClient.authorized;
    }

    @Override
    public boolean isConnected() {
        return GameApplication.get().networkWebSocketClient.isConnected();
    }

    @Override
    public void doLogin(PlatformCallback<String> callback) {
        GameApplication.get().networkWebSocketClient.sendLoginPacket();
    }

    @Override
    public String getLogin() {
        return User.get().login;
    }

}
