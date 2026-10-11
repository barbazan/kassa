package com.plagame.game.net.kassa;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.kassa.beans.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KassaUserData implements Json.Serializable {

    private static final Json JSON = new Json();

    public int id;
    public String secret;
    public String login;
    public int day; // игровой день
    public int dollars; // игровая валюта
    public int maxKassaDollars; // Максимальное кол-во долларов у игрока (для рейтинга)
    public long lastLoginTime;
    public boolean isAdHide;
    public boolean isBuyFull;
    public int advGoodIndex; // анлоченые за рекламу товары
    public long advGoodsEndTime = System.currentTimeMillis(); // когда заканчиваются анлоченые за рекламу продукты
    public Set<String> purchasedProducts = new HashSet<>(); // запурчайсеные токены
    public List<Purchase> purchaseList = new ArrayList<>(); // оплаченые покупки
    public List<LeaderboardEntry> ratingMaxDollars = new ArrayList<>(); // рейтинг
    public HashSet<Integer> buyedProducts = new HashSet<>(); // купленные продукты
    public HashMap<Integer, Integer> achievments = new HashMap<>(); // достижения

    public KassaUserData apply(User user) {
        this.id = user.id;
        this.secret = user.secret;
        this.login = user.login;
        this.day = user.day;
        this.dollars = user.dollars;
        this.maxKassaDollars = user.maxKassaDollars;
        this.lastLoginTime = user.lastLoginTime;
        this.isAdHide = user.isAdHide;
        this.isBuyFull = user.isFullVersionBuyed();
        this.advGoodIndex = user.advGoodIndex;
        this.advGoodsEndTime = user.advGoodsEndTime;
        this.buyedProducts = user.buyedProducts;
        this.purchasedProducts = user.purchasedProducts;
        this.achievments = user.achievments;
        return this;
    }

    // GWT reflection cannot access long fields or resolve inherited collection interfaces.
    @Override public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("secret", secret);
        json.writeValue("login", login);
        json.writeValue("day", day);
        json.writeValue("dollars", dollars);
        json.writeValue("maxKassaDollars", maxKassaDollars);
        json.writeValue("lastLoginTime", lastLoginTime);
        json.writeValue("isAdHide", isAdHide);
        json.writeValue("isBuyFull", isBuyFull);
        json.writeValue("advGoodIndex", advGoodIndex);
        json.writeValue("advGoodsEndTime", advGoodsEndTime);
        json.writeValue("purchasedProducts", purchasedProducts, Set.class, String.class);
        json.writeValue("purchaseList", purchaseList, ArrayList.class, Purchase.class);
        json.writeValue("ratingMaxDollars", ratingMaxDollars, ArrayList.class, LeaderboardEntry.class);
        json.writeValue("buyedProducts", buyedProducts, HashSet.class, Integer.class);
        json.writeValue("achievments", achievments, HashMap.class, Integer.class);
    }

    @Override public void read(Json json, JsonValue data) {
        id = data.getInt("id", id);
        secret = data.getString("secret", secret);
        login = data.getString("login", login);
        day = data.getInt("day", day);
        dollars = data.getInt("dollars", dollars);
        maxKassaDollars = data.getInt("maxKassaDollars", maxKassaDollars);
        lastLoginTime = data.getLong("lastLoginTime", lastLoginTime);
        isAdHide = data.getBoolean("isAdHide", isAdHide);
        isBuyFull = data.getBoolean("isBuyFull", isBuyFull);
        advGoodIndex = data.getInt("advGoodIndex", advGoodIndex);
        advGoodsEndTime = data.getLong("advGoodsEndTime", advGoodsEndTime);
        if (data.has("purchasedProducts")) purchasedProducts = json.readValue("purchasedProducts", HashSet.class, String.class, data);
        if (data.has("purchaseList")) purchaseList = json.readValue("purchaseList", ArrayList.class, Purchase.class, data);
        if (data.has("ratingMaxDollars")) ratingMaxDollars = json.readValue("ratingMaxDollars", ArrayList.class, LeaderboardEntry.class, data);
        if (data.has("buyedProducts")) buyedProducts = json.readValue("buyedProducts", HashSet.class, Integer.class, data);
        if (data.has("achievments")) {
            achievments = new HashMap<>();
            for (JsonValue entry = data.get("achievments").child; entry != null; entry = entry.next) {
                if (!"class".equals(entry.name)) achievments.put(Integer.parseInt(entry.name), entry.asInt());
            }
        }
    }

    public String serializeToString() {
        return JSON.toJson(this);
    }

    public static KassaUserData deserializeFromString(String str) {
        return JSON.fromJson(KassaUserData.class, str);
    }
}
