package controller.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.Manajemen;
import dto.AuthRequest;
import dto.AuthResponse;
import dto.ErrorResponse;
import dto.UserProfileResponse;
import filter.AuthFilter;
import io.jsonwebtoken.Claims;
import model.Pengguna.Pengguna;
import model.Pengguna.User;
import model.Pengguna.PetugasGudang;
import model.Pengguna.Kurir;
import util.JsonUtil;
import util.JwtUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AuthController implements HttpHandler {

    private final Manajemen manajemen = Manajemen.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "POST":
                    if ("/api/auth/register".equals(path)) {
                        register(exchange);
                    } else if ("/api/auth/login".equals(path)) {
                        login(exchange);
                    } else if ("/api/auth/logout".equals(path)) {
                        logout(exchange);
                    } else {
                        sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
                    }
                    break;
                case "GET":
                    if ("/api/users/profile".equals(path)) {
                        profile(exchange);
                    } else {
                        sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
                    }
                    break;
                case "OPTIONS":
                    setCorsHeaders(exchange);
                    exchange.sendResponseHeaders(204, -1);
                    break;
                default:
                    sendResponse(exchange, 405, new ErrorResponse("Method tidak diizinkan", 405));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, new ErrorResponse("Internal server error: " + e.getMessage(), 500));
        }
    }

    public void register(HttpExchange exchange) throws IOException {
        AuthRequest req = JsonUtil.fromJson(readBody(exchange), AuthRequest.class);
        if (req == null || req.getEmail() == null || req.getPassword() == null || req.getRole() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Email, password, dan role wajib diisi", 400));
            return;
        }

        // Validasi role
        String role = req.getRole();
        if (!List.of("User", "PetugasGudang", "Kurir").contains(role)) {
            sendResponse(exchange, 400, new ErrorResponse("Role tidak valid: User, PetugasGudang, atau Kurir", 400));
            return;
        }

        // Validasi field khusus role
        if ("User".equals(role) && (req.getNoTelp() == null || req.getNoTelp().isEmpty())) {
            sendResponse(exchange, 400, new ErrorResponse("no_telp wajib diisi untuk role User", 400));
            return;
        }
        if ("PetugasGudang".equals(role) && (req.getLokasiPos() == null || req.getLokasiPos().isEmpty())) {
            sendResponse(exchange, 400, new ErrorResponse("lokasi_pos wajib diisi untuk role PetugasGudang", 400));
            return;
        }
        if ("Kurir".equals(role) && (req.getPlatKendaraan() == null || req.getPlatKendaraan().isEmpty())) {
            sendResponse(exchange, 400, new ErrorResponse("plat_kendaraan wajib diisi untuk role Kurir", 400));
            return;
        }

        // Cek email sudah terdaftar
        if (manajemen.login(req.getEmail(), req.getPassword()) != null) {
            sendResponse(exchange, 409, new ErrorResponse("Email sudah terdaftar", 409));
            return;
        }

        // Buat user object
        int idPengguna = -1;
        if ("User".equals(role)) {
            var user = new model.Pengguna.User(req.getNamaLengkap(), req.getEmail(), req.getPassword(), req.getNoTelp());
            idPengguna = manajemen.register(user);
        } else if ("PetugasGudang".equals(role)) {
            var pg = new model.Pengguna.PetugasGudang(req.getNamaLengkap(), req.getEmail(), req.getPassword(), req.getLokasiPos());
            idPengguna = manajemen.register(pg);
        } else if ("Kurir".equals(role)) {
            var kurir = new model.Pengguna.Kurir(req.getNamaLengkap(), req.getEmail(), req.getPassword(), req.getPlatKendaraan());
            idPengguna = manajemen.register(kurir);
        }

        if (idPengguna > 0) {
            String token = util.JwtUtil.generateToken(idPengguna, req.getEmail(), role);
            sendResponse(exchange, 201, new AuthResponse(true, "Registrasi berhasil", token, role, idPengguna, req.getNamaLengkap(), req.getEmail()));
        } else {
            sendResponse(exchange, 500, new ErrorResponse("Gagal mendaftarkan pengguna", 500));
        }
    }

    public void login(HttpExchange exchange) throws IOException {
        AuthRequest req = JsonUtil.fromJson(readBody(exchange), AuthRequest.class);
        if (req == null || req.getEmail() == null || req.getPassword() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Email dan password wajib diisi", 400));
            return;
        }

        model.Pengguna.Pengguna user = manajemen.login(req.getEmail(), req.getPassword());
        if (user != null) {
            String token = util.JwtUtil.generateToken(user.getIdPengguna(), user.getEmail(), user.getRole());
            sendResponse(exchange, 200, new AuthResponse(true, "Login berhasil", token, user.getRole(), user.getIdPengguna(), user.getNamaLengkap(), user.getEmail()));
        } else {
            sendResponse(exchange, 401, new ErrorResponse("Email atau password salah", 401));
        }
    }

    public void logout(HttpExchange exchange) throws IOException {
        // JWT stateless - client hapus token saja
        sendResponse(exchange, 200, new AuthResponse(true, "Logout berhasil (hapus token di client)", null, null, 0, null, null));
    }

    public void profile(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null) {
            sendResponse(exchange, 401, new ErrorResponse("Token tidak valid atau tidak ada", 401));
            return;
        }

        int userId = JwtUtil.getUserId(claims);
        Pengguna user = manajemen.findById(userId);
        if (user == null) {
            sendResponse(exchange, 404, new ErrorResponse("Pengguna tidak ditemukan", 404));
            return;
        }

        // Get full profile with role-specific data
        Pengguna fullUser = manajemen.findWithRole(userId);
        UserProfileResponse response = new UserProfileResponse();
        response.setIdPengguna(fullUser.getIdPengguna());
        response.setNamaLengkap(fullUser.getNamaLengkap());
        response.setEmail(fullUser.getEmail());
        response.setRole(fullUser.getRole());

        if (fullUser instanceof User) {
            response.setNoTelp(((User) fullUser).getNoTelepon());
        } else if (fullUser instanceof PetugasGudang) {
            response.setLokasiPos(((PetugasGudang) fullUser).getLokasiPos());
        } else if (fullUser instanceof Kurir) {
            Kurir kurir = (Kurir) fullUser;
            response.setPlatKendaraan(kurir.getPlatKendaraan());
            response.setStatusKetersediaan(kurir.isStatusKetersediaan());
        }

        sendResponse(exchange, 200, response);
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, Object response) throws IOException {
        String json = JsonUtil.toJson(response);
        setCorsHeaders(exchange);
        exchange.sendResponseHeaders(statusCode, json.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
    }
}