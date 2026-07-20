package com.plagame.game.integration.yandex;

import com.plagame.game.kassa.beans.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 04.08.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class YandexSDK {
    public static List<YandexModels.Product> PRODUCTS = new ArrayList<>();

    // Универсальный колбэк для JSON-результатов
    public interface JsonCallback {
        void onResult(String json);
        void onError(String error);
    }

    public static native void notifyLoadingReady() /*-{
        console.log("Calling YSDK LoadingAPI.ready");
        if ($wnd.ysdk && $wnd.ysdk.features && $wnd.ysdk.features.LoadingAPI) {
            $wnd.ysdk.features.LoadingAPI.ready();
        } else {
            console.log("YSDK not ready or LoadingAPI not found");
        }
    }-*/;

    public static native void showFullscreenAdv() /*-{
        console.log("Calling YSDK showFullscreenAdv()");

        function focusLater() {
            $wnd.setTimeout(function() {
                @com.plagame.game.kassa.yandex.YandexSDK::focusCanvas()();
            }, 50);
        }

        if ($wnd.ysdk && $wnd.ysdk.adv && $wnd.ysdk.adv.showFullscreenAdv) {
            $wnd.ysdk.adv.showFullscreenAdv({
                callbacks: {
                    onClose: function(wasShown) {
                        console.log("Fullscreen Adv closed. Was shown: " + wasShown);
                        focusLater();
                    },
                    onError: function(err) {
                        console.error("Fullscreen Adv error: ", err);
                        focusLater();
                    }
                }
            });
        } else {
            console.log("YSDK fullscreen adv not available (probably local run)");
            focusLater();
        }
    }-*/;

    public static native void showRewardedVideo(Runnable onRewarded, Runnable onSkipped) /*-{
        function focusLater() {
            $wnd.setTimeout(function() {
                @com.plagame.game.kassa.yandex.YandexSDK::focusCanvas()();
            }, 50);
        }

        if ($wnd.ysdk && $wnd.ysdk.adv && $wnd.ysdk.adv.showRewardedVideo) {
            $wnd.ysdk.adv.showRewardedVideo({
                callbacks: {
                    onOpen: function() {
                        console.log("Rewarded video opened");
                    },
                    onRewarded: function() {
                        onRewarded.@java.lang.Runnable::run()();
                        focusLater();
                    },
                    onClose: function(wasShown) {
                        if (!wasShown) {
                            onSkipped.@java.lang.Runnable::run()();
                        }
                        focusLater();
                    },
                    onError: function(err) {
                        onSkipped.@java.lang.Runnable::run()();
                        focusLater();
                    }
                }
            });
        } else {
            onSkipped.@java.lang.Runnable::run()();
            focusLater();
        }
    }-*/;

    public static native boolean isYandexPlatform() /*-{
        try {
            return !!$wnd.isYandex;
        } catch (e) {
            return false;
        }
    }-*/;

    // --- Этот метод вернёт false, если yandexPlayer ещё не инициализирован.
    //
    // Поэтому ты должен сначала вызвать ysdk.getPlayer(...) и сохранить результат в window.yandexPlayer.
    public static native boolean isAuthorizedSync() /*-{
        if ($wnd.yandexPlayer) {
            return $wnd.yandexPlayer.isAuthorized();
        }
        return false;
    }-*/;

    // --- Проверка авторизации ---
    public static native void checkAuthorized(Runnable onYes, Runnable onNo) /*-{
        if ($wnd.ysdk) {
            $wnd.ysdk.getPlayer().then(function(player) {
                if (player.isAuthorized()) {
                    onYes.@java.lang.Runnable::run()();
                } else {
                    onNo.@java.lang.Runnable::run()();
                }
            }, function() {
                // вместо .catch используем второй аргумент then
                onNo.@java.lang.Runnable::run()();
            });
        } else {
            onNo.@java.lang.Runnable::run()();
        }
    }-*/;

    public static native void initPlayer(Runnable onReady) /*-{
        if ($wnd.initPlayer) {
            $wnd.initPlayer($entry(function() {
                if (onReady) onReady.@java.lang.Runnable::run()();
            }));
        }
    }-*/;

    public static native String getPlayerData() /*-{
        return $wnd.getPlayerData();
    }-*/;

    public static native void authorize(Runnable onSuccess, Runnable onFail) /*-{
        if ($wnd.authorizePlayer) {
            $wnd.authorizePlayer(
                $entry(function() {
                    if (onSuccess) onSuccess.@java.lang.Runnable::run()();
                }),
                $entry(function() {
                    if (onFail) onFail.@java.lang.Runnable::run()();
                })
            );
        } else {
            console.error("authorizePlayer не найден в JS");
            if (onFail) onFail.@java.lang.Runnable::run()();
        }
    }-*/;

    public static native void purchase(String productId, PurchaseCallback onSuccess, Runnable onError) /*-{
        if ($wnd.ysdk && $wnd.ysdk.getPayments) {
            $wnd.ysdk.getPayments().then(function(payments) {
                payments.purchase({ id: productId }).then(
                    function(purchase) {
                        console.log("Purchase successful: ", purchase);

                        // Создаём объект Purchase для Java
                        var javaPurchase = @com.plagame.game.kassa.yandex.YandexModels.Purchase::new()();
                        javaPurchase.productID = purchase.productID; // исправлено!
                        javaPurchase.purchaseToken = purchase.purchaseToken;

                        onSuccess.@com.plagame.game.kassa.yandex.PurchaseCallback::onSuccess(Lcom/plagame/game/kassa/yandex/YandexModels$Purchase;)(javaPurchase);
                    },
                    function(err) {
                        console.error("Purchase error: ", err);
                        onError.@java.lang.Runnable::run()();
                    }
                );
            },
            function(err) {
                console.error("getPayments failed: ", err);
                onError.@java.lang.Runnable::run()();
            });
        } else {
            console.log("YSDK not ready or payments not available");
            onError.@java.lang.Runnable::run()();
        }
    }-*/;


    public static native void getCatalog(JsonCallback callback) /*-{
        if ($wnd.ysdk && $wnd.ysdk.getPayments) {
            $wnd.ysdk.getPayments().then(function(payments) {
                payments.getCatalog().then(
                    function(products) {
                        var json = JSON.stringify(products);
                        console.log("Catalog JSON: ", json);
                        callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onResult(Ljava/lang/String;)(json);
                    },
                    function(err) {
                        console.error("getCatalog error: ", err);
                        callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onError(Ljava/lang/String;)(err.message);
                    }
                );
            },
            function(err) {
                console.error("getPayments failed: ", err);
                callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onError(Ljava/lang/String;)(err.message);
            });
        } else {
            console.log("Payments not available");
            callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onError(Ljava/lang/String;)("Payments not available");
        }
    }-*/;


    public static native void getPurchases(JsonCallback callback) /*-{
        if ($wnd.ysdk && $wnd.ysdk.getPayments) {
            $wnd.ysdk.getPayments().then(function(payments) {
                payments.getPurchases().then(
                    function(purchases) {
                        var json = JSON.stringify(purchases);
                        console.log("Purchases JSON: ", json);
                        callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onResult(Ljava/lang/String;)(json);
                    },
                    function(err) {
                        console.error("getPurchases error: ", err);
                        callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onError(Ljava/lang/String;)(err.message);
                    }
                );
            },
            function(err) {
                console.error("getPayments failed: ", err);
                callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onError(Ljava/lang/String;)(err.message);
            });
        } else {
            console.log("Payments not available");
            callback.@com.plagame.game.kassa.yandex.YandexSDK.JsonCallback::onError(Ljava/lang/String;)("Payments not available");
        }
    }-*/;


    public static native void consumePurchase(String purchaseToken, Runnable onSuccess, Runnable onError) /*-{
        if ($wnd.ysdk && $wnd.ysdk.getPayments) {
            $wnd.ysdk.getPayments().then(function(payments) {
                payments.consumePurchase(purchaseToken).then(
                    function() {
                        console.log("Purchase consumed: " + purchaseToken);
                        onSuccess.@java.lang.Runnable::run()();
                    },
                    function(err) {
                        console.error("consumePurchase error: ", err);
                        onError.@java.lang.Runnable::run()();
                    }
                );
            },
            function(err) {
                console.error("getPayments failed: ", err);
                onError.@java.lang.Runnable::run()();
            });
        } else {
            console.log("Payments not available");
            onError.@java.lang.Runnable::run()();
        }
    }-*/;

    /**
     * Возвращает фокус на HTML5-канвас.
     * Пробует несколько раз с интервалами, чтобы обойти блокировку браузера.
     */
    public static native void focusCanvas() /*-{
        var canvas = $doc.querySelector("#embed-html canvas");
        if (!canvas) {
            console.warn("Canvas not found to focus");
            return;
        }

        // На всякий случай выставляем tabindex
        canvas.setAttribute("tabindex", "0");

        function tryFocus(attempt) {
            if (!canvas) return;
            canvas.focus();
            console.log("Trying to focus canvas, attempt:", attempt);
        }

        // Пробуем несколько раз
        tryFocus(1);
        $wnd.setTimeout(function(){ tryFocus(2); }, 50);
        $wnd.setTimeout(function(){ tryFocus(3); }, 150);
        $wnd.setTimeout(function(){ tryFocus(4); }, 300);
    }-*/;

    public static native void alert(String msg) /*-{
        $wnd.alert(msg);
    }-*/;

    public static void checkPendingPurchases() {
        YandexSDK.getPurchases(new JsonCallback() {
            @Override
            public void onResult(String json) {
//                YandexSDK.alert("checkPendingPurchases  json = " + json);
                try {
                    // Парсим покупки как список Purchase
                    List<YandexModels.Purchase> purchases = YandexParser.parsePurchases(json);

                    for (YandexModels.Purchase purchase : purchases) {
                        String token = purchase.purchaseToken; // уникальный токен покупки
                        if (token == null || token.isEmpty()) continue;

                        // Проверяем, начисляли ли уже этот токен
                        if (!User.get().isPurchasedToken(token)) {

                            // Ищем продукт в каталоге, чтобы понять количество рубинов
                            YandexModels.Product prod = findProductById(purchase.productID); //todo BillingProduct product = findProductById(...)
                            if (prod != null) {
                                System.out.println("Pending purchase granted: " + purchase.productID + " token: " + token);
                                // Поглощаем покупку, чтобы нельзя было использовать повторно
                                YandexSDK.consumePurchase(
                                        token,
                                        () -> {
                                            System.out.println("Purchase consumed: " + token);
//                                            YandexSDK.alert("Purchase consumed: " + token);
//                                            YandexSDK.alert("checkPendingPurchases Purchase consumed: " + token);
//                                            YandexSDK.alert("--- checkPendingPurchases  prod.id = " + prod.id);
//                                            listener.onPurchased(product); //todo
                                            if(prod.id.equals("shop_hide_adv")) { // это отключение рекламы
                                                User.get().isAdHide = true;
                                            } else {
                                                int gold = Integer.parseInt(prod.title); // количество рубинов
                                                User.get().changeGold(gold);
                                            }
                                            User.get().markPurchasedToken(token); // помечаем токен как обработанный и сохраняем
                                        },
                                        () -> System.err.println("Failed to consume purchase: " + token)
                                );
                            } else {
                                System.err.println("Product not found in catalog: " + purchase.productID);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing pending purchases: " + e);
                }
            }

            @Override
            public void onError(String error) {
                System.err.println("getPurchases failed: " + error);
            }
        });
    }

    private static YandexModels.Product findProductById(String productId) {
        for (YandexModels.Product p : YandexSDK.PRODUCTS) {
            if (p.id.equals(productId)) {
                return p;
            }
        }
        return null; // если товар не найден
    }

    public static native void getLeaderboardEntries(String boardName, int top, int around, LeaderboardEntriesCallback cb) /*-{
        console.log("before getEntries");
        if (!$wnd.ysdk || !$wnd.ysdk.leaderboards) {
            cb.@com.plagame.game.kassa.yandex.LeaderboardEntriesCallback::onError(Ljava/lang/String;)(
                "ysdk or leaderboards not available"
            );
            return;
        }

        var options = {
            quantityTop: top,
            includeUser: true,
            quantityAround: around
        };

        console.log("options", options);
        var promise = $wnd.ysdk.leaderboards.getEntries(boardName, options);
        console.log("promise =", promise);

        promise
            .then($entry(function(res) {
                console.log("SUCCESS");
                console.log(JSON.stringify(res));

                cb.@com.plagame.game.kassa.yandex.LeaderboardEntriesCallback::onSuccess(Ljava/lang/String;)(
                    JSON.stringify(res)
                );
            }))
            ["catch"]($entry(function(err) {
                console.error("RAW ERROR:", err);

                cb.@com.plagame.game.kassa.yandex.LeaderboardEntriesCallback::onError(Ljava/lang/String;)(
                    String(err)
                );
            }));
    }-*/;

    public static void submitScore(String leaderboardName, long score, String maxDollarsAsString) {
        submitScoreImpl(leaderboardName, String.valueOf(score), maxDollarsAsString);
    }

    private static native void submitScoreImpl(String leaderboardName, String score, String formatedScore) /*-{ // этот метод тоже рабочий, просто в верхнем больше логов добавлено
        if (!$wnd.ysdk || !$wnd.ysdk.leaderboards) return;

        $wnd.ysdk.leaderboards.setScore(leaderboardName, Number(score), formatedScore)
            .then(function() {
                console.log("Score submitted: " + score);
            })["catch"](function(e) {
                console.error("Submit error:", e);
                console.log(e);
                console.log(e.code);
                console.log(e.message);
                console.log(JSON.stringify(e));
            });
    }-*/;

}
