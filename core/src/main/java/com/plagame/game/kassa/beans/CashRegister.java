package com.plagame.game.kassa.beans;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
        Image imageKassa = new Image(ATLAS_1.findRegion("kassa"));
        float kassaHeight = GameApplication.get().screenHeight * 0.71f;
        float kassaWidth = imageKassa.getWidth() * kassaHeight / imageKassa.getHeight();
        imageKassa.setSize(kassaWidth, kassaHeight);
        addActor(imageKassa);

        Image imageTerminal = new Image(ATLAS_1.findRegion("terminal"));
        float terminalHeight = kassaHeight * 0.55f;
        float terminalWidth = imageTerminal.getWidth() * terminalHeight / imageTerminal.getHeight();
        imageTerminal.setSize(terminalWidth, terminalHeight);
        imageTerminal.setPosition(kassaWidth, kassaHeight - terminalHeight);
        addActor(imageTerminal);

        setSize(kassaWidth + terminalWidth, kassaHeight);
    }
}
