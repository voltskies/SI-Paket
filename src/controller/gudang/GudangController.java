package controller.gudang;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.Manajemen;
import dto.ErrorResponse;
import dto.PaketRequest;
import dto.PaketResponse;
import filter.AuthFilter;
import model.Paket.Paket;
import model.Paket.PaketExpress;
import model.Paket.PaketReguler;
import util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class GudangController implements HttpHandler {

    private final Manajemen manajemen = Manajemen.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("OPTIONS".equals(method)) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("/api/gudang/paket".equals(path)) {
                if ("GET".equals(method)) {
                    getAllPaket(exchange);
                } else if ("POST".equals(method)) {
                    createPaket(exchange);
                } else {
                    sendResponse(exchange, 405, new ErrorResponse("Method tidak diizinkan", 405));
                }
            } else if (path.startsWith("/api/gudang/paket/")) {
                handleDetail(exchange, path);
            } else {
                sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, new ErrorResponse("Internal server error: " + e.getMessage(), 500));
        }
    }

    private void handleDetail(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length < 5) {
            sendResponse(exchange, 400, new ErrorResponse("Format path tidak valid", 400));
            return;
        }
        String resi = parts[4];
        String action = parts.length > 5 ? parts[5] : "";
        String method = exchange.getRequestMethod();

        // Check auth for all
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null || !"PetugasGudang".equalsIgnoreCase(util.JwtUtil.getRole(claims))) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya PetugasGudang yang dapat mengakses", 403));
            return;
        }

        int userId = util.JwtUtil.getUserId(claims);

        if ("transit".equals(action) && "PATCH".equals(method)) {
            updateTransit(exchange, resi, userId);
        } else if ("assign-kurir".equals(action) && "PATCH".equals(method)) {
            assignKurir(exchange, resi);
        } else {
            sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
        }
    }

    public void getAllPaket(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null || !"PetugasGudang".equalsIgnoreCase(util.JwtUtil.getRole(claims))) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya PetugasGudang yang dapat mengakses", 403));
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String statusFilter = null;
        String jenisFilter = null;
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("status=")) statusFilter = param.substring(7);
                if (param.startsWith("jenis_paket=")) jenisFilter = param.substring(12);
            }
        }

        List<Paket> paketList = manajemen.findAllPaket(statusFilter, jenisFilter);
        List<PaketResponse> responses = paketList.stream().map(this::paketToResponse).collect(Collectors.toList());
        sendResponse(exchange, 200, responses);
    }

    public void createPaket(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null || !"PetugasGudang".equalsIgnoreCase(util.JwtUtil.getRole(claims))) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya PetugasGudang yang dapat mengakses", 403));
            return;
        }

        int userId = util.JwtUtil.getUserId(claims);
        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);

        if (req == null || req.getNamaPenerima() == null || req.getAlamatAsal() == null ||
            req.getAlamatTujuan() == null || req.getBeratKg() <= 0 || req.getIdPengguna() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Semua field wajib diisi: id_pengguna, nama_penerima, alamat_asal, alamat_tujuan, berat_kg", 400));
            return;
        }

        String noResi = "RESI-" + System.currentTimeMillis();
        Paket paket;
        if ("Express".equalsIgnoreCase(req.getJenisPaket())) {
            paket = new PaketExpress();
        } else {
            paket = new PaketReguler();
        }

        paket.setIdPengguna(req.getIdPengguna());
        paket.setNoResi(noResi);
        paket.setNamaPengirim("Pengguna_" + req.getIdPengguna());
        paket.setNamaPenerima(req.getNamaPenerima());
        paket.setAlamatAsal(req.getAlamatAsal());
        paket.setAlamatTujuan(req.getAlamatTujuan());
        paket.setBeratKg(req.getBeratKg());
        paket.setJenisPaket(req.getJenisPaket());
        paket.setTarifDasar(paket.getTarifDasar());
        paket.setBiayaPrioritas(paket instanceof PaketExpress ? ((PaketExpress) paket).getBiayaPrioritas() : 0);

        if (req.getIdKurir() != null) {
            paket.setIdKurir(req.getIdKurir());
        }

        if (manajemen.tambahPaket(paket)) {
            sendResponse(exchange, 201, paketToResponse(paket));
        } else {
            sendResponse(exchange, 500, new ErrorResponse("Gagal membuat paket", 500));
        }
    }

    private void updateTransit(HttpExchange exchange, String resi, int userId) throws IOException {
        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);
        if (req == null || req.getStatusTerkini() == null || req.getPosTerkini() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Field status, lokasi_update, deskripsi_status wajib diisi", 400));
            return;
        }

        boolean success = manajemen.updatePosTransit(resi, req.getPosTerkini(), userId);
        if (success) {
            Paket paket = manajemen.cariPaketByResi(resi);
            sendResponse(exchange, 200, paketToResponse(paket));
        } else {
            sendResponse(exchange, 404, new ErrorResponse("Paket tidak ditemukan: " + resi, 404));
        }
    }

    private void assignKurir(HttpExchange exchange, String resi) throws IOException {
        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);
        if (req == null || req.getIdKurir() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Field id_kurir wajib diisi", 400));
            return;
        }

        boolean success = manajemen.assignKurir(resi, req.getIdKurir());
        if (success) {
            Paket paket = manajemen.cariPaketByResi(resi);
            sendResponse(exchange, 200, paketToResponse(paket));
        } else {
            sendResponse(exchange, 404, new ErrorResponse("Paket tidak ditemukan atau kurir tidak valid: " + resi, 404));
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
        controller.AppServer.setCorsHeaders(exchange);
    }
}