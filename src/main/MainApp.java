package main;

import controller.AppServer;

public class MainApp {
    public static void main(String[] args) {
        try {
            System.out.println("Starting SI-Paket HTTP Server...");
            AppServer.main(args);
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}