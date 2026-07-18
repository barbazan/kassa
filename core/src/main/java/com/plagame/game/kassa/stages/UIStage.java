package com.plagame.game.kassa.stages;

import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.kassa.Resources.SOUND_GOT_MONEY_FILENAME;
import static com.plagame.game.kassa.platform.service.api.model.BillingCatalog.PRODUCT_HIDE_ADV;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.components.AbsenceDialog;
import com.plagame.game.kassa.components.BoostersListPanel;
import com.plagame.game.kassa.components.DonateDialog;
import com.plagame.game.kassa.components.FooterPanel;
import com.plagame.game.kassa.components.GameHeaderPanel;
import com.plagame.game.kassa.components.GiftDialog;
import com.plagame.game.kassa.components.PereezdDialog;
import com.plagame.game.kassa.components.RatingDialog;
import com.plagame.game.kassa.components.ShopActionsDialog;
import com.plagame.game.kassa.components.ShopBoostersDialog;
import com.plagame.game.kassa.components.ShopDialog;
import com.plagame.game.kassa.components.ShopGoldDialog;
import com.plagame.game.kassa.enums.ShopInfo;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 02.05.2024.
 * Email: dmitry.malyshev@gmail.com
 */
public class UIStage extends BaseStage {

    private final Sound GOT_MONEY_SOUND = AssetUtil.getSound(SOUND_GOT_MONEY_FILENAME);
    private Long soundId = null;
    private volatile long lastDollarsParticleTime = 0;
    public boolean isTouched;
    private GameHeaderPanel headerPanel;
    private FooterPanel footerPanel;
    private boolean isDialog;
    private ShopDialog shopDialog;
    public AbsenceDialog absenceDialog;
    public PereezdDialog pereezdDialog;
    public DonateDialog donateDialog;
    public GiftDialog giftDialog;
    public ShopGoldDialog shopGoldDialog;
    public ShopActionsDialog shopActionsDialog;
    public ShopBoostersDialog shopBoostersDialog;
    public RatingDialog ratingDialog;
    private Image ratingIcon, boostersShopIcon, goldShopIcon, presentBoxIcon, actionsIcon, noAdsIcon;
    private BoostersListPanel boostersListPanel;
    private final List<Rectangle> buttonsRectangleList = new ArrayList<>();
    private final float PARTICLE_SCALE;
    private boolean isPurchasesChecked;

    public UIStage() {
        PARTICLE_SCALE = 2.1f * GameApplication.get().minScreenSize / 720;
        initHeaderPanel();
        initBoostersListPanel();
        initFooterPanel();
        initButtons();
    }

    private void initHeaderPanel() {
        headerPanel = new GameHeaderPanel();
        addActor(headerPanel);
    }

    private void initBoostersListPanel() {
        if(boostersListPanel != null) {
            boostersListPanel.setVisible(false);
        }
        boostersListPanel = new BoostersListPanel();
        boostersListPanel.setPosition(0, headerPanel.getY() - boostersListPanel.getHeight());
        addActor(boostersListPanel);
    }

    private void initFooterPanel() {
        footerPanel = new FooterPanel();
        addActor(footerPanel);
    }

    private void initButtons() {
        float iconSize = GameApplication.get().screenHeight / 15;
        float pad = iconSize / 5;

        // СЛЕВА
        ratingIcon = new Image(ATLAS_1.findRegion("icon_rating")); // иконка геймпад - ведёт на страницу рейтинга
        ratingIcon.setSize(iconSize * 1.3f, iconSize * 1.3f);
        ratingIcon.setPosition(pad, footerPanel.getY() + footerPanel.getHeight() + pad);
        ratingIcon.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                showRatingDialog();
            }
        });
