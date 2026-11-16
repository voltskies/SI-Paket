package dao;

import model.Layanan.RiwayatPelacakan;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RiwayatPelacakanDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<RiwayatPelacakan> findByResi(String noResi) {
        List<RiwayatPelacakan> riwayatList = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null) return riwayatList;
        
        String sql = "SELECT * FROM riwayat_pelacakan WHERE no_resi = ? ORDER BY waktu_update DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, noResi);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RiwayatPelacakan riwayat = mapToRiwayat(rs);
                riwayatList.add(riwayat);
            }
        } catch (SQLException e) {
            System.err.println("[RiwayatPelacakanDAO] Error findByResi: " + e.getMessage());
        }
        return riwayatList;
    }

    public boolean insert(RiwayatPelacakan riwayat) {
        Connection conn = getConn();
        if (conn == null) return false;
        
        String sql = "INSERT INTO riwayat_pelacakan (no_resi, updated_by, deskripsi_status, lokasi_update) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, riwayat.getNoResi());
            stmt.setInt(2, riwayat.getUpdatedBy());
            stmt.setString(3, riwayat.getDeskripsiStatus());
            stmt.setString(4, riwayat.getLokasiUpdate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RiwayatPelacakanDAO] Error insert: " + e.getMessage());
        }
        return false;
    }

    public List<RiwayatPelacakan> findBetween(LocalDateTime start, LocalDateTime end) {
        List<RiwayatPelacakan> riwayatList = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null) return riwayatList;
        
        String sql = "SELECT * FROM riwayat_pelacakan WHERE waktu_update BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                riwayatList.add(mapToRiwayat(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RiwayatPelacakanDAO] Error findBetween: " + e.getMessage());
        }
        return riwayatList;
    }

    public boolean insertTrackingLog(String noResi, int updatedBy, String deskripsi, String lokasi) {
        RiwayatPelacakan riwayat = new RiwayatPelacakan(noResi, updatedBy, deskripsi, lokasi);
        return insert(riwayat);
    }

    private RiwayatPelacakan mapToRiwayat(ResultSet rs) throws SQLException {
        RiwayatPelacakan riwayat = new RiwayatPelacakan();
        riwayat.setIdRiwayat(rs.getInt("id_riwayat"));
        riwayat.setNoResi(rs.getString("no_resi"));
        riwayat.setUpdatedBy(rs.getInt("updated_by"));
        riwayat.setDeskripsiStatus(rs.getString("deskripsi_status"));
        riwayat.setLokasiUpdate(rs.getString("lokasi_update"));

        Timestamp ts = rs.getTimestamp("waktu_update");
        if (ts != null) {
            riwayat.setWaktuUpdate(ts.toLocalDateTime());
        }
        return riwayat;
    }
}