package com.plagame.game.kassa.platform.service.yandex;

import static com.plagame.game.kassa.GameConfig.LEADERBOARD_MAX_DOLLARS_NAME;

import com.plagame.game.kassa.platform.service.api.BaseLeaderboardService;
import com.plagame.game.kassa.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.kassa.platform.service.api.model.PlatformCallback;
import com.plagame.game.kassa.utils.Time;
import com.plagame.game.kassa.yandex.LeaderboardEntriesCallback;
import com.plagame.game.kassa.yandex.LeaderboardParser;
import com.plagame.game.kassa.yandex.YandexModels;
import com.plagame.game.kassa.yandex.YandexSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
class YandexLeaderboardService extends BaseLeaderboardService {

    public static long LOAD_LEADERBOARD_COOLDOWN = 2 * Time.MINUTE_MILLIS;
    private long lastLoadLeaderboardTime = System.currentTimeMillis() - LOAD_LEADERBOARD_COOLDOWN;

    @Override
    public void init() {
        loadDefaultLeaderboard();
    }

    @Override
    public void submitScore(String leaderboardName, long score, String formattedScore) {
        YandexSDK.submitScore(leaderboardName, score, formattedScore);
    }

    @Override
    public void loadDefaultLeaderboard() {
        loadLeaderboard(LEADERBOARD_MAX_DOLLARS_NAME, 100, 20, new PlatformCallback<List<LeaderboardEntry>>() {
            @Override
            public void onSuccess(List<LeaderboardEntry> value) {
                leaderboard = value;
                System.out.println("Yandex Leaderbord successfully loaded");
            }

            @Override
            public void onError(String error) {
                System.out.println("error = " + error);
            }
        });
    }

    @Override
    public void loadLeaderboard(String leaderboardName, int top, int around, PlatformCallback<List<LeaderboardEntry>> callback) {
        if(System.currentTimeMillis() > lastLoadLeaderboardTime + LOAD_LEADERBOARD_COOLDOWN) {
            YandexSDK.getLeaderboardEntries(leaderboardName, top, around, new LeaderboardEntriesCallback() {
                @Override
                public void onSuccess(String json) {
                    YandexModels.LeaderboardResponse response = LeaderboardParser.parseLeaderboard(json);
                    List<LeaderboardEntry> leaderboard = new ArrayList<>();
                    for(YandexModels.YandexLeaderboardEntry entry : response.entries) {
                        LeaderboardEntry leaderboardEntry = new LeaderboardEntry();
                        leaderboardEntry.rank = entry.rank;
                        leaderboardEntry.id = entry.yandexPlayer.uniqueID.hashCode();
                        leaderboardEntry.login = entry.yandexPlayer.publicName;
                        leaderboardEntry.value = entry.score;
                        leaderboardEntry.formattedScore = entry.extraData;
                        leaderboard.add(leaderboardEntry);
                    }
                    callback.onSuccess(leaderboard);
                }

                @Override
                public void onError(String err) {
                    System.out.println("----------------Error: " + err);
//                YandexSDK.alert("----------------Error: " + err);
                    callback.onError(err);
                }
            });
            lastLoadLeaderboardTime = System.currentTimeMillis();
        }
    }

}
