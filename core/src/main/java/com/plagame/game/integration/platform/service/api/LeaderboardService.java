package com.plagame.game.integration.platform.service.api;

import com.plagame.game.integration.platform.service.api.model.LeaderboardEntry;
import com.plagame.game.integration.platform.service.api.model.PlatformCallback;

import java.util.List;

/**
 * Created by Дмитрий Малышев on 18.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public interface LeaderboardService {

    void init();
    void submitScore(String leaderboardName, long score, String formattedScore);
    void loadDefaultLeaderboard();
    void loadLeaderboard(String leaderboardName, int top, int around, PlatformCallback<List<LeaderboardEntry>> callback);

    List<LeaderboardEntry> getLeaderboard();

    void setLeaderboard(List<LeaderboardEntry> leaderboard);
}
