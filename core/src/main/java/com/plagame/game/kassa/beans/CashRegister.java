package com.plagame.game.kassa.beans;

import static com.plagame.game.kassa.GameApplication.FONT_DEFAULT;
import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.components.ModelLabel;
import com.plagame.game.kassa.enums.AchievementInfo;
import com.plagame.game.kassa.enums.ProductInfo;
import com.plagame.game.kassa.utils.CashPaymentGenerator;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.kassa.utils.SoundUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Дмитрий Малышев on 21.07.2026.
 * Email: dmitry.malyshev@gmail.com
 * ---
 * Кассовый аппарат с терминалом для карты
 */
public class CashRegister extends Group {

    private static final int MAX_PRICE_LENGTH = 8;
    public float terminalWidth, terminalHeight;
    public int totalCost; // это стоимость продуктов, которую должен оплатить покупатель
    public int payedSum;  // сколько дали налички
    public int givingSum; // сколько я даю сдачи
    public String terminalValue = ""; // это число то что ввели кнопками на терминале
    public Group kassa, terminal;
    private Table tableRight;
    private Map<Integer, Image> cardImageMap = new HashMap<>();
    private Button terminalBtnOk;
    private Button kassaBtnOk, kassaBtnReturn;
    private LinkedList<Image> dollarsImagesList = new LinkedList<>();
    public boolean isProcessPayment;
    public int cardType;
    private Set<String> paymentTypes = new HashSet<>(); // способы оплаты наличка или карта, нужно для достижений

    public CashRegister() {
        this(null);
    }

    public CashRegister(CashRegister cashRegister) {
        init(cashRegister);
    }

    public void checkProduct(ProductInfo productInfo) { // пробить продукт на кассе
        totalCost += productInfo.cost;
    }

    private void init(CashRegister cashRegister) {
        kassa = createKassa();
        addActor(kassa);

        terminal = createTerminal(kassa.getHeight() * 0.58f);
        terminal.setPosition(kassa.getWidth(), kassa.getHeight() - terminalHeight);
        addActor(terminal);

        setSize(kassa.getWidth() + terminal.getWidth(), kassa.getHeight());
        initCards();
        createKassaButtons();
        if(cashRegister != null) {
            this.isProcessPayment = cashRegister.isProcessPayment;
            this.cardType = cashRegister.cardType;
            this.totalCost = cashRegister.totalCost;
            this.terminalValue = cashRegister.terminalValue;
            this.payedSum = cashRegister.payedSum;
        }
    }

