package model.Pengguna;

public class User extends Pengguna {
    private String no_telp;

    @Override
    public String getAksesRole() {
        return "User";
    }

    public void buatPesanan() {
        // Implementasi buat pesanan
    }

    public void lacakResi(String no_resi) {
        // Implementasi lacak resi
    }

    // ! Tambahan
    public String getNomor() {
        return no_telp;
    }
}