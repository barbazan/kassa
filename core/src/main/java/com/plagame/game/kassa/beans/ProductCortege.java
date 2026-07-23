package com.plagame.game.kassa.beans;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 23.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ProductCortege extends Group {
    private final float panelWidth, panelHeight;
    private final List<ProductInfo> productList = new ArrayList<>();
    private final LinkedList<Image> productImageList = new LinkedList<>();
    private Image currentProduct;

    public ProductCortege(float width, float height) {
        this.panelWidth = width;
        this.panelHeight = height;
        init();
    }

    private void init() {
        clear();
        float width = panelWidth;
        float height = panelHeight;
        float startX;
        if(GameApplication.get().isPortrait()) {
            startX = width * 0.48f;
        } else {
            startX = width * 0.38f;
        }
        float startY = height * 0.3f;
        float heightDelta = height * 0.1f;
        float maxH = height * 1.1f;
        float prevX = startX;
        for(int i = 0; i < productList.size(); i++) {
            ProductInfo productInfo = productList.get(i);
            Image img = new Image(productInfo.getTextureRegion());
            if(img.getHeight() > maxH) { // если товар больше чем лента по высоте, то высотут товара нужно уменьшить
                float w = img.getWidth() * maxH / img.getHeight();
                img.setSize(w, maxH);
            }
            float x = prevX + img.getWidth() * 0.01f;
            float mult = GameConfig.random.nextFloat();
            float y = startY + (GameConfig.random.nextBoolean() ? -heightDelta : heightDelta) * mult;
            img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
            img.setPosition(x, y);

            img.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    try {
                        if(productImageList != null && !productImageList.isEmpty()) {
                            Image first = productImageList.get(0);
                            if(first != null && first == img) {
                                SoundUtil.playClickSound();
                                if(img != currentProduct) {
                                    currentProduct = img;
                                    productImageList.remove(img);
                                    moveProduct(img);
                                    moveAllProducts();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            addActor(img);
            prevX = x + img.getWidth();
            productImageList.add(img);
            currentProduct = null;
        }
    }

    public void nextCustomer() {
        productImageList.clear();
        productList.clear();
        for(int i = 0; i < 5; i++) {
            ProductInfo productInfo = ProductInfo.getRandom();
            productList.add(productInfo);
        }
        init();
    }

    public void resize() {
        init();
    }

    private void moveProduct(Image img) {
        Group terminal = GameApplication.get().getGameScreen().gameScene.cashRegister.terminal;
        float deltaX;
        if(GameApplication.get().isPortrait()) {
            deltaX = img.getParent().getX() + img.getX();
        } else {
            deltaX = img.getX() - terminal.getX() + img.getWidth() / 2;
        }
        img.clearActions();
        img.addAction(
            Actions.sequence(
                Actions.moveBy(-deltaX, 0, 0.7f, Interpolation.elasticOut),
                Actions.parallel(
                    Actions.sizeTo(0, 0, 0.4f, Interpolation.linear),
                    Actions.moveTo(0, 0, 0.4f, Interpolation.linear)
                ),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        currentProduct = null;
                    }
                })
            )
        );

    }

    private void moveAllProducts() {
        for(int i = 0; i < productImageList.size(); i++) {
            Image img = productImageList.get(i);
            if(currentProduct != null) {
                img.addAction(Actions.moveBy(-currentProduct.getWidth(), 0, 0.7f, Interpolation.linear));
            }
        }
    }
}
