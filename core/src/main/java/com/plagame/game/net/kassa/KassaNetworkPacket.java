package com.plagame.game.net.kassa;

import com.badlogic.gdx.utils.Json;
import com.plagame.game.kassa.beans.User;

import java.io.Serializable;

/**
 * Created by Дмитрий Малышев on 22.10.2020.
 * Email: dmitry.malyshev@gmail.com
 */
public class KassaNetworkPacket implements Serializable {

    public static final int NET_ACTION_LOGIN = 1;
    public static final int NET_ACTION_SAVE_USER = 2;
    public static final int NET_ACTION_LOAD_USER = 3;
    public static final int NET_ACTION_CHECK_PURCHASES = 4;
    public static final int NET_ACTION_GET_RATING = 5;

    private static final Json JSON = new Json();

    public int action;
    public int userId;
    public String secret;
    public UserData userData;

    public KassaNetworkPacket() { // нельзя удалять используется в com.badlogic.gdx.utils.Json
        super();
    }

    private KassaNetworkPacket(int action) {
        this();
        this.action = action;
    }

    public static KassaNetworkPacket createLoginPacket() {
        System.out.println("--------------createLoginPacket--------------new KassaNetworkPacket() ");
        KassaNetworkPacket networkPacket = new KassaNetworkPacket(NET_ACTION_LOGIN);
        networkPacket.secret = User.get().secret;
        return networkPacket;
    }

    public static KassaNetworkPacket createSaveUserPacket() {
        System.out.println("--------------createSaveUserPacket--------------new KassaNetworkPacket() ");
        KassaNetworkPacket networkPacket = new KassaNetworkPacket(NET_ACTION_SAVE_USER);
        networkPacket.userId = User.get().id;
        networkPacket.userData = new UserData().apply(User.get());
        return networkPacket;
    }

    public static KassaNetworkPacket createLoadUserPacket() {
        System.out.println("--------------createLoadUserPacket--------------new KassaNetworkPacket() ");
        KassaNetworkPacket networkPacket = new KassaNetworkPacket(NET_ACTION_LOAD_USER);
        networkPacket.userId = User.get().id;
        return networkPacket;
    }

    public static KassaNetworkPacket createCheckPurchasesPacket() {
        System.out.println("--------------createCheckPurchasesPacket--------------new KassaNetworkPacket() ");
        KassaNetworkPacket networkPacket = new KassaNetworkPacket(NET_ACTION_CHECK_PURCHASES);
        networkPacket.userId = User.get().id;
        return networkPacket;
    }

    public static KassaNetworkPacket createGetRatingPacket() {
        System.out.println("--------------createGetRatingPacket--------------new KassaNetworkPacket() ");
        KassaNetworkPacket networkPacket = new KassaNetworkPacket(NET_ACTION_GET_RATING);
        networkPacket.userId = User.get().id;
        return networkPacket;
    }

    public String serializeToString() {
        return JSON.toJson(this);
    }

    public static KassaNetworkPacket deserializeFromString(String str) {
        return JSON.fromJson(KassaNetworkPacket.class, str);
    }

    @Override
    public String toString() {
        return "KassaNetworkPacket{" +
                "action=" + action +
                ", userId=" + userId +
                ", secret='" + secret + '\'' +
                ", userData='" + userData + '\'' +
                '}';
    }

}
