package model.Pengguna;

public class PetugasGudang extends Pengguna {
    private String lokasiPos;

    public PetugasGudang() {
        super();
    }

    public PetugasGudang(int idPengguna, String namaLengkap, String email, String password, String lokasiPos) {
        super(idPengguna, namaLengkap, email, password, "PetugasGudang");
        this.lokasiPos = lokasiPos;
    }

    public PetugasGudang(String namaLengkap, String email, String password, String lokasiPos) {
        super(namaLengkap, email, password, "PetugasGudang");
        this.lokasiPos = lokasiPos;
    }

    public String getLokasiPos() {
        return lokasiPos;
    }

    public void setLokasiPos(String lokasiPos) {
        this.lokasiPos = lokasiPos;
    }

    @Override
    public String getAksesRole() {
        return "Petugas Gudang";
    }

    public void inputPaketBaru() {
    }

    public void assignKurir(int idKurir, String noResi) {
    }

    public void konfirmasiTransit(String noResi, String status) {
    }

    @Override
    public String toString() {
        return "PetugasGudang{" + super.toString() + ", lokasiPos='" + lokasiPos + "'}";
    }
}