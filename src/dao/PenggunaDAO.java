package dao;

import model.Pengguna.Pengguna;
import model.Pengguna.User;
import model.Pengguna.PetugasGudang;
import model.Pengguna.Kurir;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenggunaDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Pengguna findByEmail(String email) {
        Connection conn = getConn();
        if (conn == null) return null;
        
        String sql = "SELECT * FROM pengguna WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToPengguna(rs);
            }
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Error findByEmail: " + e.getMessage());
        }
        return null;
    }

    public Pengguna findById(int id) {
        Connection conn = getConn();
        if (conn == null) return null;
        
        String sql = "SELECT * FROM pengguna WHERE id_pengguna = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToPengguna(rs);
            }
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Error findById: " + e.getMessage());
        }
        return null;
    }

    public Pengguna findWithRole(int idPengguna) {
        Connection conn = getConn();
        if (conn == null) return null;
        
        String sql = "SELECT p.*, " +
                     "u.no_telp, " +
                     "pg.lokasi_pos, " +
                     "k.plat_kendaraan, k.status_ketersediaan " +
                     "FROM pengguna p " +
                     "LEFT JOIN user u ON p.id_pengguna = u.id_pengguna " +
                     "LEFT JOIN petugas_gudang pg ON p.id_pengguna = pg.id_pengguna " +
                     "LEFT JOIN kurir k ON p.id_pengguna = k.id_pengguna " +
                     "WHERE p.id_pengguna = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPengguna);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToPenggunaWithRole(rs);
            }
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Error findWithRole: " + e.getMessage());
        }
        return null;
    }

    public List<Pengguna> findAllKurir(boolean tersediaOnly) {
        List<Pengguna> kurirList = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null) return kurirList;
        
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, k.plat_kendaraan, k.status_ketersediaan " +
            "FROM pengguna p " +
            "JOIN kurir k ON p.id_pengguna = k.id_pengguna "
        );
        if (tersediaOnly) {
            sql.append("WHERE k.status_ketersediaan = TRUE ");
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString());
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                kurirList.add(mapToPenggunaWithRole(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Error findAllKurir: " + e.getMessage());
        }
        return kurirList;
    }

    public int register(Pengguna pengguna) {
        Connection conn = getConn();
        if (conn == null) return -1;
        
        String sqlPengguna = "INSERT INTO pengguna (nama_lengkap, email, password, role) VALUES (?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlPengguna, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, pengguna.getNamaLengkap());
                stmt.setString(2, pengguna.getEmail());
                stmt.setString(3, pengguna.getPassword());
                stmt.setString(4, pengguna.getRole());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int idPengguna = rs.getInt(1);
                    pengguna.setIdPengguna(idPengguna);

                    insertSubTable(conn, pengguna);
                    conn.commit();
                    return idPengguna;
                }
            }
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Error register: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { }
            }
        }
        return -1;
    }

    private void insertSubTable(Connection conn, Pengguna pengguna) throws SQLException {
        String role = pengguna.getRole();
        int id = pengguna.getIdPengguna();

        if ("User".equals(role) && pengguna instanceof User) {
            User user = (User) pengguna;
            String sql = "INSERT INTO user (id_pengguna, no_telp) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, user.getNoTelepon());
                stmt.executeUpdate();
            }
        } else if ("PetugasGudang".equals(role) && pengguna instanceof PetugasGudang) {
            PetugasGudang pg = (PetugasGudang) pengguna;
            String sql = "INSERT INTO petugas_gudang (id_pengguna, lokasi_pos) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, pg.getLokasiPos());
                stmt.executeUpdate();
            }
        } else if ("Kurir".equals(role) && pengguna instanceof Kurir) {
            Kurir kurir = (Kurir) pengguna;
            String sql = "INSERT INTO kurir (id_pengguna, plat_kendaraan, status_ketersediaan) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, kurir.getPlatKendaraan());
                stmt.setBoolean(3, kurir.isStatusKetersediaan());
                stmt.executeUpdate();
            }
        }
    }

    public boolean updateKurirAvailability(int idKurir, boolean status) {
        Connection conn = getConn();
        if (conn == null) return false;
        
        String sql = "UPDATE kurir SET status_ketersediaan = ? WHERE id_pengguna = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, idKurir);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Error updateKurirAvailability: " + e.getMessage());
        }
        return false;
    }

    private Pengguna mapToPengguna(ResultSet rs) throws SQLException {
        Pengguna p;
        String role = rs.getString("role");
        int id = rs.getInt("id_pengguna");

        switch (role) {
            case "User":
                p = new User();
                break;
            case "PetugasGudang":
                p = new PetugasGudang();
                break;
            case "Kurir":
                p = new Kurir();
                break;
            default:
                p = new Pengguna() {
                    @Override
                    public String getAksesRole() {
                        return role;
                    }
                };
        }
        p.setIdPengguna(id);
        p.setNamaLengkap(rs.getString("nama_lengkap"));
        p.setEmail(rs.getString("email"));
        p.setPassword(rs.getString("password"));
        p.setRole(role);
        return p;
    }

    private Pengguna mapToPenggunaWithRole(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        Pengguna p;

        if ("User".equals(role)) {
            User user = new User();
            user.setNoTelepon(rs.getString("no_telp"));
            p = user;
        } else if ("PetugasGudang".equals(role)) {
            PetugasGudang pg = new PetugasGudang();
            pg.setLokasiPos(rs.getString("lokasi_pos"));
            p = pg;
        } else if ("Kurir".equals(role)) {
            Kurir kurir = new Kurir();
            kurir.setPlatKendaraan(rs.getString("plat_kendaraan"));
            kurir.setStatusKetersediaan(rs.getBoolean("status_ketersediaan"));
            p = kurir;
        } else {
            p = new Pengguna() {
                @Override
                public String getAksesRole() {
                    return role;
                }
            };
        }

        p.setIdPengguna(rs.getInt("id_pengguna"));
        p.setNamaLengkap(rs.getString("nama_lengkap"));
        p.setEmail(rs.getString("email"));
        p.setPassword(rs.getString("password"));
        p.setRole(role);

        return p;
    }
}