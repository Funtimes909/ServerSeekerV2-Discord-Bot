package xyz.funtimes909.serverseekerv2_discord_bot.util;

import org.apache.commons.dbcp2.BasicDataSource;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setUrl("jdbc:postgresql://" + Main.url + "postgres");
        dataSource.setUsername(Main.username);
        dataSource.setPassword(Main.password);
        dataSource.setMinIdle(20);
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            Main.logger.error("Couldn't get connection to the database!", e);
            return null;
        }
    }
}