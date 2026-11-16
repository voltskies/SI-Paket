package dto;

import java.util.List;

public class TrackingResponse {
    private String noResi;
    private String namaPenerima;
    private String statusTerkini;
    private String posTerkini;
    private String estimasiHari;
    private List<RiwayatItem> riwayat;

    public TrackingResponse() {}

    public static class RiwayatItem {
        private String waktuUpdate;
        private String lokasiUpdate;
        private String deskripsiStatus;

        public RiwayatItem() {}

        public String getWaktuUpdate() { return waktuUpdate; }
        public void setWaktuUpdate(String waktuUpdate) { this.waktuUpdate = waktuUpdate; }

        public String getLokasiUpdate() { return lokasiUpdate; }
        public void setLokasiUpdate(String lokasiUpdate) { this.lokasiUpdate = lokasiUpdate; }

        public String getDeskripsiStatus() { return deskripsiStatus; }
        public void setDeskripsiStatus(String deskripsiStatus) { this.deskripsiStatus = deskripsiStatus; }
    }

    public String getNoResi() { return noResi; }
    public void setNoResi(String noResi) { this.noResi = noResi; }

    public String getNamaPenerima() { return namaPenerima; }
    public void setNamaPenerima(String namaPenerima) { this.namaPenerima = namaPenerima; }

    public String getStatusTerkini() { return statusTerkini; }
    public void setStatusTerkini(String statusTerkini) { this.statusTerkini = statusTerkini; }

    public String getPosTerkini() { return posTerkini; }
    public void setPosTerkini(String posTerkini) { this.posTerkini = posTerkini; }

    public String getEstimasiHari() { return estimasiHari; }
    public void setEstimasiHari(String estimasiHari) { this.estimasiHari = estimasiHari; }

    public List<RiwayatItem> getRiwayat() { return riwayat; }
    public void setRiwayat(List<RiwayatItem> riwayat) { this.riwayat = riwayat; }
}