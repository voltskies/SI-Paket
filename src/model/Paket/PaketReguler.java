package model.Paket;

public class PaketReguler extends Paket {
    private double tarif_dasar_reguler;

    @Override
    public double hitungBiaya() {
        // Implementasi hitung biaya reguler
        return tarif_dasar_reguler * berat_kg;
    }

    @Override
    public String getEstimasiWaktu() {
        return "3-5 Hari Kerja";
    }
}