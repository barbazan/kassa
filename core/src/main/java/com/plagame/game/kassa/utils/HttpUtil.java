package com.plagame.game.kassa.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;

/**
 * Created by Дмитрий Малышев on 05.07.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class HttpUtil {

    public static void sendYookassaRedirectHttpRequest(int userId, int amount) {
        String url = "https://gamedev.mobi/yookassa_redirect?game=six&user=" + userId + "&quantity=" + amount;
        Gdx.net.openURI(url);
//        sendHttpRequest(url);
    }

    public static void sendHttpRequest(String url) {
        System.out.println("------- SEND HTTP REQUEST url = " + url);
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl(url);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String response = httpResponse.getResultAsString();

                Gdx.app.log("HTTP", "Status: " + status);
                Gdx.app.log("HTTP", "Response: " + response);
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                Gdx.app.error("HTTP", "Request failed", t);
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Request cancelled");
            }
        });

    }
}
