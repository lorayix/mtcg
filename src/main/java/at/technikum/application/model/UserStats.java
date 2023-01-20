package at.technikum.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserStats {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Elo")
    private int elo;
    @JsonProperty("Wins")
    private int wins;
    @JsonProperty("Losses")
    private int losses;

    public UserStats(String username, int elo, int wins, int losses) {
        this.name = username;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    public String getName() {
        return name;
    }

    public int getElo() {
        return elo;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
