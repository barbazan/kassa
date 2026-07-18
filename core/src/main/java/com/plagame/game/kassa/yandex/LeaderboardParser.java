package com.plagame.game.kassa.yandex;

/**
 * Created by Дмитрий Малышев on 25.11.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class LeaderboardParser {

    public static YandexModels.LeaderboardResponse parseLeaderboard(String json) {
        YandexModels.LeaderboardResponse r = new YandexModels.LeaderboardResponse();
        json = json.trim();

        // -------- ENTITIES ARRAY --------
        String entriesBlock = extract(json, "\"entries\"", "[", "]");
        String[] entries = entriesBlock.split("\\},\\s*\\{");

//        YandexSDK.alert("entries.length=" + entries.length);

        for (String e : entries) {
            e = e.trim();

            // убрать только внешние скобки
            if (e.startsWith("{")) e = e.substring(1);
            if (e.endsWith("}")) e = e.substring(0, e.length() - 1);

            YandexModels.YandexLeaderboardEntry entry = new YandexModels.YandexLeaderboardEntry();

            entry.extraData = getValue(e, "extraData");
            entry.score = getLong(e, "score");
            entry.rank = getInt(e, "rank");
            entry.formattedScore = getValue(e, "formattedScore");

            // PLAYER BLOCK
            String playerBlock = extract(e, "\"player\"", "{", "}");
//            YandexSDK.alert("playerBlock=" + playerBlock);

            entry.yandexPlayer.lang = getValue(playerBlock, "lang");
            entry.yandexPlayer.publicName = getValue(playerBlock, "publicName");
            entry.yandexPlayer.uniqueID = getValue(playerBlock, "uniqueID");

            // scopePermissions
            String scope = extract(playerBlock, "\"scopePermissions\"", "{", "}");
            entry.yandexPlayer.avatarPermission = getValue(scope, "avatar");
            entry.yandexPlayer.publicNamePermission = getValue(scope, "public_name");

            r.entries.add(entry);
        }

//        for (String e : entries) {
////            e = e.replace("{", "").replace("}", "").trim();
//            e = e.trim();
//            YandexModels.LeaderboardEntry entry = new YandexModels.LeaderboardEntry();
//
//            entry.extraData = getValue(e, "extraData");
//            entry.score = getInt(e, "score");
//            entry.rank = getInt(e, "rank");
//            entry.formattedScore = getValue(e, "formattedScore");
//
////            YandexSDK.alert("entry.rank=" + entry.rank);
////            YandexSDK.alert("entry.score=" + entry.score);
//
//            // PLAYER BLOCK
//            String playerBlock = extract(e, "\"player\"", "{", "}");
//            YandexSDK.alert("playerBlock=" + playerBlock);
//            entry.player.lang = getValue(playerBlock, "lang");
//            entry.player.publicName = getValue(playerBlock, "publicName");
//            entry.player.uniqueID = getValue(playerBlock, "uniqueID");
//            YandexSDK.alert("entry.player=" + entry.player);
//
//            // scopePermissions
//            String scope = extract(playerBlock, "\"scopePermissions\"", "{", "}");
//            entry.player.avatarPermission = getValue(scope, "avatar");
//            entry.player.publicNamePermission = getValue(scope, "public_name");
//
//            r.entries.add(entry);
//        }
//        YandexSDK.alert("r.entries.size()=" + r.entries.size());

        // -------- LEADERBOARD --------
        String lb = extract(json, "\"leaderboard\"", "{", "}");
        r.leaderboard.name = getValue(lb, "name");
        r.leaderboard.appID = getInt(lb, "appID");
//        YandexSDK.alert("r.leaderboard.name=" + r.leaderboard.name);

        // title.ru
        String title = extract(lb, "\"title\"", "{", "}");
        r.leaderboard.titleRu = getValue(title, "ru");

        // -------- userRank --------
        r.userRank = getInt(json, "userRank");

//        YandexSDK.alert("r.userRank = " + r.userRank);
//        YandexSDK.alert("r = " + r);
        return r;
    }

    // ===============================
    //     ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ===============================

    private static String extract(String src, String key, String startSymbol, String endSymbol) {
        int k = src.indexOf(key);
        if (k == -1) return "";
        int a = src.indexOf(startSymbol, k);
        int b = findMatching(src, a, startSymbol.charAt(0), endSymbol.charAt(0));
        return (a != -1 && b != -1) ? src.substring(a + 1, b) : "";
    }

    private static int findMatching(String s, int start, char open, char close) {
        int lvl = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == open) lvl++;
            else if (c == close) lvl--;
            if (lvl == 0) return i;
        }
        return -1;
    }

    private static String getValue(String src, String key) {
        int k = src.indexOf("\"" + key + "\"");
        if (k == -1) return "";
        int colon = src.indexOf(":", k);
        int comma = src.indexOf(",", colon);
        if (comma == -1) comma = src.length();
        return clean(src.substring(colon + 1, comma));
    }

    private static int getInt(String src, String key) {
        try { return Integer.parseInt(getValue(src, key)); }
        catch (Exception e) { return 0; }
    }

    private static long getLong(String src, String key) {
        try { return Long.parseLong(getValue(src, key)); }
        catch (Exception e) { return 0; }
    }

    private static String clean(String s) {
        return s.trim().replaceAll("^\"|\"$", "");
    }
}
