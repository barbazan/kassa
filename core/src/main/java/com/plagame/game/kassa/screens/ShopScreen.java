package com.plagame.game.kassa.screens;

import static com.plagame.game.kassa.GameApplication.FONT_DEFAULT;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_BIG;
import static com.plagame.game.kassa.Resources.ATLAS_1;

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
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.Vector;

/**
 * Created by Дмитрий Малышев on 27.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ShopScreen extends BaseScreen {

    private Table dialogTable;

    public ShopScreen() {
        init();
    }

    private void init() {
        float width = GameApplication.get().minScreenSize * 0.95f;
        float height = GameApplication.get().screenHeight * 0.95f;
        float pad = width / 20;
        dialogTable = new Table();
//        dialogTable.setDebug(true);
        dialogTable.setSize(width, height);
        dialogTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_bg")));
        dialogTable.padBottom(pad * 4);

        Label totalLabel = new Label("SHOP", new Label.LabelStyle(FONT_VERY_BIG, Color.WHITE)); //todo I18N
        totalLabel.setAlignment(Align.center);
        dialogTable.add(totalLabel).align(Align.center).padTop(pad * 2).fill();
        dialogTable.row();

        Table dollarsTable = new Table();
        Label dollarsLabel = new Label("1267", new Label.LabelStyle(FONT_VERY_BIG, Color.YELLOW)); //todo
//        Label dollarsLabel = new Label(String.valueOf(User.get().dollars), new Label.LabelStyle(FONT_VERY_BIG, Color.WHITE));
        dollarsLabel.setAlignment(Align.left);
        Image dollarImage = new Image(ATLAS_1.findRegion("icon_dollar"));
        dollarImage.setSize(dollarsLabel.getHeight(), dollarsLabel.getHeight());
        dollarsTable.add(dollarImage).size(dollarImage.getWidth()).align(Align.right);
        dollarsTable.add(dollarsLabel).align(Align.left).pad(pad);
        dialogTable.add(dollarsTable).align(Align.top).fill();
        dialogTable.row();

        for(int i = 0; i < 20; i++) {
            Table goodTable = createGoodsTable(width * 0.9f);
            dialogTable.add(goodTable).size(goodTable.getWidth(), goodTable.getHeight()).align(Align.top).expandX().fill();
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
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Table createGoodsTable(float width) {
        float height = width / 2.7f;
        float iconHeight = height * 0.75f;
        float pad = iconHeight / 8;
        float groupWidth = (width - pad * 5) / 5;
        Table productsTable = new Table();
        productsTable.setSize(width, height);
//        productsTable.setDebug(true);
        productsTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_goods_bg")));

        for(int i = 0; i < 4; i++) {
            ProductInfo productInfo = ProductInfo.getRandom();
            TextureRegion textureRegion = productInfo.getTextureRegion();
            Vector2 vector2 = calcImageSize(textureRegion, height);
            Group group = new Group();
            group.setSize(groupWidth, height);

            Table costTable = new Table();
            Label costLabel = new Label(NumberFormat.formatCost(productInfo.cost), new Label.LabelStyle(FONT_DEFAULT, Color.YELLOW));
            costLabel.setAlignment(Align.center);

            Image costImg = new Image(ATLAS_1.findRegion("icon_dollar"));
            costImg.setSize(costLabel.getHeight() * 1.0f, costLabel.getHeight() * 1.0f);
//            costImg.setPosition(costLabel.getX() - costImg.getWidth() - 5, costLabel.getY());
//            group.addActor(costImg);

//            costLabel.setPosition(group.getWidth() / 2 - costLabel.getWidth() / 2, pad);
//            group.addActor(costLabel);
            costTable.setSize(costImg.getWidth() + costLabel.getWidth() + pad / 2, costImg.getHeight());
            costTable.add(costImg).padRight(pad / 2).align(Align.right);
            costTable.add(costLabel).align(Align.left);

            costTable.setPosition(group.getWidth() / 2 - costTable.getWidth() / 2, pad);
            group.addActor(costTable);

            Image image = new Image(productInfo.getTextureRegion());
            image.setSize(vector2.x, vector2.y);
            image.setPosition(group.getWidth() / 2 - image.getWidth() / 2, costLabel.getHeight() + pad);
            group.addActor(image);


            productsTable.add(group).size(group.getWidth(), group.getHeight()).align(Align.top).expandX().fill();
        }

        return productsTable;
    }

    private Vector2 calcImageSize(TextureRegion textureRegion, float parentHeight) {
        float iconHeight, iconWidth;
        if(textureRegion.getRegionHeight() > textureRegion.getRegionWidth()) {
            iconHeight = parentHeight * 0.75f;
            iconWidth = iconHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
        } else {
            iconWidth = parentHeight * 0.75f;
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
                GameApplication.get().setGameScreen();
                super.tap(event, x, y, count, button);
            }
        });

        return startBtn;
    }
}
