package at.technikum.application.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private final HikariDataSource ds;
    private DataSource(boolean isTest){
        HikariConfig config;
        if(isTest){
            config = new HikariConfig("src/test/resources/hikari.properties");
        } else {
            config = new HikariConfig("src/main/resources/hikari.properties");
        }
        ds = new HikariDataSource(config);
    }

    private static DataSource dataSource;

    public static DataSource getInstance(){
        if(dataSource == null){
            dataSource = new DataSource(false);
        }
        return dataSource;
    }

    public Connection getConnection(){
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Database not available");
        }
    };
}