    private Group createKassa() {  // Кассовый аппарат
        Group kassaGroup = new Group();
        Image imageKassa = new Image(ATLAS_1.findRegion("kassa"));

        float kassaWidth, kassaHeight;
        if (GameApplication.get().isPortrait()) {
            kassaHeight = GameApplication.get().screenHeight * 0.69f;
            kassaWidth = imageKassa.getWidth() * kassaHeight / imageKassa.getHeight();
        } else {
            kassaWidth = GameApplication.get().screenWidth * 0.25f;
            kassaHeight = imageKassa.getHeight() * kassaWidth / imageKassa.getWidth();
        }
        imageKassa.setSize(kassaWidth, kassaHeight);
        kassaGroup.setSize(kassaWidth, kassaHeight);
        kassaGroup.addActor(imageKassa);

        float btnSize = kassaWidth * 0.22f;
        float btnPad = btnSize / 10;

        Table kassaTable = new Table();
        kassaTable.setSize(kassaWidth, kassaHeight);
        Table tableLeft = new Table();
        tableLeft.setSize(kassaWidth * 0.25f, kassaHeight);
        tableRight = new Table();
//        tableRight.setDebug(true);
        tableRight.setSize(kassaWidth * 0.75f, kassaHeight);


        Label totalLabel = new Label("СУММА", new Label.LabelStyle(FONT_DEFAULT, Color.WHITE));
        totalLabel.setAlignment(Align.center);
        tableRight.add(totalLabel).align(Align.center).pad(btnPad).colspan(3).fill();
        tableRight.row();

        Label totalCostLabel = new ModelLabel("--.--", new Label.LabelStyle(FONT_DEFAULT, Color.WHITE)) {
            @Override
            protected String getValue() {
                return formatTotalCost(totalCost);
            }
        };
        totalCostLabel.setAlignment(Align.center);
        totalCostLabel.setHeight(btnSize);
        tableRight.add(totalCostLabel).align(Align.center).pad(btnPad).colspan(3).fill();
        tableRight.row();

        Label changeTextLabel = new Label("СДАЧА", new Label.LabelStyle(FONT_DEFAULT, Color.WHITE));
        changeTextLabel.setAlignment(Align.left);
        tableRight.add(changeTextLabel).align(Align.left).padLeft(btnPad * 3).padTop(btnPad * 7).fill();

        Label changeLabel = new ModelLabel("", new Label.LabelStyle(FONT_DEFAULT, Color.YELLOW)) {
            @Override
            protected String getValue() {
                if(isProcessPayment && cardType == 0) {
                    return formatTotalCost(payedSum - totalCost);
                } else {
                    return "--.--";
                }
            }
        };
        changeLabel.setAlignment(Align.right);
        changeLabel.setHeight(btnSize);
        tableRight.add(changeLabel).align(Align.right).padRight(btnPad * 3).padTop(btnPad * 7).colspan(2).expandX().fill();
        tableRight.row();

        Label givingTextLabel = new Label("", new Label.LabelStyle(FONT_DEFAULT, Color.WHITE));
        givingTextLabel.setAlignment(Align.left);
        tableRight.add(givingTextLabel).align(Align.left).padLeft(btnPad * 3).padTop(btnPad * 8).fill();

        Label givingLabel = new ModelLabel("", new Label.LabelStyle(FONT_DEFAULT, Color.YELLOW)) {
            @Override
            protected String getValue() {
                int change = payedSum - totalCost; //сдачи сколько нужно
                if(givingSum == change) {
                    setColor(Color.GREEN);
                } else {
                    setColor(Color.YELLOW);
                }
                return formatTotalCost(givingSum);
            }
        };
        givingLabel.setAlignment(Align.right);
        givingLabel.setHeight(btnSize);
        tableRight.add(givingLabel).align(Align.right).padRight(btnPad * 3).padTop(btnPad * 8).colspan(2).expandX().fill();
        tableRight.row();

        Table cashTable = createKassaCashBox(tableRight.getWidth());
        tableRight.add(cashTable).align(Align.center).pad(btnPad).padTop(btnPad * 3).expandY().fill().colspan(3);

        kassaTable.add(tableLeft).size(tableLeft.getWidth(), tableLeft.getHeight());
        kassaTable.add(tableRight).size(tableRight.getWidth(), tableRight.getHeight());
        kassaGroup.addActor(kassaTable);
        return kassaGroup;
    }

    public void createKassaButtons() {
        kassaBtnOk = createKassaOkBtn();
        Group p = kassa.getParent();
        float x = p.getX() + kassa.getX() + kassa.getWidth() / 2 + 10;
        float y = 0;
        kassaBtnOk.setPosition(x, y);
        kassa.addActor(kassaBtnOk);

        kassaBtnReturn = createKassaReturnBtn();
        x = p.getX() + kassa.getX() + kassa.getWidth() / 2 - kassaBtnReturn.getWidth() - 10;
        kassaBtnReturn.setPosition(x, y);
        kassa.addActor(kassaBtnReturn);
    }

