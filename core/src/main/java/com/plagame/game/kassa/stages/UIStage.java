package com.plagame.game.kassa.stages;

import static com.plagame.game.integration.platform.service.api.model.BillingCatalog.PRODUCT_HIDE_ADV;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.components.HeaderPanel;
import com.plagame.game.kassa.components.RatingDialog;
import com.plagame.game.kassa.components.ShopGoldDialog;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 02.05.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public class UIStage extends BaseStage {

    public boolean isTouched;
    private boolean isDialog;
    public ShopGoldDialog shopGoldDialog;
    public RatingDialog ratingDialog;
    private Image noAdsIcon;
    private final List<Rectangle> buttonsRectangleList = new ArrayList<>();
    private boolean isPurchasesChecked;
    public HeaderPanel headerPanel;

    public UIStage() {
        super();
        init();
    }

    private void init() {
        initHeaderPanel();
        initButtons();
    }

    public void resize() {
        clear();
        init();
    }

    private void initHeaderPanel() {
        headerPanel = new HeaderPanel();
        addActor(headerPanel);
    }

    private void initButtons() {
        float iconSize = GameApplication.get().screenHeight / 15;
        float pad = iconSize / 5;

        // ОТКЛЮЧИТЬ РЕКЛАМУ справа сверху под акциями
        if(GameApplication.get().platform.isAdsAvailable() && !User.get().isAdHide) {
            noAdsIcon = new Image(ATLAS_1.findRegion(PRODUCT_HIDE_ADV)) { // иконка отключить рекламу
                @Override
                public void act(float delta) {
                    noAdsIcon.setVisible(!User.get().isAdHide && GameApplication.get().platform.billing().isCatalogLoaded());
                    super.act(delta);
                }
            };
            noAdsIcon.setSize(iconSize * 1.3f, iconSize * 1.3f);
            noAdsIcon.setOrigin(noAdsIcon.getWidth() / 2, noAdsIcon.getHeight() / 2);
            noAdsIcon.setPosition(GameApplication.get().screenWidth - noAdsIcon.getWidth() - pad, GameApplication.get().screenHeight / 2 + 2 * (noAdsIcon.getHeight() + pad));
            noAdsIcon.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    SoundUtil.playClickSound();
                    showShopGoldDialog();
                }
            });
            noAdsIcon.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.delay(5),
                        Actions.rotateTo(360, 1.5f, Interpolation.bounceOut),
                        Actions.delay(1),
                        Actions.rotateTo(0, 1.5f, Interpolation.bounceOut)
                    )
                )
            );
            noAdsIcon.setVisible(false);
            addActor(noAdsIcon);
            buttonsRectangleList.add(new Rectangle((int)noAdsIcon.getX(), (int)noAdsIcon.getY(), (int)noAdsIcon.getWidth(), (int)noAdsIcon.getHeight()));
        }
    }

    public void render(float delta) {
        processTouch();
        super.render(delta);
    }

    private void processTouch() {
        if (Gdx.input.justTouched() || Gdx.input.isTouched()) {
            checkPurchases();
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if(!isDialog) {
                touchAction(x, y);  //todo
            }
        }
    }

    private void checkPurchases() {
        if(!isPurchasesChecked) {
            GameApplication.get().platform.billing().restorePurchases(); // сразу после первого клика пытаемся покупки проверить с моего сервера
            isPurchasesChecked = true;
        }
    }
    private void touchAction(float x, float y) {
        // todo
    }

    public void showShopGoldDialog() {
        hideAllDialogs();
        shopGoldDialog = new ShopGoldDialog();
        addActor(shopGoldDialog);
        isDialog = true;
    }

    public void showRatingDialog() {
        hideAllDialogs();
        GameApplication.get().networkWebSocketClient.sendSaveUserPacket();
        GameApplication.get().platform.leaderboard().loadDefaultLeaderboard();
        ratingDialog = new RatingDialog();
        addActor(ratingDialog);
        isDialog = true;
    }

    public void hideShopGoldDialog() {
        if(shopGoldDialog != null) {
            shopGoldDialog.setVisible(false);
            shopGoldDialog = null;
            isDialog = false;
        }
    }

    public void hideRatingDialog() {
        if(ratingDialog != null) {
            ratingDialog.setVisible(false);
            ratingDialog = null;
            isDialog = false;
        }
    }

    public void refresh() {
        //todo если нужно обновить какой-то компонент
//        initBoostersListPanel();
    }

    private void hideAllDialogs() {
        hideShopGoldDialog();
        hideRatingDialog();
    }

    private boolean isInButtonClick(float x, float y) {
        for(int i = 0; i < buttonsRectangleList.size(); i++) {
            Rectangle rect = buttonsRectangleList.get(i);
            if(rect.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void checkFullscreenAdv() {
        if(GameApplication.get().platform.isAdsAvailable()) {
            if(!GameApplication.get().platform.ads().isFullscreenAdCooldown() && !User.get().isAdHide && User.get().maxDollars >= 10_000_000) {
                GameApplication.get().platform.ads().showFullscreenAdv();
            }
        }
    }

}
