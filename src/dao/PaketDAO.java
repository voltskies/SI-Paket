package dao;

import model.Layanan.RiwayatPelacakan;
import model.Paket.Paket;
import model.Paket.PaketReguler;
import model.Paket.PaketExpress;
import model.Pengguna.Kurir;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaketDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Paket findByResi(String noResi) {
        Connection conn = getConn();
        if (conn == null) return null;
        
        String sql = "SELECT p.*, " +
                     "ku.plat_kendaraan, ku.status_ketersediaan " +
                     "FROM paket p " +
                     "LEFT JOIN kurir ku ON p.id_kurir = ku.id_pengguna " +
                     "WHERE p.no_resi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, noResi);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Paket paket = mapToPaket(rs);
                paket.setRiwayatPelacakan(findRiwayatByResi(noResi));
                return paket;
            }
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error findByResi: " + e.getMessage());
        }
        return null;
    }

    public List<Paket> findByPenggunaId(int idPengguna) {
        List<Paket> paketList = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null) return paketList;
        
        String sql = "SELECT p.*, " +
                     "ku.plat_kendaraan, ku.status_ketersediaan " +
                     "FROM paket p " +
                     "LEFT JOIN kurir ku ON p.id_kurir = ku.id_pengguna " +
                     "WHERE p.id_pengguna = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPengguna);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Paket paket = mapToPaket(rs);
                paket.setRiwayatPelacakan(findRiwayatByResi(paket.getNoResi()));
                paketList.add(paket);
            }
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error findByPenggunaId: " + e.getMessage());
        }
        return paketList;
    }

    public List<Paket> findByKurirId(int idKurir, String statusFilter) {
        List<Paket> paketList = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null) return paketList;
        
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, " +
            "ku.plat_kendaraan, ku.status_ketersediaan " +
            "FROM paket p " +
            "JOIN kurir ku ON p.id_kurir = ku.id_pengguna " +
            "WHERE p.id_kurir = ?"
        );
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sql.append(" AND p.status_terkini = ?");
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setInt(1, idKurir);
            int paramIndex = 2;
            if (statusFilter != null && !statusFilter.isEmpty()) {
                stmt.setString(2, statusFilter);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Paket paket = mapToPaket(rs);
                paket.setRiwayatPelacakan(findRiwayatByResi(paket.getNoResi()));
                paketList.add(paket);
            }
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error findByKurirId: " + e.getMessage());
        }
        return paketList;
    }

    public List<Paket> findAll(String statusFilter, String jenisFilter) {
        List<Paket> paketList = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null) return paketList;
        
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, " +
            "ku.plat_kendaraan, ku.status_ketersediaan " +
            "FROM paket p " +
            "LEFT JOIN kurir ku ON p.id_kurir = ku.id_pengguna " +
            "WHERE 1=1"
        );
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sql.append(" AND p.status_terkini = ?");
        }
        if (jenisFilter != null && !jenisFilter.isEmpty()) {
            sql.append(" AND p.jenis_paket = ?");
        }
        sql.append(" ORDER BY p.created_at DESC");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (statusFilter != null && !statusFilter.isEmpty()) {
                stmt.setString(paramIndex++, statusFilter);
            }
            if (jenisFilter != null && !jenisFilter.isEmpty()) {
                stmt.setString(paramIndex, jenisFilter);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Paket paket = mapToPaket(rs);
                paket.setRiwayatPelacakan(findRiwayatByResi(paket.getNoResi()));
                paketList.add(paket);
            }
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error findAll: " + e.getMessage());
        }
        return paketList;
    }

    public boolean insert(Paket paket) {
        Connection conn = getConn();
        if (conn == null) return false;
        
        String sql = "INSERT INTO paket (no_resi, id_pengguna, id_kurir, nama_penerima, alamat_asal, alamat_tujuan, " +
                     "berat_kg, status_terkini, jenis_paket, tarif_dasar, biaya_prioritas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paket.getNoResi());
            stmt.setInt(2, paket.getIdPengguna());
            stmt.setObject(3, paket.getIdKurir());
            stmt.setString(4, paket.getNamaPenerima());
            stmt.setString(5, paket.getAlamatAsal());
            stmt.setString(6, paket.getAlamatTujuan());
            stmt.setDouble(7, paket.getBeratKg());
            stmt.setString(8, paket.getStatusTerkini());
            stmt.setString(9, paket.getJenisPaket());
            stmt.setDouble(10, paket.getTarifDasar());
            stmt.setDouble(11, paket.getBiayaPrioritas());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                RiwayatPelacakan riwayat = new RiwayatPelacakan(
                    paket.getNoResi(),
                    paket.getIdPengguna(),
                    "Paket diterima di gudang pengirim",
                    paket.getAlamatAsal()
                );
                new RiwayatPelacakanDAO().insert(riwayat);
            }
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error insert: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStatus(String noResi, String status) {
        Connection conn = getConn();
        if (conn == null) return false;
        
        String sql = "UPDATE paket SET status_terkini = ? WHERE no_resi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, noResi);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error updateStatus: " + e.getMessage());
        }
        return false;
    }

    public boolean updateKurir(String noResi, int idKurir) {
        Connection conn = getConn();
        if (conn == null) return false;
        
        String sql = "UPDATE paket SET id_kurir = ? WHERE no_resi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idKurir);
            stmt.setString(2, noResi);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error updateKurir: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePosTransit(String noResi, String posBaru) {
        Connection conn = getConn();
        if (conn == null) return false;
        
        String sql = "UPDATE paket SET status_terkini = ? WHERE no_resi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "Transit ke " + posBaru);
            stmt.setString(2, noResi);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error updatePosTransit: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String noResi) {
        Connection conn = getConn();
        if (conn == null) return false;
        
        String sql = "DELETE FROM paket WHERE no_resi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, noResi);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PaketDAO] Error delete: " + e.getMessage());
        }
        return false;
    }

    private Paket mapToPaket(ResultSet rs) throws SQLException {
        String jenis = rs.getString("jenis_paket");
        Paket paket;

        if ("Express".equals(jenis)) {
            paket = new PaketExpress();
        } else {
            paket = new PaketReguler();
        }

        paket.setNoResi(rs.getString("no_resi"));
        paket.setIdPengguna(rs.getInt("id_pengguna"));
        paket.setIdKurir(rs.getObject("id_kurir") != null ? rs.getInt("id_kurir") : null);
        paket.setNamaPenerima(rs.getString("nama_penerima"));
        paket.setAlamatAsal(rs.getString("alamat_asal"));
        paket.setAlamatTujuan(rs.getString("alamat_tujuan"));
        paket.setBeratKg(rs.getDouble("berat_kg"));
        paket.setStatusTerkini(rs.getString("status_terkini"));
        paket.setJenisPaket(jenis);
        paket.setTarifDasar(rs.getDouble("tarif_dasar"));
        paket.setBiayaPrioritas(rs.getDouble("biaya_prioritas"));
        paket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        // Set kurir if available
        String platKendaraan = rs.getString("plat_kendaraan");
        if (platKendaraan != null) {
            Kurir kurir = new Kurir();
            kurir.setIdPengguna(rs.getInt("id_kurir"));
            kurir.setPlatKendaraan(platKendaraan);
            kurir.setStatusKetersediaan(rs.getBoolean("status_ketersediaan"));
            paket.setKurirPengantar(kurir);
        }

        return paket;
    }

    public List<RiwayatPelacakan> findRiwayatByResi(String noResi) {
        return new RiwayatPelacakanDAO().findByResi(noResi);
    }
}