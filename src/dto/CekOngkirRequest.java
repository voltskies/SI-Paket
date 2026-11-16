package dto;

public class CekOngkirRequest {
    private String alamatAsal;
    private String alamatTujuan;
    private double beratKg;
    private String jenisPaket;

    public CekOngkirRequest() {}

    public String getAlamatAsal() { return alamatAsal; }
    public void setAlamatAsal(String alamatAsal) { this.alamatAsal = alamatAsal; }

    public String getAlamatTujuan() { return alamatTujuan; }
    public void setAlamatTujuan(String alamatTujuan) { this.alamatTujuan = alamatTujuan; }

    public double getBeratKg() { return beratKg; }
    public void setBeratKg(double beratKg) { this.beratKg = beratKg; }

    public String getJenisPaket() { return jenisPaket; }
    public void setJenisPaket(String jenisPaket) { this.jenisPaket = jenisPaket; }
}