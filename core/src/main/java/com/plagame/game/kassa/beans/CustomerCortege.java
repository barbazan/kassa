package com.plagame.game.kassa.beans;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.CustomerInfo;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 23.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class CustomerCortege extends Group {

    public LinkedList<CustomerInfo> customerList = new LinkedList<>();
    public final LinkedList<Image> customerImageList = new LinkedList<>();

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
            if(img.getHeight() > maxH) { // если чел больше чем макс высота, то нужно уменьшить
                float w = img.getWidth() * maxH / img.getHeight();
                img.setSize(w, maxH);
            }
            float x = prevX + img.getWidth() * 0.01f;
            float y;
            if(customerInfo.isLegless() && GameApplication.get().isPortrait()) {
                y = startY + img.getHeight() * 0.2f;
            } else {
                y = startY;
            }
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
        float deltaX= img.getX() + img.getWidth() * 3;
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

    public void startPayment() {
//        boolean isCard = GameConfig.random.nextBoolean();
        boolean isCard = true;
        if(isCard) {
            int cardType = 1 + GameConfig.random.nextInt(5);
            TextureRegion textureRegion = ATLAS_1.findRegion("card_" + cardType);
            Image cardImage = new Image(textureRegion);
            float cardW = GameApplication.get().getGameScreen().gameScene.cashRegister.terminal.getWidth();
            float cardH = cardW * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
            cardImage.setSize(cardW, cardH);
            cardImage.setOrigin(cardW / 2, cardH / 2);
            cardImage.setRotation(-20);
            if(!customerImageList.isEmpty()) {
                Image targetImage = customerImageList.getFirst();
                float x1 = getStartX() + cardImage.getWidth() / 2;
                float y1 = getStartY() + targetImage.getHeight() * 0.5f;
                cardImage.setPosition(x1, y1);
            }
            cardImage.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    SoundUtil.playClickSound();
                    cardImage.setVisible(false);
                    moveCameraSlowly(isCard);
                    GameApplication.get().getGameScreen().gameScene.cashRegister.showCard(cardType);
                }
            });
            cardImage.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.rotateBy(40, 0.8f),
                        Actions.rotateBy(-40, 0.8f)
                    )
                ));
            addActor(cardImage);
        } else { //наличка
            Group cashGroup = new Group();
            float dolW = 0, dolH = 0;
            for(int i = 1; i <= 3; i++) {
                int cashType = 1 + GameConfig.random.nextInt(5);
                TextureRegion textureRegion = ATLAS_1.findRegion("dollar_" + cashType);
                Image dollarImage = new Image(textureRegion);
                dolW = GameApplication.get().getGameScreen().gameScene.cashRegister.terminal.getWidth() * 0.35f;
                dolH = dolW * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
                dollarImage.setColor(new Color(0xaaffaaff));
                dollarImage.setSize(dolW, dolH);
                dollarImage.setOrigin(dolW / 2, dolH / 2);
                dollarImage.setRotation(80);
                dollarImage.setRotation(dollarImage.getRotation() + (GameConfig.random.nextBoolean()  ? -GameConfig.random.nextInt(20) : GameConfig.random.nextInt(20)));
                cashGroup.addActor(dollarImage);
            }
            cashGroup.setSize(dolW, dolH);
            cashGroup.setOrigin(dolW / 2, dolH / 2);
            if(!customerImageList.isEmpty()) {
                Image targetImage = customerImageList.getFirst();
                float x1 = getStartX() + customerImageList.getFirst().getWidth() / 2;
                float y1 = getStartY() + targetImage.getHeight() * 0.5f;
                cashGroup.setPosition(x1, y1);
            }
            cashGroup.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    SoundUtil.playClickSound();
                    cashGroup.setVisible(false);
                    moveCameraSlowly(isCard);
                }
            });
            cashGroup.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.rotateBy(40, 0.8f),
                        Actions.rotateBy(-40, 0.8f)
                    )
                ));
            addActor(cashGroup);
        }
    }

    public void moveCameraSlowly(boolean isCard) {
        if(isCard) { // карта
            Group terminal = GameApplication.get().getGameScreen().gameScene.cashRegister.terminal;
            float targetZoom;
            float targetX;
            if(GameApplication.get().isPortrait()) {
                targetZoom = (terminal.getHeight() / GameApplication.get().screenHeight) * 1.2f;
                targetX = terminal.getParent().getX() + terminal.getX() + terminal.getWidth() / 2;
            } else {
                targetZoom = (terminal.getHeight() / GameApplication.get().screenHeight);
                targetX = terminal.getParent().getX() + terminal.getX() + terminal.getWidth();
            }
            float targetY = terminal.getParent().getY() + terminal.getY() + terminal.getHeight() / 2;
            getParent().addAction(
                Actions.sequence(
//                    Actions.delay(1.0f),
                    new CameraAction(GameApplication.get().camera, targetX, targetY, targetZoom, 0.5f)
                )
            );
        } else {  // наличка
            CashRegister cashRegister = GameApplication.get().getGameScreen().gameScene.cashRegister;
            float targetZoom;
            float targetX;
            float targetY;
            if(GameApplication.get().isPortrait()) {
                targetZoom = cashRegister.kassa.getWidth() / GameApplication.get().screenWidth;
                targetX = cashRegister.getParent().getX() + cashRegister.getX() + cashRegister.kassa.getWidth() / 2;
                targetY = cashRegister.getParent().getY() + cashRegister.getY() + cashRegister.kassa.getHeight() * 0.6f;
            } else {
                targetZoom = cashRegister.kassa.getHeight() * 0.9f / GameApplication.get().screenHeight;
                targetX = cashRegister.getParent().getX() + cashRegister.getX() + cashRegister.kassa.getWidth() * 1.3f;
                targetY = cashRegister.getParent().getY() + cashRegister.getY() + cashRegister.getHeight() * 0.55f;
            }
            getParent().addAction(
                Actions.sequence(
//                    Actions.delay(1.0f),
                    new CameraAction(GameApplication.get().camera, targetX, targetY, targetZoom, 0.5f)
                )
            );
        }
    }

}
