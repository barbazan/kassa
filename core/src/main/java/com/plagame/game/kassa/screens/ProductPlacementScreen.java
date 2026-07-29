package com.plagame.game.kassa.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.AssetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Дмитрий Малышев on 28.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class ProductPlacementScreen extends BaseScreen {
    private Image background;
    private Map<Integer, List<Image>> emptyProductImageMap = new HashMap<>(); // пустые товары чтобы сравнивать можно дропнуть при перетаскивании
    private Map<Integer, Vector2> productSizeMap = new HashMap<>();
    private LinkedList<ProductInfo> selectProductList = new LinkedList<>(); // список продуктов, которые нужно разложить
    private float polkaHeight;
    private int emptyCount;
    private DragAndDrop dragAndDrop = new DragAndDrop();

    public ProductPlacementScreen() {
        init();
    }

    private void init() {
        addBackground();
        addProducts();
    }

    private void addBackground() {
        TextureRegion textureRegion = new TextureRegion(AssetUtil.getTexture("images/game_bg.jpg"));
        background = new Image(textureRegion);
        float scaleX = GameApplication.get().screenWidth / textureRegion.getRegionWidth();
        float scaleY = GameApplication.get().screenHeight / textureRegion.getRegionHeight();
        float scale = Math.max(scaleX, scaleY);
        background.setSize(textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
        background.setPosition(GameApplication.get().screenWidth / 2 - background.getWidth() / 2, GameApplication.get().screenHeight - background.getHeight());
        stage.addActor(background);
    }

    private void addProducts() {
        // ЭТО ПОЛКИ
        selectProductList.clear();
        polkaHeight = GameApplication.get().screenHeight * 0.2f;
        float pad = polkaHeight / 25;
        Table productTable = new Table();
        productTable.setSize(GameApplication.get().screenWidth, GameApplication.get().screenHeight);
        productTable.align(Align.top);
        Set<ProductInfo> set = ProductInfo.getRandomSet(3);
        int row = 1;
        int countOnRow = GameApplication.get().isPortrait() ? 6 : 16;

        float maxIconWidth = GameApplication.get().screenWidth / countOnRow;
        for(ProductInfo productInfo : set) {
            List<Boolean> productList = new ArrayList<>(countOnRow);
            float cellWidth = (productTable.getWidth() - pad * (countOnRow + 1)) / countOnRow;
            List<Image> emptyProductImageList = new ArrayList<>();
            for(int i = 1; i <= countOnRow; i++) {
                TextureRegion textureRegion = productInfo.getTextureRegion();
                Vector2 vector2 = calcImageSize(textureRegion, polkaHeight, maxIconWidth);
                Image image = new Image(textureRegion);
                image.setSize(vector2.x, vector2.y);
                float padTop = polkaHeight - image.getHeight() + row * polkaHeight * 0.045f;
                productTable.add(image).size(image.getWidth(), image.getHeight()).align(Align.bottom).pad(padTop, pad, 0, pad);
                productSizeMap.put(productInfo.type, vector2);

                emptyProductImageList.add(image);
                boolean isEmpty = i >= (countOnRow / 2) - 1 && i <= (countOnRow / 2) + 2;
                productList.add(isEmpty);
                if(isEmpty) {
                    image.setColor(Color.BLACK);
                    System.out.println("------------selectProductList.add------productInfo.type = " + productInfo.type);
                    selectProductList.add(productInfo);
                }
            }
            emptyProductImageMap.put(productInfo.type, emptyProductImageList);


            for (Image emptySlotImage : emptyProductImageList) {
                dragAndDrop.addTarget(new DragAndDrop.Target(emptySlotImage) {
                    @Override
                    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        Image targetImage = (Image) payload.getObject();
                        int type = getProductType(emptySlotImage);
                        boolean result = ((ProductInfo)(targetImage.getUserObject())).type == type;
                        System.out.println("result = " + result);
                        return result;
                    }

                    @Override
                    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        System.out.println(" ============= drop ============= ");
                        Image target = (Image) getActor();
                        target.setColor(Color.WHITE);
                    }
                });
            }

            productTable.row();
            row++;
        }

        Collections.shuffle(selectProductList);
        emptyCount = selectProductList.size();

        // ЭТО ВНИЗУ ТОВАРЫ ДЛЯ РАСКЛАДКИ
        Table selectTable = new Table();
        for(int i = 0; i < 3; i++) {
            ProductInfo productInfo = selectProductList.get(i);
            TextureRegion textureRegion = productInfo.getTextureRegion();
            Image image = new Image(textureRegion);
            Vector2 v2 = productSizeMap.get(productInfo.type);
            image.setSize(v2.x, v2.y);
            image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);

            dragAndDrop.addSource(new DragAndDrop.Source(image) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    image.setVisible(false);
                    image.setUserObject(productInfo);
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(image);

                    // Создаем отдельную картинку, а не используем оригинал
                    Image dragImage = new Image(((Image)getActor()).getDrawable()) {
                        @Override
                        public float getY() {
                            return super.getY() - image.getHeight() * 0.5f;
                        }

                        @Override
                        public float getX() {
                            return super.getX() + image.getWidth() * 0.5f;
                        }
                    };
                    dragImage.setSize(image.getWidth(), image.getHeight());
                    dragImage.setOrigin(dragImage.getWidth() / 2, dragImage.getHeight() / 2);
                    dragImage.setScale(1.5f);

                    payload.setDragActor(dragImage);

                    return payload;
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    image.setVisible(true);
                }
            });

            selectTable.add(image).size(image.getWidth(), image.getHeight()).pad(20).align(Align.bottom);
        }

        productTable.add(selectTable).align(Align.center).colspan(countOnRow).expand();
        productTable.row();

        stage.addActor(productTable);
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Vector2 calcImageSize(TextureRegion textureRegion, float parentHeight, float maxWidth) {
        float iconHeight, iconWidth;
        iconWidth = textureRegion.getRegionWidth() > textureRegion.getRegionHeight() ? parentHeight * 0.8f: GameApplication.get().screenWidth * 0.15f;
        iconWidth = Math.min(iconWidth, maxWidth);
        iconHeight = iconWidth * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
        if(iconHeight > parentHeight) {
            iconHeight = parentHeight * 0.90f;
            iconWidth = iconHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
        }
        return new Vector2(iconWidth, iconHeight);
    }

    private int getProductType(Image image) {
        for(Map.Entry<Integer, List<Image>> entry : emptyProductImageMap.entrySet()) {
            for(Image targetImage : entry.getValue()) {
                if(targetImage == image) {
                    return entry.getKey();
                }
            }
        }
        return 0;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }

}



