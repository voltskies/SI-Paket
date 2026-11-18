package controller.tracking;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.Manajemen;
import dto.ErrorResponse;
import dto.TrackingResponse;
import model.Layanan.RiwayatPelacakan;
import model.Paket.Paket;
import util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TrackingController implements HttpHandler {

    private final Manajemen manajemen = Manajemen.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (path.startsWith("/api/tracking/") && "GET".equals(method)) {
                getTracking(exchange);
            } else if ("OPTIONS".equals(method)) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
            } else {
                sendResponse(exchange, 404, new ErrorResponse("Endpoint tidak ditemukan", 404));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, new ErrorResponse("Internal server error: " + e.getMessage(), 500));
        }
    }

    public void getTracking(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String resi = path.substring(path.lastIndexOf('/') + 1);

        Paket paket = manajemen.cariPaketByResi(resi);
        if (paket == null) {
            sendResponse(exchange, 404, new ErrorResponse("Paket tidak ditemukan: " + resi, 404));
            return;
        }

        TrackingResponse response = new TrackingResponse();
        response.setNoResi(paket.getNoResi());
        response.setNamaPenerima(paket.getNamaPenerima());
        response.setStatusTerkini(paket.getStatusTerkini());
        response.setPosTerkini(paket.getPosTerkini());
        response.setEstimasiHari(paket.getEstimasiHari());

        List<RiwayatPelacakan> riwayatList = paket.getRiwayatPelacakan();
        if (riwayatList != null) {
            List<TrackingResponse.RiwayatItem> riwayatItems = riwayatList.stream().map(r -> {
                TrackingResponse.RiwayatItem item = new TrackingResponse.RiwayatItem();
                item.setWaktuUpdate(r.getWaktuUpdate() != null ? r.getWaktuUpdate().toString() : "");
                item.setLokasiUpdate(r.getLokasiUpdate());
                item.setDeskripsiStatus(r.getDeskripsiStatus());
                return item;
            }).collect(Collectors.toList());
            response.setRiwayat(riwayatItems);
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
        controller.AppServer.setCorsHeaders(exchange);
    }
}