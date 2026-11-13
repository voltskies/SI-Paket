package model.Pengguna;

abstract class Pengguna {
    protected int id_pengguna;
    protected String nama_lengkap;
    protected String email;
    protected String password;

    public boolean login() {
        // Implementasi login
        return true; 
    }

    public void logout() {
        // Implementasi logout
    }

    public abstract String getAksesRole();
}