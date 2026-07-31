package com.plagame.game.kassa.beans;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.serialize.ByteArrayUserSerializer;
import com.plagame.game.kassa.serialize.JsonUserSerializer;
import com.plagame.game.kassa.utils.FileUtil;
import com.plagame.game.kassa.utils.FileUtilHtml;
import com.plagame.game.kassa.utils.NumberFormat;
import com.plagame.game.net.kassa.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Дмитрий Малышев on 11.04.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class User {
    private static final long SAVE_INTERVAL = 10_000;
    private static volatile User instance;

    public int id;
    public String secret = "";
    public String login = "";
    public int location = 1; // локация LocationInfo
    public float dollars = 0; // игровая валюта
    public long lastSaveTime = System.currentTimeMillis(); // последнее время сохранения, чтобы часто не сохранять
    public long lastLoginTime = System.currentTimeMillis();
    public boolean soundOn = true;
    public boolean musicOn = true;
    public HashSet<String> purchasedProducts = new HashSet<>();
    public long loginDayCount;
    public boolean isAdHide;
    public Map<String, Integer> rankMap = new HashMap<>(); // никуда не сохраняем, живет в рамках одной игровой снессии

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
        this.dollars = userData.dollars;
        this.lastLoginTime = userData.lastLoginTime;
        this.isAdHide = userData.isAdHide;
        this.purchasedProducts = new HashSet<>(userData.purchasedProducts);
        return this;
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

    public void changeGold(int amount) { // в биллинге везде голда покупается, чтобы везде не менять, просто баксы начисляю тут и все
        setDollar(Math.max(dollars + amount, 0));
    }

    public void changeDollars(float amount) {
        setDollar(Math.max(dollars + amount, 0));
    }

    public void setDollar(float dollars) {
        if (dollars < 0) {
            throw new RuntimeException("Dollars can't be < 0. Current value = " + dollars);
        }
        this.dollars = dollars;
    }

    public float getDollars() {
        return dollars;
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
            instance.purchasedProducts = new HashSet<>(cloudUser.purchasedProducts);
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
            ", dollars=" + dollars +
            ", lastSaveTime=" + lastSaveTime +
            ", lastLoginTime=" + lastLoginTime +
            ", soundOn=" + soundOn +
            ", musicOn=" + musicOn +
            ", purchasedProducts=" + purchasedProducts +
            ", loginDayCount=" + loginDayCount +
            '}';
    }
}
