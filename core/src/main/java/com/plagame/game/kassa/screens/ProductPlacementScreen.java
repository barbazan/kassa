package com.plagame.game.kassa.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
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
    private final Map<Integer, List<Image>> emptyProductImageMap = new HashMap<>(); // пустые товары чтобы сравнивать можно дропнуть при перетаскивании
    private final DragAndDrop dragAndDrop = new DragAndDrop();
    private final List<Cell<Image>> selectTableCellList = new ArrayList<>(3); // это ячейки таблицы(чтобы картинки менять в ячейках), где лежат картинки продуктов, которые нужно разложить
    private Cell<Image> currentCell; // текущая ячейка, из которой тащат картинку
    private final LinkedList<GoodProduct> selectProductList = new LinkedList<>(); // список енумов и картинок товаров, которые нужно расставить
    private int emptyCount; // сколько пустых слотов продуктов
    private int completeCount; // сколько расставил на полки

    public ProductPlacementScreen() {
        init();
    }

    private void init() {
        addBackground();
        addProducts();
    }

    private void addBackground() {
        TextureRegion textureRegion = new TextureRegion(AssetUtil.getTexture("images/game_bg.jpg"));
        Image background = new Image(textureRegion);
        float scaleX = GameApplication.get().screenWidth / textureRegion.getRegionWidth();
        float scaleY = GameApplication.get().screenHeight / textureRegion.getRegionHeight();
        float scale = Math.max(scaleX, scaleY);
        background.setSize(textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale);
        background.setPosition(GameApplication.get().screenWidth / 2 - background.getWidth() / 2, GameApplication.get().screenHeight - background.getHeight());
        stage.addActor(background);
    }

    private void addProducts() {
        // ЭТО ПОЛКИ
        emptyCount = 0;
        completeCount = 0;
        selectProductList.clear();
        emptyProductImageMap.clear();
        selectTableCellList.clear();
        currentCell = null;
        float polkaHeight;
        if(GameApplication.get().isPortrait()) {
            polkaHeight = GameApplication.get().screenHeight * 0.2f;
        } else {
            polkaHeight = GameApplication.get().screenHeight * 0.24f;
        }
        float pad = polkaHeight / 25;
        Table productTable = new Table();
//        productTable.setDebug(true);
        productTable.setSize(GameApplication.get().screenWidth, GameApplication.get().screenHeight);
        productTable.align(Align.top);
        Set<ProductInfo> set = ProductInfo.getRandomSet(3);
        int row = 1;
        int countOnRow = GameApplication.get().isPortrait() ? 5 : 16;
        int emptyCountOnRow = GameApplication.get().isPortrait() ? 3 : 5;

        float maxIconWidth = GameApplication.get().screenWidth / countOnRow;
        for(ProductInfo productInfo : set) {
            List<Image> emptyProductImageList = new ArrayList<>();
            for(int i = 1; i <= countOnRow; i++) {
                TextureRegion textureRegion = productInfo.getTextureRegion();
                Vector2 vector2 = calcImageSize(textureRegion, polkaHeight);
                Image productImage = new Image(textureRegion);
                productImage.setSize(vector2.x, vector2.y);
                Group group = new Group();
                group.setSize(polkaHeight * 0.5f, polkaHeight * 0.5f);
                productImage.setPosition(group.getWidth() / 2 - productImage.getWidth() / 2, 0);
                group.addActor(productImage);

                float padTop;
                if(GameApplication.get().isPortrait()) {
                    padTop = polkaHeight - group.getHeight() + row * polkaHeight * 0.045f;
                } else {
                    padTop = polkaHeight - group.getHeight() + row * polkaHeight * 0.045f;
//                    padTop = polkaHeight - productImage.getHeight();
                }
                productTable.add(group).size(group.getWidth(), group.getHeight()).align(Align.center).pad(padTop, pad, 0, pad).expandX().fill();
//                productTable.add(group).align(Align.bottom).pad(pad / 2);

                emptyProductImageList.add(productImage);

                boolean isEmpty;
                if(GameApplication.get().isPortrait()) {
                    isEmpty = i >= (countOnRow / 2) && i <= (countOnRow / 2) + 2;
                } else {
                    isEmpty = i >= (countOnRow / 2) - 1 && i <= (countOnRow / 2) + 2;
                }

                if(isEmpty) {
                    emptyCount++;
                    productImage.setColor(Color.BLACK); // пустой продукт красим в черный
                    // сколько пустых продуктов столько и добавляем картинок в очередь на расстановку
                    Image selectImage = createProductImage(productInfo.type, productImage); // создаем такую же картинку и помещаем её в список из которого потом будем расставлять
                    GoodProduct selectProduct = new GoodProduct(productInfo, selectImage);
                    selectProductList.add(selectProduct);
                }
            }
            emptyProductImageMap.put(productInfo.type, emptyProductImageList);


            for (Image emptySlotImage : emptyProductImageList) {
                dragAndDrop.addTarget(new DragAndDrop.Target(emptySlotImage) {
                    @Override
                    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        Image targetImage = (Image) payload.getObject();
                        ProductInfo targetProductInfo = (ProductInfo)(targetImage.getUserObject());
                        int type = getEmptyProductType(emptySlotImage);
                        return targetProductInfo.type == type;
                    }

                    @Override
                    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        Image target = (Image) getActor();
                        target.setColor(Color.WHITE);
                        Image sourceImage = (Image)payload.getObject();
                        sourceImage.setUserObject(null);

                        // следующий продукт показываем внизу если он есть
                        GoodProduct nextProduct = getNextProduct();
                        if(nextProduct != null) { // значит есть еще пустые слоты
                            addDrugAndDrop(nextProduct);
                            System.out.println("currentCell = " + currentCell);
                            currentCell.size(nextProduct.image.getWidth(), nextProduct.image.getHeight());
                            currentCell.setActor(nextProduct.image);
                        }
                        completeCount++;
                        checkDayComplete();
                    }
                });
            }

            productTable.row();
            row++;
        }

        stage.addActor(productTable);

        // ЭТО ВНИЗУ ТОВАРЫ ДЛЯ РАСКЛАДКИ
        Collections.shuffle(selectProductList);
        Table selectTable = createSelectProductTable(pad);
        productTable.add(selectTable).align(Align.center).padBottom(pad * 2).colspan(countOnRow).expand().fill();
        productTable.row();

        selectTable.setPosition(stage.getWidth() / 2 - selectTable.getWidth() / 2, 20);
        stage.addActor(selectTable);
    }

    private Table createSelectProductTable(float pad) {
        Table selectTable = new Table();
        selectTable.setSize(GameApplication.get().screenWidth, GameApplication.get().screenHeight / 4);
//        selectTable.setDebug(true);
        selectTable.add().expandX();
        for(int i = 0; i < 3; i++) {
            GoodProduct nextProduct = getNextProduct();
            if(nextProduct != null) {
                addDrugAndDrop(nextProduct);
                Image image = nextProduct.image;
                Cell<Image> cell = selectTable.add(image).size(image.getWidth(), image.getHeight()).pad(pad).align(Align.center).fill();
                selectTableCellList.add(cell);
            }
        }
        selectTable.add().expandX();
        return selectTable;
    }

    private Image createProductImage(int productType, Image origImage) {
        ProductInfo productInfo = ProductInfo.getByType(productType);
        TextureRegion textureRegion = productInfo.getTextureRegion();
        Image image = new Image(textureRegion);
        image.setSize(origImage.getWidth(), origImage.getHeight());
        image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
        return image;
    }

    private Vector2 calcImageSize(TextureRegion textureRegion, float parentHeight) {
        float iconHeight, iconWidth;
        if(textureRegion.getRegionHeight() >= textureRegion.getRegionWidth() * 3) {
            iconHeight = parentHeight * 1.19f;
            iconWidth = iconHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
        } else if(textureRegion.getRegionHeight() >= textureRegion.getRegionWidth()) {
            iconHeight = parentHeight * 0.65f;
            iconWidth = iconHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
        } else {
            iconWidth = parentHeight * 0.65f;
            iconHeight = iconWidth * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
        }
        return new Vector2(iconWidth, Math.min(iconHeight, parentHeight));
    }

