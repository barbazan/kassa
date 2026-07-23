package com.plagame.game.kassa.beans;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.enums.CustomerInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 23.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class CustomerCortege extends Group {

    public List<CustomerInfo> customerList;
    private final LinkedList<Image> customerImageList = new LinkedList<>();
    private Image currentCustomer;

    public CustomerCortege(List<CustomerInfo> customerList) {
        this.customerList = customerList;
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
            addActor(img);
            prevX = x + img.getWidth();
            customerImageList.add(img);
        }
        currentCustomer = customerImageList.get(0);
    }

    public void nextCustomer() {
        if(!customerImageList.isEmpty()) {
            Image first = customerImageList.get(0);
            if(first != null) {
                customerImageList.removeFirst();
                moveCustomer(first);
                moveAllCustomers();
            }
        }
    }

    public void resize() {
        init(customerList);
    }

    private void moveCustomer(Image img) {
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
                        currentCustomer = null;
                    }
                })
            )
        );

    }

    private void moveAllCustomers() {
        for(int i = 0; i < customerImageList.size(); i++) {
            Image img = customerImageList.get(i);
            if(currentCustomer != null) {
                img.addAction(Actions.moveBy(-currentCustomer.getWidth(), 0, 0.7f, Interpolation.linear));
            }
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
