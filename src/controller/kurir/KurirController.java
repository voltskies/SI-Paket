package controller.kurir;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.Manajemen;
import dto.ErrorResponse;
import dto.KurirResponse;
import dto.PaketRequest;
import dto.PaketResponse;
import filter.AuthFilter;
import model.Paket.Paket;
import model.Pengguna.Kurir;
import util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class KurirController implements HttpHandler {

    private final Manajemen manajemen = Manajemen.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("/api/kurir".equals(path) && "GET".equals(method)) {
                getKurirTersedia(exchange);
            } else if ("/api/kurir/tugas".equals(path) && "GET".equals(method)) {
                getTugasKurir(exchange);
            } else if ("/api/kurir/ketersediaan".equals(path) && "PATCH".equals(method)) {
                updateKetersediaan(exchange);
            } else if (path.startsWith("/api/kurir/paket/")) {
                handlePaketDetail(exchange, path, method);
            } else {
                sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, new ErrorResponse("Internal server error: " + e.getMessage(), 500));
        }
    }

    public void getKurirTersedia(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null || !"PetugasGudang".equalsIgnoreCase(util.JwtUtil.getRole(claims))) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya PetugasGudang yang dapat mengakses", 403));
            return;
        }

        List<Kurir> kurirList = manajemen.getDaftarKurirTersedia();
        List<KurirResponse> responses = kurirList.stream().map(k -> new KurirResponse(
                k.getIdPengguna(), k.getNamaLengkap(), k.getEmail(), k.getPlatKendaraan(), k.isStatusKetersediaan()
        )).collect(Collectors.toList());
        sendResponse(exchange, 200, responses);
    }

    public void getTugasKurir(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null || !"Kurir".equalsIgnoreCase(util.JwtUtil.getRole(claims))) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya Kurir yang dapat mengakses", 403));
            return;
        }

        int kurirId = util.JwtUtil.getUserId(claims);
        String statusFilter = exchange.getRequestURI().getQuery() != null ?
                java.util.Arrays.stream(exchange.getRequestURI().getQuery().split("&"))
                        .filter(s -> s.startsWith("status="))
                        .map(s -> s.substring(7))
                        .findFirst()
                        .orElse(null) : null;

        List<Paket> paketList = manajemen.getPaketByKurir(kurirId, statusFilter);
        List<PaketResponse> responses = paketList.stream().map(this::paketToResponse).collect(Collectors.toList());
        sendResponse(exchange, 200, responses);
    }

    public void updateKetersediaan(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null || !"Kurir".equalsIgnoreCase(util.JwtUtil.getRole(claims))) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya Kurir yang dapat mengakses", 403));
            return;
        }

        int kurirId = util.JwtUtil.getUserId(claims);
        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);
        if (req == null || req.getStatusKetersediaan() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Field status_ketersediaan wajib diisi", 400));
            return;
        }

        boolean success = manajemen.updateKurirAvailability(kurirId, req.getStatusKetersediaan());
        if (success) {
            sendResponse(exchange, 200, new KurirResponse(kurirId, "Kurir", "", "", req.getStatusKetersediaan()));
        } else {
            sendResponse(exchange, 500, new ErrorResponse("Gagal update ketersediaan", 500));
        }
    }

    private void handlePaketDetail(HttpExchange exchange, String path, String method) throws IOException {
        String[] parts = path.split("/");
        if (parts.length < 5) {
            sendResponse(exchange, 400, new ErrorResponse("Format path tidak valid", 400));
            return;
        }
        String resi = parts[4];
        String action = parts.length > 5 ? parts[5] : "";

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null || !"Kurir".equalsIgnoreCase(util.JwtUtil.getRole(claims))) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya Kurir yang dapat mengakses", 403));
            return;
        }

        int kurirId = util.JwtUtil.getUserId(claims);

        if ("terima".equals(action) && "PATCH".equals(method)) {
            terimaTugas(exchange, resi, kurirId);
        } else if ("lokasi".equals(action) && "POST".equals(method)) {
            updateLokasi(exchange, resi, kurirId);
        } else if ("selesai".equals(action) && "PATCH".equals(method)) {
            selesaikanPengiriman(exchange, resi, kurirId);
        } else {
            sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
        }
    }

    private void terimaTugas(HttpExchange exchange, String resi, int kurirId) throws IOException {
        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);
        String lokasi = req != null ? req.getLokasiUpdate() : "Diambil";
        
        boolean success = manajemen.updateStatusPaket(resi, "Sedang Diantar Kurir", kurirId, lokasi, "Paket diambil oleh kurir");
        if (success) {
            Paket paket = manajemen.cariPaketByResi(resi);
            sendResponse(exchange, 200, paketToResponse(paket));
        } else {
            sendResponse(exchange, 404, new ErrorResponse("Paket tidak ditemukan atau tidak ditugaskan ke kurir ini: " + resi, 404));
        }
    }

    private void updateLokasi(HttpExchange exchange, String resi, int kurirId) throws IOException {
        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);
        if (req == null || req.getLokasiUpdate() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Field lokasi_update wajib diisi", 400));
            return;
        }

        String deskripsi = req.getDeskripsiStatus() != null ? req.getDeskripsiStatus() : "Update lokasi kurir";
        boolean success = manajemen.updateStatusPaket(resi, "Sedang Diantar Kurir", kurirId, req.getLokasiUpdate(), deskripsi);
        if (success) {
            Paket paket = manajemen.cariPaketByResi(resi);
            sendResponse(exchange, 200, paketToResponse(paket));
        } else {
            sendResponse(exchange, 404, new ErrorResponse("Paket tidak ditemukan: " + resi, 404));
        }
    }

    private void selesaikanPengiriman(HttpExchange exchange, String resi, int kurirId) throws IOException {
        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);
        String lokasi = req != null && req.getLokasiUpdate() != null ? req.getLokasiUpdate() : "Tujuan";
        String deskripsi = req != null && req.getDeskripsiStatus() != null ? req.getDeskripsiStatus() : "Paket diterima oleh penerima";

        boolean success = manajemen.updateStatusPaket(resi, "Terkirim", kurirId, lokasi, deskripsi);
        if (success) {
            Paket paket = manajemen.cariPaketByResi(resi);
            sendResponse(exchange, 200, paketToResponse(paket));
        } else {
            sendResponse(exchange, 404, new ErrorResponse("Paket tidak ditemukan: " + resi, 404));
        }
    }

    private PaketResponse paketToResponse(Paket paket) {
        PaketResponse response = new PaketResponse();
        response.setNoResi(paket.getNoResi());
        response.setIdPengguna(paket.getIdPengguna());
        response.setIdKurir(paket.getIdKurir());
        response.setNamaPengirim(paket.getNamaPengirim());
        response.setNamaPenerima(paket.getNamaPenerima());
        response.setAlamatAsal(paket.getAlamatAsal());
        response.setAlamatTujuan(paket.getAlamatTujuan());
        response.setBeratKg(paket.getBeratKg());
        response.setStatusTerkini(paket.getStatusTerkini());
        response.setJenisPaket(paket.getJenisPaket());
        response.setTarifDasar(paket.getTarifDasar());
        response.setBiayaPrioritas(paket.getBiayaPrioritas());
        response.setPosTerkini(paket.getPosTerkini());
        return response;
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
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    }
}