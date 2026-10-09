package com.plagame.game.integration.vk;

import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

/** Browser bridge for VK game payments. */
public final class VKSDK {
    private VKSDK() {
    }

    public static native void init(PlatformCallback<String> callback) /*-{
        if (!$wnd.kassaVKBilling) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)("vk-billing.js is not loaded");
            return;
        }
        $wnd.kassaVKBilling.init().then($entry(function() {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onSuccess(Ljava/lang/Object;)("ready");
        }), $entry(function(error) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)(error.message || JSON.stringify(error) || String(error));
        }));
    }-*/;

    public static native void purchase(String item, PlatformCallback<String> callback, Runnable onCancel) /*-{
        if (!$wnd.kassaVKBilling) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)("vk-billing.js is not loaded");
            return;
        }
        $wnd.kassaVKBilling.purchase(item).then($entry(function(result) {
            if (result.status === "cancel") {
                onCancel.@java.lang.Runnable::run()();
            } else {
                callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onSuccess(Ljava/lang/Object;)(String(result.order_id));
            }
        }), $entry(function(error) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)(error.message || JSON.stringify(error) || String(error));
        }));
    }-*/;

    public static native boolean isVKEnvironment() /*-{
        return /(?:^|[?&])vk_app_id=\d+(?:&|$)/.test($wnd.location.search)
            && /(?:^|[?&])vk_user_id=\d+(?:&|$)/.test($wnd.location.search);
    }-*/;

    public static native String getLaunchParams() /*-{
        return $wnd.location.search;
    }-*/;

    public static native void alert(String message) /*-{
        $wnd.alert(message);
    }-*/;
}