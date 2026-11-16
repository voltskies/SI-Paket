package dto;

public class AuthRequest {
    private String email;
    private String password;
    private String role;
    private String namaLengkap;
    private String noTelp;
    private String lokasiPos;
    private String platKendaraan;

    public AuthRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }

    public String getLokasiPos() { return lokasiPos; }
    public void setLokasiPos(String lokasiPos) { this.lokasiPos = lokasiPos; }

    public String getPlatKendaraan() { return platKendaraan; }
    public void setPlatKendaraan(String platKendaraan) { this.platKendaraan = platKendaraan; }
}