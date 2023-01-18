package at.technikum.application.repository;


import at.technikum.application.config.DbConnector;
import at.technikum.application.model.User;
import at.technikum.application.model.UserData;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresUserRepository implements UserRepository {

    private final DbConnector dataSource;
    public static final String SETUP_TABLE = """
                CREATE TABLE IF NOT EXISTS users(
                    userID serial PRIMARY KEY,
                    username varchar UNIQUE NOT NULL,
                    password varchar NOT NULL,
                    name varchar NOT NULL default ' ',
                    bio varchar NOT NULL default '  ',
                    image varchar NOT NULL default ' ',
                    wins int default 0,
                    losses int default 0,
                    coins int default 20,
                    loggedIn BOOLEAN default FALSE,
                    token varchar UNIQUE NOT NULL
                );
            """;

    public static final String QUERY_SEARCH_FOR_USER_USERNAME = """
                SELECT * FROM users WHERE username = ?
            """;

    public static final String QUERY_REGISTER_USER = """
                INSERT INTO users (username, password, token)
                VALUES (?, ?, ?)
            """;

    public static final String QUERY_LOGIN = """
                UPDATE users SET loggedIn = TRUE WHERE username = ? AND password = ? AND loggedIn = FALSE;
            """;

    public static final String QUERY_GET_USERDATA = """
                SELECT name, bio, image FROM users WHERE username = ? AND token = ? AND loggedIN = TRUE;
            """;

    public static final String QUERY_UPDATE_USERDATA = """
                UPDATE users SET name = ?, bio = ?, image = ? WHERE token = ?
            """;

    public PostgresUserRepository(DbConnector dataSource) {
        this.dataSource = dataSource;
        try (PreparedStatement ps = dataSource.getConnection().
                prepareStatement(SETUP_TABLE)) {
            ps.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to setup table for User", e);
        }
    }

    @Override
    public void save(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(QUERY_REGISTER_USER)) {
                ps.setString(1, username);
                ps.setString(2, String.valueOf(password.hashCode()));
                ps.setString(3, (username + "-mtcgToken"));
                System.out.println(ps);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB query failed", e);
        }
    }

    @Override
    public User findUserByUsername(String username) {
        User user;
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(QUERY_SEARCH_FOR_USER_USERNAME)) {
                ps.setString(1, username);
                System.out.println(ps);
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                if (!resultSet.next()) {
                    user = null;
                } else {
                    user = new User(resultSet.getString("username"), resultSet.getString("password"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB query 'findUserByUsername' failed", e);
        }
        return user;
    }

    @Override
    public int loginUser(User user) {
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(QUERY_LOGIN)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, String.valueOf(user.getPassword().hashCode()));

                int state = ps.executeUpdate();
                if (state != 1) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB query 'loginUser' failed", e);
        }
    }

    @Override
    public UserData getUserData(String username, String token) {
        UserData userData;
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(QUERY_GET_USERDATA)) {
                ps.setString(1, username);
                System.out.println(username);
                ps.setString(2, token);
                ps.executeQuery();
                ResultSet resultSet = ps.getResultSet();
                System.out.println(resultSet.getStatement());
                if(!resultSet.next()){
                    userData = null;
                } else {
                    userData = new UserData(resultSet.getString("name"), resultSet.getString("bio"), resultSet.getString("image"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB query 'getUserData' failed");
        }
        return userData;
    }

    @Override
    public int updateData(String token, UserData userData) {
        String name = userData.getName();
        String bio = userData.getBio();
        String image = userData.getImage();
        try (Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_UPDATE_USERDATA)){
                ps.setString(1, name);
                ps.setString(2, bio);
                ps.setString(3, image);
                ps.setString(4, token);

                int state = ps.executeUpdate();
                if(state != 1) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query 'updateData' failed");
        }
    }

}