//    private Vector2 calcImageSize(TextureRegion textureRegion, float parentHeight, float maxWidth) {
//        float iconHeight, iconWidth;
//        iconWidth = textureRegion.getRegionWidth() > textureRegion.getRegionHeight() ? parentHeight * 0.8f: GameApplication.get().screenWidth * 0.15f;
//        iconWidth = Math.min(iconWidth, maxWidth);
//        iconHeight = iconWidth * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
//        if(iconHeight > parentHeight) {
//            iconHeight = parentHeight * 0.90f;
//            iconWidth = iconHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
//        }
//        return new Vector2(iconWidth, iconHeight);
//    }

    private Cell<Image> getSelectProductCell(Image image) {
        for(Cell<Image> cell : selectTableCellList) {
            if(cell.getActor() == image) {
                return cell;
            }
        }
        return null;
    }

    private int getEmptyProductType(Image image) {
        for(Map.Entry<Integer, List<Image>> entry : emptyProductImageMap.entrySet()) {
            for(Image targetImage : entry.getValue()) {
                if(targetImage == image && targetImage.getColor().toIntBits() == Color.BLACK.toIntBits()) {
                    return entry.getKey();
                }
            }
        }
        return 0;
    }

    private void addDrugAndDrop(GoodProduct product) {
        addDrugAndDrop(product.image, product.productInfo.type);
    }

    private void addDrugAndDrop(Image image, int productType) {
        dragAndDrop.addSource(new DragAndDrop.Source(image) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                image.setVisible(false);
                image.setUserObject(ProductInfo.getByType(productType));
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

                currentCell = getSelectProductCell(image);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if(image.getUserObject() != null) { // если этот объект все еще присутсвует, значит при перетаскивании не попали в таргет
                    image.setVisible(true);
                }
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }

    @Override
    protected InputProcessor initInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void checkDayComplete() {
        if(selectProductList.isEmpty() && completeCount >= emptyCount) {
            stage.addAction(Actions.sequence(
                Actions.delay(0.9f),
                Actions.hide(),
                Actions.delay(0.3f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        GameApplication.get().setDayCompleteScreen();
                    }
                })
            ));
        }
    }

    private GoodProduct getNextProduct() {
        if(!selectProductList.isEmpty()) {
            return selectProductList.removeFirst();
        }
        return null;
    }

    private static class GoodProduct {
        ProductInfo productInfo;
        Image image;

        public GoodProduct(ProductInfo productInfo, Image image) {
            this.productInfo = productInfo;
            this.image = image;
        }
    }
}
