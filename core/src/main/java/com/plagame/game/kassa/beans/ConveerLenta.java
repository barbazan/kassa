package com.plagame.game.kassa.beans;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.ProductInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 22.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ConveerLenta extends Group {

    private Image background;
    private List<ProductInfo> productList = new ArrayList<>();

    public ConveerLenta() {
        init();
    }

    private void init() {
        clear();
        addBackground();

        float height = background.getHeight();
        float width = background.getWidth();
        setSize(width, height);

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
                boolean isMinus = GameConfig.random.nextBoolean();
                if(isMinus) {
                    delta = -GameConfig.random.nextFloat() * (img.getWidth() * 0.5f);
                } else {
                    delta = img.getWidth() * 0.1f;
                }
                x = prevX + delta;
            }
            img.setPosition(x, y);
            addActor(img);
            prevX = x + img.getWidth();
            prevY = y;
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

    private void addBackground() {
        float lentaHeight = GameApplication.get().screenHeight * 0.30f;
        TextureRegion textureRegion = new TextureRegion(ATLAS_1.findRegion("lenta"));
        background = new Image(textureRegion);
        float scaleX = GameApplication.get().screenWidth / textureRegion.getRegionWidth();
        float scaleY = lentaHeight / textureRegion.getRegionHeight();
        float scale = Math.max(scaleX, scaleY);
        background.setSize(textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
        background.setPosition(0, 0);
        addActor(background);
    }

}
