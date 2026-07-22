package com.plagame.game.kassa.beans;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.enums.SceneObjectInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Дмитрий Малышев on 10.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameScene extends Group {

    public Map<Integer, SceneObject> objectsMap = new HashMap<>(); // предметы на сцене: машина, дом и т.д.

    public GameScene() {
        init();
    }

    private void init() {
        setSize(GameApplication.get().screenWidth, GameApplication.get().screenHeight);
        addSceneObject(SceneObjectInfo.BACKGROUND);
        addConveerLenta();
        addCashRegister();
    }

    private void addSceneObject(SceneObjectInfo sceneObjectInfo) {
        SceneObject sceneObject = new SceneObject(sceneObjectInfo); // если есть экшн то в конструкторе он включится сам
        objectsMap.put(sceneObjectInfo.type, sceneObject);
        addActor(sceneObject);
    }


    private void addConveerLenta() {
        ConveerLenta conveerLenta = new ConveerLenta();
        conveerLenta.setPosition(GameApplication.get().screenWidth / 2 - conveerLenta.getWidth() / 2, 0);
        addActor(conveerLenta);
    }

    private void addCashRegister() {
        CashRegister cashRegister = new CashRegister();
        if(GameApplication.get().isPortrait()) {
            cashRegister.setPosition(- cashRegister.getWidth() + cashRegister.terminalWidth * 1.1f, -cashRegister.getHeight() + cashRegister.terminalHeight * 1.15f);
        } else {
            cashRegister.setPosition(cashRegister.terminalWidth * 0.1f, -cashRegister.getHeight() + cashRegister.terminalHeight * 1.15f);
        }
        addActor(cashRegister);
    }

    public void refreshScene() {
        // todo тут можно экшены навешиватьт когда очередь нужно передвигать
//        if(!upgradedObjects.isEmpty()) {
//            for(Integer key: upgradedObjects) {
//                SceneObject sceneObject = objectsMap.get(key);
//                if(sceneObject != null) {
//                    sceneObject.doUpgradeAction();
//                }
//            }
//            upgradedObjects.clear();
//        }
    }

    public void resize() {
//        for(SceneObject sceneObject : objectsMap.values()) {
//            sceneObject.resize();
//        }
        clear();
        init();
    }

}
