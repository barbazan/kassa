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
    public int location; // локация LocationInfo
    public long gold; // премиум валюта
    public float dollars; // игровая валюта
    public long lastLoginTime;
    public boolean isAdHide;
    public Set<String> purchasedProducts = new HashSet<>(); // запурчайсеные токены
    public List<Purchase> purchaseList = new ArrayList<>(); // оплаченые покупки
    public List<LeaderboardEntry> ratingMaxDollars = new ArrayList<>(); // рейтинг
    public HashSet<Integer> buyedProducts = new HashSet<>(); // купленные продукты

    public UserData apply(User user) {
        this.id = user.id;
        this.secret = user.secret;
        this.login = user.login;
        this.location = user.location;
        this.dollars = user.dollars;
        this.lastLoginTime = user.lastLoginTime;
        this.isAdHide = user.isAdHide;
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
