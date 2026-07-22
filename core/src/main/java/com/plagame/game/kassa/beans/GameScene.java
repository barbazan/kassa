package com.plagame.game.kassa.beans;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.utils.AssetUtil;

/**
 * Created by Дмитрий Малышев on 10.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameScene extends Group {
    private Image background;
    public CashRegister cashRegister;
    public ConveerLenta conveerLenta;
    public ProductCortege productCortege;

    public GameScene() {
        init();
    }

    private void init() {
        setSize(GameApplication.get().screenWidth, GameApplication.get().screenHeight);
        addBackground();
        addConveerLenta();
        addCashRegister();
        addProductCortege();
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
        cashRegister = new CashRegister();
        if(GameApplication.get().isPortrait()) {
            cashRegister.setPosition(- cashRegister.getWidth() + cashRegister.terminalWidth * 1.1f, -cashRegister.getHeight() + cashRegister.terminalHeight * 1.15f);
        } else {
            cashRegister.setPosition(cashRegister.terminalWidth * 0.1f, -cashRegister.getHeight() + cashRegister.terminalHeight * 1.15f);
        }
        addActor(cashRegister);
    }

    private void addProductCortege() {
        productCortege = new ProductCortege(conveerLenta.getWidth(), conveerLenta.getHeight());
        productCortege.setPosition(GameApplication.get().screenWidth / 2 - conveerLenta.getWidth() / 2, 0);
        addActor(productCortege);
    }

    public void resize() {
        clear();
        init();
    }

}
