package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_SMALL;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.components.buttons.LabelButton;
import com.plagame.game.kassa.enums.ColorInfo;
import com.plagame.game.kassa.platform.service.api.model.BillingProduct;
import com.plagame.game.kassa.platform.service.api.model.PlatformCallback;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Дмитрий Малышев on 24.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ShopActionsDialog extends Table {
    private final List<BillingProduct> actionProducts;
    private final Table headerTable;
    private final Table bodyTable;
    private final Table footerTable;
    private float dialogWidth, dialogHeight, imageSize, pad;

    public ShopActionsDialog() {
        this.actionProducts = GameApplication.get().platform.billing().getProducts().stream()
            .filter(p -> p.id.startsWith("shop_action_"))
            .collect(Collectors.toList());
        this.dialogWidth = GameApplication.get().screenWidth * 0.96f;
        this.dialogHeight = Math.max(dialogWidth, GameApplication.get().screenHeight * 0.75f);
        this.imageSize = dialogWidth / 5;
        this.pad = imageSize / 10;
        setSize(dialogWidth, dialogHeight);
        setPosition((GameApplication.get().screenWidth - dialogWidth) / 2, (GameApplication.get().screenHeight - dialogHeight) / 2);
//        setDebug(true);

        headerTable = new Table();
//        headerTable.setDebug(true);
        headerTable.setWidth(dialogWidth);
        headerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_header_actions")));

        Label headerLabel = new Label("АКЦИИ", new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        headerLabel.setAlignment(Align.center);
        headerTable.add(headerLabel).align(Align.center).padTop(pad).expandX().fill();

        add(headerTable).align(Align.top).expandX().fill();
        row();


        bodyTable = new Table();
        bodyTable.setSize(dialogWidth, dialogHeight - headerTable.getHeight());
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("action_dialog_body")));

        if(!User.get().isAuthorized()) {
            LabelButton loginButton = new LabelButton(ATLAS_1.findRegion("button_action_buy"), "Войти", FONT_DIALOG_BUTTON, Color.WHITE) {
//            LabelButton loginButton = new LabelButton(ATLAS_1.findRegion("button_action_buy"), YandexI18n.get("LOGIN"), FONT_DIALOG_BUTTON, Color.WHITE) { //todo вместо YandexI18n сделать PlatformI18n
                @Override
                public void onClick() {
                    if(!User.get().isAuthorized()) {
                        GameApplication.get().platform.cloud().doLogin(new PlatformCallback<String>() {
                            @Override
                            public void onSuccess(String json) {
                                User.get().saveUser();
                                GameApplication.get().setGameScreen();
                            }

                            @Override
                            public void onError(String error) {
                                System.out.println("Error: " + error);
                                GameApplication.get().setGameScreen();
                            }
                        });
                    }
//                    if(GameApplication.get().isWebGLAndYandex() && !User.get().isAuthorized()) {
//                        User.get().doYandexLogin();
//                    }
                    refresh();
                }
            };
            bodyTable.add(loginButton).size(loginButton.getWidth(), loginButton.getHeight()).align(Align.center).pad(pad).expandX();

            bodyTable.row();

            Label loginDescLabel = new ModelLabel("", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)) {
                @Override
                protected String getValue() {
                    return GameApplication.get().platform.i18n().get("LOGIN_DESC");
//                    return YandexI18n.get("LOGIN_DESC");
                }
            };
            loginDescLabel.setWrap(true);
            loginDescLabel.setAlignment(Align.center);
            bodyTable.add(loginDescLabel).colspan(2).padBottom(pad).expandX().fill();
        } else {
            Table scrolledTable = new Table();
            scrolledTable.setSize(bodyTable.getWidth(), bodyTable.getHeight());

            for(BillingProduct product : actionProducts) {
                Table upgradePanel = createActionPanel(product);
                scrolledTable.add(upgradePanel).align(Align.top).pad(pad).expandX().fill();
                scrolledTable.row();
            }

            ScrollPane scrollPane = new ScrollPane(scrolledTable);
            scrollPane.setScrollingDisabled(true, false);
            bodyTable.add(scrollPane).pad(pad / 2).padBottom(pad * 2).expandX().fill();
        }

        add(bodyTable).expandX().fill();
        row();

        footerTable = new Table();
