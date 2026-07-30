package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_BIG;
import static com.plagame.game.kassa.Resources.ATLAS_1;
import static com.plagame.game.kassa.enums.ColorInfo.LOADING_SCREEN_BG_COLOR;

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
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.components.HeaderPanel;
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.AssetUtil;
import com.plagame.game.kassa.utils.NumberFormat;

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
//        dialogTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_bg")));
        dialogTable.padBottom(pad * 4);

        Label totalLabel = new Label("МАГАЗИН", new Label.LabelStyle(FONT_VERY_BIG, Color.WHITE)); //todo I18N
        totalLabel.setAlignment(Align.center);
        dialogTable.add(totalLabel).align(Align.center).pad(pad * 2).padTop(pad * 3).fill();
        dialogTable.row();

//        Table dollarsTable = new Table();
//        Label dollarsLabel = new Label("1267", new Label.LabelStyle(FONT_VERY_BIG, Color.YELLOW)); //todo
////        Label dollarsLabel = new Label(String.valueOf(User.get().dollars), new Label.LabelStyle(FONT_VERY_BIG, Color.WHITE));
//        dollarsLabel.setAlignment(Align.left);
//        Image dollarImage = new Image(ATLAS_1.findRegion("icon_dollar"));
//        dollarImage.setSize(dollarsLabel.getHeight(), dollarsLabel.getHeight());
//        dollarsTable.add(dollarImage).size(dollarImage.getWidth()).align(Align.right);
//        dollarsTable.add(dollarsLabel).align(Align.left).pad(pad);
//        dialogTable.add(dollarsTable).align(Align.top).fill();
//        dialogTable.row();

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

        Button btnStart = createStartBtn(width * 0.9f);
        btnStart.setPosition(GameApplication.get().screenWidth / 2 - btnStart . getWidth() / 2, pad / 2);
        stage.addActor(btnStart);
    }

    @Override
    public void render(float delta) {
        AssetUtil.clearScreen(LOADING_SCREEN_BG_COLOR.color);
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
        float maxWidth = tableWidth / 4;
        Table productsTable = new Table();
        productsTable.setSize(tableWidth, tableHeight);
//        productsTable.setDebug(true);
        productsTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_goods_bg")));
        productsTable.pad(pad / 2);

        productsTable.add().expandX();

        for(int i = index; i < index + 4; i++) {
            ProductInfo productInfo = ProductInfo.getByType(i);
            TextureRegion textureRegion = productInfo.getTextureRegion();
            Vector2 vector2 = calcImageSize(textureRegion, imageHeight);
            Group group = new Group();
            group.setSize(tableHeight * 0.49f, tableHeight);

            Table costPanel = createCostPanel(productInfo);
            costPanel.setPosition(group.getWidth() / 2 - costPanel.getWidth() / 2, pad);
            group.addActor(costPanel);

            Image productImage = new Image(productInfo.getTextureRegion());
            productImage.setSize(vector2.x, vector2.y);
//            productImage.setScale(1.05f);
            productImage.setScale(1.1f);
            productImage.setPosition(group.getWidth() / 2 - productImage.getWidth() / 2, costPanel.getHeight() + (group.getHeight() - costPanel.getHeight()) / 2 - productImage.getHeight() / 2);
            group.addActor(productImage);

            float padLeft = i == 0 ? pad * 3 : 0;
            productsTable.add(group).align(Align.center).pad(0, padLeft, pad / 2, pad / 2).expandX().fill();
        }

        productsTable.add().size(imageHeight * 0.55f).expandX();

        Button buyBtn = createBuyBtn(100, tableHeight * 0.65f);
        buyBtn.setPosition(productsTable.getWidth() - buyBtn.getWidth() - 2 * pad / 3, productsTable.getHeight() - buyBtn.getHeight() - pad);
        productsTable.addActor(buyBtn);
//        productsTable.add(buyBtn).size(buyBtn.getWidth(), buyBtn.getHeight()).align(Align.topRight).padTop(pad).padRight(pad / 2).expandX().fill();

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

    private Button createStartBtn(float width) {
        float btnHeight = width * 0.15f;
        final Button startBtn = new Button(
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok")),
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok"))
        );
        float h = btnHeight;
        float w = startBtn.getWidth() * h / startBtn.getHeight();
        startBtn.setSize(w, h);
        startBtn.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                GameApplication.get().setNextDayScreen();
                super.tap(event, x, y, count, button);
            }
        });
        return startBtn;
    }

    private Button createBuyBtn(int cost, float groupHeight) {
        float btnHeight = groupHeight * 0.95f;
        final Button buyBtn = new Button(
            new TextureRegionDrawable(ATLAS_1.findRegion("button_green")),
            new TextureRegionDrawable(ATLAS_1.findRegion("button_green"))
        );
        float h = btnHeight;
        float w = btnHeight;
        buyBtn.setSize(w, h);
        buyBtn.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                //todo
                super.tap(event, x, y, count, button);
            }
        });

        Table costpanel = createCostPanel(100 + 50 * GameConfig.random.nextInt(20)); //todo
        buyBtn.add(costpanel).align(Align.center).fill();

        return buyBtn;
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
