package Metro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String HOST = env("DB_HOST", "localhost");
    private static final String PORT = env("DB_PORT", "3306");
    private static final String USER = env("DB_USER", "root");
    private static final String PASSWORD = env("DB_PASSWORD", "");

    private Database() {
    }

    public static Connection getUserConnection() throws SQLException {
        return DriverManager.getConnection(url("java_user_db"), USER, PASSWORD);
    }

    public static Connection getMetroConnection() throws SQLException {
        return DriverManager.getConnection(url("metro_database"), USER, PASSWORD);
    }

    private static String url(String database) {
        return "jdbc:mysql://" + HOST + ":" + PORT + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
