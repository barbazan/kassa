package com.plagame.game.net.kassa;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.plagame.game.integration.platform.service.api.CloudSaveService;
import com.plagame.game.integration.platform.service.api.PlatformServices;
import com.plagame.game.integration.platform.service.api.model.BillingCatalog;
import com.plagame.game.integration.platform.service.api.model.DefaultPurchaseListener;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;
import com.plagame.game.integration.platform.service.api.model.TargetPlatform;
import com.plagame.game.integration.platform.service.vk.VKBillingService;
import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.beans.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

/** Exercises the real packet handler and billing service with an in-memory socket. */
public class VKBillingFlowTest {
    private final List<FakeSocket> sockets = new ArrayList<>();
    private GameApplication game;
    private KassaNetworkWebSocketClient client;
    private ReadyBilling billing;
    private User user;
    private Object oldFactory;
    private Object oldGame;
    private Object oldUser;
    private WebSocket oldSocket;
    private Application oldApp;
    private Files oldFiles;

    @Before public void setUp() throws Exception {
        oldFactory = field(WebSockets.class, "FACTORY").get(null);
        oldGame = field(GameApplication.class, "instance").get(null);
        oldUser = field(User.class, "instance").get(null);
        oldSocket = KassaNetworkWebSocketClient.webSocket;
        oldApp = Gdx.app;
        oldFiles = Gdx.files;
        Gdx.app = proxy(Application.class, (method, args) -> {
            if (method.equals("postRunnable")) ((Runnable) args[0]).run();
            return null;
        });
        Gdx.files = proxy(Files.class, (method, args) -> null);
        user = new User();
        user.id = 17;
        user.secret = "game-account-secret";
        field(User.class, "instance").set(null, user);
        billing = new ReadyBilling();
        billing.setPurchaseListener(new DefaultPurchaseListener());
        CloudSaveService cloud = proxy(CloudSaveService.class, (method, args) -> null);
        PlatformServices platform = proxy(PlatformServices.class, (method, args) -> {
            if (method.equals("getPlatform")) return TargetPlatform.HTML_VK;
            if (method.equals("billing")) return billing;
            if (method.equals("cloud")) return cloud;
            return null;
        });
        game = new GameApplication(platform);
        field(GameApplication.class, "instance").set(null, game);
        Field factory = field(WebSockets.class, "FACTORY");
        factory.set(null, proxy(factory.getType(), (method, args) -> {
            FakeSocket socket = new FakeSocket();
            sockets.add(socket);
            return socket.socket;
        }));
        KassaNetworkWebSocketClient.webSocket = null;
        client = new KassaNetworkWebSocketClient();
        game.networkWebSocketClient = client;
    }

    @After public void tearDown() throws Exception {
        Timer.Task timeout = (Timer.Task) field(KassaNetworkWebSocketClient.class, "pendingVKTimeout").get(client);
        if (timeout != null) timeout.cancel();
        game.assetManager.dispose();
        field(WebSockets.class, "FACTORY").set(null, oldFactory);
        field(GameApplication.class, "instance").set(null, oldGame);
        field(User.class, "instance").set(null, oldUser);
        KassaNetworkWebSocketClient.webSocket = oldSocket;
        Gdx.app = oldApp;
        Gdx.files = oldFiles;
    }

    @Test public void buyButtonReportsDisconnectedServerInsteadOfIgnoringTheClick() {
        billing.buyFullVersion();
        assertEquals(1, billing.errors.size());
        assertTrue(billing.errors.get(0).contains("соединения"));
        assertEquals(0, billing.initializations);
        assertEquals(0, current().count(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE));
    }

    @Test public void buyButtonRetriesFailedCatalogInitializationAndPreparesOrder() {
        authorize();
        billing.ready(false);
        billing.failInitialization = true;
        billing.buyFullVersion();
        assertFalse(billing.isCatalogLoaded());
        assertEquals(1, billing.errors.size());
        billing.failInitialization = false;
        billing.buyFullVersion();
        assertTrue(billing.isCatalogLoaded());
        assertEquals(2, billing.initializations);
        assertEquals(1, current().count(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE));
        assertFalse(user.isFullVersionBuyed());
    }

