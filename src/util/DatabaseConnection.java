package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private boolean connected = false;

    private static final String URL = "jdbc:mysql://localhost:3306/sipaket?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
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
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
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