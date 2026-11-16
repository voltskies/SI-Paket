package dto;

public class UserProfileResponse {
    private int idPengguna;
    private String namaLengkap;
    private String email;
    private String role;
    private String noTelp;
    private String lokasiPos;
    private String platKendaraan;
    private Boolean statusKetersediaan;

    public UserProfileResponse() {}

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }

    public String getLokasiPos() { return lokasiPos; }
    public void setLokasiPos(String lokasiPos) { this.lokasiPos = lokasiPos; }

    public String getPlatKendaraan() { return platKendaraan; }
    public void setPlatKendaraan(String platKendaraan) { this.platKendaraan = platKendaraan; }

    public Boolean getStatusKetersediaan() { return statusKetersediaan; }
    public void setStatusKetersediaan(Boolean statusKetersediaan) { this.statusKetersediaan = statusKetersediaan; }
}