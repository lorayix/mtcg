package at.technikum.application.model;

public class UserStats {
    private String name;
    private int elo;
    private int wins;
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
