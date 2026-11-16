package model.Paket;

import model.Layanan.LayananPengiriman;

public class PaketExpress extends Paket {

    public PaketExpress() {
        super();
        setJenisPaket("Express");
        setTarifDasar(25000);
        setBiayaPrioritas(15000);
    }

    public PaketExpress(String noResi, String namaPengirim, String namaPenerima,
                        String alamatTujuan, double beratKg) {
        this();
        setNoResi(noResi);
        setNamaPengirim(namaPengirim);
        setNamaPenerima(namaPenerima);
        setAlamatTujuan(alamatTujuan);
        setBeratKg(beratKg);
    }

    public PaketExpress(String noResi, int idPengguna, String namaPengirim, String namaPenerima,
                        String alamatAsal, String alamatTujuan, double beratKg) {
        super(noResi, idPengguna, namaPengirim, namaPenerima, alamatAsal, alamatTujuan, beratKg, "Express", 25000, 15000);
    }

    @Override
    public double hitungOngkir() {
        return (getTarifDasar() * getBeratKg()) + getBiayaPrioritas();
    }

    @Override
    public String getEstimasiHari() {
        return "1-2 Hari Kerja";
    }

    @Override
    public String getDetailLayanan() {
        return "Express";
    }
}