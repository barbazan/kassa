package com.plagame.game.kassa.yandex;

/**
 * Created by Дмитрий Малышев on 22.08.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexPlayer {
    public String id;     // соответствует player.id из JS
    public String login;  // соответствует player.login из JS

    public YandexPlayer() {
        super();
    }

    public YandexPlayer(String data) {
        super();
        if (data != null) {
            String[] parts = data.split("\\|", 2);
            id = parts[0];
            login = parts.length > 1 ? parts[1] : "";
        }
    }

    public boolean isAuthorized() {
        return id != null && !id.isEmpty();
    }

}
