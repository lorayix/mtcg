package at.technikum.application.service;

import at.technikum.application.model.User;
import at.technikum.application.model.UserData;
import at.technikum.application.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository;}
    public User findUserByUsername(String username) { return userRepository.findUserByUsername(username); }
    public int loginUser(User user) { return userRepository.loginUser(user); }
    public void save(User user){ userRepository.save(user);}
    public UserData getUserData(String username, String token) { return userRepository.getUserData(username, token); }
    public int updateData(String token, UserData userData) {
        return userRepository.updateData(token, userData);
    }
    public int getCoins(String token) { return userRepository.getCoins(token); }
    public void subtractCoinsForPackage(int coins, String token){ userRepository.subtractCoinsForPackage(coins, token); }
}
