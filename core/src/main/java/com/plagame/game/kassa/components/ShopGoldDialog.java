package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_SMALL;
import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.integration.platform.service.api.model.BillingCatalog.PRODUCT_HIDE_ADV;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Дмитрий Малышев on 24.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ShopGoldDialog extends Table {
    private final List<BillingProduct> goldProducts;
    private final Table headerTable;
    private final Table bodyTable;
    private final Table footerTable;
    private float dialogWidth, dialogHeight, imageSize, pad;

    public ShopGoldDialog() {
        this.goldProducts = GameApplication.get().platform.billing().getProducts().stream()
            .filter(p -> p.id.startsWith("shop_gold_"))
            .sorted(Comparator.comparingInt((BillingProduct p) -> p.gold).reversed())
            .collect(Collectors.toList());
        this.dialogWidth = GameApplication.get().screenWidth * 0.96f;
        this.dialogHeight = Math.max(dialogWidth, GameApplication.get().screenHeight * 0.75f);
        this.imageSize = dialogWidth / 5;
        this.pad = imageSize / 10;
        setSize(dialogWidth, dialogHeight);
        setPosition((GameApplication.get().screenWidth - dialogWidth) / 2, (GameApplication.get().screenHeight - dialogHeight) / 2);

        headerTable = new Table();
        headerTable.setWidth(dialogWidth);
        headerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_header_gold")));

        Label headerLabel = new Label("ЗОЛОТО", new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        headerLabel.setAlignment(Align.center);
        headerTable.add(headerLabel).align(Align.center).padTop(pad).expandX().fill();

        add(headerTable).align(Align.top).expandX().fill();
        row();


        bodyTable = new Table();
        bodyTable.setSize(dialogWidth, dialogHeight - headerTable.getHeight());
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("action_dialog_body")));

        if(!User.get().isAuthorized()) {
            LabelButton loginButton = new LabelButton(ATLAS_1.findRegion("button_action_buy"), "Войти", FONT_DIALOG_BUTTON, Color.WHITE) {//todo вместо YandexI18n сделать PlatformI18n
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
                    refresh();
                }
            };
            bodyTable.add(loginButton).size(loginButton.getWidth(), loginButton.getHeight()).align(Align.center).pad(pad).expandX();

            bodyTable.row();

            Label loginDescLabel = new ModelLabel("", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)) {
                @Override
                protected String getValue() {
                    return GameApplication.get().platform.i18n().get("LOGIN_DESC");
                }
            };
            loginDescLabel.setWrap(true);
            loginDescLabel.setAlignment(Align.center);
            bodyTable.add(loginDescLabel).colspan(2).padBottom(pad).expand().fill();
        } else {
            Table scrolledTable = new Table();
            scrolledTable.setSize(bodyTable.getWidth(), bodyTable.getHeight());

            if(GameApplication.get().platform.isAdsAvailable() && !User.get().isAdHide) {
                BillingProduct hideAdvProduct = null;
                List<BillingProduct> list = GameApplication.get().platform.billing().getProducts().stream()
                    .filter(p -> p.id.equals(PRODUCT_HIDE_ADV))
                    .collect(Collectors.toList());
                if(!list.isEmpty()) {
                    hideAdvProduct = list.get(0);
                }
                if(hideAdvProduct != null) {
                    System.out.println("-------hideAdvProduct = " + hideAdvProduct);
                    Table upgradePanel = new ProductPanel(hideAdvProduct, false);
                    scrolledTable.add(upgradePanel).align(Align.top).pad(pad).expandX().fill();
                    scrolledTable.row();
                }
            }

            for(BillingProduct product : goldProducts) {
                Table upgradePanel = new ProductPanel(product);
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
        footerTable.setSize(dialogWidth, dialogHeight / 10);
        footerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("action_dialog_footer")));

        Button closeButton = createCloseButton();
        footerTable.add(closeButton).size(imageSize * 1.75f, imageSize * 0.75f).align(Align.center).pad(pad).padTop(0);
        Button shopButton = createShopButton();
        footerTable.add(shopButton).size(imageSize * 1.75f, imageSize * 0.75f).align(Align.center).pad(pad).padTop(0);

        add(footerTable).expandX().fill();

    }

    private Button createGreenButton(BillingProduct product) {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button buyButton = new Button(style);
        Label costLabel = new Label("КУПИТЬ " + product.price, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
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
                GameApplication.get().getGameScreen().uiStage.hideShopGoldDialog();
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
        Label label = new Label("АКЦИИ", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        label.setAlignment(Align.center);
        button.add(label).align(Align.center).pad(pad * 2).fill();
        button.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
//                GameApplication.get().getGameScreen().uiStage.showShopActionsDialog();
            }
        });
        return button;
    }

    public void refresh() {
        GameApplication.get().getGameScreen().uiStage.showShopGoldDialog();
    }

    private class ProductPanel extends Table {

        public ProductPanel(BillingProduct product) {
            this(product, true);
        }

        public ProductPanel(BillingProduct product, boolean isGold) {
            this.align(Align.top);
            this.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_action_bg")));

            Table leftTable = new Table();
            Group group = new Group();
            Label titleLabel = new ModelLabel(getActionText(product), new Label.LabelStyle(FONT_VERY_SMALL, ColorInfo.ACTION_ORANGE.color)) {
                @Override
                protected String getValue() {
                    return getActionText(product);
                }
            };
            titleLabel.setAlignment(Align.center);
            group.setSize(titleLabel.getWidth(), titleLabel.getHeight());
            group.setOrigin(group.getWidth() / 2, group.getHeight() / 2);
            group.addActor(titleLabel);
            group.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleTo(1.28f, 1.28f, 0.38f, Interpolation.sine),
                        Actions.scaleTo(1f, 1f, 0.38f, Interpolation.sine)
                    )
                )
            );
            leftTable.add(group).align(Align.center).padTop(pad).fill();

            leftTable.row();

            Image iconImage = new Image(product.getTextureRegion());
            iconImage.setSize(imageSize, imageSize);
            leftTable.add(iconImage).size(iconImage.getWidth(), iconImage.getHeight()).align(Align.top).pad(pad);



            Table rightTable = new Table();

            Table rewardTable = new Table();
            if(isGold) {
                Image iconGoldImage = new Image(ATLAS_1.findRegion("shop_gold_1"));
                iconGoldImage.setSize(imageSize / 2, imageSize / 2);
                rewardTable.add(iconGoldImage).size(iconGoldImage.getWidth(), iconGoldImage.getHeight());
            }
            Label goldCountLabel = new Label(product.description, new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.GOLD));
            goldCountLabel.setAlignment(Align.left);
            rewardTable.add(goldCountLabel).align(Align.center).pad(pad).padRight(0).fill();

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
                }
            });
            rightTable.add(buyButton).align(Align.center).pad(pad).fill().padTop(0);

            this.add(leftTable).align(Align.top);
            this.add(rightTable).align(Align.top).expandX().fill();
        }
    }

    private String getActionText(BillingProduct product) {
        if(product.id.equals("shop_gold_4")) {
            return "выгода 12%";
        } else if(product.id.equals("shop_gold_5")) {
            return "выгода 16%";
        } else if(product.id.equals("shop_gold_6")) {
            return "выгода 20%";
        }
        return "";
    }
}