//        ratingIcon.setOrigin(Align.center);
//        ratingIcon.addAction(createActionJump());
        ratingIcon.setVisible(GameApplication.get().platform.isLeaderboardAvailable() && User.get().isAuthorized());
        addActor(ratingIcon);
        buttonsRectangleList.add(new Rectangle((int)ratingIcon.getX(), (int)ratingIcon.getY(), (int)ratingIcon.getWidth(), (int)ratingIcon.getHeight()));

        // Подарок СПРАВА СВЕРХУ
        presentBoxIcon = new Image(ATLAS_1.findRegion("icon_present_box")) { // иконка Подарок - ведёт на страницу Подарок
            @Override
            public void act(float delta) {
                setVisible(User.get().hasGift);
                super.act(delta);
            }
        };
        presentBoxIcon.setSize(iconSize * 1.3f, iconSize * 1.3f);
        presentBoxIcon.setOrigin(presentBoxIcon.getWidth() / 2, presentBoxIcon.getHeight() / 2);
        presentBoxIcon.setPosition(GameApplication.get().screenWidth - presentBoxIcon.getWidth() - pad, headerPanel.getY() - (presentBoxIcon.getHeight() + pad)); // наверху
        presentBoxIcon.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(User.get().hasGift) {
                    SoundUtil.playClickSound();
                    showGiftDialog();
                    presentBoxIcon.setVisible(false);
                }
            }
        });
        presentBoxIcon.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.08f, 1.08f, 0.4f, Interpolation.sine),
                    Actions.scaleTo(1f, 1f, 0.4f, Interpolation.sine)
                )
            )
        );
        addActor(presentBoxIcon);
        buttonsRectangleList.add(new Rectangle((int)presentBoxIcon.getX(), (int)presentBoxIcon.getY(), (int)presentBoxIcon.getWidth(), (int)presentBoxIcon.getHeight()));

        // Если подарка нет, то АКЦИИ СПРАВА СВЕРХУ
        actionsIcon = new Image(ATLAS_1.findRegion("icon_actions")) { // иконка Акции - ведёт на страницу акций
            @Override
            public void act(float delta) {
                if(!isVisible()) {
                    setVisible(GameApplication.get().platform.billing().isCatalogLoaded());
                }
                if(presentBoxIcon.isVisible() && isVisible()) {
                    setVisible(false);
                }
                if(!presentBoxIcon.isVisible() && !isVisible() && hasActions()) {
                    setVisible(true);
                }
                super.act(delta);
            }
        };
        actionsIcon.setSize(iconSize * 1.3f, iconSize * 1.3f);
        actionsIcon.setOrigin(actionsIcon.getWidth() / 2, actionsIcon.getHeight() / 2);
        actionsIcon.setPosition(GameApplication.get().screenWidth - actionsIcon.getWidth() - pad, headerPanel.getY() - (actionsIcon.getHeight() + pad)); // наверху
        actionsIcon.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                showShopActionsDialog();
                actionsIcon.clearActions();
                actionsIcon.setVisible(false);
            }
        });
        actionsIcon.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.08f, 1.08f, 0.4f, Interpolation.sine),
                    Actions.scaleTo(1f, 1f, 0.4f, Interpolation.sine)
                )
            )
        );
        actionsIcon.setVisible(false);
        addActor(actionsIcon);
        buttonsRectangleList.add(new Rectangle((int)actionsIcon.getX(), (int)actionsIcon.getY(), (int)actionsIcon.getWidth(), (int)actionsIcon.getHeight()));

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
            noAdsIcon.setPosition(GameApplication.get().screenWidth - noAdsIcon.getWidth() - pad, headerPanel.getY() - 2 * (noAdsIcon.getHeight() + pad));
            noAdsIcon.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    SoundUtil.playClickSound();
                    showShopGoldDialog();
//                    GameApplication.get().platform.billing().buyHideAdv();
                }
            });
            noAdsIcon.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.delay(5),
                        Actions.rotateTo(360, 1.5f, Interpolation.bounceOut),
                        Actions.delay(1),
                        Actions.rotateTo(0, 1.5f, Interpolation.bounceOut)

                        // или
//                    Actions.delay(5),
//                    Actions.rotateTo(360, 1.0f, Interpolation.sine),
//                    Actions.rotateTo(0, 1.0f, Interpolation.sine)
                    )
                )
            );
            noAdsIcon.setVisible(false);
            addActor(noAdsIcon);
            buttonsRectangleList.add(new Rectangle((int)noAdsIcon.getX(), (int)noAdsIcon.getY(), (int)noAdsIcon.getWidth(), (int)noAdsIcon.getHeight()));
        }

        // СПРАВА
        goldShopIcon = new Image(ATLAS_1.findRegion("icon_shop_gold")); // иконка тележка - ведет в магазин золото
        goldShopIcon.setSize(iconSize * 1.3f, iconSize * 1.3f);
        goldShopIcon.setPosition(GameApplication.get().screenWidth - goldShopIcon.getWidth() - pad, footerPanel.getY() + footerPanel.getHeight() + pad);
        goldShopIcon.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
//                goldShopIcon.clearActions();
//                showYandexGoldDialog();
                showShopGoldDialog();
            }
        });
