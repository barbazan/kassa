package com.plagame.game.integration.platform.service.api.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * User: mitrofan
 * Date: 06.09.11
 */
public class LeaderboardEntry implements Json.Serializable {

    public int id;
    public String login;
    public long value;
    public int rank;
    public String formattedScore;

    // Long scores need explicit JSON access in the GWT build, like account timestamps.
    @Override public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("login", login);
        json.writeValue("value", value);
        json.writeValue("rank", rank);
        json.writeValue("formattedScore", formattedScore);
    }

    @Override public void read(Json json, JsonValue data) {
        id = data.getInt("id", id);
        login = data.getString("login", login);
        value = data.getLong("value", value);
        rank = data.getInt("rank", rank);
        formattedScore = data.getString("formattedScore", formattedScore);
    }

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(Integer id, String login, Long value, Integer rank) {
        this.id = id;
        this.login = login;
        this.value = value;
        this.rank = rank;
    }

}
