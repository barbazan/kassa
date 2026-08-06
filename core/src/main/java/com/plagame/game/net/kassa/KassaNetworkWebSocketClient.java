package com.plagame.game.net.kassa;

import static com.plagame.game.kassa.GameConfig.RELEASE_BUILD;
import static com.plagame.game.net.kassa.KassaNetworkPacket.NET_ACTION_CHECK_PURCHASES;
import static com.plagame.game.net.kassa.KassaNetworkPacket.NET_ACTION_GET_RATING;
import static com.plagame.game.net.kassa.KassaNetworkPacket.NET_ACTION_LOAD_USER;
import static com.plagame.game.net.kassa.KassaNetworkPacket.NET_ACTION_LOGIN;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import com.plagame.game.integration.platform.service.api.model.BillingCatalog;
import com.plagame.game.integration.platform.service.api.model.BillingProduct;
import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.kassa.utils.Time;

import java.util.List;

/**
 * Created by Дмитрий Малышев on 16.11.2023.
 * Email: dmitry.malyshev@gmail.com
 */
public class KassaNetworkWebSocketClient {

    private static final boolean NETWORK_DEBUG = true;
    public static final long NETWORK_RECONNECT_INTERVAL = 10 * Time.SECOND_MILLIS; // Интервал реконнекта к серверу в случае обрыва соединения, сейчас 1 раз в 10 сек
    public static final long SAVE_USER_INTERVAL = 20 * Time.SECOND_MILLIS; // сохранение юзера на сервер, пакет большой(20 килобайт и больше), поэтому не часто

    public static WebSocket webSocket;
    private static KassaNetworkPacket userPacket; // пакет для отправки чтоб каждый раз new не делать
    public volatile boolean authorized;
    private long lastReconnectTime = System.currentTimeMillis() - NETWORK_RECONNECT_INTERVAL;
    private long lastSaveUserTime = System.currentTimeMillis();

    public KassaNetworkWebSocketClient() {
        init();
    }

    private void init() {
        checkConnect();
    }

    public boolean isConnected() {
        return !isDisconnected() && webSocket.isOpen();
    }

    public boolean isDisconnected() {
        return webSocket == null || webSocket.isClosed() || webSocket.isClosing();
    }

    public void checkConnect() {
        if(isDisconnected()) {
            System.out.println("-------------------isDisconnected() = " + isDisconnected());
            try {
                System.out.println("NetworkWebSocketClient starting...");
                authorized = false;
                webSocket = createNewWebSocket();
                System.out.println("Connecting...");
                webSocket.connect();
            } catch (Throwable t) {
                System.out.println("Failed to connect: " + t.getMessage());
            }
        }
    }

    public void tryReconnect() {
        if(canTryReconnect()) {
            lastReconnectTime = System.currentTimeMillis();
            System.out.println("========================== TRY RECONNECT ==========================");
            System.out.println("Reconnecting...");
            try {
                if(webSocket != null) {
                    closeConnection();
                }
                webSocket = createNewWebSocket();
                System.out.println("Connecting...");
                webSocket.connect();
                if(webSocket.isConnecting()) {
                    System.out.println("Reconnect successful");
                }
            } catch (Exception e) {
                System.out.println("Can not reconnect!!!");
            }
        }
    }

    private boolean canTryReconnect() {
        return System.currentTimeMillis() - lastReconnectTime > NETWORK_RECONNECT_INTERVAL;
    }

    public boolean canSaveUserToServer() {
        return System.currentTimeMillis() - lastSaveUserTime > SAVE_USER_INTERVAL;
    }

    public void sendLoginPacket() {
        if(!authorized) {
            KassaNetworkPacket packet = KassaNetworkPacket.createLoginPacket();
            System.out.println("====> OUT LoginPacket = " + packet);
            webSocket.send(packet.serializeToString());
        }
    }

    public void sendSaveUserPacket() {
        System.out.println("----------------- sendSaveUserPacket -----------------");
        if(authorized) {
            KassaNetworkPacket packet = KassaNetworkPacket.createSaveUserPacket();
            if(packet != null) {
                sendPacketNow(packet);
            }
        }
    }

    public void sendLoadUserPacket() {
        System.out.println("----------------- sendLoadUserPacket -----------------");
        if(authorized) {
            KassaNetworkPacket packet = KassaNetworkPacket.createLoadUserPacket();
            if(packet != null) {
                sendPacketNow(packet);
            }
        }
    }

    public void sendCheckPurchasesPacket() {
        System.out.println("----------------- sendCheckPurchasesPacket ----------------- authorized = " + authorized);
        if(authorized) {
            KassaNetworkPacket packet = KassaNetworkPacket.createCheckPurchasesPacket();
            System.out.println("packet = " + packet);
            if(packet != null) {
                sendPacketNow(packet);
            }
        }
    }

    public void sendGetRatingPacket() {
        System.out.println("----------------- sendGetRatingPacket ----------------- authorized = " + authorized);
        if(authorized) {
            KassaNetworkPacket packet = KassaNetworkPacket.createGetRatingPacket();
            System.out.println("packet = " + packet);
            if(packet != null) {
                sendPacketNow(packet);
            }
        }
    }

