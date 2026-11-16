package model.Paket;

import model.Layanan.LayananPengiriman;

public class PaketReguler extends Paket {

    public PaketReguler() {
        super();
        setJenisPaket("Reguler");
        setTarifDasar(15000);
        setBiayaPrioritas(0);
    }

    public PaketReguler(String noResi, String namaPengirim, String namaPenerima,
                        String alamatTujuan, double beratKg) {
        this();
        setNoResi(noResi);
        setNamaPengirim(namaPengirim);
        setNamaPenerima(namaPenerima);
        setAlamatTujuan(alamatTujuan);
        setBeratKg(beratKg);
    }

    public PaketReguler(String noResi, int idPengguna, String namaPengirim, String namaPenerima,
                        String alamatAsal, String alamatTujuan, double beratKg) {
        super(noResi, idPengguna, namaPengirim, namaPenerima, alamatAsal, alamatTujuan, beratKg, "Reguler", 15000, 0);
    }

    @Override
    public double hitungOngkir() {
        return getTarifDasar() * getBeratKg();
    }

    @Override
    public String getEstimasiHari() {
        return "3-5 Hari Kerja";
    }

    @Override
    public String getDetailLayanan() {
        return "Reguler";
    }
}