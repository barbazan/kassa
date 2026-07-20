package com.plagame.game.net.kassa;

import com.plagame.game.kassa.GameConfig;
import com.plagame.game.kassa.beans.User;

import java.util.Date;

public class Purchase {

    public int id;
    private User actor;
    public int amount;
    public String token;
    public boolean consumed;
    public Date creationTime;

    public Purchase() {
    }

    public Purchase(User actor, int amount, String token) {
        this.actor = actor;
        this.amount = amount;
        this.token = token;
        this.consumed = false;
        this.creationTime = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getActor() {
        return actor;
    }

    public void setActor(User actor) {
        this.actor = actor;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public String serializeToString() {
        return GameConfig.JSON.toJson(this);
    }

    public static Purchase deserializeFromString(String str) {
        return GameConfig.JSON.fromJson(Purchase.class, str);
    }

}
