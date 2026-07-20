package com.plagame.game.integration.platform.service.api;

import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface CloudSaveService {

    void init();
    void loadUser(PlatformCallback<User> callback);
    void saveUser(User user);
    void doLogin(PlatformCallback<String> callback);
    String getLogin();
    boolean isAuthorized();
    boolean isConnected();
}
