package at.technikum.application.repository;

import at.technikum.application.model.User;

public interface UserRepository {
    void save(User user);
    User findUserByUsername(String username);
}
