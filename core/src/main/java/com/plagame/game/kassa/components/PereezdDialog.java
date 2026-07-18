package com.plagame.game.kassa.components;

import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_HEADER;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.enums.LocationInfo;
import com.plagame.game.kassa.enums.UpgradeInfo;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.List;

/**
 * Created by Дмитрий Малышев on 15.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class PereezdDialog extends Table {

    private float dialogWidth, dialogHeight, imageSize, pad;
    private Table headerTable;
    private Table bodyTable;

    public PereezdDialog() {
        this.dialogWidth = GameApplication.get().screenWidth * 0.9f;
        this.dialogHeight = GameApplication.get().screenHeight * 0.6f;
        this.imageSize = dialogWidth / 10;
        this.pad = imageSize / 5;
        setSize(dialogWidth, dialogHeight);
        setPosition((GameApplication.get().screenWidth - dialogWidth) / 2, (GameApplication.get().screenHeight - dialogHeight) / 2);

        TextureRegion textureRegion = ATLAS_1.findRegion("icon_location");
//        initHeader(textureRegion, "Переезд", true);
        initHeader(textureRegion, User.get().getNextLocationInfo().name, true);
        initBody();
    }

    private void initHeader(TextureRegion icon, String text, boolean needCloseImage) {
        headerTable = new Table();
//        headerTable.setDebug(true);
        headerTable.setWidth(dialogWidth);
        headerTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_header")));
        Image headerImage = new Image(icon);
        headerImage.setSize(imageSize * 1.5f, imageSize * 1.5f);
        headerTable.add(headerImage).size(headerImage.getWidth(), headerImage.getHeight()).align(Align.left).pad(pad);

        Label headerLabel = new Label(text, new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        headerLabel.setAlignment(Align.center);
        headerTable.add(headerLabel).align(Align.center).expandX().fill();

        Image closeImage = new Image(ATLAS_1.findRegion("icon_close"));
        closeImage.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SoundUtil.playClickSound();
                GameApplication.get().getGameScreen().uiStage.hidePereezdDialog();
            }
        });
        closeImage.setSize(imageSize, imageSize);
        headerTable.add(closeImage).size(closeImage.getWidth(), closeImage.getHeight()).align(Align.topRight).pad(pad);
        closeImage.setVisible(needCloseImage);

        add(headerTable).align(Align.top).expandX().fill();
        row();
    }

    private void initBody() {
        bodyTable = new Table();
        bodyTable.setSize(dialogWidth, dialogHeight - headerTable.getHeight());
        bodyTable.setBackground(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_body")));
        Table scrolledTable = new Table();
        scrolledTable.setSize(bodyTable.getWidth(), bodyTable.getHeight());
//        scrolledTable.setBackground(new TextureRegionDrawable(User.get().getLocationInfo().getNextLocationTexture()));
        TextureRegion textureRegion = User.get().getLocationInfo().getNextLocationTexture();
        Image nextLocationImage = new Image(textureRegion);
        float w = bodyTable.getWidth() * 0.8f;
        float h = w * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
        nextLocationImage.setSize(w, h);
        scrolledTable.add(nextLocationImage).size(nextLocationImage.getWidth(), nextLocationImage.getHeight());
        scrolledTable.row();

        Button button = new Button(new TextureRegionDrawable(ATLAS_1.findRegion("dialog_line")));
        Label bodyLabel = new Label("Доход x" + User.get().getNextLocationInfo().getMultiplierAsString(), new Label.LabelStyle(FONT_DIALOG_HEADER, Color.WHITE));
        bodyLabel.setAlignment(Align.center);
        button.add(bodyLabel).align(Align.center).pad(pad * 2).padRight(0);

        Image dollarImage = new Image(ATLAS_1.findRegion("icon_dollar"));
        dollarImage.setSize(imageSize * 0.65f, imageSize * 0.65f);
        button.add(dollarImage).size(dollarImage.getWidth(), dollarImage.getHeight()).pad(pad / 2);

        scrolledTable.add(button).align(Align.top).expandX().fill();
        bodyTable.add(scrolledTable).pad(pad / 2).expand().fill();

        bodyTable.row();

        if(User.get().hasNextLocation()) {
            if(!User.get().isAllUpgradesMax()) {
                Table t = new Table();
                Label labelNeed = new Label("Нужны вещи:", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
                t.add(labelNeed).align(Align.right).pad(pad);
                List<UpgradeInfo> list = User.get().getUpgradesNeedForPereezd();
                if(list.size() > 5) {
                    list = list.subList(0, 5);
                }
                for(UpgradeInfo upgradeInfo : list) {
                    Image image = new Image(upgradeInfo.getTexture());
                    image.setSize(imageSize * 0.75f, imageSize * 0.75f);
                    t.add(image).size(image.getWidth(), image.getHeight()).pad(pad / 2);
                }
                bodyTable.add(t).align(Align.center).expandX().fill();
            }
        } else {
            Label labelLater = new Label("В РАЗРАБОТКЕ ДО ОКТЯБРЯ", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE));
            labelLater.setAlignment(Align.center);
            bodyTable.add(labelLater).align(Align.center).expandX().fill().pad(pad);
        }
        bodyTable.row();
        Button pereezdButton = createGreenButton();
        bodyTable.add(pereezdButton).align(Align.center).pad(pad);

        add(bodyTable).expand().fill();
    }

    private Button createGreenButton() {
        TextureRegionDrawable buttonGray = new TextureRegionDrawable(ATLAS_1.findRegion("button_gray"));
        TextureRegionDrawable buttonGreen = new TextureRegionDrawable(ATLAS_1.findRegion("button_green"));
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = buttonGreen;     // по умолчанию
        style.disabled = buttonGray;  // при нажатии (опционально)
        Button buyButton = new Button(style) {
            @Override
            public void act(float delta) {
                boolean canPereezd = User.get().isAllUpgradesMax();
                setDisabled(!canPereezd);
                setVisible(User.get().hasNextLocation());
                super.act(delta);
            }
        };
        Label costLabel = new Label("ПЕРЕЕХАТЬ", new Label.LabelStyle(FONT_DIALOG_BUTTON, Color.WHITE)) ;
        costLabel.setAlignment(Align.left);
        buyButton.add(costLabel).align(Align.left).pad(pad * 2, pad, pad * 2, pad).fill();
        buyButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(User.get().hasNextLocation()) {
                    if(User.get().isAllUpgradesMax()) {
                        User.get().location = Math.min(User.get().location + 1, LocationInfo.MAX_LOCATION.type);
                        User.get().saveUser();
                        GameApplication.get().setGameScreen();
                    }
                }
            }
        });
        buyButton.setDisabled(!User.get().isAllUpgradesMax());
        return buyButton;
    }


}
