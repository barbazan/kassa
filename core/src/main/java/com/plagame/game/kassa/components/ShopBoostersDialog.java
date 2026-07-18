package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_SMALL;
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
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.enums.BoosterInfo;
import com.plagame.game.kassa.enums.ColorInfo;
import com.plagame.game.kassa.utils.SoundUtil;
import com.plagame.game.kassa.utils.Time;

/**
 * Created by Дмитрий Малышев on 24.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ShopBoostersDialog extends Table {
    private final Table headerTable;
    private final Table bodyTable;
    private final Table footerTable;
    private float dialogWidth, dialogHeight, imageSize, pad;

    public ShopBoostersDialog() {
        this.dialogWidth = GameApplication.get().screenWidth * 0.96f;
        this.dialogHeight = Math.max(dialogWidth, GameApplication.get().screenHeight * 0.75f);
        this.imageSize = dialogWidth / 10;
        this.pad = imageSize / 5;
        setSize(dialogWidth, dialogHeight);
        setPosition((GameApplication.get().screenWidth - dialogWidth) / 2, (GameApplication.get().screenHeight - dialogHeight) / 2);
//        setDebug(true);

        headerTable = new Table();
//        headerTable.setDebug(true);
        headerTable.setWidth(dialogWidth);
        headerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_header_actions")));

        Label headerLabel = new Label("БУСТЕРЫ", new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        headerLabel.setAlignment(Align.center);
        headerTable.add(headerLabel).align(Align.center).padTop(pad).expandX().fill();

        add(headerTable).align(Align.top).expandX().fill();
        row();


        bodyTable = new Table();
        bodyTable.setSize(dialogWidth, dialogHeight - headerTable.getHeight());
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("action_dialog_body")));

        Table scrolledTable = new Table();
        scrolledTable.setSize(bodyTable.getWidth(), bodyTable.getHeight());

        for(BoosterInfo boosterInfo : BoosterInfo.values()) {
            Table buffPanel = createBuffPanel(boosterInfo);
            scrolledTable.add(buffPanel).align(Align.left).pad(pad).expandX().fill();
            scrolledTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(scrolledTable);
        scrollPane.setScrollingDisabled(true, false);
        bodyTable.add(scrollPane).pad(pad / 2).padBottom(pad * 2).expandX().fill();

        add(bodyTable).expandX().fill();
        row();


        footerTable = new Table();
//        footerTable.setDebug(true);
        footerTable.setSize(dialogWidth, dialogHeight / 10);
        footerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("action_dialog_footer")));

        Button closeButton = createCloseButton();
        footerTable.add(closeButton).size(imageSize * 3.0f, imageSize * 1.25f).align(Align.center).pad(pad).padTop(0);
        Button shopButton = createShopBoosterButton();
        footerTable.add(shopButton).size(imageSize * 3.5f, imageSize * 1.25f).align(Align.center).pad(pad).padTop(0);

        add(footerTable).expandX().fill();

    }


    private Table createBuffPanel(BoosterInfo boosterInfo) {
        float panelWidth = headerTable.getWidth() * 0.95f;
        Table panelTable = new Table();
        panelTable.setWidth(panelWidth);
        panelTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_action_bg")));
//        panelTable.setDebug(true);

        Table leftTable = new Table();

        Image iconImage = new Image(boosterInfo.getTextureRegion());
        iconImage.setSize(imageSize * 2f, imageSize * 2f);
        leftTable.add(iconImage).size(iconImage.getWidth(), iconImage.getHeight()).align(Align.top).pad(pad).padTop(pad * 2).padRight(0).fill();

        leftTable.row();

        Label timeLabel = new ModelLabel("", new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK)) {
            @Override
            public void act(float delta) {
                setVisible(User.get().getBoosterTimeleft(boosterInfo) > 0);
                super.act(delta);
            }

            @Override
            protected String getValue() {
                if(User.get().getBoosterTimeleft(boosterInfo) > 0) {
                    return new Time(User.get().getBoosterTimeleft(boosterInfo)).toStringInHumanFormat2();
                }
                return "11:22:33";
            }
        };
        timeLabel.setAlignment(Align.center);
        leftTable.add(timeLabel).align(Align.top).padLeft(pad).fill();

        leftTable.row();
        leftTable.add().expand().fill();

        panelTable.add(leftTable).fill();



        Table rightTable = new Table();
//        rightTable.setDebug(true);
        Color color = GameConfig.random.nextBoolean() ? ColorInfo.ACTION_GREEN.color : ColorInfo.ACTION_ORANGE.color; //todo remove
        Label nameLabel = new Label(boosterInfo.name, new Label.LabelStyle(FONT_DIALOG_HEADER, color));
        nameLabel.setAlignment(Align.center);
        rightTable.add(nameLabel).align(Align.center).expandX();
        rightTable.row();

        Table rewardTable = new Table();
        Label descLabel = new Label(boosterInfo.description, new Label.LabelStyle(FONT_SMALL, Color.WHITE));
        descLabel.setAlignment(Align.left);
        rewardTable.add(descLabel).align(Align.left).pad(pad).fill();

        rightTable.add(rewardTable).expandX().fill();
        rightTable.row();

        Button buyButton = createBuyButton(boosterInfo);
        rightTable.add(buyButton).align(Align.center).pad(pad).padTop(0).expandX().fill();

        panelTable.add(rightTable).align(Align.center).pad(pad).padLeft(0).expand().fill();
        return panelTable;
    }

    private Button createBuyButton(BoosterInfo boosterInfo) {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy_disable"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_action_buy"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button buyButton = new Button(style) {
            @Override
            public void act(float delta) {
                boolean canPay = User.get().canPayGold(boosterInfo.costGold);
                setDisabled(!canPay);
                super.act(delta);
            }
        };

        Image goldIcon = new Image(ATLAS_1.findRegion("icon_gold"));
        goldIcon.setSize(imageSize / 2, imageSize / 2);
        buyButton.add(goldIcon).size(goldIcon.getWidth(), goldIcon.getHeight()).align(Align.right).pad(pad).padRight(0).fill();

        Label costLabel = new Label(String.valueOf(boosterInfo.costGold), new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
        costLabel.setAlignment(Align.left);
        buyButton.add(costLabel).align(Align.left).pad(pad * 2).padLeft(pad).fill();
        buyButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(User.get().doPayGold(boosterInfo.costGold)) {
                    SoundUtil.playSpentMoneySound();
                    User.get().buyBooster(boosterInfo);
                    User.get().saveUser();
                }
            }
        });
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
                GameApplication.get().getGameScreen().uiStage.hideShopBoostersDialog();
                GameApplication.get().getGameScreen().uiStage.refresh();
                GameApplication.get().getGameScreen().uiStage.checkFullscreenAdv();
            }
        });
        return button;
    }

    private Button createShopBoosterButton() {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_shop"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_shop"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button button = new Button(style);
        Label label = new Label("ЗОЛОТО", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
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

    public void refresh() {
        GameApplication.get().getGameScreen().uiStage.showShopBoostersDialog();
    }

}
