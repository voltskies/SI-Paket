package dto;

public class KurirResponse {
    private int idPengguna;
    private String namaLengkap;
    private String email;
    private String platKendaraan;
    private boolean statusKetersediaan;

    public KurirResponse() {}

    public KurirResponse(int idPengguna, String namaLengkap, String email, String platKendaraan, boolean statusKetersediaan) {
        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.platKendaraan = platKendaraan;
        this.statusKetersediaan = statusKetersediaan;
    }

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPlatKendaraan() { return platKendaraan; }
    public void setPlatKendaraan(String platKendaraan) { this.platKendaraan = platKendaraan; }

    public boolean isStatusKetersediaan() { return statusKetersediaan; }
    public void setStatusKetersediaan(boolean statusKetersediaan) { this.statusKetersediaan = statusKetersediaan; }
}