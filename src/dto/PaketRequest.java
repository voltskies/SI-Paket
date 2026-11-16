package dto;

public class PaketRequest {
    private String namaPenerima;
    private String alamatAsal;
    private String alamatTujuan;
    private double beratKg;
    private String jenisPaket;
    private Integer idPengguna;
    private Integer idKurir;
    private String statusTerkini;
    private String posTerkini;
    private String deskripsiStatus;
    private String lokasiUpdate;
    private Boolean statusKetersediaan;

    public PaketRequest() {}

    public String getNamaPenerima() { return namaPenerima; }
    public void setNamaPenerima(String namaPenerima) { this.namaPenerima = namaPenerima; }

    public String getAlamatAsal() { return alamatAsal; }
    public void setAlamatAsal(String alamatAsal) { this.alamatAsal = alamatAsal; }

    public String getAlamatTujuan() { return alamatTujuan; }
    public void setAlamatTujuan(String alamatTujuan) { this.alamatTujuan = alamatTujuan; }

    public double getBeratKg() { return beratKg; }
    public void setBeratKg(double beratKg) { this.beratKg = beratKg; }

    public String getJenisPaket() { return jenisPaket; }
    public void setJenisPaket(String jenisPaket) { this.jenisPaket = jenisPaket; }

    public Integer getIdPengguna() { return idPengguna; }
    public void setIdPengguna(Integer idPengguna) { this.idPengguna = idPengguna; }

    public Integer getIdKurir() { return idKurir; }
    public void setIdKurir(Integer idKurir) { this.idKurir = idKurir; }

    public String getStatusTerkini() { return statusTerkini; }
    public void setStatusTerkini(String statusTerkini) { this.statusTerkini = statusTerkini; }

    public String getPosTerkini() { return posTerkini; }
    public void setPosTerkini(String posTerkini) { this.posTerkini = posTerkini; }

    public String getDeskripsiStatus() { return deskripsiStatus; }
    public void setDeskripsiStatus(String deskripsiStatus) { this.deskripsiStatus = deskripsiStatus; }

    public String getLokasiUpdate() { return lokasiUpdate; }
    public void setLokasiUpdate(String lokasiUpdate) { this.lokasiUpdate = lokasiUpdate; }

    public Boolean getStatusKetersediaan() { return statusKetersediaan; }
    public void setStatusKetersediaan(Boolean statusKetersediaan) { this.statusKetersediaan = statusKetersediaan; }
}