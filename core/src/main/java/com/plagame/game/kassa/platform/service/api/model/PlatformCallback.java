package com.plagame.game.kassa.platform.service.api.model;

/**
 * Created by Дмитрий Малышев on 20.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface PlatformCallback<T> {
    void onSuccess(T value);
    void onError(String error);
}
