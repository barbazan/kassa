package com.plagame.game.net.kassa;

import com.badlogic.gdx.utils.Json;
import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.kassa.beans.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KassaUserData {

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

    public String serializeToString() {
        return JSON.toJson(this);
    }

    public static KassaUserData deserializeFromString(String str) {
        return JSON.fromJson(KassaUserData.class, str);
    }
}
