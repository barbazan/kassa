package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_BIG_TOYZ;
import static com.plagame.game.kassa.GameApplication.FONT_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_SMALL;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_BIG_TOYZ;
import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.kassa.enums.ColorInfo.LOADING_SCREEN_BG_2_COLOR;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
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
import com.plagame.game.kassa.components.HeaderPanel;
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;

/**
 * Created by Дмитрий Малышев on 27.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ShopScreen extends BaseScreen {

    public ShopScreen() {
        init();
    }

    private void init() {
        stage.clear();
        stage.addActor(new HeaderPanel());
        float width = GameApplication.get().minScreenSize * 0.99f;
        float height = GameApplication.get().screenHeight * 0.99f;
        float pad = width / 20;
        Table dialogTable = new Table();
//        dialogTable.setDebug(true);
        dialogTable.setSize(width, height);
        dialogTable.padBottom(pad * 4);

        Label totalLabel = new Label("МАГАЗИН", new Label.LabelStyle(FONT_VERY_BIG_TOYZ, Color.WHITE)); //todo I18N
        totalLabel.setAlignment(Align.center);
        float padTop = GameApplication.get().isPortrait() ? pad * 3 : pad / 2;
        dialogTable.add(totalLabel).align(Align.left).pad(pad / 2).padTop(padTop).fill();
        dialogTable.row();

        for(int i = 1; i <= ProductInfo.values().length; i+=4) {
            Table goodTable = createGoodsTable(i,width * 0.96f);
            dialogTable.add(goodTable).size(goodTable.getWidth(), goodTable.getHeight()).align(Align.center).expandX().fill();
            dialogTable.row();
        }

        dialogTable.setPosition(GameApplication.get().screenWidth / 2 - width / 2, GameApplication.get().screenHeight / 2 - height / 2);

        ScrollPane scrollPane = new ScrollPane(dialogTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setSize(width, height);

        scrollPane.setPosition(GameApplication.get().screenWidth / 2 - scrollPane.getWidth() / 2, GameApplication.get().screenHeight / 2 - scrollPane.getHeight() / 2);

        stage.addActor(scrollPane);

        Button btnStart = createStartBtn();
        if(GameApplication.get().isPortrait()) {
            btnStart.setPosition(GameApplication.get().screenWidth / 2 - btnStart.getWidth() / 2, pad / 2);
        } else {
            btnStart.setPosition(GameApplication.get().screenWidth  - btnStart.getWidth() - pad / 2, pad / 2);
        }
        stage.addActor(btnStart);
    }

    @Override
    public void render(float delta) {
        AssetUtil.clearScreen(LOADING_SCREEN_BG_2_COLOR.color);
        super.render(delta);
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Table createGoodsTable(int index, float tableWidth) {
        float tableHeight = tableWidth / 2.90f;
        float imageHeight = tableHeight * 0.9f;
        float pad = imageHeight / 10;
        Table productsTable = new Table();
        productsTable.setSize(tableWidth, tableHeight);
//        productsTable.setDebug(true);
        productsTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_goods_bg_2")));
        productsTable.pad(pad / 2);

        productsTable.add().expandX();

        for(int i = index; i < index + 4; i++) {
            ProductInfo productInfo = ProductInfo.getByType(i);
            TextureRegion textureRegion = productInfo.getTextureRegion();
            Vector2 vector2 = calcImageSize(textureRegion, imageHeight);
            Group group = new Group();
            group.setSize(tableHeight * 0.49f, tableHeight);

            Table costPanel = createCostPanel(productInfo);
            costPanel.setPosition(group.getWidth() / 2 - costPanel.getWidth() / 2, pad * 0.75f);
            group.addActor(costPanel);

            Image productImage = new Image(productInfo.getTextureRegion());
            productImage.setSize(vector2.x, vector2.y);
            productImage.setScale(1.1f);
            productImage.setPosition(group.getWidth() / 2 - productImage.getWidth() / 2, costPanel.getHeight() + (group.getHeight() - costPanel.getHeight()) / 2 - productImage.getHeight() / 2);
            group.addActor(productImage);

            float padLeft = i == 0 ? pad * 3 : 0;
            productsTable.add(group).align(Align.center).pad(0, padLeft, pad / 2, pad / 2).expandX().fill();
        }

        productsTable.add().size(imageHeight * 0.55f).expandX();

        // табличка "Продано" показываем если товар куплен
        Group soldTable = createSoldTable(tableHeight * 0.65f);
        soldTable.setPosition(productsTable.getWidth() - soldTable.getWidth() - pad * 3 / 2, productsTable.getHeight() - soldTable.getHeight() + pad);
        soldTable.setVisible(User.get().buyedProducts.contains(ProductInfo.getByType(index).type));
        productsTable.addActor(soldTable);

        if(User.get().isFullVersionBuyed || ProductInfo.UNLOCK_PRODUCT_LIST.contains(ProductInfo.getByType(index))) { // кнопку "Купить" показываем если куплена полная версия или продукт входи в начальный анлокнутый набор
            int unlockPrice = (int)(45 * Math.pow(index, 1.5));
            Button buyBtn = createBuyBtn(unlockPrice, tableHeight * 0.65f);
            buyBtn.setPosition(productsTable.getWidth() - buyBtn.getWidth() - 2 * pad / 3, productsTable.getHeight() - buyBtn.getHeight() - pad);
            buyBtn.setVisible(!User.get().buyedProducts.contains(ProductInfo.getByType(index).type));
            buyBtn.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    if(User.get().doPayDollars(unlockPrice)) {
                        SoundUtil.playKassaClickSound();
                        for(int i = index; i < index + 4; i++) {
                            User.get().buyedProducts.add(i);
                        }
                        User.get().saveUser();
                        buyBtn.setVisible(false);
                        soldTable.setVisible(true);
                    }
                    super.tap(event, x, y, count, button);
                }
            });
            productsTable.addActor(buyBtn);
        } else {
            // кнопку "купить полную версию" показываем если продукт не куплен
            Button unlockBtn = createRedBtn(tableHeight * 0.65f);
            unlockBtn.setPosition(productsTable.getWidth() - unlockBtn.getWidth() - 2 * pad / 3, productsTable.getHeight() - unlockBtn.getHeight() - pad);
            unlockBtn.setVisible(!User.get().buyedProducts.contains(ProductInfo.getByType(index).type));
            productsTable.addActor(unlockBtn);
        }

        return productsTable;
    }

    private Vector2 calcImageSize(TextureRegion textureRegion, float parentHeight) {
        float iconHeight, iconWidth;
        if(textureRegion.getRegionHeight() >= (textureRegion.getRegionWidth() + 10)) {
            iconHeight = parentHeight * 0.65f;
            iconWidth = iconHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
        } else {
            iconWidth = parentHeight * 0.65f;
            iconHeight = iconWidth * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
        }
        return new Vector2(iconWidth, iconHeight);
    }

    private Button createStartBtn() {
        TextureRegion textureRegion = ATLAS_1.findRegion("button_green");
        final Button startBtn = new Button(
            new TextureRegionDrawable(textureRegion),
            new TextureRegionDrawable(textureRegion)
        );
        Label label = new Label("СТАРТ", new Label.LabelStyle(FONT_BIG_TOYZ, Color.DARK_GRAY)); //todo I18N
        label.setAlignment(Align.center);
        float w = label.getWidth() * 1.5f;
        float h = w * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
        startBtn.setSize(w, h);
        startBtn.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                User.get().day++;
                User.get().saveUser();
                GameApplication.get().setNextDayScreen();
                super.tap(event, x, y, count, button);
            }
        });
        label.setPosition(startBtn.getWidth() / 2 - label.getWidth() / 2, startBtn.getHeight() / 2 - label.getHeight() / 2);
        startBtn.add(label);

        return startBtn;
    }

    private Button createBuyBtn(int cost, float groupHeight) {
        float btnHeight = groupHeight * 0.95f;
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_gray"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        TextureRegionDrawable buttonGreenDown = new TextureRegionDrawable(ATLAS_1.findRegion("button_green_down"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.down = buttonGreenDown;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button buyButton = new Button(style) {
            @Override
            public void act(float delta) {
                boolean canPay = User.get().canPayDollars(cost);
                setDisabled(!canPay);
                super.act(delta);
            }
        };
        float h = btnHeight;
        float w = btnHeight;
        buyButton.setSize(w, h);
        Table costpanel = createCostPanel(cost);
        buyButton.add(costpanel).align(Align.center).fill();

        return buyButton;
    }

    private Button createRedBtn(float groupHeight) {
        float btnHeight = groupHeight * 0.95f;
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_gray"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        TextureRegionDrawable buttonGreenDown = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.down = buttonGreenDown;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button redButton = new Button(style);
        redButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().setBuyFullVersionScreen();
                super.tap(event, x, y, count, button);
            }
        });
        float h = btnHeight;
        float w = btnHeight;
        redButton.setSize(w, h);

        Label label = new Label("купить" +
            "\n полную" +
            "\n версию", new Label.LabelStyle(FONT_SMALL, Color.DARK_GRAY)); //todo I18N
        label.setAlignment(Align.center);
        redButton.add(label).align(Align.center).fill();

        return redButton;
    }

    private Group createSoldTable(float groupHeight) {
        float h = groupHeight;
        float w = h;
        Group group = new Group();
        group.setSize(w, h);
        Image image = new Image(ATLAS_1.findRegion("sold_table"));
        image.setSize(w, h);
        image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
        image.setScale(1.1f);
        image.setPosition(group.getWidth() / 2 - image.getWidth() / 2, group.getHeight() / 2- image.getHeight() / 2);
        group.addActor(image);

        Label label = new Label("ПРОДАНО", new Label.LabelStyle(FONT_SMALL, Color.DARK_GRAY)); //todo I18N
        label.setAlignment(Align.center);
        label.setPosition(group.getWidth() / 2 - label.getWidth() / 2, group.getHeight() * 0.675f - label.getHeight() / 2);
        group.addActor(label);
        group.setRotation(-30);

        return group;
    }

    private Table createCostPanel(ProductInfo productInfo) {
        return createCostPanel(productInfo.cost);
    }

    private Table createCostPanel(float cost) {
        Table costTable = new Table();
        Label costLabel = new Label(NumberFormat.formatCost(cost), new Label.LabelStyle(FONT_HEADER, Color.WHITE));
        costLabel.setAlignment(Align.center);

        Image costImg = new Image(ATLAS_1.findRegion("icon_dollar"));
        costImg.setSize(costLabel.getHeight() * 1.0f, costLabel.getHeight() * 1.0f);
        costTable.add(costImg).size(costImg.getWidth(), costImg.getHeight()).padRight(10).align(Align.right);
        costTable.add(costLabel).size(costLabel.getWidth(), costLabel.getHeight()).align(Align.left);

        costTable.setSize(costImg.getWidth() + costLabel.getWidth(), costImg.getHeight());
        return costTable;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }
}