    @Test public void repeatedClicksShareInitializationAndDoNotPrepareDuplicateOrders() {
        authorize();
        billing.ready(false);
        billing.holdInitialization = true;
        billing.buyFullVersion();
        billing.buyFullVersion();
        assertEquals(1, billing.initializations);
        assertEquals(0, current().count(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE));
        billing.initializationCallback.onSuccess("ready");
        billing.buyFullVersion();
        assertEquals(1, current().count(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE));
    }

    @Test public void reconnectLogsInAndRestoresPurchasesEvenOutsideGameScreen() throws Exception {
        authorize();
        assertTrue(client.authorized);
        assertEquals(1, current().count(KassaNetworkPacket.NET_ACTION_CHECK_PURCHASES));
        FakeSocket disconnected = current();
        disconnected.disconnect();
        assertFalse(client.authorized);
        field(KassaNetworkWebSocketClient.class, "lastReconnectTime").setLong(client, 0);
        game.render(); // No screen is required: recovery also runs on the purchase screen.
        assertEquals(2, sockets.size());
        assertFalse(client.authorized);
        assertEquals(1, current().count(KassaNetworkPacket.NET_ACTION_LOGIN));
        authorize();
        assertEquals(1, current().count(KassaNetworkPacket.NET_ACTION_CHECK_PURCHASES));
        disconnected.listener.onClose(disconnected.socket, 1006, "late close");
        assertTrue(client.authorized);
        disconnected.receive("{\"action\":1,\"userId\":999,\"secret\":\"stale\"}");
        assertEquals(17, user.id);
    }

    @Test public void earlyPurchasePushIsRestoredAndDuplicateNotificationDoesNotGrantAgain() {
        authorize();
        billing.ready(false);
        current().receive(purchasePacket());
        assertFalse(user.isFullVersionBuyed());
        assertFalse(user.isPurchasedToken("vk:app:12345"));
        billing.ready(true);
        billing.restorePurchases();
        current().receive(purchasePacket());
        assertTrue(user.isFullVersionBuyed());
        assertTrue(user.isPurchasedToken("vk:app:12345"));
        assertEquals(1, billing.grants);
        current().receive(purchasePacket());
        assertEquals(1, billing.grants);
    }

    @Test public void preparationCarriesAccountAndRawLaunchParametersAndMatchesResponse() {
        authorize();
        AtomicReference<String> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        String launch = "?vk_app_id=123&vk_user_id=456&sign=raw%2Bsignature";
        client.prepareVKPurchase(BillingCatalog.PRODUCT_FULL_VERSION, launch, callback(result, error));
        KassaNetworkPacket request = current().last(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE);
        assertEquals(17, request.userId);
        assertEquals(user.secret, request.secret);
        assertEquals("full_version", request.productId);
        assertEquals(launch, request.vkLaunchParams);
        current().receive("{\"action\":6,\"vkRequestId\":\"other\",\"vkItem\":\"wrong\"}");
        assertNull(result.get());
        current().receive("{\"action\":6,\"vkRequestId\":\"" + request.vkRequestId + "\",\"vkItem\":\"signed.item\"}");
        assertEquals("signed.item", result.get());
        assertNull(error.get());
        assertFalse(user.isFullVersionBuyed()); // Preparation and browser results cannot grant a purchase.
    }

    @Test public void disconnectFailsPreparationAndReleasesItForRetry() {
        authorize();
        AtomicReference<String> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        client.prepareVKPurchase("full_version", "?launch", callback(result, error));
        current().disconnect();
        assertNotNull(error.get());
        assertNull(result.get());
        assertFalse(client.authorized);
        current().open = true;
        current().listener.onOpen(current().socket);
        authorize();
        error.set(null);
        client.prepareVKPurchase("full_version", "?launch", callback(result, error));
        assertNull(error.get());
        assertEquals(2, current().count(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE));
    }

