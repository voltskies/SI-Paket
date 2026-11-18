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
        
        // Build DB_URL from components if not provided
        String dbUrl = env.getProperty("DB_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            String host = env.getProperty("DB_HOST", "localhost");
            String port = env.getProperty("DB_PORT", "3306");
            String name = env.getProperty("DB_NAME", "sipaket");
            dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + name;
        } else if (!dbUrl.startsWith("jdbc:")) {
            // If URL provided but missing jdbc: prefix
            dbUrl = "jdbc:" + dbUrl;
        }
        
        this.user = env.getProperty("DB_USER", "root");
        this.password = env.getProperty("DB_PASSWORD", "");

        // Build final URL with proper SSL parameters for TiDB Cloud
        StringBuilder finalUrl = new StringBuilder(dbUrl);
        boolean hasQuery = dbUrl.contains("?");
        
        // Check if it's TiDB Cloud
        boolean isTiDB = dbUrl.contains("tidbcloud") || dbUrl.contains("tiDB") || dbUrl.contains("TiDB");
        
        if (!hasQuery) {
            finalUrl.append("?");
        } else {
            finalUrl.append("&");
        }
        
        if (isTiDB) {
            // TiDB Cloud requires SSL
            finalUrl.append("useSSL=true&requireSSL=true&verifyServerCertificate=true&enabledTLSProtocols=TLSv1.2,TLSv1.3");
        } else {
            // Local MySQL can use insecure
            finalUrl.append("useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        }
        
        this.url = finalUrl.toString();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.util.Properties connProps = new java.util.Properties();
            connProps.setProperty("user", user);
            connProps.setProperty("password", password);
            connProps.setProperty("connectTimeout", "10000");
            connProps.setProperty("socketTimeout", "10000");
            this.connection = DriverManager.getConnection(url, connProps);
            this.connected = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[Database] Driver MySQL tidak ditemukan: " + e.getMessage());
            this.connected = false;
        } catch (SQLException e) {
            System.err.println("[Database] Gagal koneksi: " + e.getMessage());
            this.connected = false;
        }
    }

    private Properties loadEnv() {
        Properties props = new Properties();
        
        // 1. First priority: System environment variables (Render, production)
        String dbUrl = System.getenv("DB_URL");
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");
        String jwtSecret = System.getenv("JWT_SECRET");
        String jwtExp = System.getenv("JWT_EXPIRATION_MS");
        
        if (dbUrl != null) props.setProperty("DB_URL", dbUrl);
        if (dbHost != null) props.setProperty("DB_HOST", dbHost);
        if (dbPort != null) props.setProperty("DB_PORT", dbPort);
        if (dbName != null) props.setProperty("DB_NAME", dbName);
        if (dbUser != null) props.setProperty("DB_USER", dbUser);
        if (dbPass != null) props.setProperty("DB_PASSWORD", dbPass);
        if (jwtSecret != null) props.setProperty("JWT_SECRET", jwtSecret);
        if (jwtExp != null) props.setProperty("JWT_EXPIRATION_MS", jwtExp);
        
        // 2. Second priority: .env file (local development)
        try (InputStream input = Files.newInputStream(Paths.get(".env"))) {
            Properties fileProps = new Properties();
            fileProps.load(input);
            // Only set if not already set by system env
            if (dbUrl == null) props.setProperty("DB_URL", fileProps.getProperty("DB_URL", ""));
            if (dbHost == null) props.setProperty("DB_HOST", fileProps.getProperty("DB_HOST", "localhost"));
            if (dbPort == null) props.setProperty("DB_PORT", fileProps.getProperty("DB_PORT", "3306"));
            if (dbName == null) props.setProperty("DB_NAME", fileProps.getProperty("DB_NAME", "sipaket"));
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