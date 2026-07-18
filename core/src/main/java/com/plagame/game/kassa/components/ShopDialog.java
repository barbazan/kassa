package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.GameApplication.FONT_SMALL;
import static com.plagame.game.kassa.GameApplication.FONT_VERY_SMALL;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.enums.ShopInfo;
import com.plagame.game.kassa.enums.UpgradeInfo;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Дмитрий Малышев on 30.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ShopDialog extends Table {

    private final ShopInfo shopInfo;
    private final Table headerTable;
    private final Table bodyTable;
    private final Map<Integer, Cell<Table>> dialogPanelsMap = new HashMap<>();
    private float dialogWidth, dialogHeight, imageSize, pad;

    public ShopDialog(ShopInfo shopInfo) {
        this.shopInfo = shopInfo;
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
        headerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_header")));
        Image headerImage = new Image(shopInfo.getIconTexture());
        headerImage.setSize(imageSize * 1.5f, imageSize * 1.5f);
        headerTable.add(headerImage).size(headerImage.getWidth(), headerImage.getHeight()).align(Align.left).pad(pad);

        Table t = new Table();
        Label headerLabel = new Label(shopInfo.name, new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        headerLabel.setAlignment(Align.center);
        t.add(headerLabel).align(Align.center).padTop(pad).expandX().fill();
        t.row();

        Table maxButton = createMaxSwitchButton(imageSize, pad);
        t.add(maxButton).pad(pad).expandX();
        headerTable.add(t).align(Align.center).expandX().fill();

        Image closeImage = new Image(ATLAS_1.findRegion("icon_close"));
        closeImage.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.hideShopDialog();
                GameApplication.get().getGameScreen().gameScene.refreshScene(); // после закрытия диалога рефрешим сцену
            }
        });
        closeImage.setSize(imageSize, imageSize);
        headerTable.add(closeImage).size(closeImage.getWidth(), closeImage.getHeight()).align(Align.topRight).pad(pad);

        add(headerTable).align(Align.top).expandX().fill();
        row();

        bodyTable = new Table();
        bodyTable.setSize(dialogWidth, dialogHeight - headerTable.getHeight());
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_body")));
        Table scrolledTable = new Table();
        scrolledTable.setSize(bodyTable.getWidth(), bodyTable.getHeight());

        for(UpgradeInfo upgradeInfo : shopInfo.getUpgradesList()) {
            Table upgradePanel = createUpgradeInfoPanel(upgradeInfo);
            Cell<Table> cell = scrolledTable.add(upgradePanel).align(Align.left).pad(pad).expandX().fill();
            dialogPanelsMap.put(upgradeInfo.type, cell);
            scrolledTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(scrolledTable);
        scrollPane.setScrollingDisabled(true, false);
        bodyTable.add(scrollPane).pad(pad / 2).padBottom(pad * 2).expandX().fill();
        add(bodyTable).expandX().fill();
    }

    private Table createUpgradeInfoPanel(UpgradeInfo upgradeInfo) {
        float panelWidth = headerTable.getWidth() * 0.95f;
        float iconSize = panelWidth / 5;
        float pad = iconSize / 10;
        Table table = new Table();
        table.setWidth(panelWidth);
//        table.setDebug(true);

        Table titleTable = new Table();
        titleTable.setWidth(panelWidth);
        titleTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_header")));
        Label titleLabel = new Label(upgradeInfo.name, new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        titleLabel.setAlignment(Align.center);
        titleTable.add(titleLabel).align(Align.center).expandX().fill();
        table.add(titleTable).expandX().fillX();
        table.row();

        Table bodyTable = new Table();
        bodyTable.setWidth(panelWidth);
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_body")));

        Group iconGroup = new Group();
        iconGroup.setSize(iconSize, iconSize);
        Image iconBgImage = new Image(ATLAS_1.findRegion("icon_bg"));
        iconBgImage.setSize(iconSize, iconSize);
        iconGroup.addActor(iconBgImage);
        Image iconImage = new Image(upgradeInfo.getTexture());
        iconImage.setSize(iconSize, iconSize);
        iconGroup.addActor(iconImage);
        bodyTable.add(iconGroup).size(iconSize).align(Align.left).pad(pad);

        // бонус за апгрейд или инфа какие предметы прокачать
        if(User.get().isUpgradeLock(upgradeInfo)) {
            Table lockTable = createLockPanel(upgradeInfo);
            bodyTable.add(lockTable).align(Align.center).pad(pad, 0, pad, 0).expand().fill();
        } else {
            Table bonusTable = createBonusPanel(upgradeInfo);
            bodyTable.add(bonusTable).align(Align.center).pad(pad, 0, pad, 0).expand().fill();
        }

        // Кнопки
        int curLevel = User.get().getUpgradeLevel(upgradeInfo);
        if(User.get().isUpgradeMax(upgradeInfo)) { // если апгрейд максимальный, то показываем кнопку "нужен переезд"
            Button button = createRedButton(" нужен \n переезд");
            bodyTable.add(button).align(Align.left).pad(pad).fillX();
        } else if(User.get().isUpgradeLock(upgradeInfo)) { // если залочен, то показываем кнопку "нужны другие предметы"
            Button button = createRedButton(" нужны \n другие \n вещи");
            bodyTable.add(button).align(Align.left).pad(pad).fillX();
        } else {
            long cost = upgradeInfo.getUpgradeCost(curLevel + 1);
            if(User.get().isMax && User.get().canPayDollars(cost)) { // кнопка мах покупки доступна если включена настройка МАХ и игрок может заплатить хоть за 1 ур
                Button buyMaxButton = createBuyMaxButton(upgradeInfo, curLevel);
                bodyTable.add(buyMaxButton).align(Align.left).pad(pad).fillX();
            } else { // кнопка покупки
                Button buyButton = createBuyButton(upgradeInfo, curLevel);
                bodyTable.add(buyButton).align(Align.left).pad(pad).fillX();
            }
        }

        bodyTable.row();

        // уровни с прогрессбаром
        Label curLevellabel = new Label("УР." + curLevel, new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK));
        curLevellabel.setAlignment(Align.center);
        bodyTable.add(curLevellabel).align(Align.center).pad(0, pad, pad, pad).padBottom(pad).fill();

        //todo progressbar
        bodyTable.add().pad(pad).expandX();

        long plusCountLevels = 1;
        if(User.get().isMax) {
            plusCountLevels = Math.max(1, upgradeInfo.getUpgradeCountMax(curLevel + 1));
        }
        Label nextLevellabel = new Label("+" + plusCountLevels + " УР.", new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK));
        nextLevellabel.setAlignment(Align.center);
        nextLevellabel.setVisible(!User.get().isUpgradeMax(upgradeInfo) && !User.get().isUpgradeLock(upgradeInfo));
        bodyTable.add(nextLevellabel).align(Align.center).pad(0, pad, pad, pad).fill();

        table.add(bodyTable).expandX().fill();
        return table;
    }

    private Table createMaxSwitchButton(float imageSize, float pad) {
        Table maxTable = new Table();
        maxTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("title_bg_2")));
        Label label1 = new Label("x1", new Label.LabelStyle(FONT_SMALL, Color.WHITE));
        maxTable.add(label1).align(Align.right).padLeft(pad);

        TextureRegionDrawable buttonOff = new TextureRegionDrawable(ATLAS_1.findRegion("button_switch_off"));
        TextureRegionDrawable buttonOn = new TextureRegionDrawable(ATLAS_1.findRegion("button_switch_on"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonOn;     // по умолчанию
        style.disabled = buttonOff;  // при нажатии (опционально)
        Button maxButton = new Button(style) {
            @Override
            public void act(float delta) {
                boolean isMax = User.get().isMax;
                setDisabled(!isMax);
//                refreshAllPanels();
                super.act(delta);
            }
        };
        maxButton.setSize(imageSize, imageSize / 2f);
        maxTable.add(maxButton).size(maxButton.getWidth(), maxButton.getHeight()).align(Align.center).pad(pad / 4, pad, pad / 4, pad);

        Label label2 = new Label("MAX", new Label.LabelStyle(FONT_SMALL, Color.WHITE));
        maxTable.add(label2).align(Align.left).padRight(pad);

        maxTable.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                SoundUtil.playClickSound();
                User.get().isMax = !User.get().isMax;
                User.get().saveUser();
                refreshAllPanels();
            }
        });
        return maxTable;
    }

    private Table createBonusPanel(UpgradeInfo upgradeInfo) {
        Table bonusTable = new Table();
        bonusTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_bonus_bg")));
        int curLevel = User.get().getUpgradeLevel(upgradeInfo);
        int tapAddToNextLevel = User.get().isMax ? upgradeInfo.getTapAddToMaxLevel(curLevel + 1) : upgradeInfo.getTapAddToNextLevel(curLevel + 1);
        Label bonusTapLabel = new Label("+" + tapAddToNextLevel + "% ", new Label.LabelStyle(FONT_VERY_SMALL, Color.OLIVE));
        bonusTapLabel.setAlignment(Align.right);
        bonusTable.add(bonusTapLabel).align(Align.right).padLeft(pad);
        Label bonusTapLabel2 = new Label(GameApplication.get().platform.i18n().get("TAP_INCOME"), new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK));
