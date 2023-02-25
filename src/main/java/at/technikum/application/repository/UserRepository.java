package at.technikum.application.repository;

import at.technikum.application.model.Credentials;
import at.technikum.application.model.User;
import at.technikum.application.model.UserData;
import at.technikum.application.model.UserStats;

import java.util.List;

public interface UserRepository {
    void save(User user);
    User findUserByUsername(String username);
    boolean loginUser(Credentials user);
    UserData getUserData(String username, String token);
    boolean updateData(String token, UserData userData);
    int getCoins(String token);
    void subtractCoinsForPackage(int coins, String token);
    List<UserStats> getScoreboard();
    UserStats getUserScore(String token);
    void userWin(String winner);
    void userLoss(String loser);
}
