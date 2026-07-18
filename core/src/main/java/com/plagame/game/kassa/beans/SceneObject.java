package com.plagame.game.kassa.beans;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.plagame.game.kassa.enums.SceneObjectInfo;
import com.plagame.game.kassa.enums.UpgradeInfo;

/**
 * Created by Дмитрий Малышев on 05.05.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class SceneObject extends Group {

    public SceneObjectInfo sceneObjectInfo;
    public Image image;
    private int levelNum = 1; // номер картинки о 1 до 3 в зависимости от уровня

    public SceneObject(SceneObjectInfo sceneObjectInfo) {
        this.sceneObjectInfo = sceneObjectInfo;
        init();
    }

    public void init() {
        setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());  // размер для всей группы ставим
        image = new Image(sceneObjectInfo.getTexture());
        image.setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());
        addActor(image);
        if(sceneObjectInfo.canAction()) {
            if(sceneObjectInfo.isNeedMoveAction()) { // если предмет влетает из за экрана, то
                setPosition(sceneObjectInfo.getStartX(), sceneObjectInfo.getStartY()); // ставим его в стартовую позицию за экраном
                sceneObjectInfo.actionMoveIn.reset();
                addAction(sceneObjectInfo.actionMoveIn);
            } else { // значит затемнение
                setPosition(sceneObjectInfo.getX(), sceneObjectInfo.getY());
                Color oldColor = image.getColor();
                image.addAction(Actions.sequence(
                    Actions.color(Color.BLACK, 0.35f, Interpolation.linear),
                    Actions.color(oldColor, 0.35f, Interpolation.linear)
                ));
            }
        } else {
            setPosition(sceneObjectInfo.getX(), sceneObjectInfo.getY());
        }
    }

    public void doUpgradeAction() {
        if(levelNum == User.get().getUpgradeLevelSceneObjectNum(UpgradeInfo.getUpgradeInfo(sceneObjectInfo))) { // если картинку не надо менять то ниче не делаем
            return;
        } else {
            levelNum = User.get().getUpgradeLevelSceneObjectNum(UpgradeInfo.getUpgradeInfo(sceneObjectInfo));
            if(sceneObjectInfo.canAction()) {
                if(sceneObjectInfo.isNeedMoveAction()) {
                    sceneObjectInfo.actionMoveIn.reset();
                    sceneObjectInfo.actionMoveOut.reset();
                    addAction(Actions.sequence(sceneObjectInfo.actionMoveOut,
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                image.setDrawable(new TextureRegionDrawable(sceneObjectInfo.getTexture()));
                                image.setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());
                                SceneObject.this.setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());
                            }
                        }),
                        sceneObjectInfo.actionMoveIn));
                } else {
                    Color oldColor = image.getColor();
                    image.addAction(Actions.sequence(
                        Actions.color(Color.BLACK, 0.35f, Interpolation.linear),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                image.setDrawable(new TextureRegionDrawable(sceneObjectInfo.getTexture()));
                                image.setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());
                                SceneObject.this.setSize(sceneObjectInfo.getWidth(), sceneObjectInfo.getHeight());
                            }
                        }),
                        Actions.color(oldColor, 0.35f, Interpolation.linear)
                    ));
                }
            } else { // это земля и задний план, они без движения должны менятся
                image.setDrawable(new TextureRegionDrawable(sceneObjectInfo.getTexture()));
            }
        }
    }

    public void resize() {
        init();
    }
}
