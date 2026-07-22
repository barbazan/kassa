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

    private List<ProductInfo> productList = new ArrayList<>();

    public ConveerLenta() {
        init();
    }

    private void init() {
        clear();
        TextureRegion lentaTexture = ATLAS_1.findRegion("lenta");
        float height = GameApplication.get().screenHeight * 0.30f;
        float width = lentaTexture.getRegionWidth() * height / lentaTexture.getRegionHeight();
        setSize(width, height);

        Image lentaImage = new Image(lentaTexture);
        lentaImage.setSize(width, height);
        addActor(lentaImage);

        for(int i = 0; i < productList.size(); i++) {
            ProductInfo productInfo = productList.get(i);
            Image img = new Image(productInfo.getTextureRegion());
            float maxH = height * 1.1f;
            if(img.getHeight() > maxH) { // если товар больше чем лента по высоте, то высотут товара нужно уменьшить
                float h = maxH;
                float w = img.getWidth() * h / img.getHeight();
                img.setSize(w, h);
                float startX = width / 2;
                float startY = height + height * 0.2f;
                float x = startX + 2 * w * i;
                float y = startY + GameConfig.random.nextFloat() * height * 0.5f;
                img.setPosition(x, y);
            }
            addActor(img);
        }
    }

    public void nextCustomer() {
        productList.clear();
        for(int i = 0; i < 5; i++) {
            ProductInfo productInfo = ProductInfo.getRandom();
            productList.add(productInfo);
        }
        clear();

    }

    public void resize() {
        init();
    }
}