//        goldShopIcon.setOrigin(0, goldShopIcon.getHeight());
//        goldShopIcon.addAction(createAction1());
//        goldShopIcon.addAction(createActionJump());

        goldShopIcon.setOrigin(Align.center);
        goldShopIcon.addAction(createActionJump());
        addActor(goldShopIcon);
        buttonsRectangleList.add(new Rectangle((int)goldShopIcon.getX(), (int)goldShopIcon.getY(), (int)goldShopIcon.getWidth(), (int)goldShopIcon.getHeight()));

        boostersShopIcon = new Image(ATLAS_1.findRegion("icon_shop_boosters")); // иконка шоколадка - ведет в магазин бустеров
        boostersShopIcon.setSize(iconSize * 1.3f, iconSize * 1.3f);
//        boostersShopIcon.setPosition(pad, ratingIcon.getY() + ratingIcon.getHeight() + pad);
        boostersShopIcon.setPosition(GameApplication.get().screenWidth - boostersShopIcon.getWidth() - pad, goldShopIcon.getY() + goldShopIcon.getHeight() + pad);
        boostersShopIcon.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
//                boostersShopIcon.clearActions();
                showShopBoostersDialog();
            }
        });
        boostersShopIcon.setOrigin(Align.center);
        boostersShopIcon.addAction(createActionJump2());
        addActor(boostersShopIcon);
        buttonsRectangleList.add(new Rectangle((int)boostersShopIcon.getX(), (int)boostersShopIcon.getY(), (int)boostersShopIcon.getWidth(), (int)boostersShopIcon.getHeight()));

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
                boolean tapForDollars = isTapForDollars(x, y);
                if(tapForDollars) {
                    isTouched = true;
                    touchAction(x, y);
                }
            } else {
                lastDollarsParticleTime = System.currentTimeMillis();
            }
        } else {
            if(soundId != null) {
                GOT_MONEY_SOUND.stop(soundId);
                soundId = null;
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
        if(User.get().hasEnergy()) {
            if(canParticleDollars()) {
                User.get().doTap();
                GameApplication.get().particlePool.obtainEffectDollars(x, y, PARTICLE_SCALE); // PARTICLE_SCALE примерно 3
                if(soundId == null) {
                    soundId = GOT_MONEY_SOUND.loop();
                }
                lastDollarsParticleTime = System.currentTimeMillis();
            }
        } else {
            if(soundId != null) {
                GOT_MONEY_SOUND.stop(soundId);
                soundId = null;
            }
        }
    }

    private boolean canParticleDollars() {
        return lastDollarsParticleTime < System.currentTimeMillis() - 60;
    }

    public void showShopDialog(ShopInfo shopInfo) {
        hideAllDialogs();
        shopDialog = new ShopDialog(shopInfo);
        addActor(shopDialog);
        isDialog = true;
    }

    public void showAbsenceDialog() {
        hideAllDialogs();
        if(absenceDialog == null) {
            absenceDialog = new AbsenceDialog();
            addActor(absenceDialog);
            isDialog = true;
        }
    }

    public void showPereezdDialog() {
        hideAllDialogs();
        pereezdDialog = new PereezdDialog();
        addActor(pereezdDialog);
        isDialog = true;
    }

    public void showDonateDialog(long donate) {
        hideAllDialogs();
        donateDialog = new DonateDialog(donate);
        addActor(donateDialog);
        isDialog = true;
    }

    public void showGiftDialog() {
        hideAllDialogs();
        giftDialog = new GiftDialog();
        addActor(giftDialog);
        isDialog = true;
    }

    public void showShopGoldDialog() {
        hideAllDialogs();
        shopGoldDialog = new ShopGoldDialog();
        addActor(shopGoldDialog);
        isDialog = true;
    }

    public void showShopActionsDialog() {
        hideAllDialogs();
        shopActionsDialog = new ShopActionsDialog();
        addActor(shopActionsDialog);
        isDialog = true;
    }

    public void showShopBoostersDialog() {
        hideAllDialogs();
        shopBoostersDialog = new ShopBoostersDialog();
        addActor(shopBoostersDialog);
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

    public void hideShopDialog() {
        if(shopDialog != null) {
            shopDialog.setVisible(false);
            shopDialog = null;
            isDialog = false;
        }
    }

    public void hideAbsenceDialog() {
        if(absenceDialog != null) {
            absenceDialog.setVisible(false);
            absenceDialog = null;
            isDialog = false;
        }
    }

    public void hidePereezdDialog() {
        if(pereezdDialog != null) {
            pereezdDialog.setVisible(false);
            pereezdDialog = null;
            isDialog = false;
        }
    }

    public void hideDonateDialog() {
        if(donateDialog != null) {
            donateDialog.setVisible(false);
            donateDialog = null;
            isDialog = false;
        }
    }

    public void hideGiftDialog() {
        if(giftDialog != null) {
            giftDialog.setVisible(false);
            giftDialog = null;
            isDialog = false;
        }
    }

    public void hideShopGoldDialog() {
        if(shopGoldDialog != null) {
            shopGoldDialog.setVisible(false);
            shopGoldDialog = null;
            isDialog = false;
        }
    }

    public void hideShopActionsDialog() {
        if(shopActionsDialog != null) {
            shopActionsDialog.setVisible(false);
            shopActionsDialog = null;
            isDialog = false;
        }
    }

    public void hideShopBoostersDialog() {
        if(shopBoostersDialog != null) {
            shopBoostersDialog.setVisible(false);
            shopBoostersDialog = null;
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
        initBoostersListPanel();
    }

    private void hideAllDialogs() {
        hideShopDialog();
        hideAbsenceDialog();
        hidePereezdDialog();
        hideDonateDialog();
        hideGiftDialog();
        hideShopBoostersDialog();
        hideShopGoldDialog();
        hideShopActionsDialog();
        hideRatingDialog();
        refresh();
    }

    private boolean isTapForDollars(float x, float y) {
        boolean isButton = isInButtonClick(x, y);
        return !isButton && y > footerPanel.getHeight() && y < Gdx.graphics.getHeight() - headerPanel.getHeight();
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

    private Action createAction1() {
        return Actions.forever(
            Actions.sequence(
                Actions.delay(5),
                Actions.rotateTo(-70, 1.0f, Interpolation.sine),
                Actions.rotateTo(-45, 0.5f, Interpolation.sine),
                Actions.rotateTo(-70, 0.5f, Interpolation.sine),
                Actions.rotateTo(0, 1.0f, Interpolation.sine)
            )
        );
    }

    private Action createActionJump() { // Этот варик пока взял. Получится большой прыжок и сразу небольшой "допрыг" после него.
        return Actions.forever(
            Actions.sequence(
                Actions.delay(4f),

                Actions.parallel(
                    Actions.moveBy(0, 18, 0.18f, Interpolation.sineOut),
                    Actions.rotateTo(-12, 0.18f, Interpolation.sineOut)
                ),

                Actions.parallel(
                    Actions.moveBy(0, -18, 0.35f, Interpolation.bounceOut),
                    Actions.rotateTo(0, 0.35f, Interpolation.sineIn)
                ),

                Actions.delay(0.15f),

                Actions.parallel(
                    Actions.moveBy(0, 10, 0.12f, Interpolation.sineOut),
                    Actions.rotateTo(8, 0.12f, Interpolation.sineOut)
                ),

                Actions.parallel(
                    Actions.moveBy(0, -10, 0.25f, Interpolation.bounceOut),
                    Actions.rotateTo(0, 0.25f, Interpolation.sineIn)
                )
            )
        );
    }

    private Action createActionJump2() { // Вот это очень хороший вариант
        return Actions.forever(
            Actions.sequence(
                Actions.delay(5f),

                Actions.parallel(
                    Actions.moveBy(0, 20, 0.16f, Interpolation.sineOut),
                    Actions.rotateTo(-10, 0.16f)
                ),

                Actions.parallel(
                    Actions.moveBy(0, -20, 0.30f, Interpolation.bounceOut),
                    Actions.rotateTo(0, 0.30f)
                )
            )
        );
    }

    private Action createActionJump3() { // Тоже норм вариант, более "живой" вариант — немного сжимать и растягивать иконку во время прыжка:
        return Actions.forever(
            Actions.sequence(
                Actions.delay(7f),

                Actions.parallel(
                    Actions.moveBy(0, 18, 0.18f, Interpolation.sineOut),
                    Actions.rotateTo(-8, 0.18f),
                    Actions.scaleTo(1.08f, 0.92f, 0.18f)
                ),

                Actions.parallel(
                    Actions.moveBy(0, -18, 0.32f, Interpolation.bounceOut),
                    Actions.rotateTo(0, 0.32f),
                    Actions.scaleTo(1f, 1f, 0.32f)
                )
            )
        );
    }

    public void checkFullscreenAdv() {
        if(GameApplication.get().platform.isAdsAvailable()) {
            if(!GameApplication.get().platform.ads().isFullscreenAdCooldown() && !User.get().isAdHide && User.get().maxDollars >= 10_000_000) {
                GameApplication.get().platform.ads().showFullscreenAdv();
            }
        }
//        if(GameApplication.get().isWebGLAndYandex()  && !GameApplication.get().isFullscreenAdCooldown() && !User.get().isAdHide) {
//            GameApplication.get().lastFullscreenAdTime = System.currentTimeMillis();
//            YandexSDK.showFullscreenAdv();
//        }
    }

}
