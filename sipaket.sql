-- SI-Paket Database Schema
-- Database: sipaket
-- Created for SI-Paket Expedition System

CREATE DATABASE IF NOT EXISTS sipaket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sipaket;

-- Table: pengguna (base table for all users)
CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna INT AUTO_INCREMENT PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('User', 'PetugasGudang', 'Kurir') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table: user (sub-table for role User)
CREATE TABLE IF NOT EXISTS user (
    id_pengguna INT PRIMARY KEY,
    no_telp VARCHAR(20),
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table: petugas_gudang (sub-table for role PetugasGudang)
CREATE TABLE IF NOT EXISTS petugas_gudang (
    id_pengguna INT PRIMARY KEY,
    lokasi_pos VARCHAR(100),
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table: kurir (sub-table for role Kurir)
CREATE TABLE IF NOT EXISTS kurir (
    id_pengguna INT PRIMARY KEY,
    plat_kendaraan VARCHAR(20),
    status_ketersediaan BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table: paket
CREATE TABLE IF NOT EXISTS paket (
    no_resi VARCHAR(20) PRIMARY KEY,
    id_pengguna INT NOT NULL,
    id_kurir INT NULL,
    nama_penerima VARCHAR(100) NOT NULL,
    alamat_asal TEXT NOT NULL,
    alamat_tujuan TEXT NOT NULL,
    berat_kg DECIMAL(10,2) NOT NULL,
    status_terkini VARCHAR(50) DEFAULT 'Menunggu Penjemputan',
    jenis_paket ENUM('Reguler', 'Express') NOT NULL,
    tarif_dasar DECIMAL(10,2) NOT NULL,
    biaya_prioritas DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE RESTRICT,
    FOREIGN KEY (id_kurir) REFERENCES kurir(id_pengguna) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Table: riwayat_pelacakan
CREATE TABLE IF NOT EXISTS riwayat_pelacakan (
    id_riwayat INT AUTO_INCREMENT PRIMARY KEY,
    no_resi VARCHAR(20) NOT NULL,
    updated_by INT NOT NULL,
    deskripsi_status TEXT NOT NULL,
    lokasi_update VARCHAR(100),
    waktu_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (no_resi) REFERENCES paket(no_resi) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES pengguna(id_pengguna) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Indexes for better query performance
CREATE INDEX idx_paket_id_pengguna ON paket(id_pengguna);
CREATE INDEX idx_paket_id_kurir ON paket(id_kurir);
CREATE INDEX idx_paket_status ON paket(status_terkini);
CREATE INDEX idx_riwayat_no_resi ON riwayat_pelacakan(no_resi);
CREATE INDEX idx_riwayat_waktu ON riwayat_pelacakan(waktu_update);

-- Sample Data for Testing
-- Passwords are plain text for testing (in production should be hashed)
INSERT IGNORE INTO pengguna (id_pengguna, nama_lengkap, email, password, role) VALUES
(1, 'Admin Gudang', 'admin@sipaket.com', 'admin123', 'PetugasGudang'),
(2, 'Kurir Budi', 'budi@sipaket.com', 'kurir123', 'Kurir'),
(3, 'Kurir Siti', 'siti@sipaket.com', 'kurir123', 'Kurir'),
(4, 'User Andi', 'andi@sipaket.com', 'user123', 'User'),
(5, 'User Sari', 'sari@sipaket.com', 'user123', 'User');

INSERT IGNORE INTO petugas_gudang (id_pengguna, lokasi_pos) VALUES
(1, 'DC Cakung');

INSERT IGNORE INTO kurir (id_pengguna, plat_kendaraan, status_ketersediaan) VALUES
(2, 'B 1234 ABC', TRUE),
(3, 'B 5678 DEF', TRUE);

INSERT IGNORE INTO user (id_pengguna, no_telp) VALUES
(4, '081234567890'),
(5, '081987654321');

-- Sample Paket Data
INSERT IGNORE INTO paket (no_resi, id_pengguna, id_kurir, nama_penerima, alamat_asal, alamat_tujuan, berat_kg, status_terkini, jenis_paket, tarif_dasar, biaya_prioritas) VALUES
('RESI-001', 4, 2, 'Budi Santoso', 'Jakarta Pusat', 'Jl. Sudirman No. 10, Surabaya', 2.5, 'Sedang Diantar Kurir', 'Reguler', 15000, 0),
('RESI-002', 5, 3, 'Siti Rahma', 'Bandung', 'Jl. Gatot Subroto No. 25, Medan', 1.0, 'Menunggu Penjemputan', 'Express', 25000, 15000);

-- Sample Riwayat Pelacakan
INSERT IGNORE INTO riwayat_pelacakan (no_resi, updated_by, deskripsi_status, lokasi_update) VALUES
('RESI-001', 1, 'Paket diterima di gudang pengirim', 'DC Cakung'),
('RESI-001', 2, 'Paket diambil oleh kurir untuk pengiriman', 'DC Cakung'),
('RESI-002', 1, 'Paket diterima di gudang pengirim', 'DC Cakung');