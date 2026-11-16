package model.Paket;

import model.Layanan.LayananPengiriman;
import model.Layanan.RiwayatPelacakan;
import model.Pengguna.Kurir;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Paket implements LayananPengiriman {
    private String noResi;
    private int idPengguna;
    private Integer idKurir;
    private String namaPengirim;
    private String namaPenerima;
    private String alamatAsal;
    private String alamatTujuan;
    private double beratKg;
    private String statusTerkini;
    private String jenisPaket;
    private double tarifDasar;
    private double biayaPrioritas;
    private LocalDateTime createdAt;
    private Kurir kurirPengantar;
    private List<RiwayatPelacakan> riwayatPelacakan;
    private boolean sudahDiantarKurir;
    private boolean sudahDikonfirmasiUser;
    private String posTerkini;

    public Paket() {
        this.riwayatPelacakan = new ArrayList<>();
        this.statusTerkini = "Menunggu Penjemputan";
        this.createdAt = LocalDateTime.now();
        this.sudahDiantarKurir = false;
        this.sudahDikonfirmasiUser = false;
        this.posTerkini = "";
    }

    public Paket(String noResi, int idPengguna, String namaPengirim, String namaPenerima,
                 String alamatAsal, String alamatTujuan, double beratKg, String jenisPaket,
                 double tarifDasar, double biayaPrioritas) {
        this();
        this.noResi = noResi;
        this.idPengguna = idPengguna;
        this.namaPengirim = namaPengirim;
        this.namaPenerima = namaPenerima;
        this.alamatAsal = alamatAsal;
        this.alamatTujuan = alamatTujuan;
        this.beratKg = beratKg;
        this.jenisPaket = jenisPaket;
        this.tarifDasar = tarifDasar;
        this.biayaPrioritas = biayaPrioritas;
    }

    public String getNoResi() {
        return noResi;
    }

    public void setNoResi(String noResi) {
        this.noResi = noResi;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public Integer getIdKurir() {
        return idKurir;
    }

    public void setIdKurir(Integer idKurir) {
        this.idKurir = idKurir;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.namaPengirim = namaPengirim;
    }

    public String getNamaPenerima() {
        return namaPenerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.namaPenerima = namaPenerima;
    }

    public String getAlamatAsal() {
        return alamatAsal;
    }

    public void setAlamatAsal(String alamatAsal) {
        this.alamatAsal = alamatAsal;
    }

    public String getAlamatTujuan() {
        return alamatTujuan;
    }

    public void setAlamatTujuan(String alamatTujuan) {
        this.alamatTujuan = alamatTujuan;
    }

    public double getBeratKg() {
        return beratKg;
    }

    public void setBeratKg(double beratKg) {
        this.beratKg = beratKg;
    }

    public String getStatusTerkini() {
        return statusTerkini;
    }

    public void setStatusTerkini(String statusTerkini) {
        this.statusTerkini = statusTerkini;
    }

    public String getJenisPaket() {
        return jenisPaket;
    }

    public void setJenisPaket(String jenisPaket) {
        this.jenisPaket = jenisPaket;
    }

    public double getTarifDasar() {
        return tarifDasar;
    }

    public void setTarifDasar(double tarifDasar) {
        this.tarifDasar = tarifDasar;
    }

    public double getBiayaPrioritas() {
        return biayaPrioritas;
    }

    public void setBiayaPrioritas(double biayaPrioritas) {
        this.biayaPrioritas = biayaPrioritas;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Kurir getKurirPengantar() {
        return kurirPengantar;
    }

    public void setKurirPengantar(Kurir kurirPengantar) {
        this.kurirPengantar = kurirPengantar;
    }

    public List<RiwayatPelacakan> getRiwayatPelacakan() {
        return riwayatPelacakan;
    }

    public void setRiwayatPelacakan(List<RiwayatPelacakan> riwayatPelacakan) {
        this.riwayatPelacakan = riwayatPelacakan;
    }

    public void tambahRiwayat(RiwayatPelacakan riwayat) {
        if (this.riwayatPelacakan == null) {
            this.riwayatPelacakan = new ArrayList<>();
        }
        this.riwayatPelacakan.add(riwayat);
    }

    public boolean isSudahDiantarKurir() {
        return sudahDiantarKurir;
    }

    public void setSudahDiantarKurir(boolean sudahDiantarKurir) {
        this.sudahDiantarKurir = sudahDiantarKurir;
    }

    public boolean isSudahDikonfirmasiUser() {
        return sudahDikonfirmasiUser;
    }

    public void setSudahDikonfirmasiUser(boolean sudahDikonfirmasiUser) {
        this.sudahDikonfirmasiUser = sudahDikonfirmasiUser;
    }

    public String getPosTerkini() {
        return posTerkini;
    }

    public void setPosTerkini(String posTerkini) {
        this.posTerkini = posTerkini;
    }

    public String generateResi() {
        return "RESI-" + System.currentTimeMillis();
    }

    public void updateStatus(String status) {
        this.statusTerkini = status;
    }

    public void tandaiSedangDiantarKurir() {
        this.statusTerkini = "Sedang Diantar Kurir";
        this.sudahDiantarKurir = true;
    }

    public void tandaiSudahDiantarKurir() {
        this.statusTerkini = "Telah Diantar (Menunggu Konfirmasi User)";
        this.sudahDiantarKurir = true;
    }

    public void konfirmasiDiterimaUser() {
        this.statusTerkini = "Terkirim";
        this.sudahDikonfirmasiUser = true;
    }

    public void updatePosTransit(String posBaru) {
        this.posTerkini = posBaru;
        this.statusTerkini = "Transit ke " + posBaru;
    }

    @Override
    public String getDetailLayanan() {
        return jenisPaket;
    }

    public String getRiwayatTrackingLengkap() {
        if (riwayatPelacakan == null || riwayatPelacakan.isEmpty()) {
            return "Belum ada riwayat pelacakan.";
        }
        StringBuilder sb = new StringBuilder();
        for (RiwayatPelacakan r : riwayatPelacakan) {
            sb.append("- [").append(r.getWaktuUpdate()).append("] ")
              .append(r.getLokasiUpdate()).append(": ")
              .append(r.getDeskripsiStatus()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Paket{noResi='" + noResi + "', penerima='" + namaPenerima + "', status='" + statusTerkini + "'}";
    }
}