package controller;

import com.sun.net.httpserver.HttpServer;
import controller.auth.AuthController;
import controller.paket.PaketController;
import controller.gudang.GudangController;
import controller.kurir.KurirController;
import controller.tracking.TrackingController;
import filter.AuthFilter;
import util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class AppServer {
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(null); // default executor

        // Register routes - each controller handles its own routing internally
        registerRoutes();

        // Default 404 handler
        server.createContext("/", exchange -> handleDefault(exchange));

        server.start();
        System.out.println("[HTTP Server] Started on port " + port);
        System.out.println("[HTTP Server] Available endpoints:");
        System.out.println("  POST   /api/auth/register");
        System.out.println("  POST   /api/auth/login");
        System.out.println("  POST   /api/auth/logout");
        System.out.println("  GET    /api/users/profile");
        System.out.println("  GET    /api/kurir");
        System.out.println("  POST   /api/paket/cek-ongkir");
        System.out.println("  POST   /api/paket");
        System.out.println("  GET    /api/paket/saya");
        System.out.println("  GET    /api/paket/{no_resi}");
        System.out.println("  GET    /api/gudang/paket");
        System.out.println("  POST   /api/gudang/paket");
        System.out.println("  PATCH  /api/gudang/paket/{no_resi}/transit");
        System.out.println("  PATCH  /api/gudang/paket/{no_resi}/assign-kurir");
        System.out.println("  GET    /api/kurir/tugas");
        System.out.println("  PATCH  /api/kurir/paket/{no_resi}/terima");
        System.out.println("  POST   /api/kurir/paket/{no_resi}/lokasi");
        System.out.println("  PATCH  /api/kurir/paket/{no_resi}/selesai");
        System.out.println("  PATCH  /api/kurir/ketersediaan");
        System.out.println("  GET    /api/tracking/{no_resi}");
    }

    private static void registerRoutes() {
        // Auth - single handler for all /api/auth/* and /api/users/profile
        AuthController authController = new AuthController();
        server.createContext("/api/auth/register", authController);
        server.createContext("/api/auth/login", authController);
        server.createContext("/api/auth/logout", authController);
        server.createContext("/api/users/profile", authController);

        // Paket
        PaketController paketController = new PaketController();
        server.createContext("/api/paket/cek-ongkir", paketController);
        server.createContext("/api/paket", paketController); // POST & GET
        server.createContext("/api/paket/saya", paketController);
        server.createContext("/api/paket/", paketController); // GET /api/paket/{no_resi}

        // Gudang
        GudangController gudangController = new GudangController();
        server.createContext("/api/gudang/paket", gudangController);
        server.createContext("/api/gudang/paket/", gudangController);

        // Kurir
        KurirController kurirController = new KurirController();
        server.createContext("/api/kurir", kurirController);
        server.createContext("/api/kurir/tugas", kurirController);
        server.createContext("/api/kurir/paket/", kurirController);
        server.createContext("/api/kurir/ketersediaan", kurirController);

        // Tracking
        TrackingController trackingController = new TrackingController();
        server.createContext("/api/tracking/", trackingController);
    }

    private static void handleDefault(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            setCorsHeaders(exchange);
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        setCorsHeaders(exchange);
        String response = JsonUtil.toJson(new ErrorResponse("Endpoint tidak ditemukan", 404));
        exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void setCorsHeaders(com.sun.net.httpserver.HttpExchange exchange) {
        String origin = exchange.getRequestHeaders().getFirst("Origin");
        String allowedOrigin = "http://localhost:50927";
        if (origin != null && origin.equals(allowedOrigin)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", allowedOrigin);
        } else {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        }
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    }

    public static void shutdown() {
        if (server != null) {
            server.stop(0);
            System.out.println("[HTTP Server] Stopped");
        }
    }

    // Static inner class for ErrorResponse to avoid import
    private static class ErrorResponse {
        private boolean success = false;
        private String message;
        private int status;

        public ErrorResponse(String message, int status) {
            this.message = message;
            this.status = status;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getStatus() { return status; }
    }
}