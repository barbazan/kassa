package com.plagame.game.kassa.beans;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.BoosterInfo;
import com.plagame.game.kassa.enums.LocationInfo;
import com.plagame.game.kassa.enums.PerkInfo;
import com.plagame.game.kassa.enums.UpgradeInfo;
import com.plagame.game.kassa.net.kassa.UserData;
import com.plagame.game.kassa.serialize.ByteArrayUserSerializer;
import com.plagame.game.kassa.serialize.JsonUserSerializer;
import com.plagame.game.kassa.utils.FileUtil;
import com.plagame.game.kassa.utils.FileUtilHtml;
import com.plagame.game.kassa.utils.NumberFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Дмитрий Малышев on 11.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class User {
    private static final long REFRESH_INTERVAL = 100;
    private static final long SAVE_INTERVAL = 10_000;
    private static final long GIFT_INTERVAL = 10 * 60 * 1000; //10 минут
    private static final long LONG_ABSENCE_INTERVAL = 3 * 60 * 1000; //3 минуты
    public static final int MAX_ENERGY = 100;
    public static final long MAX_ENERGY_TIME = 3 * 60 * 1000; // за три минуты енергия восстанавливается
    public static final long LAST_TAP_PAUSE = 5 * 1000; // через сколько после тапа начинать генерить энергию
    public static final int BASE_TAP_INCOME = 100;
    public static final int BASE_PAS_INCOME = 100;
    public static final int BASE_FOLOWERS_INCOME = 10;
    private static volatile User instance;

    public int id;
    public String secret = "";
    public String login = "";
    public int location = 1; // локация LocationInfo
    public long gold = 0; // премиум валюта
//    public long gold = 100_000; // премиум валюта
    public long dollars = 0; // игровая валюта
//    public long dollars = 10_000_000_000_000L; // игровая валюта
    public long folowers; // подписчики
    public long maxFolowers = 100; // локальный максимум подписчиков, чтобы прогрессбар заполнять
    public long lastMaxFolowers = 0; // предыдущий максимум подписчиков, чтобы прогрессбар с него начинать
    public float pasIncome = BASE_PAS_INCOME; // пассивный доход dollar в секунду
    public float tapIncome = BASE_TAP_INCOME; // доход dollar по клику
    public float folowersIncome = 1; // рост подпизчиков в сек
    public long maxEnergy = MAX_ENERGY; // максимальное значение энергии
    public float energy = MAX_ENERGY; // энергия
    public boolean isMax; // в магазине
    public ConcurrentHashMap<String, Integer> upgradesMap = new ConcurrentHashMap<>(); // улучшение UpgradeInfo тип на уровень
    public ConcurrentHashMap<String, Long> boostersMap = new ConcurrentHashMap<>(); // временные бафы, хранится время окончания
    public ConcurrentHashMap<String, Integer> perksMap = new ConcurrentHashMap<>(); // порстоянные бустеры за золото, хранится уровень
    public long lastRefreshTime = System.currentTimeMillis(); // время последнего авторефреша, от него считается сколько сгенерить пассивного дохода
    public transient long lastTapTime = System.currentTimeMillis(); // время последнего тапа по экрану, не храним, только в рантайме для генерации энергии используем, когда начинать генерить после тапа
    public long lastSaveTime = System.currentTimeMillis(); // последнее время сохранения, чтобы часто не сохранять
    public long lastLoginTime = System.currentTimeMillis();
    public long lastGiftTime = System.currentTimeMillis() - GIFT_INTERVAL; // не сохраняем
    public long absenceMinutes = 0; // сколько минут не было игрока
    public long absenceDollars = 0; // начисленные доллары после перерыва
    public boolean soundOn = true;
    public boolean musicOn = true;
    public HashSet<String> purchasedProducts = new HashSet<>();
    public long loginDayCount;
    public long maxDollars; // Максимальное кол-во долларов у игрока (для рейтинга)
    public boolean isAdHide;
    public boolean hasGift;
    public boolean needSubmitScore; // флаг нужно ли репортить рейтинг, было ли новый максимум, не сохраняем
    public Map<String, Integer> rankMap = new HashMap<>(); // никуда не сохраняем, живет в рамках одной игровой снессии

    public User() {
        super();
        for (UpgradeInfo upgradeInfo : UpgradeInfo.values()) {
            upgradesMap.put(String.valueOf(upgradeInfo.type), 1);
        }
    }

    public static User get() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = loadUserLocal();
                    System.out.println("---------------loadUserLocal-----------instance = " + instance);
                    if (instance == null) {
                        instance = new User();
                        instance.saveUserLocal();
                    }
                }
            }
        }

        return instance;
    }

    public User apply(UserData userData) {
        this.id = userData.id;
        this.secret = userData.secret;
        this.login = userData.login;
        this.location = userData.location;
        this.gold = userData.gold;
        this.dollars = userData.dollars;
        this.folowers = userData.folowers;
        this.maxFolowers = userData.maxFolowers;
        this.lastMaxFolowers = userData.lastMaxFolowers;
        this.pasIncome = userData.pasIncome;
        this.tapIncome = userData.tapIncome;
        this.folowersIncome = userData.folowersIncome;
//        this.energy = userData.energy;
        this.isMax = userData.isMax;
        this.lastLoginTime = userData.lastLoginTime;
        this.maxDollars = userData.maxDollars;
        this.isAdHide = userData.isAdHide;
        this.hasGift = userData.hasGift;
        this.upgradesMap = new ConcurrentHashMap<>(userData.upgradesMap);
        this.boostersMap = new ConcurrentHashMap<>(userData.boostersMap);
        this.perksMap = new ConcurrentHashMap<>(userData.perksMap);
        this.purchasedProducts = new HashSet<>(userData.purchasedProducts);
        return this;
    }

    public LocationInfo getLocationInfo() {
        return LocationInfo.getByType(location);
    }

    public LocationInfo getNextLocationInfo() {
        return LocationInfo.getByType(Math.min(LocationInfo.MAX_LOCATION.type, location + 1));
    }

    public boolean hasNextLocation() {
        return location < LocationInfo.DUBAI.type;
    }

    public float getEnergyPercent() {
        float result = energy / (float) MAX_ENERGY;
        return Math.min(1, Math.max(0, result));
    }

    public float getFolowersPercent() {
        float start = lastMaxFolowers;
        float result = (getFolowers() - start) / (maxFolowers - start);
        return Math.min(1, Math.max(0, result));
    }

    public float getLocationProgress() {
        int allCount = UpgradeInfo.values().length;
        int startLevel = getLocationInfo().getStartUpgradeLevel();
        int upgradeMaxSum = allCount * (getLocationInfo().getMaxUpgradeLevel() - startLevel);
        float sum = 0;
        for(UpgradeInfo upgradeInfo : UpgradeInfo.values()) {
            int count = getUpgradeLevel(upgradeInfo) - startLevel;
            sum += count;
        }
        return sum / upgradeMaxSum;
    }

    public boolean isUpgradeLock(UpgradeInfo upgradeInfo) {
        int curLevel = getUpgradeLevel(upgradeInfo);
        int lockLevel = LocationInfo.getByType(location).getLockUpgradeLevel();
        return curLevel >= lockLevel && isNeedAnotherUpgradesFor(upgradeInfo);
    }

    public boolean isUpgradeMax(UpgradeInfo upgradeInfo) {
        int curLevel = getUpgradeLevel(upgradeInfo);
        return curLevel >= LocationInfo.getByType(location).getMaxUpgradeLevel();
    }

    public boolean isAllUpgradesMax() {
        for(Map.Entry<String, Integer> entry : upgradesMap.entrySet()) {
            UpgradeInfo upgradeInfo = UpgradeInfo.getByType(Integer.parseInt(entry.getKey()));
            if(!isUpgradeMax(upgradeInfo)) {
                return false;
            }
        }
        return true;
    }

    // проверяем нужно ли прокачать другие скилы для продолжения прокачки этого
    public boolean isNeedAnotherUpgradesFor(UpgradeInfo upgradeInfo) {
        return !getAnotherUpgradesNeedUpFor(upgradeInfo).isEmpty();
    }

    // список апгрейдов, которые нужно сделать чтоб качать этот дальше
    public List<UpgradeInfo> getAnotherUpgradesNeedUpFor(UpgradeInfo upgradeInfo) {
        List<UpgradeInfo> needList = new ArrayList<>();
        int lockLevel = upgradeInfo.getLockLevel();
        List<UpgradeInfo> anotherList = upgradeInfo.getAnotherUpgradesList();
        for(int i = 0; i < anotherList.size(); i++) {
            UpgradeInfo anotherUpgradeInfo = anotherList.get(i);
            int anotherUpgradeLevel = getUpgradeLevel(anotherUpgradeInfo);
            if(anotherUpgradeLevel < lockLevel - 1) {
                needList.add(anotherUpgradeInfo);
            }
        }
        return needList;
    }

    // список апгрейдов, которые нужно сделать чтобы переехать
    public List<UpgradeInfo> getUpgradesNeedForPereezd() {
        List<UpgradeInfo> needList = new ArrayList<>();
        int maxLevel = LocationInfo.getByType(User.get().location).getMaxUpgradeLevel();
        for(UpgradeInfo upgradeInfo : UpgradeInfo.values()) {
            if(User.get().getUpgradeLevel(upgradeInfo) != maxLevel) {
                needList.add(upgradeInfo);
            }
        }
        return needList;
    }

    public int getUpgradeLevel(UpgradeInfo upgradeInfo) {
        if(upgradeInfo != null) {
            return upgradesMap.computeIfAbsent(String.valueOf(upgradeInfo.type), k -> 1);
        }
        return 1;
    }

    public int getPerkLevel(PerkInfo boosterInfo) {
        if(boosterInfo != null) {
            return perksMap.computeIfAbsent(String.valueOf(boosterInfo.type), k -> 0);
        }
        return 0;
    }

    public long getBoosterEndTime(BoosterInfo boosterInfo) {
        if(boosterInfo != null) {
            return boostersMap.computeIfAbsent(String.valueOf(boosterInfo.type), k -> 0L);
        }
        return 0;
    }

    public long getBoosterTimeleft(BoosterInfo boosterInfo) {
        return getBoosterEndTime(boosterInfo) - System.currentTimeMillis();
    }

    public boolean hasBooster(BoosterInfo boosterInfo) {
        return getBoosterTimeleft(boosterInfo) > 0;
    }

    public int getUpgradeLevelSceneObjectNum(UpgradeInfo upgradeInfo) {
        int level = getUpgradeLevel(upgradeInfo);
        if(level < upgradeInfo.getLockLevel()) { // первая картинка
            return 1;
        } else if(level < upgradeInfo.getMaxLevel()) { // вторая картинка
            return 2;
        } else { // третья картинка
            return 3;
        }
    }

    public boolean hasEnergy() {
        return energy > 0;
    }

    public void doTap() {
        if(hasEnergy()) {
            long addDollars = (long)tapIncome;
            if(hasBooster(BoosterInfo.BOOSTER_KOFE)) { // если есть бустер на тап то
                addDollars = addDollars * 4; // увеличиваем доход в 4 раза
            }
            changeDollars(addDollars);
            if(!hasBooster(BoosterInfo.BOOSTER_ENERGY)) { // если нет бустера на бесконечную энергию, то
                energy = Math.max(0, energy - 1); // тратим энергию за тап
            }
            lastTapTime = System.currentTimeMillis();
        }
    }

    public boolean canPayDollars(long amount) {
        return dollars >= amount;
    }

    public boolean doPayDollars(long amount) {
        if(canPayDollars(amount)) {
            changeDollars(-amount);
            return true;
        }
        return false;
    }

    public boolean canPayGold(long amount) {
        return gold >= amount;
    }

    public boolean doPayGold(long amount) {
        if(canPayGold(amount)) {
            changeGold(-amount);
            return true;
        }
        return false;
    }

    public void buyUpgrade(UpgradeInfo upgradeInfo) {
        buyUpgrade(upgradeInfo, 1);
    }

    public void buyUpgrade(UpgradeInfo upgradeInfo, int countLevels) {
        int curlevel = getUpgradeLevel(upgradeInfo);
        upgradesMap.put(String.valueOf(upgradeInfo.type), curlevel + countLevels);
        GameApplication.get().getGameScreen().gameScene.upgradedObjects.add(upgradeInfo.getSceneObjectInfo().type); // помечаем на сцене что это предмет апгрейдили
    }

    public void buyBooster(BoosterInfo boosterInfo) { // покупка временного бафа за золото
        long endTime = getBoosterEndTime(boosterInfo);
        if(endTime < System.currentTimeMillis()) {
            endTime = System.currentTimeMillis() + boosterInfo.duration;
        } else {
            endTime = endTime + boosterInfo.duration;
        }
        boostersMap.put(String.valueOf(boosterInfo.type), endTime);
        if(boosterInfo.type == BoosterInfo.BOOSTER_ENERGY.type) {
            energy = MAX_ENERGY;
            saveUser();
        }
    }

    public void buyBooster(PerkInfo boosterInfo) { // покупка постоянного бустера за золото
        int curlevel = getPerkLevel(boosterInfo);
        upgradesMap.put(String.valueOf(boosterInfo.type), curlevel + 1);
    }

    public void changeGold(long amount) {
        setGold(gold + amount);
    }

    public void setGold(long gold) {
        if (gold < 0) {
            throw new RuntimeException("Rubies can't be < 0. Current value = " + gold);
        }
        this.gold = gold;
    }

    public void changeDollars(long amount) {
        setDollar(dollars + amount);
        if(dollars > maxDollars) {
            maxDollars = dollars;
            needSubmitScore = true;
        }
    }

    public void setDollar(long dollars) {
        if (dollars < 0) {
            throw new RuntimeException("Dollars can't be < 0. Current value = " + dollars);
        }
        this.dollars = dollars;
    }

    public long getDollars() {
        return dollars;
    }

    public void changeFolowers(long amount) {
        setFolowers(folowers + amount);
    }

    public void setFolowers(long folowers) {
        if (folowers < 0) {
            throw new RuntimeException("Folowers can't be < 0. Current value = " + folowers);
        }
        this.folowers = folowers;
    }
    public long getFolowers() {
        return folowers;
    }

    public long getTotalPasIncome() {
        if(hasBooster(BoosterInfo.BOOSTER_PIZZA)) { // если есть бустер на пасивный доход то
            return (long)pasIncome * 4; // увеличиваем доход в 4 раза
        }
        return (long)pasIncome;
    }

    public void refresh() {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - lastRefreshTime;
        if (diff >= REFRESH_INTERVAL) {
            recalcEnergy(diff);
            recalcIncomes();
            recalcFolowers(diff);
            long absenceTime = System.currentTimeMillis() - lastSaveTime;
            if(absenceTime > LONG_ABSENCE_INTERVAL) { // если долго не заходил то через диалог начисление
                recalcAbsenceDollars(absenceTime);
                energy = MAX_ENERGY;
                saveUser();
            } else {
                recalcDollars(diff);
            }
            lastRefreshTime = currentTime;
            trySaveWithCooldown();
        }
    }

    private void trySaveWithCooldown() {
        if(System.currentTimeMillis() > lastSaveTime + SAVE_INTERVAL) {
            saveUser();
            if(needSubmitScore) { // репортим рейтинг
                submitScore();
                needSubmitScore = false;
            }
        }
    }

    private void recalcEnergy(long diff) {
        if(System.currentTimeMillis() - lastTapTime > LAST_TAP_PAUSE) {
            float addEnergy = Math.min(2, MAX_ENERGY * (float)diff / MAX_ENERGY_TIME); // нужно плавно генерить, по 1, но если долго не генерил и много насчиталось, то 2
            energy = Math.min(MAX_ENERGY, energy + addEnergy);
        }
    }

    private void recalcIncomes() {
        try {
            tapIncome = recalcTapIncome();
            pasIncome = recalcPasIncome();
            folowersIncome = recalcFolowersIncome();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void recalcAbsenceDollars(long diff) {
        try {
            float incomeDoll = (diff * pasIncome) / 1000f;
            if (incomeDoll > 0) {
                absenceMinutes = diff / 1000 / 60;
                absenceDollars = (long)incomeDoll;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void recalcDollars(long diff) {
        try {
            float incomeDoll = (diff * pasIncome) / 1000f;
            if(hasBooster(BoosterInfo.BOOSTER_PIZZA)) { // если есть бустер на пасивный доход то
                incomeDoll = incomeDoll * 4; // увеличиваем доход в 4 раза
            }

            if (incomeDoll > 0) {
                dollars += incomeDoll;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void recalcFolowers(long diff) {
        try {
            float incomeFol = (diff * folowersIncome) / 1000f;
            if (incomeFol > 0) {
                folowers += incomeFol;
            }
//            if(!hasGift && getFolowersPercent() >= 0.5f && !isGiftCooldown()) { // подарок раз в 10 минут)
            if(!hasGift && !isGiftCooldown()) { // подарок раз в 10 минут)
                hasGift = true;
                lastGiftTime = System.currentTimeMillis();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private boolean isGiftCooldown() { // подарок раз в 10 минут
        return System.currentTimeMillis() - lastGiftTime < GIFT_INTERVAL;
    }

    private static User loadUserLocal() {
        if(GameApplication.get().isWebGL()) {
            return FileUtilHtml.loadUserLocalAsBytes();
        } else {
            return FileUtil.loadUserLocal();
        }
    }

    private void saveUserLocal() {
        GameApplication.get().platform.cloud().saveUser(this);
//        lastSaveTime = System.currentTimeMillis();
//        if(GameApplication.get().isWebGL()) {
//            FileUtilHtml.saveUserLocal(this);
//        } else {
//            FileUtil.saveUserLocal(this);
//        }
    }

    public void saveUser() {
        GameApplication.get().platform.cloud().saveUser(this);    // сохраняем и локально и в облако
        lastSaveTime = System.currentTimeMillis();
    }

    public void submitScore() {
        if(GameApplication.get().platform.isLeaderboardAvailable() && isAuthorized()) {
            int leaderboardScore = (int)Math.min(Integer.MAX_VALUE, maxDollars / 1_000_000); // в рейтинге только от миллиона
            GameApplication.get().platform.leaderboard().submitScore(GameConfig.LEADERBOARD_MAX_DOLLARS_NAME, leaderboardScore, NumberFormat.format(maxDollars));
        }
    }

    public boolean isAuthorized() {
        return GameApplication.get().platform.cloud().isAuthorized();
    }

    public void applyCloudUser(User cloudUser) {
//        YandexSDK.alert("cloudUser.id = " + cloudUser.id);
        if(cloudUser.id == 0) { // это значит что в облако ничего не сохраняли и нужно не потерять локальный прогресс, все локальное добавить и сохранить в облако
            instance.id = 1; // помечаем юзера и в следующий раз уже будет только облачный браться
            // instance.location = 1; // НЕ СБРАСЫВАЕМ локацию, иначе прогресс пропадет
            instance.gold = cloudUser.gold;
            instance.dollars = cloudUser.dollars;
            instance.folowers = cloudUser.folowers;
            instance.maxFolowers = cloudUser.maxFolowers;
            instance.lastMaxFolowers = cloudUser.lastMaxFolowers;
            instance.pasIncome = cloudUser.pasIncome;
            instance.tapIncome = cloudUser.tapIncome;
            instance.folowersIncome = cloudUser.folowersIncome;
            instance.maxEnergy = MAX_ENERGY;
//            instance.energy = Math.max(cloudUser.energy, instance.energy);
            instance.isMax = instance.isMax || cloudUser.isMax;
            instance.lastRefreshTime = cloudUser.lastRefreshTime;
            instance.lastSaveTime = cloudUser.lastSaveTime;
            instance.lastLoginTime = cloudUser.lastLoginTime;
            instance.absenceMinutes = cloudUser.absenceMinutes;
            instance.absenceDollars = cloudUser.absenceDollars;
            instance.soundOn = cloudUser.soundOn;
            instance.musicOn = cloudUser.musicOn;
            instance.loginDayCount = cloudUser.loginDayCount;
            instance.maxDollars = cloudUser.maxDollars;
            instance.isAdHide = cloudUser.isAdHide;
            instance.upgradesMap = new ConcurrentHashMap<>(cloudUser.upgradesMap);
            instance.boostersMap = new ConcurrentHashMap<>(cloudUser.boostersMap);
            instance.perksMap = new ConcurrentHashMap<>(cloudUser.perksMap);
            instance.purchasedProducts = new HashSet<>(cloudUser.purchasedProducts);
            instance.saveUserLocal(); // сохраняем локально
        } else { // иначе сохраняем локально то что пришло из облака
            instance = cloudUser;
            instance.saveUserLocal(); // сохраняем локально
//            YandexSDK.alert("isYesterday = " + Time.isYesterday(instance.lastLoginTime));
//            if(Time.isYesterday(instance.lastLoginTime)) { // перед локальным сохранением увеличим счетчик дней захода подряд, если это требуется
////                YandexSDK.alert("loginDayCount before = " + instance.loginDayCount);
//                if(instance.loginDayCount >= 7) {
//                    instance.loginDayCount = 1;
//                } else {
//                    instance.loginDayCount = instance.loginDayCount + 1;
//                }
////                YandexSDK.alert("loginDayCount after = " + instance.loginDayCount);
//                instance.lastLoginTime = System.currentTimeMillis();
//                instance.saveUser(); // сохраняем и локально и в облако
////                GameApplication.get().setDailyRewardScreen();
//            } else {
//                instance.saveUserLocal(); // сохраняем локально
//            }
        }
    }

    public void markPurchasedToken(String token) {
        purchasedProducts.add(token);
        instance.saveUser(); // сохраняем сразу через инстанс
    }

    public boolean isPurchasedToken(String token) {
        return purchasedProducts.contains(token);
    }

    private long getLocationMultiplier() {
        return LocationInfo.getByType(location).getMultiplier();
    }

    private float getTapUpgradesMultiplier() {
        float sum = 0;
        for(Map.Entry<String, Integer> entry : upgradesMap.entrySet()) {
            int curLevel = entry.getValue();
            float mult = UpgradeInfo.getTapIncomeMultiplierSum(curLevel);
            sum = sum + mult;
        }
        return sum;
    }

    private float getPasUpgradesMultiplier() {
        float sum = 0;
        for(Map.Entry<String, Integer> entry : upgradesMap.entrySet()) {
            int curLevel = entry.getValue();
            float mult = UpgradeInfo.getPasIncomeMultiplierSum(curLevel);
            sum = sum + mult;
        }
        return sum;
    }

    private float recalcTapIncome() {
        return Math.round(BASE_TAP_INCOME + BASE_TAP_INCOME * getTapUpgradesMultiplier() * getLocationMultiplier());
    }

    private float recalcPasIncome() {
        return BASE_PAS_INCOME + BASE_PAS_INCOME * getPasUpgradesMultiplier() * getLocationMultiplier();
    }

    private float recalcFolowersIncome() {
        return BASE_FOLOWERS_INCOME + BASE_FOLOWERS_INCOME * getPasUpgradesMultiplier() / 10 * getLocationMultiplier() / 10;
    }

    public String toJson() {
        return JsonUserSerializer.serialize(this);
    }

    public static User fromJson(String json) {
        return JsonUserSerializer.deserialize(json);
    }

    public byte[] serialize() {
        return ByteArrayUserSerializer.serialize(this);
    }

    public static User deserialize(byte[] data) {
        return ByteArrayUserSerializer.deserialize(data);
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", location=" + location +
            ", gold=" + gold +
            ", dollars=" + dollars +
            ", folowers=" + folowers +
            ", maxFolowers=" + maxFolowers +
            ", pasIncome=" + pasIncome +
            ", tapIncome=" + tapIncome +
            ", folowersIncome=" + folowersIncome +
            ", maxEnergy=" + maxEnergy +
            ", energy=" + energy +
            ", isMax=" + isMax +
            ", upgradesMap=" + upgradesMap +
            ", lastRefreshTime=" + lastRefreshTime +
            ", lastSaveTime=" + lastSaveTime +
            ", lastLoginTime=" + lastLoginTime +
            ", absenceMinutes=" + absenceMinutes +
            ", absenceDollars=" + absenceDollars +
            ", soundOn=" + soundOn +
            ", musicOn=" + musicOn +
            ", purchasedProducts=" + purchasedProducts +
            ", loginDayCount=" + loginDayCount +
            '}';
    }
}
