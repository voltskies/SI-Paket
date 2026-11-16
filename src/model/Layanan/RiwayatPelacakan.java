package model.Layanan;

import java.time.LocalDateTime;

public class RiwayatPelacakan {
    private int idRiwayat;
    private String noResi;
    private int updatedBy;
    private String deskripsiStatus;
    private String lokasiUpdate;
    private LocalDateTime waktuUpdate;

    public RiwayatPelacakan() {
        this.waktuUpdate = LocalDateTime.now();
    }

    public RiwayatPelacakan(int idRiwayat, String noResi, int updatedBy, String deskripsiStatus, String lokasiUpdate, LocalDateTime waktuUpdate) {
        this.idRiwayat = idRiwayat;
        this.noResi = noResi;
        this.updatedBy = updatedBy;
        this.deskripsiStatus = deskripsiStatus;
        this.lokasiUpdate = lokasiUpdate;
        this.waktuUpdate = waktuUpdate;
    }

    public RiwayatPelacakan(String noResi, int updatedBy, String deskripsiStatus, String lokasiUpdate) {
        this();
        this.noResi = noResi;
        this.updatedBy = updatedBy;
        this.deskripsiStatus = deskripsiStatus;
        this.lokasiUpdate = lokasiUpdate;
    }

    public int getIdRiwayat() {
        return idRiwayat;
    }

    public void setIdRiwayat(int idRiwayat) {
        this.idRiwayat = idRiwayat;
    }

    public String getNoResi() {
        return noResi;
    }

    public void setNoResi(String noResi) {
        this.noResi = noResi;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDeskripsiStatus() {
        return deskripsiStatus;
    }

    public void setDeskripsiStatus(String deskripsiStatus) {
        this.deskripsiStatus = deskripsiStatus;
    }

    public String getLokasiUpdate() {
        return lokasiUpdate;
    }

    public void setLokasiUpdate(String lokasiUpdate) {
        this.lokasiUpdate = lokasiUpdate;
    }

    public LocalDateTime getWaktuUpdate() {
        return waktuUpdate;
    }

    public void setWaktuUpdate(LocalDateTime waktuUpdate) {
        this.waktuUpdate = waktuUpdate;
    }

    @Override
    public String toString() {
        return "RiwayatPelacakan{noResi='" + noResi + "', status='" + deskripsiStatus + "', lokasi='" + lokasiUpdate + "'}";
    }
}