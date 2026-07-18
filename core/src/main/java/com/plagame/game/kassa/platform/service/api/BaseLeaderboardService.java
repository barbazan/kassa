package com.plagame.game.kassa.platform.service.api;

import com.plagame.game.kassa.platform.service.api.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дмитрий Малышев on 21.06.2026.
 * Email: dmitry.malyshev@gmail.com
 */
public abstract class BaseLeaderboardService implements LeaderboardService {

    protected List<LeaderboardEntry> leaderboard = new ArrayList<>();

    public List<LeaderboardEntry> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(List<LeaderboardEntry> leaderboard) {
        this.leaderboard = leaderboard;
    }
}
