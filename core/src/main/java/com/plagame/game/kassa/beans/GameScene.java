package com.plagame.game.kassa.beans;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.enums.SceneObjectInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Дмитрий Малышев on 10.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class GameScene extends Group {

    public Map<Integer, SceneObject> objectsMap = new HashMap<>(); // предметы на сцене: машина, дом и т.д.
    public Set<Integer> upgradedObjects = new HashSet<>(); // предметы, которые апгрейдили и которые нужно поменять на сцене

    public GameScene() {
        setSize(GameApplication.get().screenWidth, GameApplication.get().screenHeight);
        init();
    }

    private void init() {
        addSceneObject(SceneObjectInfo.GROUND);
//        addSceneObject(SceneObjectInfo.SKY);
        addSceneObject(SceneObjectInfo.BACKGROUND);
        addSceneObject(SceneObjectInfo.TREE);
        addSceneObject(SceneObjectInfo.HOUSE);
        addSceneObject(SceneObjectInfo.STATUE);
        addSceneObject(SceneObjectInfo.CAR);
        addSceneObject(SceneObjectInfo.CAMERA);
        addSceneObject(SceneObjectInfo.MICRO);
        addSceneObject(SceneObjectInfo.LIGHT);
        addSceneObject(SceneObjectInfo.GIRL);
    }

    private void addSceneObject(SceneObjectInfo sceneObjectInfo) {
        SceneObject sceneObject = new SceneObject(sceneObjectInfo); // если есть экшн то в конструкторе он включится сам
        objectsMap.put(sceneObjectInfo.type, sceneObject);
        addActor(sceneObject);
    }

    public void refreshScene() {
        if(!upgradedObjects.isEmpty()) {
            for(Integer key: upgradedObjects) {
                SceneObject sceneObject = objectsMap.get(key);
                if(sceneObject != null) {
                    sceneObject.doUpgradeAction();
                }
            }
            upgradedObjects.clear();
        }
    }

    public void resize() {
        clear();
        init();
    }

}
