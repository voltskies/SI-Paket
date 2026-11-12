package view;

import controller.Manajemen;
import model.Paket;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Manajemen sistem = new Manajemen();
        Scanner scanner = new Scanner(System.in);

        System.out.println("SISTEM SI-PAKET");
        System.out.println("\n");
        System.out.println("Contoh Resi yang tersedia: RESI-001 atau RESI-002");
        System.out.print("Masukkan Nomor Resi yang ingin dilacak: ");
        String inputResi = scanner.nextLine();

        // proses
        Paket hasilCari = sistem.cariPaketByResi(inputResi);

        if (hasilCari != null) {
            System.out.println("\n[INFO] Resi Ditemukan!");
            System.out.println("-------------------------------------------------");
            System.out.println("No. Resi       : " + hasilCari.getNoResi());
            System.out.println("Pengirim       : " + hasilCari.getNamaPengirim());
            System.out.println("Penerima       : " + hasilCari.getNamaPenerima());
            System.out.println("Alamat Tujuan  : " + hasilCari.getAlamatTujuan());
            System.out.println("Berat          : " + hasilCari.getBeratKg() + " Kg");
            System.out.println("Jenis Layanan  : " + hasilCari.getDetailLayanan());
            System.out.println("Estimasi Tiba  : " + hasilCari.getEstimasiHari());
            System.out.println("Total Ongkir   : Rp " + hasilCari.hitungOngkir());
            System.out.println("Pos Terkini    : " + hasilCari.getPosTerkini());
            System.out.println("Status Paket   : " + hasilCari.getStatusPengiriman());
            
            if (hasilCari.getKurirPengantar() != null) {
                System.out.println("Kurir Bertugas : " + hasilCari.getKurirPengantar().getNamaKurir() + 
                                   " (" + hasilCari.getKurirPengantar().getNoTelepon() + ")");
            }

            System.out.println("\nTRACKING LOG:");
            System.out.println(hasilCari.getRiwayatTrackingLengkap());

            // konfir user
            if (hasilCari.isSudahDiantarKurir() && !hasilCari.isSudahDikonfirmasiUser()) {
                System.out.print("Paket telah sampai! Lakukan Konfirmasi Paket Diterima? (y/n): ");
                String konfirmasi = scanner.nextLine();
                if (konfirmasi.equalsIgnoreCase("y")) {
                    hasilCari.konfirmasiDiterimaUser();
                    System.out.println("\n[SUKSES] Status paket sekarang: " + hasilCari.getStatusPengiriman());
                }
            }
        } else {
            System.out.println("\n[ERROR] Maaf, Nomor Resi '" + inputResi + "' tidak ditemukan dalam sistem.");
        }

        scanner.close();
    }
}