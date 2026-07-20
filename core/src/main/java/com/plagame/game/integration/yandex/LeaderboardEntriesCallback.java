package com.plagame.game.integration.yandex;

/**
 * Created by Дмитрий Малышев on 25.11.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public interface LeaderboardEntriesCallback {
    void onSuccess(String json);  // JSON строка ответа от YaSDK
    void onError(String error);
}
