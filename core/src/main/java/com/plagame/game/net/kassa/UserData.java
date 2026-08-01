package com.plagame.game.net.kassa;

import com.badlogic.gdx.utils.Json;
import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.kassa.beans.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserData {

    private static final Json JSON = new Json();

    public int id;
    public String secret;
    public String login;
    public float dollars; // игровая валюта
    public int day; // игровой день
    public long lastLoginTime;
    public boolean isAdHide;
    public boolean isBuyFull;
    public Set<String> purchasedProducts = new HashSet<>(); // запурчайсеные токены
    public List<Purchase> purchaseList = new ArrayList<>(); // оплаченые покупки
    public List<LeaderboardEntry> ratingMaxDollars = new ArrayList<>(); // рейтинг
    public HashSet<Integer> buyedProducts = new HashSet<>(); // купленные продукты

    public UserData apply(User user) {
        this.id = user.id;
        this.secret = user.secret;
        this.login = user.login;
        this.dollars = user.dollars;
        this.day = user.day;
        this.lastLoginTime = user.lastLoginTime;
        this.isAdHide = user.isAdHide;
        this.isBuyFull = user.isBuyFull;
        this.buyedProducts = user.buyedProducts;
        this.purchasedProducts = user.purchasedProducts;
        return this;
    }

    public String serializeToString() {
        return JSON.toJson(this);
    }

    public static UserData deserializeFromString(String str) {
        return JSON.fromJson(UserData.class, str);
    }
}
