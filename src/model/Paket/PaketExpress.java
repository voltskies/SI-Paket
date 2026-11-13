package model.Paket;

public class PaketExpress extends Paket {
    private double tarif_dasar_express;
    private double biaya_prioritas;

    @Override
    public double hitungBiaya() {
        // Implementasi hitung biaya express
        return (tarif_dasar_express * berat_kg) + biaya_prioritas;
    }

    @Override
    public String getEstimasiWaktu() {
        return "1-2 Hari Kerja";
    }
}