//        if(textureRegion.getRegionHeight() > textureRegion.getRegionWidth()) {
//            iconHeight = parentHeight * 0.75f;
//            iconWidth = iconHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
//        } else {
//            iconWidth = parentHeight * 0.75f;
//            iconHeight = iconWidth * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
//        }




//            image.addListener(new ActorGestureListener() {
//                @Override
//                public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                    dragAndDrop.addSource(new DragAndDrop.Source(image) {
//                        @Override
//                        public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
//                            DragAndDrop.Payload payload = new DragAndDrop.Payload();
//                            payload.setObject(image);
//                            // Что отображается во время перетаскивания
//                            payload.setDragActor(image);
//                            return payload;
//                        }
//                    });
//                }
//
//                @Override
//                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                    dragAndDrop.addTarget(new DragAndDrop.Target(image) {
//                        @Override
//                        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                            List<Image> emptyProductList = emptyProductImageMap.get(productType);
//                            if(emptyProductList != null) {
//                                for(Image image : emptyProductList) {
//                                    if(x >= image.getX() && y > image.getY() && x < image.getX() + image.getWidth() && y < image.getY() + image.getHeight()) {
//                                        return true; // Разрешаем бросить
//                                    }
//                                }
//                            }
//                            return false;
//                        }
//
//                        @Override
//                        public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                            Image dragged = (Image) payload.getObject();
//                            // Перемещаем картинку в центр target
//                            dragged.setPosition(image.getX(), image.getY());
//                        }
//                    });
//                    super.touchUp(event, x, y, pointer, button);
//                }
//            });
