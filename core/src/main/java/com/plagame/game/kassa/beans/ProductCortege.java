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
    private float panelWidth, panelHeight;
    private List<ProductInfo> productList = new ArrayList<>();
    private LinkedList<Image> productImageList = new LinkedList<>();
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
        float startY = height * 0.11f;
        float maxH = height * 1.1f;
        float prevX = startX;
        float prevY = startY;
        for(int i = 0; i < productList.size(); i++) {
            ProductInfo productInfo = productList.get(i);
            Image img = new Image(productInfo.getTextureRegion());
            if(img.getHeight() > maxH) { // если товар больше чем лента по высоте, то высотут товара нужно уменьшить
                float h = maxH;
                float w = img.getWidth() * h / img.getHeight();
                img.setSize(w, h);
            }
            float x = prevX + img.getWidth() * 0.01f;
            float y = startY + GameConfig.random.nextFloat() * height * 0.34f;
            if(prevY > y) {
                float delta;
//                boolean isMinus = GameConfig.random.nextBoolean();
//                if(isMinus) {
//                    delta = -GameConfig.random.nextFloat() * (img.getWidth() * 0.5f);
//                } else {
//                    delta = img.getWidth() * 0.1f;
//                }
                delta = img.getWidth() * 0.1f;
                x = prevX + delta;
            }
            img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
            img.setPosition(x, y);

            img.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    SoundUtil.playClickSound();
                    if(img.getActions().isEmpty()) {
                        currentProduct = img;
                        Group terminal = GameApplication.get().getGameScreen().gameScene.cashRegister.terminal;
                        moveProduct(img, terminal.getX());
                        moveAllProducts();
                    }
                }
            });

            addActor(img);
            prevX = x + img.getWidth();
            prevY = y;
            productImageList.add(img);
        }
    }

    public void nextCustomer() {
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

    private void moveProduct(Image img, float targetX) {
        img.addAction(
            Actions.sequence(
                Actions.moveTo(targetX - img.getWidth() / 2, img.getY(), 0.7f, Interpolation.elasticOut),
                Actions.parallel(
                    Actions.sizeTo(0, 0, 0.4f, Interpolation.linear),
                    Actions.moveTo(0, 0, 0.4f, Interpolation.linear)
                ),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        productImageList.removeFirst();
                        currentProduct = null;
                    }
                })
            )
        );

    }

    private void moveAllProducts() {
        for(int i = 0; i < productImageList.size(); i++) {
            Image img = productImageList.get(i);
            if(i > 0 && currentProduct != null) {
                img.addAction(Actions.moveBy(-currentProduct.getWidth(), 0, 0.7f, Interpolation.linear));
            }
        }
    }
}
