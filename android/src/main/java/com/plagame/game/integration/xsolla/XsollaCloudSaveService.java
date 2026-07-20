package com.plagame.game.integration.xsolla;

import android.app.Activity;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.BaseCloudSaveService;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

/**
 * Created by Дмитрий Малышев on 25.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class XsollaCloudSaveService extends BaseCloudSaveService {
    private final Activity activity;

    public XsollaCloudSaveService(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void init() {

    }

    @Override
    public void loadUser(PlatformCallback<User> callback) {
        try {
            User localUser = loadUserLocal();
            callback.onSuccess(localUser);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.getMessage());
        }
    }


    @Override
    public void saveUser(User user) {
        saveUserLocal(user);
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
        GameApplication.get().networkWebSocketClient.sendLoadUserPacket();
    }

    @Override
    public String getLogin() {
        return User.get().login;
    }
}
