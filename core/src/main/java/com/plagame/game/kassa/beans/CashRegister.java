package com.plagame.game.kassa.beans;

import static com.plagame.game.kassa.GameApplication.FONT_DEFAULT;
import static com.plagame.game.kassa.GameApplication.FONT_DIALOG_BUTTON;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;

/**
 * Created by Дмитрий Малышев on 21.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class CashRegister extends Group {

    public CashRegister() {
        init();
    }

    private void init() {
        Group kassaGroup = createKassa();
        addActor(kassaGroup);

        float terminalHeight = kassaGroup.getHeight() * 0.55f;
        Group terminalGroup = createTerminal(terminalHeight);
        terminalGroup.setPosition(kassaGroup.getWidth(), kassaGroup.getHeight() - terminalHeight);
        addActor(terminalGroup);

        setSize(kassaGroup.getWidth() + terminalGroup.getWidth(), kassaGroup.getHeight());
    }

    private Group createKassa() {
        Group kassaGroup = new Group();
        Image imageKassa = new Image(ATLAS_1.findRegion("kassa"));
        float kassaHeight = GameApplication.get().screenHeight * 0.71f;
        float kassaWidth = imageKassa.getWidth() * kassaHeight / imageKassa.getHeight();
        imageKassa.setSize(kassaWidth, kassaHeight);
        kassaGroup.setSize(kassaWidth, kassaHeight);
        kassaGroup.addActor(imageKassa);
        return kassaGroup;
    }

    private Group createTerminal(float height) {
        Group terminalGroup = new Group();
        Image imageTerminal = new Image(ATLAS_1.findRegion("terminal"));
        float terminalHeight = height;
        float terminalWidth = imageTerminal.getWidth() * terminalHeight / imageTerminal.getHeight();
        imageTerminal.setSize(terminalWidth, terminalHeight);
        terminalGroup.setSize(terminalWidth, terminalHeight);
        terminalGroup.addActor(imageTerminal);

        float btnSize = terminalWidth / 5;
        float btnPad = btnSize / 3;
        Table btnTable = new Table();
        btnTable.pad(btnPad * 1.5f);
//        btnTable.setDebug(true);
        btnTable.setSize(terminalWidth, terminalHeight);
        btnTable.add().colspan(3).expand();
        btnTable.row();

        Label costLabel = new Label("12.55", new Label.LabelStyle(FONT_DEFAULT, Color.BLACK));
        costLabel.setAlignment(Align.center);
        btnTable.add(costLabel).pad(btnPad).align(Align.center).colspan(3).fill();
        btnTable.row();

        for(int i = 1; i <= 9; i++) {
            Button btn = new Button(
                new TextureRegionDrawable(ATLAS_1.findRegion("btn_" + i + "_up")),
                new TextureRegionDrawable(ATLAS_1.findRegion("btn_" + i + "_down"))
            );
            btn.setSize(btnSize, btnSize);
            float padLeft = btnPad / 2;
            if(i % 3 == 1) {
                padLeft = 0;
            }
            btnTable.add(btn).size(btnSize, btnSize).padLeft(padLeft).padTop(btnPad);
            if(i % 3 == 0) {
                btnTable.row();
            }
        }

        Button btnOk = new Button(
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok")),
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok"))
        );
        float h = btnSize * 1.1f;
        float w = btnOk.getWidth() * h / btnOk.getHeight();
        btnOk.setSize(w, h);
        btnTable.add(btnOk).size(btnOk.getWidth(), btnOk.getHeight()).colspan(3).align(Align.center).pad(btnPad * 2);

        terminalGroup.addActor(btnTable);
        return terminalGroup;
    }
}
