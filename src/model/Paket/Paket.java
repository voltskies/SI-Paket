package model.Paket;

public abstract class Paket {
    protected String no_resi;
    protected int id_pengguna;
    protected String nama_penerima;
    protected String alamat_tujuan;
    protected double berat_kg;
    protected String status_terkini;

    public String generateResi() {
        // Implementasi generate resi
        return "RESI123456"; 
    }

    public void updateStatus(String status) {
        this.status_terkini = status;
    }

    public abstract double hitungBiaya();
    public abstract String getEstimasiWaktu();
}