//        Label bonusTapLabel2 = new Label("TAP INCOME", new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK));
        bonusTapLabel2.setAlignment(Align.left);
        bonusTable.add(bonusTapLabel2).align(Align.left).pad(pad).padLeft(0);
        bonusTable.row();
        int pasAddToNextLevel = User.get().isMax ? upgradeInfo.getPasAddToMaxLevel(curLevel + 1) : upgradeInfo.getPasAddToNextLevel(curLevel + 1);
        Label bonusPasLabel = new Label("+" + pasAddToNextLevel + "% ", new Label.LabelStyle(FONT_VERY_SMALL, Color.FOREST));
        bonusPasLabel.setAlignment(Align.right);
        bonusTable.add(bonusPasLabel).padLeft(pad).align(Align.right);
        Label bonusPasLabel2 = new Label(GameApplication.get().platform.i18n().get("PAS_INCOME"), new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK));
//        Label bonusPasLabel2 = new Label("PAS INCOME", new Label.LabelStyle(FONT_VERY_SMALL, Color.BLACK));
        bonusPasLabel2.setAlignment(Align.left);
        bonusTable.add(bonusPasLabel2).align(Align.left).pad(pad).padLeft(0);
        return bonusTable;
    }

    private Table createLockPanel(UpgradeInfo upgradeInfo) {
        Table bonusTable = new Table();
        bonusTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("panel_bonus_bg")));
        Label bonusTapLabel = new Label("нужны другие вещи", new Label.LabelStyle(FONT_VERY_SMALL, Color.SALMON));
        bonusTapLabel.setAlignment(Align.center);
        bonusTable.add(bonusTapLabel).align(Align.center).padTop(pad).expandX().fill();
        bonusTable.row();

        Table imageTable = new Table();
        List<UpgradeInfo> list = User.get().getAnotherUpgradesNeedUpFor(upgradeInfo);
        for(UpgradeInfo upInfo : list) {
            Image image = new Image(upInfo.getTexture());
            image.setSize(imageSize * 0.75f, imageSize * 0.75f);
            imageTable.add(image).size(image.getWidth(), image.getHeight()).pad(pad / 2);
        }
        bonusTable.add(imageTable).align(Align.center).pad(pad);

        return bonusTable;
    }

    private Button createBuyButton(UpgradeInfo upgradeInfo, int curLevel) {
        long cost = upgradeInfo.getUpgradeCost(curLevel + 1);
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_gray"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button buyButton = new Button(style) {
            @Override
            public void act(float delta) {
                boolean canPay = User.get().canPayDollars(cost);
                setDisabled(!canPay);
                super.act(delta);
            }
        };
        Image dollarImage = new Image(ATLAS_1.findRegion("icon_dollars"));
        buyButton.add(dollarImage).size(imageSize / 2).align(Align.right).pad(pad / 2).padLeft(pad).padRight(0);
        Label costLabel = new NumberFormatLabel(String.valueOf(NumberFormat.format(cost)), new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)) {
            @Override
            public long getLongValue() {
                return cost;
            }
        };
        costLabel.setAlignment(Align.left);
        buyButton.add(costLabel).align(Align.left).pad(pad * 2, pad, pad * 2, pad).fill();
        buyButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(User.get().doPayDollars(cost)) {
                    SoundUtil.playSpentMoneySound();
                    User.get().buyUpgrade(upgradeInfo);
                    User.get().saveUser();
                    refreshAllPanels();
                }
            }
        });
        return buyButton;
    }

    private Button createBuyMaxButton(UpgradeInfo upgradeInfo, int curLevel) {
        long cost = upgradeInfo.getUpgradeCostMax(curLevel + 1);
        int count = upgradeInfo.getUpgradeCountMax(curLevel + 1);
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_gray"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button buyButton = new Button(style) {
            @Override
            public void act(float delta) {
                boolean canPay = User.get().canPayDollars(cost);
                setDisabled(!canPay);
                super.act(delta);
            }
        };
        Image dollarImage = new Image(ATLAS_1.findRegion("icon_dollars"));
        buyButton.add(dollarImage).size(imageSize / 2).align(Align.right).pad(pad / 2).padLeft(pad).padRight(0).fill();
        Label costLabel = new NumberFormatLabel(String.valueOf(NumberFormat.format(cost)), new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)) {
            @Override
            public long getLongValue() {
                return cost;
            }
        };
        costLabel.setAlignment(Align.left);
        buyButton.add(costLabel).align(Align.left).pad(pad * 2, pad, pad * 2, pad).fill();
        buyButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int pointer, int button) {
                if(User.get().doPayDollars(cost)) {
                    SoundUtil.playSpentMoneySound();
                    User.get().buyUpgrade(upgradeInfo, count);
                    User.get().saveUser();
                    refreshAllPanels();
                }
            }
        });
        return buyButton;
    }

    private Button createRedButton(String text) {
        TextureRegionDrawable buttonImage = new TextureRegionDrawable(ATLAS_1.findRegion("button_red"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonImage;     // по умолчанию
        style.disabled = buttonImage;  // при нажатии (опционально)
        Button buyButton = new Button(style);
        Label label = new Label(text, new Label.LabelStyle(FONT_VERY_SMALL, Color.WHITE));
        label.setAlignment(Align.center);
        buyButton.add(label).align(Align.center).pad(pad).fill();
        return buyButton;
    }

    private void refreshPanel(UpgradeInfo upgradeInfo) {
        Cell<Table> cell = dialogPanelsMap.get(upgradeInfo.type);
        cell.setActor(createUpgradeInfoPanel(upgradeInfo));
    }

    private void refreshAllPanels() {
        for(UpgradeInfo upgradeInfo : shopInfo.getUpgradesList()) {
            Cell<Table> cell = dialogPanelsMap.get(upgradeInfo.type);
            cell.setActor(createUpgradeInfoPanel(upgradeInfo));
        }
    }


}