    public void sendPacketWithCooldown(KassaNetworkPacket packet) {
        try {
            if(authorized && canSaveUserToServer()) {
                sendPacketNow(packet);
            }
        } catch (Exception e) {
            System.out.println("====> OUT packet exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPacketNow(KassaNetworkPacket packet) {
        try {
            if(authorized) {
                if(webSocket.isOpen()) {
                    String outString = packet.serializeToString();
                    if(NETWORK_DEBUG) {
                        System.out.println("====> OUT packet = " + packet);
                        System.out.println("====> OUT packet.serializeToString() = " + outString);
                        System.out.println("====> OUT sendSaveUserPacket outString.length() = " + outString.length());
                    }
                    webSocket.send(outString);
                    lastSaveUserTime = System.currentTimeMillis();
                } else {
                    tryReconnect();
                }
            }
        } catch (Exception e) {
            System.out.println("====> OUT packet exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void packetReceived(String message) {
        KassaNetworkPacket packet = null;
        System.out.println("-----------------message = " + message);
        try {
            packet = KassaNetworkPacket.deserializeFromString(message);
            System.out.println("packet = " + packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(NETWORK_DEBUG) {
            System.out.println("<==== IN packet = " + packet);
        }
        if (packet != null) {
            try {
                switch (packet.action) {
                    case NET_ACTION_LOGIN:
                        System.out.println("========= GET LOGIN PACKET =========== packet = " + packet);
                        System.out.println("========= SET NEW ID and SECRET =========== packet.userId = " + packet.userId + ", packet.secret = " + packet.secret);
                        User.get().id = packet.userId;
                        User.get().secret = packet.secret;
                        if(User.get().login == null || User.get().login.isEmpty()) {
                            User.get().login = "User_" + packet.userId;
                        }
                        User.get().saveUser();
                        authorized = true;
                        sendGetRatingPacket(); // сразу после логина рейтинг запрашиваем
                        break;
                    case NET_ACTION_LOAD_USER:
                        System.out.println("========= GET LOAD USER PACKET =========== packet = " + packet);
                        if(authorized) {
                            User.get().apply(packet.kassaUserData).saveUser();
                        }
                        break;
                    case NET_ACTION_CHECK_PURCHASES:
                        System.out.println("========= GET CHECK PURCHASES PACKET =========== packet = " + packet);
                        if(authorized) {
                            User.get().apply(packet.kassaUserData);
                            List<Purchase> purchaseList = packet.kassaUserData.purchaseList;
                            if(purchaseList != null && !purchaseList.isEmpty()) {
                                for(Purchase purchase : purchaseList) {
                                    if(!User.get().isPurchasedToken(purchase.token)) {
                                        BillingProduct billingProduct = BillingCatalog.getByGold(purchase.amount);
                                        if(billingProduct != null) {
                                            User.get().markPurchasedToken(purchase.token);
                                            GameApplication.get().platform.billing().consume(billingProduct);
                                        } else {
                                            System.err.println("Error: BillingProduct not found for purchase with gold amount = " + purchase.amount);
                                        }
                                    } else {
                                        System.out.println("Purchase with token = " + purchase.token + " already consumed.");
                                    }
                                }
                            }
                        }
                        break;
                    case NET_ACTION_GET_RATING:
                        System.out.println("========= GET RATING PACKET =========== packet = " + packet + ", authorized = " + authorized);
                        if(authorized) {
                            System.out.println("packet.userData = " + packet.kassaUserData);
                            if(packet.kassaUserData != null) {
                                List<LeaderboardEntry> ratingList = packet.kassaUserData.ratingMaxDollars;
                                System.out.println("ratingList.size() = " + ratingList.size());
                                GameApplication.get().platform.leaderboard().setLeaderboard(ratingList);
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                NetworkPacketPools.get().free(packet);
            }
        }
    }

    private void closeConnection() {
        try {
            webSocket.close();
        } catch (Throwable t2) {
            // do nothing
        } finally {
            webSocket = null;
        }
    }

    private WebSocket createNewWebSocket() {
        WebSocket newWebSocket;
        if(RELEASE_BUILD) {
            newWebSocket = WebSockets.newSocket("wss://mir-game.ru/kassaws");
        } else {
            newWebSocket = WebSockets.newSocket("ws://192.168.0.8:8886/");
        }
        newWebSocket.setSendGracefully(true);
        newWebSocket.setUseTcpNoDelay(true);
        newWebSocket.setSerializeAsString(false);
        newWebSocket.addListener(new WebSocketListener() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                System.out.println(" ========================== onOpen ============================ ");
                System.out.println("webSocket.getState() = " + webSocket.getState());
                if(!authorized) {
                    System.out.println("Authorizing...");
                    sendLoginPacket();
                }
                return false;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                System.out.println(" ========================== onClose ============================ ");
                return false;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String message) {
                if(NETWORK_DEBUG) {
                    System.out.println("<==== IN message = " + message);
                }
                packetReceived(message);
                return false;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                System.out.println(" ========================== onMessage_byte[] ============================ ");
                return false;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                System.out.println(" ========================== onError ============================ ");
                System.out.println("error = " + error.getMessage());
                return false;
            }
        });
        return newWebSocket;
    }
}
