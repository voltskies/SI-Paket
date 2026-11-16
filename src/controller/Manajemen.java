package controller;

import dao.PenggunaDAO;
import dao.PaketDAO;
import dao.RiwayatPelacakanDAO;
import model.Paket.Paket;
import model.Paket.PaketReguler;
import model.Paket.PaketExpress;
import model.Pengguna.Pengguna;
import model.Layanan.RiwayatPelacakan;

import java.util.ArrayList;
import java.util.List;

public class Manajemen {
    private static Manajemen instance;
    private PenggunaDAO penggunaDAO;
    private PaketDAO paketDAO;
    private RiwayatPelacakanDAO riwayatDAO;

    public Manajemen() {
        this.penggunaDAO = new PenggunaDAO();
        this.paketDAO = new PaketDAO();
        this.riwayatDAO = new RiwayatPelacakanDAO();
    }

    public static synchronized Manajemen getInstance() {
        if (instance == null) {
            instance = new Manajemen();
        }
        return instance;
    }

    // 1. AUTENTIKASI & PENGGUNA
    public Pengguna login(String email, String password) {
        Pengguna p = penggunaDAO.authenticate(email, password);
        if (p != null) {
            return penggunaDAO.findWithRole(p.getIdPengguna());
        }
        return null;
    }

    public int register(Pengguna pengguna) {
        return penggunaDAO.register(pengguna);
    }

    public Pengguna findById(int id) {
        return penggunaDAO.findById(id);
    }

    public Pengguna findWithRole(int id) {
        return penggunaDAO.findWithRole(id);
    }

    public List<model.Pengguna.Kurir> getDaftarKurir() {
        List<Pengguna> list = penggunaDAO.findAllKurir(false);
        List<model.Pengguna.Kurir> kurirList = new ArrayList<>();
        for (Pengguna p : list) {
            if (p instanceof model.Pengguna.Kurir) {
                kurirList.add((model.Pengguna.Kurir) p);
            }
        }
        return kurirList;
    }

    public List<model.Pengguna.Kurir> getDaftarKurirTersedia() {
        List<Pengguna> list = penggunaDAO.findAllKurir(true);
        List<model.Pengguna.Kurir> kurirList = new ArrayList<>();
        for (Pengguna p : list) {
            if (p instanceof model.Pengguna.Kurir) {
                kurirList.add((model.Pengguna.Kurir) p);
            }
        }
        return kurirList;
    }

    public boolean updateKurirAvailability(int idKurir, boolean status) {
        return penggunaDAO.updateKurirAvailability(idKurir, status);
    }

    // 2. MANAJEMEN PAKET
    public Paket cariPaketByResi(String noResi) {
        return paketDAO.findByResi(noResi);
    }

    public List<Paket> getDaftarPaket() {
        return paketDAO.findAll(null, null);
    }

    public List<Paket> findAllPaket(String statusFilter, String jenisFilter) {
        return paketDAO.findAll(statusFilter, jenisFilter);
    }

    public List<Paket> findByPengguna(int idPengguna, String statusFilter) {
        List<Paket> list = paketDAO.findByPenggunaId(idPengguna);
        if (statusFilter != null && !statusFilter.isEmpty()) {
            List<Paket> filtered = new ArrayList<>();
            for (Paket p : list) {
                if (p.getStatusTerkini().equalsIgnoreCase(statusFilter)) {
                    filtered.add(p);
                }
            }
            return filtered;
        }
        return list;
    }

    public List<Paket> getPaketByKurir(int idKurir, String statusFilter) {
        return paketDAO.findByKurirId(idKurir, statusFilter);
    }

    public boolean tambahPaket(Paket paket) {
        return paketDAO.insert(paket);
    }

    public boolean updateStatusPaket(String noResi, String status, int idUser, String lokasi, String deskripsi) {
        boolean updated = paketDAO.updateStatus(noResi, status);
        if (updated) {
            riwayatDAO.insertTrackingLog(noResi, idUser, deskripsi, lokasi);
            return true;
        }
        return false;
    }

    public boolean assignKurir(String noResi, int idKurir) {
        return paketDAO.updateKurir(noResi, idKurir);
    }

    public boolean updatePosTransit(String noResi, String posBaru, int idPetugas) {
        boolean updated = paketDAO.updatePosTransit(noResi, posBaru);
        if (updated) {
            riwayatDAO.insertTrackingLog(noResi, idPetugas, "Paket transit di " + posBaru, posBaru);
            return true;
        }
        return false;
    }

    public boolean hapusPaket(String noResi) {
        return paketDAO.delete(noResi);
    }

    // Hitung Ongkir tanpa simpan DB
    public double hitungOngkirSimulasi(double beratKg, String jenisPaket) {
        if ("Express".equalsIgnoreCase(jenisPaket)) {
            PaketExpress pe = new PaketExpress();
            pe.setBeratKg(beratKg);
            return pe.hitungOngkir();
        } else {
            PaketReguler pr = new PaketReguler();
            pr.setBeratKg(beratKg);
            return pr.hitungOngkir();
        }
    }
}