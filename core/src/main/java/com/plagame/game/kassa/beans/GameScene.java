package com.plagame.game.kassa.beans;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.CustomerInfo;
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.AssetUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Дмитрий Малышев on 10.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameScene extends Group {
    private Image background;
    public CashRegister cashRegister;
    public ConveerLenta conveerLenta;
    public ProductCortege productCortege;
    public CustomerCortege customerCortege;

    public GameScene() {
        nextDay();
    }

    private void init(List<CustomerInfo> customerList, List<ProductInfo> productList) {
        clear();
        setSize(GameApplication.get().screenWidth, GameApplication.get().screenHeight);
        addBackground();
        addCustomerCortege(customerList);
        addConveerLenta();
        addCashRegister();
        addProductCortege(productList);
    }

    private void addBackground() {
        TextureRegion textureRegion = new TextureRegion(AssetUtil.getTexture("images/game_bg.jpg"));
        background = new Image(textureRegion);
        float scaleX = GameApplication.get().screenWidth / textureRegion.getRegionWidth();
        float scaleY = GameApplication.get().screenHeight / textureRegion.getRegionHeight();
        float scale = Math.max(scaleX, scaleY);
        background.setSize(textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
        background.setPosition(GameApplication.get().screenWidth / 2 - background.getWidth() / 2, GameApplication.get().screenHeight / 2 - background.getHeight() / 2);
        addActor(background);
    }

    private void addConveerLenta() {
        conveerLenta = new ConveerLenta();
        conveerLenta.setPosition(GameApplication.get().screenWidth / 2 - conveerLenta.getWidth() / 2, 0);
        addActor(conveerLenta);
    }

    private void addCashRegister() {
        if(cashRegister != null) {
            cashRegister = new CashRegister(cashRegister);
        } else {
            cashRegister = new CashRegister();
        }
        if(GameApplication.get().isPortrait()) {
            cashRegister.setPosition(- cashRegister.getWidth() + cashRegister.terminalWidth * 1.1f, -cashRegister.getHeight() + cashRegister.terminalHeight * 1.15f);
        } else {
            cashRegister.setPosition(cashRegister.terminalWidth * 0.1f, -cashRegister.getHeight() + cashRegister.terminalHeight * 1.15f);
        }
        addActor(cashRegister);
    }

    private void addCustomerCortege(List<CustomerInfo> customerList) {
        customerCortege = new CustomerCortege(customerList);
        customerCortege.setPosition(GameApplication.get().screenWidth / 2 - customerCortege.getWidth() / 2, 0);
        addActor(customerCortege);
    }

    private void addProductCortege(List<ProductInfo> productList) {
        productCortege = new ProductCortege(conveerLenta.getWidth(), conveerLenta.getHeight(), productList);
        productCortege.setPosition(GameApplication.get().screenWidth / 2 - conveerLenta.getWidth() / 2, 0);
        addActor(productCortege);
    }

    public void nextDay() {
        List<CustomerInfo> customerList = createCustomerList();
        List<ProductInfo> productList = createProductList();
        init(customerList, productList);
    }

    public void resize() {
        clear();
        init(customerCortege.customerList, productCortege.productList);
        if(GameApplication.get().getGameScreen().gameScene.cashRegister.isProcessPayment) {
            GameApplication.get().getGameScreen().gameScene.customerCortege.moveCameraSlowly(GameApplication.get().getGameScreen().gameScene.cashRegister.cardType > 0);
        }
    }

    private List<CustomerInfo> createCustomerList() {
        Set<CustomerInfo> set = new HashSet<>();
        int count = 4 + GameConfig.random.nextInt(4);
        while(set.size() < count) {
            try {
                set.add(CustomerInfo.getRandom());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(set);
    }

    private List<ProductInfo> createProductList() {
        List<ProductInfo> productList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            ProductInfo productInfo = ProductInfo.getRandom();
            productList.add(productInfo);
        }
        return productList;
    }
}
