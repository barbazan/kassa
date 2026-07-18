package com.plagame.game.kassa.yandex;

/**
 * Created by Дмитрий Малышев on 20.08.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public interface PurchaseCallback {
    void onSuccess(YandexModels.Purchase purchase);
}
