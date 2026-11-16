package model.Pengguna;

public class User extends Pengguna {
    private String noTelepon;

    public User() {
        super();
    }

    public User(int idPengguna, String namaLengkap, String email, String password, String noTelepon) {
        super(idPengguna, namaLengkap, email, password, "User");
        this.noTelepon = noTelepon;
    }

    public User(String namaLengkap, String email, String password, String noTelepon) {
        super(namaLengkap, email, password, "User");
        this.noTelepon = noTelepon;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    @Override
    public String getAksesRole() {
        return "User";
    }

    public void buatPesanan() {
    }

    public void lacakResi(String noResi) {
    }

    @Override
    public String toString() {
        return "User{" + super.toString() + ", noTelp='" + noTelepon + "'}";
    }
}