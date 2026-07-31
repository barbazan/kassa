package com.plagame.game.kassa.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Logger;
import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 14.02.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public class FileUtil {

    static Logger logger = new Logger("FileUtil", Logger.INFO);

    private static final String USER_FILENAME = "kassa_user.json";

    public static void saveUserLocal(User user) {
        try {
            FileHandle file = Gdx.files.local(USER_FILENAME);

            String json = user.toJson();

            System.out.println("----------------------------FileUtil.saveUserLocal json.length() = " + json.length());
//            System.out.println("----------------------------FileUtil.saveUserLocal json = " + json);

            file.writeString(json, false);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static User loadUserLocal() {
        try {
            FileHandle file = Gdx.files.local(USER_FILENAME);

            if (!file.exists()) {
                return null;
            }

            String json = file.readString();

            System.out.println("----------------------------FileUtil.loadUserLocal json.length() = " + json.length());
//            System.out.println("----------------------------FileUtil.loadUserLocal json = " + json);

            return User.fromJson(json);

        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Can`t load user local. Error: " + t.getMessage());
            return null;
        }
    }
}
