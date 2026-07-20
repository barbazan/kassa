package com.plagame.game.net.kassa;

import com.badlogic.gdx.utils.Json;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserData {

    private static final Json JSON = new Json();

    public int id;
    public String secret;
    public String login;
    public int location; // локация LocationInfo
    public long gold; // премиум валюта
    public long dollars; // игровая валюта
    public long folowers; // подписчики
    public long maxFolowers; // локальный максимум подписчиков, чтобы прогрессбар заполнять
    public long lastMaxFolowers; // предыдущий максимум подписчиков, чтобы прогрессбар с него начинать
    public float pasIncome; // пассивный доход dollar в секунду
    public float tapIncome; // доход dollar по клику
    public float folowersIncome; // рост подпизчиков в сек
    public float energy; // энергия
    public boolean isMax; // в магазине
    public long lastLoginTime;
    public long maxDollars; // Максимальное кол-во долларов у игрока (для рейтинга)
    public boolean isAdHide;
    public boolean hasGift;
    public Map<String, Integer> upgradesMap = new HashMap<>(); // улучшение UpgradeInfo тип на уровень
    public Map<String, Long> boostersMap = new HashMap<>(); // временные бафы, хранится время окончания
    public Map<String, Integer> perksMap = new HashMap<>(); // порстоянные бустеры за золото, хранится уровень
    public Set<String> purchasedProducts = new HashSet<>(); // запурчайсеные токены
    public List<Purchase> purchaseList = new ArrayList<>(); // оплаченые покупки
    public List<LeaderboardEntry> ratingMaxDollars = new ArrayList<>(); // рейтинг

    public UserData apply(User user) {
        this.id = user.id;
        this.secret = user.secret;
        this.login = user.login;
        this.location = user.location;
        this.gold = user.gold;
        this.dollars = user.dollars;
        this.lastLoginTime = user.lastLoginTime;
        this.maxDollars = Math.max(user.maxDollars, user.dollars);
        this.isAdHide = user.isAdHide;
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