    private Group createTerminal(float height) { // Терминал
        Image imageTerminal = new Image(ATLAS_1.findRegion("terminal"));
        terminalHeight = height;
        terminalWidth = imageTerminal.getWidth() * terminalHeight / imageTerminal.getHeight();
        Group terminalGroup = new Group();
        imageTerminal.setSize(terminalWidth, terminalHeight);
        terminalGroup.setSize(terminalWidth, terminalHeight);
        terminalGroup.addActor(imageTerminal);

        float btnSize = terminalWidth * 0.22f;
        float btnPad = btnSize / 10;
        Table btnTable = new Table();
        btnTable.pad(btnPad * 2.9f).padTop(0);
//        btnTable.setDebug(true);
        btnTable.setSize(terminalWidth, terminalHeight);

        Label totalLabel = new Label("СУММА", new Label.LabelStyle(FONT_DEFAULT, Color.BLACK));
        totalLabel.setAlignment(Align.center);
        btnTable.add(totalLabel).align(Align.center).pad(btnPad).colspan(3).fill();
        btnTable.row();

        Label totalCostLabel = new ModelLabel("--.--", new Label.LabelStyle(FONT_DEFAULT, Color.BLACK)) {
            @Override
            protected String getValue() {
                return formatTotalCost(totalCost);
            }
        };
        totalCostLabel.setAlignment(Align.center);
        totalCostLabel.setHeight(btnSize);
        btnTable.add(totalCostLabel).align(Align.center).pad(btnPad * 2).colspan(3).fill();
        btnTable.row();

        btnTable.add().colspan(3).expand();
        btnTable.row();

        Label costLabel = new ModelLabel(terminalValue, new Label.LabelStyle(FONT_DEFAULT, Color.BLACK)) {
            @Override
            protected String getValue() {
                if(terminalValue.length() > 0) {
                    return "$" + terminalValue;
                } else {
                    return "";
                }
            }
        };
        costLabel.setAlignment(Align.center);
        costLabel.setHeight(btnSize);
        btnTable.add(costLabel).align(Align.center).padBottom(btnPad).colspan(3).fill();
        btnTable.row();

        for(int i = 1; i <= 9; i++) {
            Button btn = createBtn(String.valueOf(i), btnSize);
            int btnValue = i;
            btn.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    if(isProcessPayment && cardType > 0) {
                        SoundUtil.playTerminalClickSound();
                        if(terminalValue.length() < MAX_PRICE_LENGTH) {
                            if(!terminalValue.contains(".") || terminalValue.substring(terminalValue.indexOf(".")).length() <= 2) {
                                terminalValue = terminalValue + btnValue;
                            }
                        }
                    }
                }
            });

            float padLeft = btnPad / 2;
            if(i % 3 == 1) {
                padLeft = 0;
            }
            btnTable.add(btn).size(btnSize, btnSize).padLeft(padLeft).padTop(btnPad).fill();
            if(i % 3 == 0) {
                btnTable.row();
            }
        }

        Button btnBack = createBtn("back", btnSize);
        btnBack.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(isProcessPayment && cardType > 0) {
                    SoundUtil.playTerminalClickSound();
                    if(!terminalValue.isEmpty()) {
                        terminalValue = terminalValue.substring(0, terminalValue.length() - 1);
                    }
                    super.tap(event, x, y, count, button);
                }
            }
        });
        btnTable.add(btnBack).size(btnSize, btnSize).padLeft(0).padTop(btnPad);

        Button btnZero = createBtn("0", btnSize);
        btnZero.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(isProcessPayment && cardType > 0) {
                    SoundUtil.playTerminalClickSound();
                    if(terminalValue.length() < MAX_PRICE_LENGTH) {
                        terminalValue = terminalValue + "0";
                    }
                    super.tap(event, x, y, count, button);
                }
            }
        });
        btnTable.add(btnZero).size(btnSize, btnSize).padLeft(btnPad / 2).padTop(btnPad);

        Button btnDot = createBtn("dot", btnSize);
        btnDot.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(isProcessPayment && cardType > 0) {
                    SoundUtil.playTerminalClickSound();
                    if(terminalValue.length() < MAX_PRICE_LENGTH && !terminalValue.isEmpty() && !terminalValue.contains(".")) {
                        terminalValue = terminalValue + ".";
                    }
                    super.tap(event, x, y, count, button);
                }
            }
        });
        btnTable.add(btnDot).size(btnSize, btnSize).padLeft(btnPad / 2).padTop(btnPad);

        btnTable.row();

        terminalBtnOk = new Button(
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok_up")),
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok_down"))
        ) {
            @Override
            public void act(float delta) {
//                if(isProcessPayment && cardType > 0) {
                if(GameApplication.get().getGameScreen().gameScene.productCortege.isEmpty()) {
                    setDisabled(false);
                    terminalBtnOk.setColor(Color.WHITE);
                } else {
                    setDisabled(true);
                    terminalBtnOk.setColor(Color.DARK_GRAY);
                }
                super.act(delta);
            }
        };
        float h = btnSize * 0.91f;
        float w = terminalBtnOk.getWidth() * h / terminalBtnOk.getHeight();
        terminalBtnOk.setSize(w, h);
        terminalBtnOk.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(GameApplication.get().getGameScreen().gameScene.productCortege.isEmpty()) {
                    if(isProcessPayment && cardType > 0) {
                        String totalCostStr = formatTotalCost(totalCost);
                        if(totalCost > 0 && totalCostStr.equals("$" + terminalValue)) {
                            SoundUtil.playTerminalOkSound();
                            try {
                                int terminalPayedSum = 0;
                                String[] parts = terminalValue.split("\\.");
                                int dol = Integer.parseInt(parts[0]);
                                int cent = parts.length > 1 ? Integer.parseInt((parts[1] + "00").substring(0, 2)) : 0;
                                terminalPayedSum = dol * 100 + cent;
                                User.get().changeDollars(terminalPayedSum); // начислить игроку terminalPayedSum
                                addAchievementsProgress(true); // добавляем прогресс всем очивкам связанными с этим событием расчета картой
                                User.get().saveUser();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            finishPayment();
                            GameApplication.get().getGameScreen().gameScene.customerCortege.nextCustomer();
                            moveCameraSlowlyBack();
                            hideCard();
                        } else {
                            SoundUtil.playWrongClickSound();
                        }
                    } else {
                        SoundUtil.playWrongClickSound();
                        if(GameApplication.get().getGameScreen().gameScene.customerCortege.customerList.isEmpty()) { //todo remove
                            moveCameraSlowlyBack();
                            hideCard();
                            GameApplication.get().getGameScreen().gameScene.nextDay(); //todo remove
                        }
                     }
                    super.tap(event, x, y, count, button);
                }
            }
        });
        btnTable.add(terminalBtnOk).size(terminalBtnOk.getWidth(), terminalBtnOk.getHeight()).colspan(3).align(Align.center).padTop(btnPad).padBottom(btnSize * 0.67f);

        terminalGroup.addActor(btnTable);
        return terminalGroup;
    }

    public void hideCard() {
        for(Image image : cardImageMap.values()) {
            image.setVisible(false);
        }
    }

    public void payCard(int cardType) {
        this.cardType = cardType;
        Image cardImage = cardImageMap.get(cardType);
        if(cardImage != null) {
            cardImage.setVisible(true);
        }
        this.isProcessPayment = true;
    }

    public void payCash() {
        this.payedSum = CashPaymentGenerator.generatePaidAmount(totalCost);
        this.isProcessPayment = true;
        this.cardType = 0;
    }

    private Button createBtn(String btnNum, float btnSize) {
        Button btn = new Button(
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_" + btnNum + "_up")),
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_" + btnNum + "_down"))
        );
        btn.setSize(btnSize, btnSize);
        return btn;
    }

    private String formatTotalCost(int cost) {
        if(cost == 0) {
            return "--.--";
        }
        return "$" + NumberFormat.formatCost(cost);
    }

    private void moveCameraSlowlyBack() {
        float targetZoom = GameApplication.get().DEFAULT_CAMERA_ZOOM;
        float targetX = GameApplication.get().DEFAULT_CAMERA_POSITION.x;
        float targetY = GameApplication.get().DEFAULT_CAMERA_POSITION.y;
        getParent().addAction(
            new CameraAction(GameApplication.get().camera, targetX, targetY, targetZoom, 0.5f)
        );
    }

    private Table createKassaCashBox(float width) {
        Table cashTable = new Table();
        TextureRegion textureRegion = ATLAS_1.findRegion("kassa_slot");
        float w = (width / 5);
        float h = w * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
        w = w * 0.9f;
        h = h * 0.9f;
        cashTable.setSize(width, h * 2);
        for(int i = 1; i <= 5; i++) {
            Group slotGroup = new Group();
            slotGroup.setSize(w, h);

            Image slotImage = new Image(textureRegion);
            slotImage.setSize(w, h);
            slotGroup.addActor(slotImage);

            Image dollarPackImage = new Image(ATLAS_1.findRegion("dollar_pack_" + i));
            dollarPackImage.setColor(getColorForDollarsPack(i));
            float dw = w * 0.999f;
            float dh = dw * h / w;
            dollarPackImage.setSize(dw, dh);
            dollarPackImage.setPosition(slotGroup.getWidth() / 2 - dollarPackImage.getWidth() / 2, slotGroup.getHeight() / 2 - dollarPackImage.getHeight() / 2);
            slotGroup.addActor(dollarPackImage);

            int finalI = i;
            slotGroup.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    if(isProcessPayment && cardType == 0) {
                        SoundUtil.playKassaCashSound();
                        Image img = new Image(ATLAS_1.findRegion("dollar_" + finalI));
                        img.setColor(getColorForDollarsPack(finalI));
                        float globalX = kassa.getX() + tableRight.getX() + cashTable.getX() + slotGroup.getX();
                        float globalY = kassa.getY() + tableRight.getY() + cashTable.getY() + slotGroup.getY() + slotGroup.getHeight() / 2;
                        img.setPosition(globalX, globalY);
                        img.setSize(dw * 0.6f, dh * 0.6f);
                        img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
                        img.setScale(getScaleForDollarsPack(finalI));
                        float targetX = kassa.getX() + kassa.getWidth() * 0.1f;
                        float dy = GameConfig.random.nextFloat() * dh * 0.3f;
                        float targetY = kassa.getY() + kassa.getHeight() * 0.60f + (GameConfig.random.nextBoolean() ? -dy: dy);
                        img.addAction(Actions.parallel(
                            Actions.moveTo(targetX, targetY, 0.6f),
                            Actions.rotateBy(180 + 180 * GameConfig.random.nextFloat(), 0.6f)
                        ));
                        addActor(img);
                        givingSum += getDollarValue(finalI);
                        dollarsImagesList.add(img);
                    }
                }
            });

            cashTable.add(slotGroup).size(w, h).align(Align.center);
        }

        cashTable.row();

        for(int i = 1; i <= 5; i++) {
            Group slotGroup = new Group();
            slotGroup.setSize(w, h);

            Image slotImage = new Image(textureRegion);
            slotImage.setSize(w, h);
            slotGroup.addActor(slotImage);

            TextureRegion coinTextureRegion = ATLAS_1.findRegion("coin_pack_" + i);
            Image coinPackImage = new Image(coinTextureRegion);
            float dw = w * 0.75f;
            float dh = dw * coinTextureRegion.getRegionHeight() / coinTextureRegion.getRegionWidth();
            coinPackImage.setSize(dw, dh);
            coinPackImage.setPosition(slotGroup.getWidth() / 2 - coinPackImage.getWidth() / 2, slotGroup.getHeight() / 2 - coinPackImage.getHeight() / 2);
            slotGroup.addActor(coinPackImage);

            int finalI = i;
            slotGroup.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    if(isProcessPayment && cardType == 0) {
                        SoundUtil.playKassaCoinSound();
                        Image img = new Image(ATLAS_1.findRegion(finalI == 1 ? "coin_1" : "coin_2"));
                        float globalX = kassa.getX() + tableRight.getX() + cashTable.getX() + slotGroup.getX();
                        float globalY = kassa.getY() + tableRight.getY() + cashTable.getY() + slotGroup.getY() + slotGroup.getHeight() / 2;
                        img.setPosition(globalX, globalY);
                        img.setSize(dw * 0.5f, dw * 0.5f);
                        img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
                        float dx = GameConfig.random.nextFloat() * slotGroup.getWidth() / 2;
                        float targetX = kassa.getX() + kassa.getWidth() * 0.10f + (GameConfig.random.nextBoolean() ? -dx : dx);
                        float dy = GameConfig.random.nextFloat() * slotGroup.getHeight() * 0.3f;
                        float targetY = kassa.getY() + kassa.getHeight() * 0.65f + (GameConfig.random.nextBoolean() ? -dy: dy);
                        img.addAction(Actions.parallel(
                            Actions.moveTo(targetX, targetY, 0.6f),
                            Actions.rotateBy(180 + 180 * GameConfig.random.nextFloat(), 0.6f)
                        ));
                        addActor(img);
                        float coinValue = getCoinValue(finalI);
                        givingSum += coinValue;
                        dollarsImagesList.add(img);
                    }
                }
            });

            cashTable.add(slotGroup).size(w, h).align(Align.center);
        }
        return cashTable;
    }

    private Color getColorForDollarsPack(int i) {
        Color color = new Color(0x00ff00ff);
        if(i == 1) {
            color = new Color(0xaaffaaff);
        } else if(i == 2) {
            color = new Color(0xaaffaaff);
        } else if(i == 3) {
            color = new Color(0x88ff88ff);
        } else if(i == 4) {
            color = new Color(0x44ff44ff);
        } else if(i == 5) {
            color = new Color(0x00ff00ff);
        }
        return color;
    }

    private float getScaleForDollarsPack(int i) {
        float scale = 1;
        if(i == 1) {
            scale = 1;
        } else if(i == 2) {
            scale = 1.05f;
        } else if(i == 3) {
            scale = 1.05f;
        } else if(i == 4) {
            scale = 1.1f;
        } else if(i == 5) {
            scale = 1.15f;
        }
        return scale;
    }

    private int getDollarValue(int index) {
        switch(index) {
            case 1: return 1_00;
            case 2: return 5_00;
            case 3: return 10_00;
            case 4: return 20_00;
            case 5: return 50_00;
        }
        return 0;
    }

    private int getCoinValue(int index) {
        switch(index) {
            case 1: return 1;
            case 2: return 5;
            case 3: return 10;
            case 4: return 20;
            case 5: return 50;
        }
        return 0;
    }

