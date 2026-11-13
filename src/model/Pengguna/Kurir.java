package model.Pengguna;

public class Kurir extends Pengguna {
    private String plat_kendaraan;
    private boolean status_ketersediaan;

    @Override
    public String getAksesRole() {
        return "Kurir";
    }

    public void terimaTugas() {
        // Implementasi terima tugas
    }

    public void updateLokasiTerkini() {
        // Implementasi update lokasi
    }

    public void selesaikanPengiriman(String no_resi) {
        // Implementasi selesaikan pengiriman
    }

    // ! Tambahan
    public String getPlatKendaraan() {
        return plat_kendaraan;
    }

    public boolean isStatusKetersediaan() {
        return status_ketersediaan;
    }
}