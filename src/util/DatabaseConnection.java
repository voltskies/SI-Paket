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
        String dbUrl = env.getProperty("DB_URL", "jdbc:mysql://localhost:3306/sipaket?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        this.user = env.getProperty("DB_USER", "root");
        this.password = env.getProperty("DB_PASSWORD", "");

        // Auto-detect TiDB and add required SSL parameters
        if (dbUrl.contains("tidbcloud") || dbUrl.contains("tiDB") || dbUrl.contains("TiDB")) {
            if (!dbUrl.contains("useSSL") && !dbUrl.contains("useSsl")) {
                dbUrl += (dbUrl.contains("?") ? "&" : "?") + "useSSL=true&requireSSL=true&verifyServerCertificate=false";
            }
        }
        this.url = dbUrl;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, password);
            this.connected = true;
            System.out.println("[Database] Koneksi ke Database berhasil! URL: " + url);
        } catch (ClassNotFoundException e) {
            System.err.println("[Database] Driver MySQL tidak ditemukan: " + e.getMessage());
            this.connected = false;
        } catch (SQLException e) {
            System.err.println("[Database] Gagal koneksi: " + e.getMessage());
            e.printStackTrace();
            this.connected = false;
        }
    }

    private Properties loadEnv() {
        Properties props = new Properties();
        
        // 1. First priority: System environment variables (Render, production)
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");
        String jwtSecret = System.getenv("JWT_SECRET");
        String jwtExp = System.getenv("JWT_EXPIRATION_MS");
        
        if (dbUrl != null) props.setProperty("DB_URL", dbUrl);
        if (dbUser != null) props.setProperty("DB_USER", dbUser);
        if (dbPass != null) props.setProperty("DB_PASSWORD", dbPass);
        if (jwtSecret != null) props.setProperty("JWT_SECRET", jwtSecret);
        if (jwtExp != null) props.setProperty("JWT_EXPIRATION_MS", jwtExp);
        
        // 2. Second priority: .env file (local development)
        try (InputStream input = Files.newInputStream(Paths.get(".env"))) {
            Properties fileProps = new Properties();
            fileProps.load(input);
            // Only set if not already set by system env
            if (dbUrl == null) props.setProperty("DB_URL", fileProps.getProperty("DB_URL", "jdbc:mysql://localhost:3306/sipaket?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"));
            if (dbUser == null) props.setProperty("DB_USER", fileProps.getProperty("DB_USER", "root"));
            if (dbPass == null) props.setProperty("DB_PASSWORD", fileProps.getProperty("DB_PASSWORD", ""));
            if (jwtSecret == null) props.setProperty("JWT_SECRET", fileProps.getProperty("JWT_SECRET", "default-secret-key-change-me-in-production"));
            if (jwtExp == null) props.setProperty("JWT_EXPIRATION_MS", fileProps.getProperty("JWT_EXPIRATION_MS", "86400000"));
        } catch (IOException e) {
            System.err.println("[Config] File .env tidak ditemukan, menggunakan system env: " + e.getMessage());
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