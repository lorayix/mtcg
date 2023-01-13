package at.technikum.application.service;

import at.technikum.application.model.User;
import at.technikum.application.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository;}

    public User findUserByUsername(String username) { return userRepository.findUserByUsername(username); }

}
