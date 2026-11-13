package model.Pengguna;

public class PetugasGudang extends Pengguna {
    private String lokasi_pos;

    @Override
    public String getAksesRole() {
        return "Petugas Gudang";
    }

    public void inputPaketBaru() {
        // Implementasi input paket
    }

    public void assignKurir(int id_kurir, String no_resi) {
        // Implementasi assign kurir
    }

    public void konfirmasiTransit(String no_resi, String status) {
        // Implementasi konfirmasi transit
    }

    // ! Tambahan
    public String getLokasiPos() {
        return lokasi_pos;
    }
}