//        footerTable.setDebug(true);
        footerTable.setSize(dialogWidth, dialogHeight / 10);
        footerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("action_dialog_footer")));

        Button closeButton = createCloseButton();
        footerTable.add(closeButton).size(imageSize * 1.75f, imageSize * 0.75f).align(Align.center).pad(pad).padTop(0);
        Button shopButton = createShopButton();
        footerTable.add(shopButton).size(imageSize * 1.75f, imageSize * 0.75f).align(Align.center).pad(pad).padTop(0);

        add(footerTable).expandX().fill();

    }


    private Table createActionPanel(BillingProduct product) {
        return new ActionPanel(product);
    }

    private Button createGreenButton(BillingProduct product) {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button buyButton = new Button(style);
        Label costLabel = new Label(product.price, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        costLabel.setAlignment(Align.left);
        buyButton.add(costLabel).align(Align.left).pad(pad * 2).fill();
        return buyButton;
    }

    private Button createCloseButton() {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_white"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_white"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button button = new Button(style);
        Label label = new Label("ЗАКРЫТЬ", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        label.setAlignment(Align.center);
        button.add(label).align(Align.center).pad(pad * 2).fill();
        button.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.hideShopActionsDialog();
                GameApplication.get().getGameScreen().uiStage.checkFullscreenAdv();
            }
        });
        return button;
    }

    private Button createShopButton() {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_shop"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_shop"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button button = new Button(style);
        Label label = new Label("МАГАЗИН", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        label.setAlignment(Align.center);
        button.add(label).align(Align.center).pad(pad * 2).fill();
        button.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.showShopGoldDialog();
            }
        });
        return button;
    }

//    private void onBuyProduct(BillingProduct product) {
//        GameApplication.get().platform.billing().purchase(product);
//    }

//    private void onBuyProduct(BillingProduct product, long dollars, long folowers) {
//        if (GameApplication.get().isWebGLAndYandex()) {
//            YandexSDK.checkAuthorized(
//                () -> { // ✅ Уже авторизован
//                    YandexSDK.purchaseProduct(product);
//                },
//                () -> { // ❌ Не авторизован → пробуем авторизовать
//                    YandexSDK.authorize(
//                        () -> { // ✅ Авторизация успешна
//                            YandexBridge.loadUserFromCloud();
//                            YandexSDK.purchaseProduct(product);
//                        },
//                        () -> { // ❌ Авторизация не удалась
//                            System.err.println("❌ Authorization failed");
//                        }
//                    );
//                }
//            );
//        } else {
//            // 🔹 Локальный тест (WebGL не Яндекс)
//            User.get().changeGold(Integer.parseInt(product.title));
//            if(dollars > 0) {
//                User.get().changeDollars(dollars);
//            }
//            if(folowers > 0) {
//                User.get().changeFolowers(folowers);
//            }
//            User.get().saveUser();
//            refresh();
//        }
//    }

    public void refresh() {
        GameApplication.get().getGameScreen().uiStage.showShopActionsDialog();
    }

    private class ActionPanel extends Table {
        private long actionDollars;
        private long actionFolowers;

        public ActionPanel(BillingProduct product) {
            float panelWidth = headerTable.getWidth() * 0.95f;
            this.align(Align.top);
//            this.setSize(panelWidth, 0);
//            this.setWidth(panelWidth);
            this.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_action_bg")));
//            this.setDebug(true);

            Table leftTable = new Table();
//            leftTable.setDebug(true);
            Label titleLabel = new ModelLabel("00:11:56", new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK)) {
                @Override
                protected String getValue() {
                    return "00:11:56"; //todo
                }
            };
            titleLabel.setVisible(false); //todo
            titleLabel.setAlignment(Align.top);
            leftTable.add(titleLabel).align(Align.top).fill();

            leftTable.row();

            Image iconImage = new Image(product.getTextureRegion());
            iconImage.setSize(imageSize, imageSize);
            leftTable.add(iconImage).size(iconImage.getWidth(), iconImage.getHeight()).align(Align.top).pad(pad);



            boolean isDollarsAction = isDollarsAction(product);
            Table rightTable = new Table();
//            rightTable.setDebug(true);
            Color color = isDollarsAction ? ColorInfo.ACTION_GREEN.color : ColorInfo.ACTION_ORANGE.color; //todo remove
            Label descLabel = new Label(getActionName(product), new Label.LabelStyle(FONT_DIALOG_BUTTON, color));
            descLabel.setAlignment(Align.center);
            rightTable.add(descLabel).align(Align.center).padTop(pad).expandX();
            rightTable.row();

            Table rewardTable = new Table();
            Image iconGoldImage = new Image(ATLAS_1.findRegion("shop_gold_1"));
            iconGoldImage.setSize(imageSize / 2, imageSize / 2);
            rewardTable.add(iconGoldImage).size(iconGoldImage.getWidth(), iconGoldImage.getHeight());;
//        Label goldCountLabel = new Label(product.title + " шт.", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.GOLD));
            Label goldCountLabel = new Label(product.title, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.GOLD));
            goldCountLabel.setAlignment(Align.left);
            rewardTable.add(goldCountLabel).align(Align.left).pad(pad).padRight(0).fill();

            // плюс
            Image iconPlusImage = new Image(ATLAS_1.findRegion("icon_plus_2"));
            iconPlusImage.setSize(imageSize / 4, imageSize / 4);
            rewardTable.add(iconPlusImage).size(iconPlusImage.getWidth(), iconPlusImage.getHeight()).align(Align.center).pad(pad);

            if(isDollarsAction) { // в акции кроме золото дают либо баксы
//                Image iconDollarsImage = new Image(ATLAS_1.findRegion("dollars_pack_2"));
                Image iconDollarsImage = new Image(ATLAS_1.findRegion("icon_dollars"));
                iconDollarsImage.setSize(imageSize / 3, imageSize / 3);
                rewardTable.add(iconDollarsImage).size(iconDollarsImage.getWidth(), iconDollarsImage.getHeight());
                actionDollars = getActionDollars(product);
                Label countLabel = new Label(NumberFormat.format(actionDollars), new Label.LabelStyle(FONT_DIALOG_BUTTON, ColorInfo.ACTION_GREEN.color)); //todo вычислять сколько долларов давать
                countLabel.setAlignment(Align.left);
                rewardTable.add(countLabel).align(Align.left).pad(pad).fill();
            } else { // эти акции дают подписчиков
                Image iconFolowersImage = new Image(ATLAS_1.findRegion("icon_folowers"));
                iconFolowersImage.setSize(imageSize / 3, imageSize / 3);
                rewardTable.add(iconFolowersImage).size(iconFolowersImage.getWidth(), iconFolowersImage.getHeight());
                actionFolowers = getActionFolowers(product);
                Label countLabel = new Label(NumberFormat.format(actionFolowers), new Label.LabelStyle(FONT_DIALOG_BUTTON, ColorInfo.ACTION_ORANGE.color)); //todo вычислять сколько подписчиков давать
                countLabel.setAlignment(Align.left);
                rewardTable.add(countLabel).align(Align.left).pad(pad).fill();
            }

            rightTable.add(rewardTable).expandX().fill();
            rightTable.row();

            Button buyButton = createGreenButton(product);
            buyButton.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    SoundUtil.playClickSound();
                    if(User.get().isAuthorized()) {
                        GameApplication.get().platform.billing().purchase(product);
                    }
