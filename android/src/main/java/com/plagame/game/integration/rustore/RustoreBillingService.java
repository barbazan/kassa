package com.plagame.game.integration.rustore;

import android.app.Activity;

import com.plagame.game.integration.platform.service.api.BaseBillingService;
import com.plagame.game.integration.platform.service.api.model.BillingProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class RustoreBillingService extends BaseBillingService {

    private final Activity activity;

    public RustoreBillingService(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void init() {
        loadCatalog();
    }

    @Override
    public void purchase(BillingProduct product) {
        purchaseInternal(product);
    }

    @Override
    public void buyHideAdv() {
        //todo
    }

    @Override
    public void buyFullVersion() {
        //todo
    }

    @Override
    public void restorePurchases() {

        checkPurchases();

    }

    @Override
    public List<BillingProduct> getProducts() {
        return new ArrayList<>(products.values());
    }

    //--------------------------------------------------------------------

    private void purchaseInternal(BillingProduct product) {

//        rustoreClient.purchase(activity, product.getId());

    }

    //--------------------------------------------------------------------

    private void loadCatalog() {

        // RuStore.getProducts()
        catalogLoaded = true;
    }

    //--------------------------------------------------------------------

    private void checkPurchases() {

        // RuStore.getPurchases()

    }

}
