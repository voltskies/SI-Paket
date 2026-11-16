package dto;

public class CekOngkirResponse {
    private double tarifDasar;
    private double biayaPrioritas;
    private double totalBiaya;
    private String estimasiHari;
    private String detailLayanan;

    public CekOngkirResponse() {}

    public CekOngkirResponse(double tarifDasar, double biayaPrioritas, double totalBiaya, String estimasiHari, String detailLayanan) {
        this.tarifDasar = tarifDasar;
        this.biayaPrioritas = biayaPrioritas;
        this.totalBiaya = totalBiaya;
        this.estimasiHari = estimasiHari;
        this.detailLayanan = detailLayanan;
    }

    public double getTarifDasar() { return tarifDasar; }
    public void setTarifDasar(double tarifDasar) { this.tarifDasar = tarifDasar; }

    public double getBiayaPrioritas() { return biayaPrioritas; }
    public void setBiayaPrioritas(double biayaPrioritas) { this.biayaPrioritas = biayaPrioritas; }

    public double getTotalBiaya() { return totalBiaya; }
    public void setTotalBiaya(double totalBiaya) { this.totalBiaya = totalBiaya; }

    public String getEstimasiHari() { return estimasiHari; }
    public void setEstimasiHari(String estimasiHari) { this.estimasiHari = estimasiHari; }

    public String getDetailLayanan() { return detailLayanan; }
    public void setDetailLayanan(String detailLayanan) { this.detailLayanan = detailLayanan; }
}