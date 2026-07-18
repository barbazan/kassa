package com.plagame.game.kassa.platform.service.api.model;

/**
 * User: mitrofan
 * Date: 06.09.11
 */
public class LeaderboardEntry {

    public int id;
    public String login;
    public long value;
    public int rank;
    public String formattedScore;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(Integer id, String login, Long value, Integer rank) {
        this.id = id;
        this.login = login;
        this.value = value;
        this.rank = rank;
    }

}
