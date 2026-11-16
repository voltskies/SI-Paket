package controller;

import dao.PenggunaDAO;
import dao.PaketDAO;
import dao.RiwayatPelacakanDAO;
import model.Paket.Paket;
import model.Paket.PaketReguler;
import model.Paket.PaketExpress;
import model.Pengguna.Pengguna;
import model.Pengguna.User;
import model.Pengguna.PetugasGudang;
import model.Pengguna.Kurir;
import model.Layanan.RiwayatPelacakan;

import java.util.ArrayList;
import java.util.List;

public class Manajemen {
    private static Manajemen instance;
    private PenggunaDAO penggunaDAO;
    private PaketDAO paketDAO;
    private RiwayatPelacakanDAO riwayatDAO;

    // Fallback/in-memory list if database is not available
    private List<Paket> inMemoryPaketList;
    private List<Kurir> inMemoryKurirList;
    private boolean useDatabase = true;

    public Manajemen() {
        this.penggunaDAO = new PenggunaDAO();
        this.paketDAO = new PaketDAO();
        this.riwayatDAO = new RiwayatPelacakanDAO();
        
        this.inMemoryPaketList = new ArrayList<>();
        this.inMemoryKurirList = new ArrayList<>();

        initDummyDataIfDBFails();
    }

    public static synchronized Manajemen getInstance() {
        if (instance == null) {
            instance = new Manajemen();
        }
        return instance;
    }

    private void initDummyDataIfDBFails() {
        // Prepare dummy data in case DB is unreachable
        Kurir k1 = new Kurir(2, "Kurir Budi", "budi@sipaket.com", "kurir123", "B 1234 ABC", true);
        Kurir k2 = new Kurir(3, "Kurir Siti", "siti@sipaket.com", "kurir123", "B 5678 DEF", true);
        inMemoryKurirList.add(k1);
        inMemoryKurirList.add(k2);

        Paket p1 = new PaketReguler("RESI-001", "Andi", "Budi Santoso", "Jl. Sudirman No. 10, Surabaya", 2.5);
        p1.setKurirPengantar(k1);
        p1.setPosTerkini("DC Cakung");
        p1.tambahRiwayat(new RiwayatPelacakan("RESI-001", 1, "Paket diterima di gudang pengirim", "DC Cakung"));

        Paket p2 = new PaketExpress("RESI-002", "Sari", "Siti Rahma", "Jl. Gatot Subroto No. 25, Medan", 1.0);
        p2.setKurirPengantar(k2);
        p2.setPosTerkini("Hub Bandung");
        p2.tambahRiwayat(new RiwayatPelacakan("RESI-002", 1, "Paket diterima di gudang pengirim", "Hub Bandung"));

        inMemoryPaketList.add(p1);
        inMemoryPaketList.add(p2);
    }

    // 1. AUTENTIKASI & PENGGUNA
    public Pengguna login(String email, String password) {
        if (useDatabase) {
            Pengguna p = penggunaDAO.findByEmail(email);
            if (p != null && p.login(email, password)) {
                return penggunaDAO.findWithRole(p.getIdPengguna());
            }
        }
        return null;
    }

    public int register(Pengguna pengguna) {
        if (useDatabase) {
            return penggunaDAO.register(pengguna);
        }
        return -1;
    }

    public List<Kurir> getDaftarKurir() {
        if (useDatabase) {
            List<Pengguna> list = penggunaDAO.findAllKurir(false);
            List<Kurir> kurirList = new ArrayList<>();
            for (Pengguna p : list) {
                if (p instanceof Kurir) {
                    kurirList.add((Kurir) p);
                }
            }
            if (!kurirList.isEmpty()) {
                return kurirList;
            }
        }
        return inMemoryKurirList;
    }

    public List<Kurir> getDaftarKurirTersedia() {
        if (useDatabase) {
            List<Pengguna> list = penggunaDAO.findAllKurir(true);
            List<Kurir> kurirList = new ArrayList<>();
            for (Pengguna p : list) {
                if (p instanceof Kurir) {
                    kurirList.add((Kurir) p);
                }
            }
            if (!kurirList.isEmpty()) {
                return kurirList;
            }
        }
        List<Kurir> result = new ArrayList<>();
        for (Kurir k : inMemoryKurirList) {
            if (k.isStatusKetersediaan()) {
                result.add(k);
            }
        }
        return result;
    }

    // 2. MANAJEMEN PAKET
    public Paket cariPaketByResi(String noResi) {
        if (useDatabase) {
            Paket p = paketDAO.findByResi(noResi);
            if (p != null) {
                return p;
            }
        }
        for (Paket p : inMemoryPaketList) {
            if (p.getNoResi().equalsIgnoreCase(noResi)) {
                return p;
            }
        }
        return null;
    }

    public List<Paket> getDaftarPaket() {
        if (useDatabase) {
            List<Paket> dbList = paketDAO.findAll(null, null);
            if (dbList != null && !dbList.isEmpty()) {
                return dbList;
            }
        }
        return inMemoryPaketList;
    }

    public boolean tambahPaket(Paket paket) {
        if (useDatabase) {
            boolean success = paketDAO.insert(paket);
            if (success) return true;
        }
        inMemoryPaketList.add(paket);
        return true;
    }

    public boolean updateStatusPaket(String noResi, String status, int idUser, String lokasi, String deskripsi) {
        if (useDatabase) {
            boolean updated = paketDAO.updateStatus(noResi, status);
            if (updated) {
                riwayatDAO.insertTrackingLog(noResi, idUser, deskripsi, lokasi);
                return true;
            }
        }
        Paket p = cariPaketByResi(noResi);
        if (p != null) {
            p.updateStatus(status);
            p.tambahRiwayat(new RiwayatPelacakan(noResi, idUser, deskripsi, lokasi));
            return true;
        }
        return false;
    }

    public boolean assignKurir(String noResi, int idKurir) {
        if (useDatabase) {
            return paketDAO.updateKurir(noResi, idKurir);
        }
        Paket p = cariPaketByResi(noResi);
        if (p != null) {
            for (Kurir k : inMemoryKurirList) {
                if (k.getIdPengguna() == idKurir) {
                    p.setKurirPengantar(k);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updatePosTransit(String noResi, String posBaru, int idPetugas) {
        if (useDatabase) {
            boolean updated = paketDAO.updatePosTransit(noResi, posBaru);
            if (updated) {
                riwayatDAO.insertTrackingLog(noResi, idPetugas, "Paket transit di " + posBaru, posBaru);
                return true;
            }
        }
        Paket p = cariPaketByResi(noResi);
        if (p != null) {
            p.updatePosTransit(posBaru);
            p.tambahRiwayat(new RiwayatPelacakan(noResi, idPetugas, "Paket transit di " + posBaru, posBaru));
            return true;
        }
        return false;
    }

    public boolean hapusPaket(String noResi) {
        if (useDatabase) {
            boolean deleted = paketDAO.delete(noResi);
            if (deleted) return true;
        }
        Paket p = cariPaketByResi(noResi);
        if (p != null) {
            return inMemoryPaketList.remove(p);
        }
        return false;
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