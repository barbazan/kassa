package com.plagame.game.integration.ok;

import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

/** Browser bridge for OK game payments. */
public final class OKSDK {
    private OKSDK() {
    }

    public static native void init(PlatformCallback<String> callback) /*-{
        if (!$wnd.kassaOKBilling) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)("ok-billing.js is not loaded");
            return;
        }
        $wnd.kassaOKBilling.init().then($entry(function() {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onSuccess(Ljava/lang/Object;)("ready");
        }), $entry(function(error) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)(error.message || JSON.stringify(error) || String(error));
        }));
    }-*/;

    public static native void purchase(String item, PlatformCallback<String> callback, Runnable onCancel) /*-{
        if (!$wnd.kassaOKBilling) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)("ok-billing.js is not loaded");
            return;
        }
        $wnd.kassaOKBilling.purchase(item).then($entry(function(result) {
            if (result.status === "cancel") {
                onCancel.@java.lang.Runnable::run()();
            } else {
                callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onSuccess(Ljava/lang/Object;)("ok");
            }
        }), $entry(function(error) {
            callback.@com.plagame.game.integration.platform.service.api.model.PlatformCallback::onError(Ljava/lang/String;)(error.message || JSON.stringify(error) || String(error));
        }));
    }-*/;

    public static native boolean isOKEnvironment() /*-{
        var query = $wnd.location.search;
        return /(?:^|[?&])vk_client=ok(?:&|$)/.test(query)
            || (/(?:^|[?&])logged_user_id=\d+(?:&|$)/.test(query)
                && /(?:^|[?&])api_server=/.test(query));
    }-*/;

    public static native void notifyLoadingReady() /*-{
        if ($wnd.kassaOKBilling) $wnd.kassaOKBilling.loaded();
    }-*/;

    public static native String getLaunchParams() /*-{
        return $wnd.location.search;
    }-*/;

    public static native void alert(String message) /*-{
        $wnd.alert(message);
    }-*/;
}