package com.plagame.game.kassa.beans;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.plagame.game.kassa.enums.SceneObjectInfo;

/**
 * Created by Дмитрий Малышев on 05.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class SceneObject extends Group {

    public SceneObjectInfo sceneObjectInfo;
    public Image image;

    public SceneObject(SceneObjectInfo sceneObjectInfo) {
        this.sceneObjectInfo = sceneObjectInfo;
        init();
    }

    public void init() {
        setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());  // размер для всей группы ставим
        image = new Image(sceneObjectInfo.getTexture());
        image.setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());
        addActor(image);
        setPosition(sceneObjectInfo.getX(), sceneObjectInfo.getY());
    }

    public void resize() {
        init();
    }
}
