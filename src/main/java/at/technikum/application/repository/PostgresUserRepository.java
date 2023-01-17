package at.technikum.application.repository;


import at.technikum.application.config.DbConnector;
import at.technikum.application.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostgresUserRepository implements UserRepository {

    private final DbConnector dataSource;
    public static final String SETUP_TABLE = """
                CREATE TABLE IF NOT EXISTS users(
                    userID serial PRIMARY KEY
                    username text NOT NULL UNIQUE
                    password text NOT NULL
                    bio text
                    image text
                    elo int default 100
                    wins int default 0
                    losses int default 0
                    coins int default 20
                );
            """;

    public static final String SEARCH_FOR_USER = """
                SELECT * FROM user WHERE username = ?
            """;

    public static final String QUERY_REGISTER_USER = """
                INSERT INTO users (username, password)
                VALUES (?, ?)
            """;

    public PostgresUserRepository(DbConnector dataSource) {
        this.dataSource = dataSource;
        try (PreparedStatement ps = dataSource.getConnection().
                prepareStatement(SETUP_TABLE)){
            ps.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to setup table for User", e);
        }
    }

    @Override
    public void save(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        try(Connection c = dataSource.getConnection()) {
            try(PreparedStatement ps = c.prepareStatement(QUERY_REGISTER_USER)){
                ps.setString(1, username);
                ps.setString(2, password);
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query failed", e);
        }
    }


    @Override
    public User findUserByUsername(String username) {
        User user;
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(SEARCH_FOR_USER)){
                ps.setString(1, username);
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                user = new User(resultSet.getString(2), resultSet.getString(3));
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query failed", e);
        }
        return user;
    }
}
