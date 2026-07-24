package com.plagame.game.kassa.beans;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 23.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ProductCortege extends Group {
    private final float panelWidth, panelHeight;
    public final LinkedList<ProductInfo> productList = new LinkedList<>();
    private final LinkedList<Image> productImageList = new LinkedList<>();
    private Image currentProductImage;

    public ProductCortege(float width, float height, List<ProductInfo> newProductList) {
        this.panelWidth = width;
        this.panelHeight = height;
        init(newProductList);
    }

    private void init(List<ProductInfo> newProductList) {
        clear();
        this.productImageList.clear();
        this.productList.clear();
        this.productList.addAll(newProductList);
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

            img.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    try {
                        if(productImageList != null && !productImageList.isEmpty()) {
                            Image first = productImageList.get(0);
                            if(first != null && first == img) {
                                SoundUtil.playClickSound();
                                if(img != currentProductImage) {
                                    currentProductImage = img;
                                    productImageList.removeFirst();
                                    productList.removeFirst();
                                    moveProduct(img, productInfo);
                                    moveAllProducts();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            img.setVisible(false);
            img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
            img.setPosition(x, y + 100);
            float duration = 0.2f;
            float delay = 0.1f * (i + 1);
            img.addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        img.setVisible(true);
                    }
                }),
                Actions.moveBy(0, -100, duration, Interpolation.sine)
            ));

            addActor(img);
            prevX = x + img.getWidth();
            productImageList.add(img);
        }
        currentProductImage = null;
        if(!productImageList.isEmpty()) {
            addForeverScaleAction(productImageList.getFirst());
        }
    }

    public void nextProducts() {
        List<ProductInfo> newProductList = new LinkedList<>();
        for(int i = 0; i < 5; i++) {
            ProductInfo productInfo = ProductInfo.getRandom();
            newProductList.add(productInfo);
        }
        init(newProductList);
    }

    public void resize() {
        init(productList);
    }

    private void moveProduct(Image img, ProductInfo productInfo) {
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
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        GameApplication.get().getGameScreen().gameScene.cashRegister.checkProduct(productInfo);
                    }
                }),
                Actions.parallel(
                    Actions.sizeTo(0, 0, 0.4f, Interpolation.linear),
                    Actions.moveTo(0, 0, 0.4f, Interpolation.linear)
                ),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        currentProductImage = null;
                    }
                })
            )
        );

    }

    private void moveAllProducts() {
        for(int i = 0; i < productImageList.size(); i++) {
            Image img = productImageList.get(i);
            if(currentProductImage != null) {
                img.addAction(Actions.moveBy(-currentProductImage.getWidth(), 0, 0.7f, Interpolation.linear));
                if(i == 0) {
                    addForeverScaleAction(img);
                }
            }
        }
    }

    public boolean isEmpty() {
        return productImageList.isEmpty();
    }

    private void addForeverScaleAction(Actor actor) {
        float scaleDelta = 0.15f;
        float duration = 0.4f;
        actor.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleBy(scaleDelta, scaleDelta, duration, Interpolation.sine),
                Actions.scaleBy(-scaleDelta, -scaleDelta, duration, Interpolation.sine)
            )
        ));
    }
}
