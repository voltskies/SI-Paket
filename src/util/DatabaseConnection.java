package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private boolean connected = false;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        Properties env = loadEnv();
        this.url = env.getProperty("DB_URL", "jdbc:mysql://localhost:3306/sipaket?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        this.user = env.getProperty("DB_USER", "root");
        this.password = env.getProperty("DB_PASSWORD", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, password);
            this.connected = true;
            System.out.println("[Database] Koneksi ke MySQL berhasil!");
        } catch (ClassNotFoundException e) {
            System.err.println("[Database] Driver MySQL tidak ditemukan: " + e.getMessage());
            this.connected = false;
        } catch (SQLException e) {
            System.err.println("[Database] Gagal koneksi ke MySQL: " + e.getMessage());
            this.connected = false;
        }
    }

    private Properties loadEnv() {
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(".env"))) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("[Config] File .env tidak ditemukan, menggunakan default: " + e.getMessage());
        }
        return props;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        if (!connected) {
            return null;
        }
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                connected = true;
            }
        } catch (SQLException e) {
            System.err.println("[Database] Gagal mendapatkan koneksi: " + e.getMessage());
            connected = false;
            return null;
        }
        return connection;
    }

    public boolean isConnected() {
        return connected;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[Database] Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[Database] Error menutup koneksi: " + e.getMessage());
        }
        connected = false;
    }
}