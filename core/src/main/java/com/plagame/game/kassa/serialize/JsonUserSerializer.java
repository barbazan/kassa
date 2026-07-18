package com.plagame.game.kassa.serialize;

import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 04.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class JsonUserSerializer {
    private static final int SAVE_VERSION = 1;

    public static String serialize(User user) {
        try {
            return GameConfig.JSON.toJson(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User deserialize(String json) {
        try {
            return GameConfig.JSON.fromJson(User.class, json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
