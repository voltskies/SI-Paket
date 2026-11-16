package dto;

import model.Layanan.RiwayatPelacakan;

import java.util.List;

public class PaketResponse {
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
    private String posTerkini;
    private KurirResponse kurirPengantar;
    private List<RiwayatResponse> riwayatPelacakan;

    public PaketResponse() {}

    public static class KurirResponse {
        private int idPengguna;
        private String namaLengkap;
        private String platKendaraan;
        private boolean statusKetersediaan;

        public KurirResponse() {}

        public KurirResponse(int idPengguna, String namaLengkap, String platKendaraan) {
            this.idPengguna = idPengguna;
            this.namaLengkap = namaLengkap;
            this.platKendaraan = platKendaraan;
        }

        public int getIdPengguna() { return idPengguna; }
        public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }

        public String getNamaLengkap() { return namaLengkap; }
        public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

        public String getPlatKendaraan() { return platKendaraan; }
        public void setPlatKendaraan(String platKendaraan) { this.platKendaraan = platKendaraan; }

        public boolean isStatusKetersediaan() { return statusKetersediaan; }
        public void setStatusKetersediaan(boolean statusKetersediaan) { this.statusKetersediaan = statusKetersediaan; }
    }

    public static class RiwayatResponse {
        private int idRiwayat;
        private int updatedBy;
        private String deskripsiStatus;
        private String lokasiUpdate;
        private String waktuUpdate;

        public RiwayatResponse() {}

        public int getIdRiwayat() { return idRiwayat; }
        public void setIdRiwayat(int idRiwayat) { this.idRiwayat = idRiwayat; }

        public int getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(int updatedBy) { this.updatedBy = updatedBy; }

        public String getDeskripsiStatus() { return deskripsiStatus; }
        public void setDeskripsiStatus(String deskripsiStatus) { this.deskripsiStatus = deskripsiStatus; }

        public String getLokasiUpdate() { return lokasiUpdate; }
        public void setLokasiUpdate(String lokasiUpdate) { this.lokasiUpdate = lokasiUpdate; }

        public String getWaktuUpdate() { return waktuUpdate; }
        public void setWaktuUpdate(String waktuUpdate) { this.waktuUpdate = waktuUpdate; }
    }

    public String getNoResi() { return noResi; }
    public void setNoResi(String noResi) { this.noResi = noResi; }

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }

    public Integer getIdKurir() { return idKurir; }
    public void setIdKurir(Integer idKurir) { this.idKurir = idKurir; }

    public String getNamaPengirim() { return namaPengirim; }
    public void setNamaPengirim(String namaPengirim) { this.namaPengirim = namaPengirim; }

    public String getNamaPenerima() { return namaPenerima; }
    public void setNamaPenerima(String namaPenerima) { this.namaPenerima = namaPenerima; }

    public String getAlamatAsal() { return alamatAsal; }
    public void setAlamatAsal(String alamatAsal) { this.alamatAsal = alamatAsal; }

    public String getAlamatTujuan() { return alamatTujuan; }
    public void setAlamatTujuan(String alamatTujuan) { this.alamatTujuan = alamatTujuan; }

    public double getBeratKg() { return beratKg; }
    public void setBeratKg(double beratKg) { this.beratKg = beratKg; }

    public String getStatusTerkini() { return statusTerkini; }
    public void setStatusTerkini(String statusTerkini) { this.statusTerkini = statusTerkini; }

    public String getJenisPaket() { return jenisPaket; }
    public void setJenisPaket(String jenisPaket) { this.jenisPaket = jenisPaket; }

    public double getTarifDasar() { return tarifDasar; }
    public void setTarifDasar(double tarifDasar) { this.tarifDasar = tarifDasar; }

    public double getBiayaPrioritas() { return biayaPrioritas; }
    public void setBiayaPrioritas(double biayaPrioritas) { this.biayaPrioritas = biayaPrioritas; }

    public String getPosTerkini() { return posTerkini; }
    public void setPosTerkini(String posTerkini) { this.posTerkini = posTerkini; }

    public KurirResponse getKurirPengantar() { return kurirPengantar; }
    public void setKurirPengantar(KurirResponse kurirPengantar) { this.kurirPengantar = kurirPengantar; }

    public List<RiwayatResponse> getRiwayatPelacakan() { return riwayatPelacakan; }
    public void setRiwayatPelacakan(List<RiwayatResponse> riwayatPelacakan) { this.riwayatPelacakan = riwayatPelacakan; }
}