    @Test public void timedOutPreparationIgnoresLateReplyAndCanRetry() throws Exception {
        authorize();
        AtomicReference<String> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        client.prepareVKPurchase("full_version", "?launch", callback(result, error));
        String expiredId = current().last(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE).vkRequestId;
        ((Timer.Task) field(KassaNetworkWebSocketClient.class, "pendingVKTimeout").get(client)).run();
        assertNotNull(error.get());
        error.set(null);
        client.prepareVKPurchase("full_version", "?launch", callback(result, error));
        current().receive("{\"action\":6,\"vkRequestId\":\"" + expiredId + "\",\"vkItem\":\"expired.item\"}");
        assertNull(result.get());
        assertNull(error.get());
        String newId = current().last(KassaNetworkPacket.NET_ACTION_PREPARE_VK_PURCHASE).vkRequestId;
        current().receive("{\"action\":6,\"vkRequestId\":\"" + newId + "\",\"vkError\":\"server rejected purchase\"}");
        assertEquals("server rejected purchase", error.get());
    }

    private void authorize() {
        current().receive("{\"action\":1,\"userId\":17,\"secret\":\"game-account-secret\"}");
    }
    private FakeSocket current() { return sockets.get(sockets.size() - 1); }
    private static String purchasePacket() {
        return "{\"action\":4,\"userId\":17,\"kassaUserData\":{\"purchaseList\":[{\"amount\":1,\"token\":\"vk:app:12345\"}]}}";
    }
    private static PlatformCallback<String> callback(AtomicReference<String> result, AtomicReference<String> error) {
        return new PlatformCallback<String>() {
            @Override public void onSuccess(String value) { result.set(value); }
            @Override public void onError(String value) { error.set(value); }
        };
    }
    private static Field field(Class<?> type, String name) throws Exception {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
    private interface Invocation { Object call(String method, Object[] args); }
    @SuppressWarnings("unchecked") private static <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            Object result = invocation.call(method.getName(), args);
            if (result != null) return result;
            if (method.getReturnType() == boolean.class) return false;
            if (method.getReturnType() == int.class) return 0;
            if (method.getReturnType() == long.class) return 0L;
            return null;
        });
    }
    private static class ReadyBilling extends VKBillingService {
        int grants;
        int initializations;
        boolean failInitialization;
        boolean holdInitialization;
        PlatformCallback<String> initializationCallback;
        final List<String> errors = new ArrayList<>();
        @Override protected void initializeVK(PlatformCallback<String> callback) {
            initializations++;
            initializationCallback = callback;
            if (holdInitialization) return;
            if (failInitialization) callback.onError("VK initialization timed out");
            else callback.onSuccess("ready");
        }
        @Override protected String getVKLaunchParams() { return "?signed-test-launch"; }
        @Override protected void showPurchaseError(String message) { errors.add(message); }
        ReadyBilling() { ready(true); }
        void ready(boolean value) { catalogLoaded = value; }
        @Override public void consume(com.plagame.game.integration.platform.service.api.model.BillingProduct product) {
            grants++;
            super.consume(product);
        }
    }
    private static class FakeSocket {
        final List<String> sent = new ArrayList<>();
        WebSocketListener listener;
        boolean open;
        final WebSocket socket = proxy(WebSocket.class, (method, args) -> {
            if (method.equals("addListener")) listener = (WebSocketListener) args[0];
            if (method.equals("isOpen")) return open;
            if (method.equals("isClosed")) return !open;
            if (method.equals("send")) sent.add((String) args[0]);
            if (method.equals("connect")) { open = true; listener.onOpen(socketRef()); }
            if (method.equals("close")) disconnect();
            return null;
        });
        private WebSocket socketRef() { return socket; }
        void disconnect() { open = false; listener.onClose(socket, 1006, "disconnected"); }
        void receive(String json) { listener.onMessage(socket, json); }
        int count(int action) {
            int count = 0;
            for (String json : sent) if (KassaNetworkPacket.deserializeFromString(json).action == action) count++;
            return count;
        }
        KassaNetworkPacket last(int action) {
            for (int i = sent.size() - 1; i >= 0; i--) {
                KassaNetworkPacket packet = KassaNetworkPacket.deserializeFromString(sent.get(i));
                if (packet.action == action) return packet;
            }
            throw new AssertionError("No packet for action " + action);
        }
    }
}
