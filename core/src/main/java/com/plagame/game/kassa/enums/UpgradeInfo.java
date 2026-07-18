package com.plagame.game.kassa.enums;

import static com.plagame.game.kassa.Resources.ATLAS_1;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.kassa.utils.NumberFormat;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 28.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public enum UpgradeInfo {

    // ------ Имущество ------ tapIncome: +2% * nextLevel, pasIncome: +4% * nextLevel - (+4% * nextLevel) / 100
    HOUSE(1, "дом", 2, 4), // дом (машина, сад, камера)
    CAR(2, "машина", 2, 4), // машина (сад, камера)
    GRASS_ROAD(3, "газон и дорога", 2, 4), // газон и дорога (камера)
    BACKYARD(4, "задний двор", 2, 4), // задний двор (статую) // фонтан?
    STATUE(5, "статуя", 2, 4), // статуя

    // ------ Техника ------ tapIncome: +4% * nextLevel, pasIncome: +2% * nextLevel
    CAMERA(11, "камера", 4, 2), // камера
    TRIPOD(12, "штатив", 4, 2), // штатив
    MICRO(13, "микрофон", 4, 2), // микрофон
    LIGHT(14, "светильник", 4, 2), // светильник

    // ------ Стиль ------ tapIncome: +2% * nextLevel, pasIncome: +4% * nextLevel
    CLOTH(21, "одежда", 2, 4),// одежда
    HAIRSTYLE(22, "прическа", 2, 4), // прическа (требует одежду)
    GLASSES(23, "очки", 2, 4), // очки ( одежду)
    WATCH(24, "часы", 2, 4), // часы (одежду и прическу)

    // ------ Навыки ------ tapIncome: +3% * nextLevel, pasIncome: +3% * nextLevel
    CHARISMA(31, "харизма", 3, 3), // Харизма (Импровизация, Эмоциональность)
    IMPROVISATION(32, "импровизация", 3, 3), // Импровизация
    ERUDITION(33, "эрудиция", 3, 3), // Эрудиция (Харизма, Импровизация, Остроумие, Эмоциональность)
    SMARTNESS(34, "остроумие", 3, 3), // Остроумие (Харизма, Импровизация, Эмоциональность)
    EMOTIONALITY(35, "эмоциональность", 3, 3), // Эмоциональность
    ;

    private static final int START_BONUS = 1; // 1%
    public final int type;
    public final String name;
    private final int tapAdd; // сколь +% добавляется с каждым уровнем
    private final int pasAdd;
    private List<UpgradeInfo> anotherUpgradesList;

    UpgradeInfo(int type, String name, int tapAdd, int pasAdd) {
        this.type = type;
        this.name = name;
        this.tapAdd = tapAdd;
        this.pasAdd = pasAdd;
        ENUM_MAPS.UpgradeInfoMap.put(type, this);
    }

    public static UpgradeInfo getByType(int type) {
        return ENUM_MAPS.UpgradeInfoMap.get(type);
    }

    public List<UpgradeInfo> getAnotherUpgradesList() {
        if (anotherUpgradesList == null) {
            anotherUpgradesList = createAnotherUpgradesList();
        }
        return anotherUpgradesList;
    }

    private List<UpgradeInfo> createAnotherUpgradesList() {
        List<UpgradeInfo> list = new LinkedList<>();
        if (this == HOUSE) {
            list.addAll(ShopInfo.THINGS.getUpgradesList());
            list.remove(0);
            list.add(UpgradeInfo.CAMERA);
        } else {
            if (this.type > HOUSE.type && this.type <= STATUE.type) {
                return ShopInfo.THINGS.getUpgradesList();
            } else if (this.type >= CAMERA.type && this.type <= LIGHT.type) {
                return ShopInfo.EQUIPMENT.getUpgradesList();
            } else if (this.type >= CLOTH.type && this.type <= WATCH.type) {
                return ShopInfo.STYLE.getUpgradesList();
            } else if (this.type >= CHARISMA.type && this.type <= EMOTIONALITY.type) {
                return ShopInfo.SKILLS.getUpgradesList();
            }
        }
        return list;
    }

    public TextureRegion getTexture() {
        return ATLAS_1.findRegion("icon_upgrade_" + type);
    }

    public static float getTapIncomeMultiplierSum(int curLevel) {
        int tapAdd = 2;
        return tapAdd * (curLevel * (curLevel + 1) / 2f - 1) / 100f;
    }

    public static float getPasIncomeMultiplierSum(int curLevel) {
        int pasAdd = 4;
        return pasAdd * (curLevel * (curLevel + 1) / 2f - 1) / 100f;
    }

    public int getLockLevel() {
        return LocationInfo.getByType(User.get().location).getLockUpgradeLevel();
    }

    public int getMaxLevel() {
        return LocationInfo.getByType(User.get().location).getMaxUpgradeLevel();
    }

    public int getTapAddToNextLevel(int curLevel) { // для отображения прибавки за следующий уровень
        return tapAdd * curLevel;
    }

    public int getPasAddToNextLevel(int nextLevel) { // для отображения прибавки за следующий уровень
        return pasAdd * nextLevel;
    }

    public int getPasAddToMaxLevel(int nextLevel) { // для отображения MAX прибавки PasAdd за уровени которые можно прокачать
        int percentAsInt = 0;
        int count = getUpgradeCountMax(nextLevel);
        for(int i = nextLevel; i < nextLevel + count; i++) {
            percentAsInt += getPasAddToNextLevel(i);
        }
        if(percentAsInt == 0) {
            return getPasAddToNextLevel(nextLevel);
        }
        return percentAsInt;
    }

    public int getTapAddToMaxLevel(int nextLevel) { // для отображения MAX прибавки TapAdd за уровени которые можно прокачать
        int percentAsInt = 0;
        int count = getUpgradeCountMax(nextLevel);
        for(int i = nextLevel; i < nextLevel + count; i++) {
            percentAsInt += getTapAddToNextLevel(i);
        }
        if(percentAsInt == 0) {
            return getTapAddToNextLevel(nextLevel);
        }
        return percentAsInt;
    }

    public long getUpgradeCostMax(int nextLevel) { // для отображения MAX стоимости прокачки за уровени которые можно прокачать
        long sum = 0;
        int count = getUpgradeCountMax(nextLevel);
        for(int i = nextLevel; i < nextLevel + count; i++) {
            sum += getUpgradeCost(i);
        }
        if(sum == 0) {
            return getUpgradeCost(nextLevel);
        }
        return sum;
    }

    public int getUpgradeCountMax(int nextLevel) { // для отображения MAX количества уровней которые можно прокачать
        int count = 0;
        long dollars = User.get().getDollars();

        LocationInfo locationInfo = LocationInfo.getByType(User.get().location);
        int limit = nextLevel <= locationInfo.getLockUpgradeLevel()
            ? locationInfo.getLockUpgradeLevel()
            : locationInfo.getMaxUpgradeLevel();

        for (int i = nextLevel; i <= limit; i++) {
            long cost = getUpgradeCost(i);
            if (dollars >= cost) {
                count++;
                dollars -= cost;
            } else {
                return count;
            }
        }
        return count;
    }

    public long getUpgradeCost(int nextLevel) {
        if (nextLevel <= 1) return 0;

        float baseIncome = 100f;     // якорь баланса
        float T = 60f;               // время окупаемости апгрейда в секундах
        float r = 1.05f;             // экспоненциальный рост

        float k = tapAdd + pasAdd;   // % за уровень
        long locMult = User.get().getLocationInfo().getMultiplier();
        float locationDifficulty = 1f + (User.get().location - 1) * 0.15f; // время окупаемости растет с локацией, тут будет 1.00, 1.15, 1.30 и т.д.

        float deltaPercent = k * nextLevel;

        float cost = baseIncome
            * locMult
            * locationDifficulty
            * (deltaPercent / 100f)
            * T
            * (float)Math.pow(r, nextLevel);

        return (long) cost;
    }

    public SceneObjectInfo getSceneObjectInfo() {
        switch (this) {
            case HOUSE: return SceneObjectInfo.HOUSE;
            case CAR: return SceneObjectInfo.CAR;
            case GRASS_ROAD: return SceneObjectInfo.GROUND;
            case BACKYARD: return SceneObjectInfo.TREE;
            case STATUE: return SceneObjectInfo.STATUE;

            case CAMERA: return SceneObjectInfo.CAMERA;
            case TRIPOD: return SceneObjectInfo.CAMERA; //todo
            case MICRO: return SceneObjectInfo.MICRO;
            case LIGHT: return SceneObjectInfo.LIGHT;

            case CLOTH:
            case HAIRSTYLE:
            case GLASSES:
            case WATCH: return SceneObjectInfo.GIRL;

            case CHARISMA:
            case IMPROVISATION:
            case ERUDITION:
            case SMARTNESS:
            case EMOTIONALITY: return SceneObjectInfo.BACKGROUND;

            default: return SceneObjectInfo.GIRL;
        }
    }

    public static UpgradeInfo getUpgradeInfo(SceneObjectInfo sceneObjectInfo) {
        for(UpgradeInfo upgradeInfo : UpgradeInfo.values()) {
            if(sceneObjectInfo.type == upgradeInfo.getSceneObjectInfo().type) {
                return upgradeInfo;
            }
        }
        return UpgradeInfo.HOUSE;
    }

    public static void main(String[] args) {
        int k = 2;
        for(int  i = 1; i <= 599; i++) {
            float sum = k * (i * (i + 1) / 2f - 1f);
            long cost = (long)UpgradeInfo.HOUSE.getUpgradeCost(i + 1);
            System.out.println("-- i = " + i + " ---------sum = " + (long)sum + "%  cost = " + NumberFormat.format(cost));
        }


    }
}
