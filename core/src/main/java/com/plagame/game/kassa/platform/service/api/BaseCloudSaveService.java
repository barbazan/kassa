package com.plagame.game.kassa.platform.service.api;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.utils.FileUtil;
import com.plagame.game.kassa.utils.FileUtilHtml;

/**
 * Created by Дмитрий Малышев on 21.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class BaseCloudSaveService implements CloudSaveService {

    protected static final long SAVE_INTERVAL = 20_000;

    public User loadUserLocal() {
        if(GameApplication.get().isWebGL()) {
            return FileUtilHtml.loadUserLocal();
        } else {
            return FileUtil.loadUserLocal();
        }
    }

    public void saveUserLocal(User user) {
        user.lastSaveTime = System.currentTimeMillis();
        if(GameApplication.get().isWebGL()) {
            FileUtilHtml.saveUserLocal(user);
        } else {
            FileUtil.saveUserLocal(user);
        }
    }

    public void saveUserToServer() {
        if(isConnected()) {
            GameApplication.get().networkWebSocketClient.sendSaveUserPacket();
        }
    }

    public void loadUserFromServer() {
        if(isConnected()) {
            GameApplication.get().networkWebSocketClient.sendLoadUserPacket();
        }
    }

    @Override
    public boolean isConnected() {
        return true; // считаем что по умолчанию сервис сохранения заннекчен, (только у меня где веб-сокеты там реально возвращается законнекчен или нет)
    }
}
