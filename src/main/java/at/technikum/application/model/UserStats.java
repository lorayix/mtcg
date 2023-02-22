package at.technikum.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserStats {
    @JsonProperty("Name")
    private final String name;
    @JsonProperty("Elo")
    private final int elo;
    @JsonProperty("Wins")
    private final int wins;
    @JsonProperty("Losses")
    private final int losses;
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
