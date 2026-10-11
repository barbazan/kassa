package com.plagame.game.net.kassa;

import com.badlogic.gdx.utils.Json;
import com.plagame.game.kassa.beans.User;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class KassaJsonCompatibilityTest {
    @Test public void existingSavePreservesProgressPurchasesAndLongTimestamps() throws Exception {
        String saved = "{id:17,secret:account,login:player,location:3,day:7,dollars:123456,maxKassaDollars:234567,"
            + "lastSaveTime:1700000000123,lastLoginTime:1700000000456,soundOn:false,musicOn:false,"
            + "buyedProducts:[1,4],purchasedProducts:[paid],achievments:{3:11},loginDayCount:5,"
            + "isAdHide:true,isFullVersionBuyed:true,advGoodIndex:2,advGoodsEndTime:1700000000789}";
        Json json = new Json();
        User user = json.fromJson(User.class, saved);
        User restored = json.fromJson(User.class, json.toJson(user));
        assertTrue(restored.isFullVersionBuyed());
        assertEquals(1700000000123L, restored.lastSaveTime);
        assertEquals(Integer.valueOf(11), restored.achievments.get(3));
        assertTrue(restored.purchasedProducts.contains("paid"));
        assertEquals(3, restored.location);
        for (Field field : User.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            assertEquals(field.getName(), field.get(user), field.get(restored));
        }
        assertFalse(json.toJson(restored).contains("rankMap"));
    }

    @Test public void serverPacketsReadCollectionsPurchasesScoresAndLongTimestamps() {
        String packet = "{action:4,userId:17,kassaUserData:{id:17,lastLoginTime:1700000000123,"
            + "isBuyFull:true,advGoodsEndTime:1700000000456,purchasedProducts:{class:java.util.HashSet,items:[paid]},"
            + "purchaseList:[{id:2,amount:1,token:paid}],ratingMaxDollars:[{id:17,value:9007199254740993,rank:1}],"
            + "buyedProducts:[1,4],achievments:{3:11}}}";
        KassaUserData data = KassaNetworkPacket.deserializeFromString(packet).kassaUserData;
        KassaUserData restored = KassaUserData.deserializeFromString(data.serializeToString());
        assertEquals(1700000000123L, restored.lastLoginTime);
        assertEquals(1700000000456L, restored.advGoodsEndTime);
        assertTrue(restored.isBuyFull);
        assertTrue(restored.purchasedProducts.contains("paid"));
        assertEquals("paid", restored.purchaseList.get(0).token);
        assertEquals(9007199254740993L, restored.ratingMaxDollars.get(0).value);
        assertEquals(Integer.valueOf(11), restored.achievments.get(3));
        assertTrue(restored.buyedProducts.contains(4));
    }

    @Test public void outgoingPurchaseTokensRemainReadableByServerSetField() {
        KassaUserData data = new KassaUserData();
        data.purchasedProducts.add("paid");
        Json serverJson = new Json();
        serverJson.setIgnoreUnknownFields(true);
        ServerCollections restored = serverJson.fromJson(ServerCollections.class, data.serializeToString());
        assertTrue(restored.purchasedProducts instanceof HashSet);
        assertTrue(restored.purchasedProducts.contains("paid"));
    }

    public static class ServerCollections {
        public Set<String> purchasedProducts = new HashSet<>();
    }

    @Test public void oldPartialSaveKeepsDefaultsAndIgnoresRetiredFields() {
        User user = new Json().fromJson(User.class, "{dollars:54321,retiredField:17}");
        assertEquals(54321, user.dollars);
        assertEquals(1, user.location);
        assertTrue(user.soundOn);
        assertFalse(user.isFullVersionBuyed());
        assertNotNull(user.purchasedProducts);
        assertNotNull(user.rankMap);
    }
}
