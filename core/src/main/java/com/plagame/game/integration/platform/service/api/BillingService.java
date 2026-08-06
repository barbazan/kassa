package com.plagame.game.integration.platform.service.api;

import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.plagame.game.integration.platform.service.api.model.PurchaseListener;

import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface BillingService {

    /**
     * Перед инициализацией сервиса init() нужно установить purchaseListener, который будет обрабатывать платежи
     * Это для андройд версий, потому что сервис лежит в android модуле, а PurchaseListener в core
     */
    public void setPurchaseListener(PurchaseListener purchaseListener);

    /**
     * Инициализация сервиса.
     * Например загрузка каталога товаров.
     */
    void init();

    /**
     * Купить товар. Это начало покупки, завершается покупка всегда purchaseListener.onPurchased(product);
     */
    void purchase(BillingProduct product);

    /**
     * Купить товар по id.
     */
    void purchase(String productId);

    /**
     * Отключить рекламу.
     */
    void buyHideAdv();

    /**
     * Купить полную версию игры.
     */
    void buyFullVersion();

    /**
     * Проверить незавершенные покупки
     * (Google/RuStore/Yandex делают это по-разному).
     */
    void restorePurchases();

    /**
     * Завершить покупку, ну т.е. начислить её игроку, вызвав purchaseListener.onPurchased(product)
     * Это требуется, когда площадка по колбэку это не делает, например ЮКасса.
     * И это нужео сделать руками, когда от моего сервера пришло событие.
     */
    void consume(BillingProduct product);

    /**
     * Список продуктов.
     */
    List<BillingProduct> getProducts();

    public boolean isCatalogLoaded();

}
