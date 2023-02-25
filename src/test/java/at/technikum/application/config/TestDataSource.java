package at.technikum.application.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDataSource implements DbConnector {
    private final HikariDataSource ds;
    private TestDataSource() {
        HikariConfig config = new HikariConfig(
                "src/test/resources/hikari.properties"
        );
        ds = new HikariDataSource(config);
    }

    private static TestDataSource dataSource;

    public static TestDataSource getInstance() {
        if (dataSource == null) {
            dataSource = new TestDataSource();
        }
        return dataSource;
    }

    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Database not available!", e);
        }
    }
}