//                    if(GameApplication.get().isWebGLAndYandex() && User.get().isAuthorized()) {
//                        onBuyProduct(product, actionDollars, actionFolowers);
//                    }
                }
            });
            rightTable.add(buyButton).align(Align.center).pad(pad).fill().padTop(0);

            this.add(leftTable).align(Align.top);
            this.add(rightTable).align(Align.top).expandX().fill();
        }

        public boolean isDollarsAction(BillingProduct product) { // эти акции дают доллары
            return product.id.startsWith("shop_action_1") || product.id.startsWith("shop_action_3");
        }

        private String getActionName(BillingProduct product) {
            if(product.id.startsWith("shop_action_1")) {
                return "МИЛЕЙШАЯ АКЦИЯ";
            } else if(product.id.startsWith("shop_action_2")) {
                return "АКЦИЯ ПО ЦЕНЕ КОФЕ";
            } else if(product.id.startsWith("shop_action_3")) {
                return "РОСКОШНОЕ ПРЕДЛОЖЕНИЕ";
            } else if(product.id.startsWith("shop_action_4")) {
                return "ДЕСЕРТ ДЛЯ КОШЕЛЬКА";
            }
            return "";
        }

        private long getActionDollars(BillingProduct product) {
            int goldCount = 0;
            try {
                goldCount = Integer.parseInt(product.title);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(product.id.startsWith("shop_action_1")) {
                return (long)(User.get().dollars + goldCount * User.get().pasIncome);
            } else if(product.id.startsWith("shop_action_3")) {
                return (long)(2 * User.get().dollars + 2 * goldCount * User.get().pasIncome);
            }
            return 0;
        }

        private long getActionFolowers(BillingProduct product) {
            int goldCount = 0;
            try {
                goldCount = Integer.parseInt(product.title);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(product.id.startsWith("shop_action_2")) {
                return (long)(User.get().folowers + goldCount * User.get().folowersIncome);
            } else if(product.id.startsWith("shop_action_4")) {
                return (long)(2 * User.get().folowers + 2 * goldCount * User.get().folowersIncome);
            }
            return 0;
        }
    }
}
