package com.plagame.game.kassa.beans;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.CustomerInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 23.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class CustomerCortege extends Group {

    public LinkedList<CustomerInfo> customerList = new LinkedList<>();
    private final LinkedList<Image> customerImageList = new LinkedList<>();

    public CustomerCortege(List<CustomerInfo> customerList) {
        this.customerList.clear();
        this.customerList.addAll(customerList);
        init(customerList);
    }

    private void init(List<CustomerInfo> customerList) {
        clear();
        float width = GameApplication.get().screenWidth;
        float height = GameApplication.get().screenHeight;
        setSize(width, height);
        float startX = getStartX();
        float startY = getStartY();
        float maxH = GameApplication.get().screenHeight * 0.90f;
        float prevX = startX;
        for(int i = 0; i < customerList.size(); i++) {
            CustomerInfo customerInfo = customerList.get(i);
            Image img = new Image(customerInfo.getTextureRegion());
            if(img.getHeight() > maxH) { // если товар больше чем лента по высоте, то высотут товара нужно уменьшить
                float w = img.getWidth() * maxH / img.getHeight();
                img.setSize(w, maxH);
            }
            float x = prevX + img.getWidth() * 0.01f;
            float y = startY;
            img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
            img.setPosition(x, y);
            float scaleDelta = 0.04f;
            float duration = 1.1f + GameConfig.random.nextFloat();
            img.addAction(Actions.forever(
                Actions.sequence(
                    Actions.scaleBy(scaleDelta, scaleDelta, duration, Interpolation.sine),
                    Actions.scaleBy(-scaleDelta, -scaleDelta, duration, Interpolation.sine)
                )
            ));
            addActor(img);
            prevX = x + img.getWidth();
            customerImageList.add(img);
        }
    }

    public void nextCustomer() {
        if(!customerImageList.isEmpty()) {
            Image first = customerImageList.get(0);
            if(first != null) {
                customerImageList.removeFirst();
                customerList.removeFirst();
                moveCustomer(first);
                moveAllCustomers(first.getWidth());
                if(!customerImageList.isEmpty()) {
                    GameApplication.get().getGameScreen().gameScene.productCortege.nextProducts();
                }
            }
        } else {
            GameApplication.get().getGameScreen().gameScene.nextDay();
        }
    }

    public void resize() {
        init(customerList);
    }

    private void moveCustomer(Image img) {
        float deltaX= img.getX() + img.getWidth() * 2;
        float deltaY= img.getHeight() * 0.02f;
        float duration = 2.8f;
        float stepDuration = duration / 10;
        img.addAction(
            Actions.parallel(
                Actions.moveBy(-deltaX, 0, duration, Interpolation.linear),
                Actions.sequence(
                    Actions.moveBy(0, deltaY, stepDuration, Interpolation.linear),
                    Actions.moveBy(0, -deltaY, stepDuration, Interpolation.linear),
                    Actions.delay(stepDuration / 2 + GameConfig.random.nextFloat() * stepDuration / 2),
                    Actions.moveBy(0, deltaY, stepDuration, Interpolation.linear),
                    Actions.moveBy(0, -deltaY, stepDuration, Interpolation.linear),
                    Actions.moveBy(0, deltaY, stepDuration, Interpolation.linear),
                    Actions.moveBy(0, -deltaY, stepDuration, Interpolation.linear),
                    Actions.delay(stepDuration / 2 + GameConfig.random.nextFloat() * stepDuration / 2),
                    Actions.moveBy(0, deltaY, stepDuration, Interpolation.linear),
                    Actions.moveBy(0, -deltaY, stepDuration, Interpolation.linear)
                )
            )
        );

    }

    private void moveAllCustomers(float deltaX) {
        for(int i = 0; i < customerImageList.size(); i++) {
            Image img = customerImageList.get(i);
            float deltaY= img.getHeight() * 0.02f;
            float duration = 1.2f;
            float stepDuration = duration / 6;
            img.addAction(
                Actions.parallel(
                    Actions.moveBy(-deltaX, 0, duration, Interpolation.linear),
                    Actions.sequence(
                        Actions.moveBy(0, deltaY, stepDuration, Interpolation.linear),
                        Actions.moveBy(0, -deltaY, stepDuration, Interpolation.linear),
                        Actions.delay(stepDuration / 2 + GameConfig.random.nextFloat() * stepDuration / 2),
                        Actions.moveBy(0, deltaY, stepDuration, Interpolation.linear),
                        Actions.moveBy(0, -deltaY, stepDuration, Interpolation.linear)
                    )
                )
            );
        }
    }

    public float getStartX() {
        if(GameApplication.get().isPortrait()) {
            return getWidth() * 0.30f;
        } else {
            return getWidth() * 0.38f;
        }
    }

    public float getStartY() {
        if(GameApplication.get().isPortrait()) {
            return getHeight() * 0.07f;
        } else {
            return 0;
        }
    }

}
