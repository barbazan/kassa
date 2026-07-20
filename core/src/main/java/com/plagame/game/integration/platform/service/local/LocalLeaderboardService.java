package com.plagame.game.integration.platform.service.local;

import static com.plagame.game.kassa.GameConfig.LEADERBOARD_MAX_DOLLARS_NAME;

import com.plagame.game.kassa.GameApplication;
import com.plagame.game.kassa.GameConfig;
import com.plagame.game.integration.platform.service.api.BaseLeaderboardService;
import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public class LocalLeaderboardService extends BaseLeaderboardService {

    @Override
    public void init() {
        loadDefaultLeaderboard();
    }

    @Override
    public void submitScore(String leaderboardName, long score, String formattedScore) {
        if(GameApplication.get().networkWebSocketClient.isConnected()) {
            GameApplication.get().networkWebSocketClient.sendSaveUserPacket();
        }
    }

    public void loadDefaultLeaderboard() {
        loadLeaderboard(LEADERBOARD_MAX_DOLLARS_NAME, 100, 20, new PlatformCallback<List<LeaderboardEntry>>() {
            @Override
            public void onSuccess(List<LeaderboardEntry> value) {
                leaderboard = value;
                System.out.println("MyServer Leaderbord successfully loaded");
            }

            @Override
            public void onError(String error) {
                System.out.println("error = " + error);
            }
        });
    }

    @Override
    public void loadLeaderboard(String leaderboardName, int top, int around, PlatformCallback<List<LeaderboardEntry>> callback) {
        if(GameApplication.get().networkWebSocketClient.isConnected()) {
            GameApplication.get().networkWebSocketClient.sendGetRatingPacket();
        } else {
            loadMockLeaderboardEntries(leaderboardName, top, around, callback);
        }
    }

    private void loadMockLeaderboardEntries(String leaderboardName, int top, int around, PlatformCallback<List<LeaderboardEntry>> callback) {
        leaderboard.clear();
        int MAX_NUM = 100;
        for (int i = 1; i <= MAX_NUM; i++) {
            String login = NickGenerator.generateNick();
            if(login.length() > 14) {
                login = login.substring(0,14);
            }
            int id = i;
            long score = 123123 * (MAX_NUM - i);
            int rank = i;
            LeaderboardEntry leaderboardEntry = new LeaderboardEntry(id, login, score, rank);
            if(i == 3) {
                leaderboardEntry.login = GameApplication.get().platform.cloud().getLogin();
            }
            leaderboard.add(leaderboardEntry);
        }
        callback.onSuccess(leaderboard);
    }

    public static class NickGenerator {

        private static final String[] ADJECTIVES = {
            "Crazy", "Dark", "Fast", "Lucky", "Wild", "Epic", "Silent",
            "Brave", "Swift", "Iron", "Shadow", "Golden", "Magic",
            "Red", "Blue", "Frozen", "Cyber", "Royal", "Ancient", "Nova"
        };

        private static final String[] NOUNS = {
            "Wolf", "Fox", "Tiger", "Dragon", "Falcon", "Bear", "Lion",
            "Knight", "Wizard", "Ninja", "Hunter", "Pirate", "Phoenix",
            "Panda", "Raven", "Storm", "Ghost", "Viking", "Samurai", "Eagle"
        };

        public static String generateNick() {
            String adjective = ADJECTIVES[GameConfig.random.nextInt(ADJECTIVES.length)];
            String noun = NOUNS[GameConfig.random.nextInt(NOUNS.length)];

            if (GameConfig.random.nextBoolean()) {
                return adjective + noun;
            }

            int number = GameConfig.random.nextInt(9000) + 1000;
            return adjective + noun + number;
        }
    }
}
