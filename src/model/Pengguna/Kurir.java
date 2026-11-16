package model.Pengguna;

public class Kurir extends Pengguna {
    private String platKendaraan;
    private boolean statusKetersediaan;

    public Kurir() {
        super();
        this.statusKetersediaan = true;
    }

    public Kurir(int idPengguna, String namaLengkap, String email, String password, String platKendaraan, boolean statusKetersediaan) {
        super(idPengguna, namaLengkap, email, password, "Kurir");
        this.platKendaraan = platKendaraan;
        this.statusKetersediaan = statusKetersediaan;
    }

    public Kurir(String namaLengkap, String email, String password, String platKendaraan) {
        super(namaLengkap, email, password, "Kurir");
        this.platKendaraan = platKendaraan;
        this.statusKetersediaan = true;
    }

    public String getPlatKendaraan() {
        return platKendaraan;
    }

    public void setPlatKendaraan(String platKendaraan) {
        this.platKendaraan = platKendaraan;
    }

    public boolean isStatusKetersediaan() {
        return statusKetersediaan;
    }

    public void setStatusKetersediaan(boolean statusKetersediaan) {
        this.statusKetersediaan = statusKetersediaan;
    }

    @Override
    public String getAksesRole() {
        return "Kurir";
    }

    public void terimaTugas() {
    }

    public void updateLokasiTerkini() {
    }

    public void selesaikanPengiriman(String noResi) {
    }

    // Compat method
    public Integer getIdKurir() {
        return getIdPengguna();
    }

    public String getNamaKurir() {
        return getNamaLengkap();
    }

    public String getNoTelepon() {
        return getPlatKendaraan();
    }

    @Override
    public String toString() {
        return getNamaLengkap() + " (" + (platKendaraan != null ? platKendaraan : "No Plat") + ")";
    }
}