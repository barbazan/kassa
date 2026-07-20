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
        addSceneObject(SceneObjectInfo.LENTA);
    }

    private void addSceneObject(SceneObjectInfo sceneObjectInfo) {
        SceneObject sceneObject = new SceneObject(sceneObjectInfo); // если есть экшн то в конструкторе он включится сам
        objectsMap.put(sceneObjectInfo.type, sceneObject);
        addActor(sceneObject);
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
        clearChildren();
        init();
    }

}
