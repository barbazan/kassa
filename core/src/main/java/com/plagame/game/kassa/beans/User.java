package com.plagame.game.kassa.beans;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import static com.plagame.game.kassa.enums.ProductInfo.START_PRODUCT_LIST;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.enums.AchievementInfo;
import com.plagame.game.kassa.serialize.JsonUserSerializer;
import com.plagame.game.kassa.utils.FileUtil;
import com.plagame.game.kassa.utils.FileUtilHtml;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.net.kassa.KassaUserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Дмитрий Малышев on 11.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class User implements Json.Serializable {
    private static final long SAVE_INTERVAL = 10_000;
    private static volatile User instance;

    public int id;
    public String secret = "";
    public String login = "";
    public int location = 1; // локация LocationInfo
    public int day = 1; // игровая валюта
    public int dollars = 250_00; // игровая валюта доллары, хранятся в центах, чтобы без флота
    public int maxKassaDollars; // Максимальное кол-во долларов у игрока (для рейтинга)
    public long lastSaveTime = System.currentTimeMillis(); // последнее время сохранения, чтобы часто не сохранять
    public long lastLoginTime = System.currentTimeMillis();
    public boolean soundOn = true;
    public boolean musicOn = true;
    public HashSet<Integer> buyedProducts = new HashSet<>();
    public HashSet<String> purchasedProducts = new HashSet<>();
    public HashMap<Integer, Integer> achievments = new HashMap();
    public long loginDayCount;
    public boolean isAdHide;
    private boolean isFullVersionBuyed;
    public int advGoodIndex; // анлоченые за рекламу товары
    public long advGoodsEndTime; // когда заканчиваются анлоченые за рекламу продукты
    public transient Map<String, Integer> rankMap = new HashMap<>(); // никуда не сохраняем, живет в рамках одной игровой снессии
    public transient Set<Integer> completeAchievments = new HashSet<>();

    public User() {
        super();
    }

    public static User get() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = loadUserLocal();
                    System.out.println("---------------loadUserLocal-----------instance = " + instance);
                    if (instance == null) {
                        instance = new User();
                        instance.buyedProducts.addAll(START_PRODUCT_LIST);
                        instance.saveUser();
                    }
                }
            }
        }

        return instance;
    }

    public User apply(KassaUserData userData) {
        this.id = userData.id;
        this.secret = userData.secret;
        this.login = userData.login;
        this.dollars = userData.dollars;
        this.maxKassaDollars = userData.maxKassaDollars;
        this.day = userData.day;
        this.lastLoginTime = userData.lastLoginTime;
        this.isAdHide = userData.isAdHide;
        this.isFullVersionBuyed = userData.isBuyFull;
        this.advGoodIndex = userData.advGoodIndex;
        this.advGoodsEndTime = userData.advGoodsEndTime;
        this.buyedProducts = new HashSet<>(userData.buyedProducts);
        this.achievments = new HashMap<>(userData.achievments);
        this.purchasedProducts = new HashSet<>(userData.purchasedProducts);
        return this;
    }

    public void fillCompleteAchievements() {
        for(AchievementInfo achievementInfo : AchievementInfo.values()) {
            if(achievementInfo.isComplete()) {
                completeAchievments.add(achievementInfo.type);
            }
        }
    }

    public String getDollarsAsString() {
        return NumberFormat.formatCost(dollars);
    }

    public boolean canPayDollars(float amount) {
        return dollars >= amount;
    }

    public boolean doPayDollars(int amount) {
        if(canPayDollars(amount)) {
            changeDollars(-amount);
            return true;
        }
        return false;
    }

    public void changeGold(int amount) { // в биллинге везде голда покупается, чтобы везде не менять, просто баксы начисляю тут и все
        setDollar(Math.max(dollars + amount, 0));
    }

    public void changeDollars(int amount) {
        setDollar(Math.max(dollars + amount, 0));
    }

    public void setDollar(int dollars) {
        if (dollars < 0) {
            throw new RuntimeException("Dollars can't be < 0. Current value = " + dollars);
        }
        this.dollars = dollars;
    }

    private void trySaveWithCooldown() {
        if(System.currentTimeMillis() > lastSaveTime + SAVE_INTERVAL) {
            saveUser();
        }
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
    }

    public void saveUser() {
        GameApplication.get().platform.cloud().saveUser(this);    // сохраняем и локально и в облако
        lastSaveTime = System.currentTimeMillis();
    }

    public void submitScore() {
        if(GameApplication.get().platform.isLeaderboardAvailable() && isAuthorized()) {
            int leaderboardScore = (int)dollars;
            GameApplication.get().platform.leaderboard().submitScore(GameConfig.LEADERBOARD_MAX_DOLLARS_NAME, leaderboardScore, NumberFormat.format(leaderboardScore));
        }
    }

    public void addAchievementProgress(AchievementInfo achievementInfo, int value) {
        int curValue = achievments.getOrDefault(achievementInfo.type, 0);
        achievments.put(achievementInfo.type, curValue + value);
    }

    public boolean isAuthorized() {
        return GameApplication.get().platform.cloud().isAuthorized();
    }

    public void applyCloudUser(User cloudUser) {
//        YandexSDK.alert("cloudUser.id = " + cloudUser.id);
        if(cloudUser.id == 0) { // это значит что в облако ничего не сохраняли и нужно не потерять локальный прогресс, все локальное добавить и сохранить в облако
            instance.id = 1; // помечаем юзера и в следующий раз уже будет только облачный браться
            instance.dollars = cloudUser.dollars;
            instance.lastSaveTime = cloudUser.lastSaveTime;
            instance.lastLoginTime = cloudUser.lastLoginTime;
            instance.soundOn = cloudUser.soundOn;
            instance.musicOn = cloudUser.musicOn;
            instance.loginDayCount = cloudUser.loginDayCount;
            instance.isAdHide = cloudUser.isAdHide;
            instance.buyedProducts.addAll(cloudUser.buyedProducts);
            instance.purchasedProducts.addAll(cloudUser.purchasedProducts);
            instance.saveUserLocal(); // сохраняем локально
        } else { // иначе сохраняем локально то что пришло из облака
            instance = cloudUser;
            instance.saveUserLocal(); // сохраняем локально
        }
    }

    public void markPurchasedToken(String token) {
        purchasedProducts.add(token);
        instance.saveUser(); // сохраняем сразу через инстанс
    }

    public boolean isPurchasedToken(String token) {
        return purchasedProducts.contains(token);
    }

    // GWT reflection cannot access long fields or resolve inherited collection interfaces.
    @Override public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("secret", secret);
        json.writeValue("login", login);
        json.writeValue("location", location);
        json.writeValue("day", day);
        json.writeValue("dollars", dollars);
        json.writeValue("maxKassaDollars", maxKassaDollars);
        json.writeValue("lastSaveTime", lastSaveTime);
        json.writeValue("lastLoginTime", lastLoginTime);
        json.writeValue("soundOn", soundOn);
        json.writeValue("musicOn", musicOn);
        json.writeValue("buyedProducts", buyedProducts, HashSet.class, Integer.class);
        json.writeValue("purchasedProducts", purchasedProducts, HashSet.class, String.class);
        json.writeValue("achievments", achievments, HashMap.class, Integer.class);
        json.writeValue("loginDayCount", loginDayCount);
        json.writeValue("isAdHide", isAdHide);
        json.writeValue("isFullVersionBuyed", isFullVersionBuyed);
        json.writeValue("advGoodIndex", advGoodIndex);
        json.writeValue("advGoodsEndTime", advGoodsEndTime);
    }

    @Override public void read(Json json, JsonValue data) {
        id = data.getInt("id", id);
        secret = data.getString("secret", secret);
        login = data.getString("login", login);
        location = data.getInt("location", location);
        day = data.getInt("day", day);
        dollars = data.getInt("dollars", dollars);
        maxKassaDollars = data.getInt("maxKassaDollars", maxKassaDollars);
        lastSaveTime = data.getLong("lastSaveTime", lastSaveTime);
        lastLoginTime = data.getLong("lastLoginTime", lastLoginTime);
        soundOn = data.getBoolean("soundOn", soundOn);
        musicOn = data.getBoolean("musicOn", musicOn);
        if (data.has("buyedProducts")) buyedProducts = json.readValue("buyedProducts", HashSet.class, Integer.class, data);
        if (data.has("purchasedProducts")) purchasedProducts = json.readValue("purchasedProducts", HashSet.class, String.class, data);
        if (data.has("achievments")) {
            achievments = new HashMap<>();
            for (JsonValue entry = data.get("achievments").child; entry != null; entry = entry.next) {
                if (!"class".equals(entry.name)) achievments.put(Integer.parseInt(entry.name), entry.asInt());
            }
        }
        loginDayCount = data.getLong("loginDayCount", loginDayCount);
        isAdHide = data.getBoolean("isAdHide", isAdHide);
        isFullVersionBuyed = data.getBoolean("isFullVersionBuyed", isFullVersionBuyed);
        advGoodIndex = data.getInt("advGoodIndex", advGoodIndex);
        advGoodsEndTime = data.getLong("advGoodsEndTime", advGoodsEndTime);
    }

    public String toJson() {
        return JsonUserSerializer.serialize(this);
    }

    public static User fromJson(String json) {
        return JsonUserSerializer.deserialize(json);
    }

    public byte[] serialize() {
        return toJson().getBytes();
//        return ByteArrayUserSerializer.serialize(this);
    }

    public static User deserialize(byte[] data) {
        return fromJson(new String(data));
//        return ByteArrayUserSerializer.deserialize(data);
    }

    public boolean isFullVersionBuyed() {
//        return true;
        return isFullVersionBuyed;
    }

    public void setFullVersionBuyed(boolean fullVersionBuyed) {
        isFullVersionBuyed = fullVersionBuyed;
    }

    public boolean hasAdvGoods() {
        return advGoodIndex > 0 && advGoodsEndTime > System.currentTimeMillis();
    }

    public long getAdvGoodsTimeleft() {
        return advGoodsEndTime - System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", location=" + location +
            ", dollars=" + dollars +
            ", lastSaveTime=" + lastSaveTime +
            ", lastLoginTime=" + lastLoginTime +
            ", soundOn=" + soundOn +
            ", musicOn=" + musicOn +
            ", buyedProducts=" + buyedProducts +
            ", purchasedProducts=" + purchasedProducts +
            ", loginDayCount=" + loginDayCount +
            '}';
    }
}
