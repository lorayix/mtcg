package at.technikum.application.repository;

import at.technikum.application.config.TestDataSource;
import at.technikum.application.model.Credentials;
import at.technikum.application.model.User;
import at.technikum.application.model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostgresUserRepositoryTest {

    private final PostgresUserRepository postgresUserRepository = new PostgresUserRepository(TestDataSource.getInstance());

    @AfterEach
    void cleanUpTable() throws SQLException{
        try(Connection c = TestDataSource.getInstance().getConnection()){
            c.prepareStatement("""
                    DELETE FROM users;
                    """
            ).execute();
        }
    }

    @Test
    void createUser() {
        User user = new User("test", "user");
        postgresUserRepository.save(user);

        User found = postgresUserRepository.findUserByUsername(user.getUsername());

        assertEquals(user.getUsername(), found.getUsername());
        assertEquals(String.valueOf(user.getPassword().hashCode()), found.getPassword());
    }

    @Test
    void loginUser(){
        String username = "test";
        String pw = "user";
        User user = new User(username, pw);
        Credentials cred = new Credentials(username, pw);
        postgresUserRepository.save(user);

        boolean success = postgresUserRepository.loginUser(cred);

        assertTrue(success);
    }

    @Test
    void getDataWorks() throws SQLException {
        String token = "test-mtcgToken";
        String username = "test";
        String pw = "user";
        postgresUserRepository.save(new User(username, pw));
        postgresUserRepository.loginUser(new Credentials(username, String.valueOf(pw.hashCode())));
        setupTestData();

        UserData userData = postgresUserRepository.getUserData(username, token);

        assertEquals("tired", userData.getName());
        assertEquals("so sleepy", userData.getBio());
        assertEquals("zzZzzZzZ", userData.getImage());
    }

    private void setupTestData() throws SQLException{
        try(Connection c = TestDataSource.getInstance().getConnection()){
            PreparedStatement ps = c.prepareStatement("""
                    UPDATE users SET name = ?, bio = ?, image = ? WHERE token = ?
                    """);
            ps.setString(1, "tired");
            ps.setString(2, "so sleepy");
            ps.setString(3, "zzZzzZzZ");
            ps.setString(4, "test-mtcgToken");
            int state = ps.executeUpdate();
            System.out.println(state);
        }
    }
}
