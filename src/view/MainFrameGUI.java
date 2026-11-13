package view;

import controller.Manajemen;
import model.Pengguna.Kurir;
import model.Paket.Paket;
import model.Paket.PaketExpress;
import model.Paket.PaketReguler;
import model.Layanan.LayananPengiriman;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrameGUI extends JFrame {
    private Manajemen manajemen;
    private JTabbedPane tabbedPane; 

    // User
    private JTextField txtSearchResi;
    private JTextArea txtAreaInfoResi;
    private JButton btnCariResi, btnKonfirmasiTerima;
    private Paket paketDitemukanUser = null;

    // kurir
    private JComboBox<Kurir> cmbKurirLogin;
    private JTable tblKurirTugas;
    private DefaultTableModel modelTabelKurir;
    private JButton btnKurirDiantar, btnKurirTelahDiantar;

    // admin
    private JTextField txtInputResi, txtInputPengirim, txtInputPenerima, txtInputBerat;
    private JTextArea txtInputAlamat;
    private JComboBox<String> cmbJenisLayanan, cmbPosTransit;
    private JComboBox<Kurir> cmbPilihKurirAdmin;
    private JTable tblAdminPaket;
    private DefaultTableModel modelTabelAdmin;
    private JButton btnAdminTambah, btnAdminUpdatePos, btnAdminHapus;

    public MainFrameGUI() {
        manajemen = new Manajemen();

        setTitle("SI-PAKET : Sistem Manajemen Ekspedisi & Tracking (Kelompok 8)");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // 3 role
        tabbedPane.addTab("1. Cek Resi", initTabUser());
        tabbedPane.addTab("2. Panel Kurir", initTabKurir());
        tabbedPane.addTab("3. Panel Admin Pos", initTabAdmin());

        add(tabbedPane);
        refreshDataTabel();
    }

    // cek resi
    private JPanel initTabUser() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("Masukkan Nomor Resi:"));
        txtSearchResi = new JTextField(20);
        topPanel.add(txtSearchResi);
        btnCariResi = new JButton("Cari / Lacak");
        topPanel.add(btnCariResi);
        panel.add(topPanel, BorderLayout.NORTH);

        // info
        txtAreaInfoResi = new JTextArea();
        txtAreaInfoResi.setEditable(false);
        txtAreaInfoResi.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(txtAreaInfoResi), BorderLayout.CENTER);

        // konfir user
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnKonfirmasiTerima = new JButton("Konfirmasi Paket Diterima (Selesai)");
        btnKonfirmasiTerima.setEnabled(false);
        bottomPanel.add(btnKonfirmasiTerima);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // cari resi
        btnCariResi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String resi = txtSearchResi.getText().trim();
                if (resi.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Nomor resi tidak boleh kosong", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                paketDitemukanUser = manajemen.cariPaketByResi(resi);
                if (paketDitemukanUser != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("INFORMASI DETAIL RESI\n");
                    sb.append("\n");
                    sb.append("No. Resi      : ").append(paketDitemukanUser.getNoResi()).append("\n");
                    sb.append("Pengirim      : ").append(paketDitemukanUser.getNamaPengirim()).append("\n");
                    sb.append("Penerima      : ").append(paketDitemukanUser.getNamaPenerima()).append("\n");
                    sb.append("Alamat Tujuan : ").append(paketDitemukanUser.getAlamatTujuan()).append("\n");
                    sb.append("Berat / Layanan: ").append(paketDitemukanUser.getBeratKg()).append(" Kg | ").append(paketDitemukanUser.getDetailLayanan()).append("\n");
                    sb.append("Estimasi Tiba : ").append(paketDitemukanUser.getEstimasiHari()).append("\n");
                    sb.append("Total Ongkir  : Rp ").append(paketDitemukanUser.hitungOngkir()).append("\n");
                    sb.append("Pos Terkini   : ").append(paketDitemukanUser.getPosTerkini()).append("\n");
                    sb.append("Status Terkini: ").append(paketDitemukanUser.getStatusPengiriman()).append("\n");
                    if (paketDitemukanUser.getKurirPengantar() != null) {
                        sb.append("Kurir Bertugas: ").append(paketDitemukanUser.getKurirPengantar().getNamaKurir()).append(" (" ).append(paketDitemukanUser.getKurirPengantar().getNoTelepon()).append(")\n");
                    }
                    sb.append("\nRIWAYAT PERJALANAN / TRACKING LOG:\n");
                    sb.append(paketDitemukanUser.getRiwayatTrackingLengkap());

                    txtAreaInfoResi.setText(sb.toString());

                    if (paketDitemukanUser.isSudahDiantarKurir() && !paketDitemukanUser.isSudahDikonfirmasiUser()) {
                        btnKonfirmasiTerima.setEnabled(true);
                    } else {
                        btnKonfirmasiTerima.setEnabled(false);
                    }
                } else {
                    txtAreaInfoResi.setText("Maaf, Nomor Resi '" + resi + "' tidak ditemukan dalam sistem.");
                    btnKonfirmasiTerima.setEnabled(false);
                }
            }
        });

        btnKonfirmasiTerima.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paketDitemukanUser != null) {
                    paketDitemukanUser.konfirmasiDiterimaUser();
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Konfirmasi berhasil! Status paket sekarang: Selesai.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    btnCariResi.doClick();
                    refreshDataTabel();
                }
            }
        });

        return panel;
    }

    // kurir
    private JPanel initTabKurir() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Panel: Pilih Kurir Login
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("Pilih Identitas Kurir:"));
        cmbKurirLogin = new JComboBox<>();
        for (Kurir k : manajemen.getDaftarKurir()) {
            cmbKurirLogin.addItem(k);
        }
        topPanel.add(cmbKurirLogin);
        panel.add(topPanel, BorderLayout.NORTH);

        // tugas kurir nanti
        String[] kolomKurir = {"No. Resi", "Penerima", "Alamat Tujuan", "Status Pengiriman"};
        modelTabelKurir = new DefaultTableModel(kolomKurir, 0);
        tblKurirTugas = new JTable(modelTabelKurir);
        panel.add(new JScrollPane(tblKurirTugas), BorderLayout.CENTER);

        // action kurir
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnKurirDiantar = new JButton("Update: Sedang Diantar Kurir");
        btnKurirTelahDiantar = new JButton("Tandai: Telah Diantar (Tunggu Konfirmasi User)");
        bottomPanel.add(btnKurirDiantar);
        bottomPanel.add(btnKurirTelahDiantar);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // update status kurir
        btnKurirDiantar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblKurirTugas.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "pilih paket pada tabel terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String resi = (String) modelTabelKurir.getValueAt(selectedRow, 0);
                Paket p = manajemen.cariPaketByResi(resi);
                if (p != null) {
                    p.tandaiSedangDiantarKurir();
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Status diperbarui: Sedang Diantar Kurir.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshDataTabel();
                }
            }
        });

        // button diantar
        btnKurirTelahDiantar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblKurirTugas.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "pilih paket pada tabel terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String resi = (String) modelTabelKurir.getValueAt(selectedRow, 0);
                Paket p = manajemen.cariPaketByResi(resi);
                if (p != null) {
                    p.tandaiSudahDiantarKurir();
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Status diperbarui: Telah Diantar (Menunggu Konfirmasi User).", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshDataTabel();
                }
            }
        });

        return panel;
    }

    // admin
    private JPanel initTabAdmin() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // form
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Input & Update Paket"));

        formPanel.add(new JLabel("No. Resi Otomatis:"));
        txtInputResi = new JTextField("RESI-003");
        formPanel.add(txtInputResi);

        formPanel.add(new JLabel("Nama Pengirim:"));
        txtInputPengirim = new JTextField();
        formPanel.add(txtInputPengirim);

        formPanel.add(new JLabel("Nama Penerima:"));
        txtInputPenerima = new JTextField();
        formPanel.add(txtInputPenerima);

        formPanel.add(new JLabel("Alamat Tujuan:"));
        txtInputAlamat = new JTextArea(2, 20);
        formPanel.add(new JScrollPane(txtInputAlamat));

        formPanel.add(new JLabel("Berat Barang (Kg):"));
        txtInputBerat = new JTextField();
        formPanel.add(txtInputBerat);

        formPanel.add(new JLabel("Jenis Layanan:"));
        cmbJenisLayanan = new JComboBox<>(new String[]{"Reguler", "Express"});
        formPanel.add(cmbJenisLayanan);

        formPanel.add(new JLabel("Update Pos Transit / DC:"));
        cmbPosTransit = new JComboBox<>(new String[]{"DC Cakung", "DC Cengkareng", "Hub Surabaya", "DC Semarang"});
        formPanel.add(cmbPosTransit);

        formPanel.add(new JLabel("Tugaskan Kurir:"));
        cmbPilihKurirAdmin = new JComboBox<>();
        for (Kurir k : manajemen.getDaftarKurir()) {
            cmbPilihKurirAdmin.addItem(k);
        }
        formPanel.add(cmbPilihKurirAdmin);

        panel.add(formPanel, BorderLayout.WEST);

        // tabel
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        String[] kolomAdmin = {"No. Resi", "Penerima", "Layanan", "Berat", "Pos Terkini", "Status"};
        modelTabelAdmin = new DefaultTableModel(kolomAdmin, 0);
        tblAdminPaket = new JTable(modelTabelAdmin);
        rightPanel.add(new JScrollPane(tblAdminPaket), BorderLayout.CENTER);

        JPanel btnAdminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdminTambah = new JButton("Tambah Paket");
        btnAdminUpdatePos = new JButton("Update Pos Transit");
        btnAdminHapus = new JButton("Hapus Paket");
        btnAdminPanel.add(btnAdminTambah);
        btnAdminPanel.add(btnAdminUpdatePos);
        btnAdminPanel.add(btnAdminHapus);
        rightPanel.add(btnAdminPanel, BorderLayout.SOUTH);

        panel.add(rightPanel, BorderLayout.CENTER);

        // tambah paket
        btnAdminTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String resi = txtInputResi.getText().trim();
                    String pengirim = txtInputPengirim.getText().trim();
                    String penerima = txtInputPenerima.getText().trim();
                    String alamat = txtInputAlamat.getText().trim();
                    double berat = Double.parseDouble(txtInputBerat.getText().trim());
                    String layanan = (String) cmbJenisLayanan.getSelectedItem();
                    Kurir kurirTerpilih = (Kurir) cmbPilihKurirAdmin.getSelectedItem();

                    if (pengirim.isEmpty() || penerima.isEmpty() || alamat.isEmpty()) {
                        JOptionPane.showMessageDialog(MainFrameGUI.this, "Semua field data wajib diisi", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Paket paketBaru;
                    if (layanan.equals("Reguler")) {
                        paketBaru = new PaketReguler(resi, pengirim, penerima, alamat, berat);
                    } else {
                        paketBaru = new PaketExpress(resi, pengirim, penerima, alamat, berat);
                    }
                    paketBaru.setKurirPengantar(kurirTerpilih);

                    manajemen.tambahPaket(paketBaru);
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Paket berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshDataTabel();
                    
                    txtInputResi.setText("RESI-00" + (manajemen.getDaftarPaket().size() + 1));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Berat barang harus berupa angka (contoh: 2.5)!", "Format Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // admin transit
        btnAdminUpdatePos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblAdminPaket.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Pilih paket pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String resi = (String) modelTabelAdmin.getValueAt(selectedRow, 0);
                String posBaru = (String) cmbPosTransit.getSelectedItem();

                Paket p = manajemen.cariPaketByResi(resi);
                if (p != null) {
                    p.updatePosTransit(posBaru);
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Pos transit diperbarui ke: " + posBaru, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshDataTabel();
                }
            }
        });

        // hapus paket
        btnAdminHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblAdminPaket.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Pilih paket pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String resi = (String) modelTabelAdmin.getValueAt(selectedRow, 0);
                Paket p = manajemen.cariPaketByResi(resi);
                if (p != null) {
                    manajemen.getDaftarPaket().remove(p);
                    JOptionPane.showMessageDialog(MainFrameGUI.this, "Paket berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshDataTabel();
                }
            }
        });

        return panel;
    }

    // refresh tabel
    public void refreshDataTabel() {
        modelTabelAdmin.setRowCount(0);
        for (Paket p : manajemen.getDaftarPaket()) {
            Object[] row = {
                p.getNoResi(),
                p.getNamaPenerima(),
                p.getDetailLayanan(),
                p.getBeratKg() + " Kg",
                p.getPosTerkini(),
                p.getStatusPengiriman()
            };
            modelTabelAdmin.addRow(row);
        }

        // refresh tabel kurir
        modelTabelKurir.setRowCount(0);
        Kurir kurirAktif = (Kurir) cmbKurirLogin.getSelectedItem();
        if (kurirAktif != null) {
            for (Paket p : manajemen.getDaftarPaket()) {
                if (p.getKurirPengantar() != null && p.getKurirPengantar().getIdKurir().equals(kurirAktif.getIdKurir())) {
                    Object[] row = {
                        p.getNoResi(),
                        p.getNamaPenerima(),
                        p.getAlamatTujuan(),
                        p.getStatusPengiriman()
                    };
                    modelTabelKurir.addRow(row);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrameGUI().setVisible(true);
            }
        });
    }
}