//    private float getDollarValue(int i) {
//        int value = 1;
//        if(i == 1) {
//            value = 1;
//        } else if(i == 2) {
//            value = 5;
//        } else if(i == 3) {
//            value = 10;
//        } else if(i == 4) {
//            value = 20;
//        } else if(i == 5) {
//            value = 50;
//        }
//        return value;
//    }

//    private float getCoinValue(int i) {
//        float value = 0.01f;
//        if(i == 1) {
//            value = 0.01f;
//        } else if(i == 2) {
//            value = 0.05f;
//        } else if(i == 3) {
//            value = 0.10f;
//        } else if(i == 4) {
//            value = 0.20f;
//        } else if(i == 5) {
//            value = 0.50f;
//        }
//        return value;
//    }

    private void initCards() {
        for(int i = 1; i <= 5; i++) {
            TextureRegion textureRegion = ATLAS_1.findRegion("card_" + i);
            Image cardImage = new Image(textureRegion);
            float cardW = terminal.getWidth();
            float cardH = cardW * textureRegion.getRegionHeight() / textureRegion.getRegionWidth();
            cardImage.setSize(cardW, cardH);
            cardImage.setOrigin(cardW / 2, cardH / 2);
            cardImage.setRotation(-90);
            cardImage.setPosition(terminal.getParent().getX() + terminal.getX() + terminalWidth / 2 - cardW / 2, terminal.getY() - cardH);
            addActor(cardImage);
            cardImage.setVisible(false);
            cardImageMap.put(i, cardImage);
        }
    }

    private Button createKassaOkBtn() {
        float btnHeight = kassa.getWidth() * 0.15f;

        final Button btnOk = new Button(
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok_up")),
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_ok_down"))
        ) {
            @Override
            public void act(float delta) {
                float change = payedSum - totalCost; //сдачи сколько нужно
                if(isProcessPayment && cardType == 0 && givingSum >= change) {
//                    setDisabled(false);
                    kassaBtnOk.setColor(Color.WHITE);
                } else {
//                    setDisabled(true);
                    kassaBtnOk.setColor(Color.DARK_GRAY);
                }
                super.act(delta);
            }
        };
        float h = btnHeight;
        float w = btnOk.getWidth() * h / btnOk.getHeight();
        btnOk.setSize(w, h);
        btnOk.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                int change = payedSum - totalCost; //сдачи сколько нужно
                if(isProcessPayment && cardType == 0 && givingSum >= change) {
                    SoundUtil.playKassaClickSound();
                    User.get().changeDollars(payedSum); // начислить игроку payedSum
                    User.get().changeDollars(-givingSum); //списать с игрока givingSum
                    addAchievementsProgress(false); // добавляем прогресс всем очивкам связанными с этим событием рассчета на кассе
                    User.get().saveUser();
                    finishPayment();
                    GameApplication.get().getGameScreen().gameScene.customerCortege.nextCustomer();
                    moveCameraSlowlyBack();
                } else {
                    SoundUtil.playWrongClickSound();
                }
                super.tap(event, x, y, count, button);
            }
        });

        return btnOk;
    }

    private Button createKassaReturnBtn() {
        float btnHeight = kassa.getWidth() * 0.15f;

        final Button btnReturn = new Button(
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_return_up")),
            new TextureRegionDrawable(ATLAS_1.findRegion("btn_return_down"))
        ) {
            @Override
            public void act(float delta) {
                if(isProcessPayment && cardType == 0 && givingSum > 0) {
                    setDisabled(false);
                    kassaBtnReturn.setColor(Color.WHITE);
                } else {
                    setDisabled(true);
                    kassaBtnReturn.setColor(Color.DARK_GRAY);
                }
                super.act(delta);
            }
        };
        float h = btnHeight;
        float w = btnReturn.getWidth() * h / btnReturn.getHeight();
        btnReturn.setSize(w, h);
        btnReturn.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if(isProcessPayment && cardType == 0 && givingSum > 0) {
                    SoundUtil.playClickSound();
                    givingSum = 0;
                    clearCash();
                    super.tap(event, x, y, count, button);
                }
            }
        });

        return btnReturn;
    }

    private void finishPayment() {
        totalCost = 0;
        payedSum = 0;
        givingSum = 0;
        terminalValue = "";
        clearCash();
        isProcessPayment = false;
    }

    private void clearCash() {
        for(Image image : dollarsImagesList) {
            image.setVisible(false);
            removeActor(image);
        }
        dollarsImagesList.clear();
    }

    private void addAchievementsProgress(boolean isCard) {
        if(isCard) {
            paymentTypes.add("card");
        } else {
            paymentTypes.add("cash");
        }

        User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_1, 1);

        if(!AchievementInfo.ACHIEVEMENT_INFO_2.isComplete()) {
            User.get().achievments.put(AchievementInfo.ACHIEVEMENT_INFO_2.type, paymentTypes.size());
        }

        if(!AchievementInfo.ACHIEVEMENT_INFO_3.isComplete()) {
            User.get().achievments.put(AchievementInfo.ACHIEVEMENT_INFO_3.type, User.get().dollars);
        }

        User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_4, 1);

        User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_5, 1);

        if(!isCard) { // только если наличка, по карте всегда точный рассчет
            int change = payedSum - totalCost; //сдачи сколько нужно
            boolean isExactly = givingSum == change;
            if(isExactly) {
                User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_6, 1);
            }
        }

        //todo ACHIEVEMENT_INFO_7

        if(totalCost > 50) {
            User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_8, 1);
        }

        if(totalCost > 100) {
            User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_9, 1);
        }

        if(totalCost > 500) {
            User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_10, 1);
        }

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 21) { // После 21:00
            User.get().addAchievementProgress(AchievementInfo.ACHIEVEMENT_INFO_11, 1);
        }
    }

}
