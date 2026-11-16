package controller.paket;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.Manajemen;
import dto.CekOngkirRequest;
import dto.CekOngkirResponse;
import dto.ErrorResponse;
import dto.PaketResponse;
import filter.AuthFilter;
import model.Paket.Paket;
import model.Paket.PaketExpress;
import model.Paket.PaketReguler;
import util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PaketController implements HttpHandler {

    private final Manajemen manajemen = Manajemen.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "POST":
                    if ("/api/paket/cek-ongkir".equals(path)) {
                        cekOngkir(exchange);
                    } else if ("/api/paket".equals(path)) {
                        createPaket(exchange);
                    } else {
                        sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
                    }
                    break;
                case "GET":
                    if ("/api/paket/saya".equals(path)) {
                        getPaketSaya(exchange);
                    } else if (path.startsWith("/api/paket/")) {
                        getPaketByResi(exchange);
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

    public void cekOngkir(HttpExchange exchange) throws IOException {
        CekOngkirRequest req = JsonUtil.fromJson(readBody(exchange), CekOngkirRequest.class);
        if (req == null || req.getBeratKg() <= 0 || req.getJenisPaket() == null) {
            sendResponse(exchange, 400, new ErrorResponse("Berat kg dan jenis paket wajib diisi", 400));
            return;
        }

        CekOngkirResponse response = new CekOngkirResponse();
        if ("Express".equalsIgnoreCase(req.getJenisPaket())) {
            PaketExpress express = new PaketExpress();
            express.setBeratKg(req.getBeratKg());
            response.setTarifDasar(express.getTarifDasar());
            response.setBiayaPrioritas(express.getBiayaPrioritas());
            response.setTotalBiaya(express.hitungOngkir());
            response.setEstimasiHari(express.getEstimasiHari());
            response.setDetailLayanan("Express");
        } else {
            PaketReguler reguler = new PaketReguler();
            reguler.setBeratKg(req.getBeratKg());
            response.setTarifDasar(reguler.getTarifDasar());
            response.setBiayaPrioritas(0);
            response.setTotalBiaya(reguler.hitungOngkir());
            response.setEstimasiHari(reguler.getEstimasiHari());
            response.setDetailLayanan("Reguler");
        }

        sendResponse(exchange, 200, response);
    }

    public void createPaket(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null) {
            sendResponse(exchange, 401, new ErrorResponse("Token tidak valid atau tidak ada", 401));
            return;
        }

        int userId = util.JwtUtil.getUserId(claims);
        String role = util.JwtUtil.getRole(claims);
        if (!"User".equalsIgnoreCase(role)) {
            sendResponse(exchange, 403, new ErrorResponse("Hanya User yang dapat membuat paket", 403));
            return;
        }

        dto.PaketRequest req = JsonUtil.fromJson(readBody(exchange), dto.PaketRequest.class);
        if (req == null || req.getNamaPenerima() == null || req.getAlamatAsal() == null ||
            req.getAlamatTujuan() == null || req.getBeratKg() <= 0) {
            sendResponse(exchange, 400, new ErrorResponse("Semua field wajib diisi kecuali id_kurir", 400));
            return;
        }

        String noResi = "RESI-" + System.currentTimeMillis();
        Paket paket;
        if ("Express".equalsIgnoreCase(req.getJenisPaket())) {
            paket = new PaketExpress();
        } else {
            paket = new PaketReguler();
        }

        paket.setIdPengguna(userId);
        paket.setNoResi(noResi);
        paket.setNamaPengirim("Pengguna_" + userId);
        paket.setNamaPenerima(req.getNamaPenerima());
        paket.setAlamatAsal(req.getAlamatAsal());
        paket.setAlamatTujuan(req.getAlamatTujuan());
        paket.setBeratKg(req.getBeratKg());
        paket.setJenisPaket(req.getJenisPaket());
        paket.setTarifDasar(paket.getTarifDasar());
        paket.setBiayaPrioritas(paket instanceof PaketExpress ? ((PaketExpress) paket).getBiayaPrioritas() : 0);

        if (manajemen.tambahPaket(paket)) {
            sendResponse(exchange, 201, paketToResponse(paket));
        } else {
            sendResponse(exchange, 500, new ErrorResponse("Gagal membuat paket", 500));
        }
    }

    public void getPaketSaya(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null) {
            sendResponse(exchange, 401, new ErrorResponse("Token tidak valid atau tidak ada", 401));
            return;
        }

        int userId = util.JwtUtil.getUserId(claims);
        String statusFilter = exchange.getRequestURI().getQuery() != null ?
                java.util.Arrays.stream(exchange.getRequestURI().getQuery().split("&"))
                        .filter(s -> s.startsWith("status="))
                        .map(s -> s.substring(7))
                        .findFirst()
                        .orElse(null) : null;

        java.util.List<Paket> paketList = manajemen.findByPengguna(userId, statusFilter);
        java.util.List<PaketResponse> responses = new java.util.ArrayList<>();
        for (Paket p : paketList) {
            responses.add(paketToResponse(p));
        }

        sendResponse(exchange, 200, responses);
    }

    public void getPaketByResi(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        io.jsonwebtoken.Claims claims = filter.AuthFilter.extractClaims(authHeader);
        if (claims == null) {
            sendResponse(exchange, 401, new ErrorResponse("Token tidak valid atau tidak ada", 401));
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String resi = path.substring(path.lastIndexOf('/') + 1);
        Paket paket = manajemen.cariPaketByResi(resi);
        if (paket != null) {
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

        if (paket.getKurirPengantar() != null) {
            PaketResponse.KurirResponse kr = new PaketResponse.KurirResponse();
            kr.setIdPengguna(paket.getKurirPengantar().getIdPengguna());
            kr.setNamaLengkap(paket.getKurirPengantar().getNamaLengkap());
            kr.setPlatKendaraan(paket.getKurirPengantar().getPlatKendaraan());
            kr.setStatusKetersediaan(paket.getKurirPengantar().isStatusKetersediaan());
            response.setKurirPengantar(kr);
        }

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