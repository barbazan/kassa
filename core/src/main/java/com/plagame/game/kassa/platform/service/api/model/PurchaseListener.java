package com.plagame.game.kassa.platform.service.api.model;

/**
 * Created by Дмитрий Малышев on 19.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface PurchaseListener {

    /**
     * Вызывается, когда покупка успешно завершена
     * и подтверждена платформой (consume/acknowledge/confirm уже сделан).
     */
    void onPurchased(BillingProduct product);

}
