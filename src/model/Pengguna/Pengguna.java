package model.Pengguna;

import java.time.LocalDateTime;

public abstract class Pengguna {
    private int idPengguna;
    private String namaLengkap;
    private String email;
    private String password;
    private String role;
    private LocalDateTime createdAt;

    public Pengguna() {
    }

    public Pengguna(int idPengguna, String namaLengkap, String email, String password, String role) {
        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public Pengguna(String namaLengkap, String email, String password, String role) {
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean login(String inputEmail, String inputPassword) {
        return this.email.equals(inputEmail) && this.password.equals(inputPassword);
    }

    public void logout() {
    }

    public abstract String getAksesRole();

    @Override
    public String toString() {
        return "Pengguna{id=" + idPengguna + ", nama='" + namaLengkap + "', email='" + email + "', role='" + role + "'}";
    }
}