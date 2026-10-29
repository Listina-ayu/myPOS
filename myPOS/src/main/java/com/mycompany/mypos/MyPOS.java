/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mypos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Aplikasi POS (Point of Sale) Sederhana dengan Konsep PBO
 * @author trili
 */

// ============================================================================
// 1. KONSEP PBO: ABSTRAKSI (ABSTRACT CLASS)
// ============================================================================
abstract class Item {
    private String id;
    private String nama;
    private double harga;

    public Item(String id, String nama, double harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    // Encapsulation: Getter
    public String getId() { return id; }
    public String getNama() { return nama; }
    public double getHarga() { return harga; }

    // Method abstrak (Polimorfisme)
    public abstract String getKategori();
}

// ============================================================================
// 2. KONSEP PBO: INHERITANCE (PEWARISAN) & ENCAPSULATION
// ============================================================================
class Produk extends Item {
    private int stok;

    public Produk(String id, String nama, double harga, int stok) {
        super(id, nama, harga); // Memanggil constructor class induk
        this.stok = stok;
    }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    @Override
    public String getKategori() {
        return "Produk Toko";
    }
}

// Class untuk item di keranjang belanja
class CartItem {
    private Produk produk;
    private int jumlah;

    public CartItem(Produk produk, int jumlah) {
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public Produk getProduk() { return produk; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public double getSubtotal() {
        return produk.getHarga() * jumlah;
    }
}

// ============================================================================
// 3. TAMPILAN GUI (SWING) & FRAME UTAMA APLIKASI
// ============================================================================
public class MyPOS extends JFrame {

    private List<Produk> daftarProduk = new ArrayList<>();
    private List<CartItem> keranjang = new ArrayList<>();

    private DefaultTableModel tableModelProduk;
    private DefaultTableModel tableModelKeranjang;
    private JLabel lblTotalHarga;
    private JTextField txtBayar;
    private JLabel lblKembalian;

    // Palette Warna "Fresh Green"
    private final Color COLOR_PRIMARY = new Color(46, 125, 50);    // Hijau Tua
    private final Color COLOR_SECONDARY = new Color(200, 230, 201); // Hijau Muda Fresh
    private final Color COLOR_BG = new Color(245, 247, 245);        // Background
    private final Color COLOR_ACCENT = new Color(76, 175, 80);      // Hijau Tombol
    private final Color COLOR_DANGER = new Color(211, 47, 47);       // Merah

    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public MyPOS() {
        initData();

        setTitle("FreshPOS - Kasir Hijau & Mudah");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private void initData() {
        daftarProduk.add(new Produk("P001", "Kopi Susu Gula Aren", 18000, 20));
        daftarProduk.add(new Produk("P002", "Teh Hijau Matcha", 15000, 15));
        daftarProduk.add(new Produk("P003", "Roti Bakar Cokelat", 12000, 10));
        daftarProduk.add(new Produk("P004", "Air Mineral 600ml", 4000, 50));
        daftarProduk.add(new Produk("P005", "Snack Kentang Goreng", 10000, 25));
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("🌱 myPOS - Sistem Kasir Modern");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Pilih barang di kiri ➔ Masuk ke keranjang di kanan");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(COLOR_SECONDARY);

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        mainPanel.setBackground(COLOR_BG);

        // Panel Kiri: Produk
        JPanel panelProduk = new JPanel(new BorderLayout(5, 5));
        panelProduk.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 2), " 📦 Daftar Produk Ready ", 0, 0,
                new Font("Segoe UI", Font.BOLD, 14), COLOR_PRIMARY));
        panelProduk.setBackground(Color.WHITE);

        String[] colProduk = {"Kode", "Nama Barang", "Harga", "Stok"};
        tableModelProduk = new DefaultTableModel(colProduk, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableProduk = new JTable(tableModelProduk);
        formatTable(tableProduk);
        refreshTableProduk();

        JButton btnTambahKeranjang = new JButton("➕ Tambah ke Keranjang");
        styleButton(btnTambahKeranjang, COLOR_ACCENT);
        btnTambahKeranjang.addActionListener(e -> {
            int selectedRow = tableProduk.getSelectedRow();
            if (selectedRow >= 0) {
                Produk p = daftarProduk.get(selectedRow);
                tambahKeKeranjang(p);
            } else {
                JOptionPane.showMessageDialog(this, "Klik/Pilih produk dari tabel terlebih dahulu!", "Petunjuk", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panelProduk.add(new JScrollPane(tableProduk), BorderLayout.CENTER);
        panelProduk.add(btnTambahKeranjang, BorderLayout.SOUTH);

        // Panel Kanan: Keranjang
        JPanel panelKeranjang = new JPanel(new BorderLayout(5, 5));
        panelKeranjang.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 2), " 🛒 Keranjang Belanja ", 0, 0,
                new Font("Segoe UI", Font.BOLD, 14), COLOR_PRIMARY));
        panelKeranjang.setBackground(Color.WHITE);

        String[] colKeranjang = {"Nama Barang", "Qty", "Harga", "Subtotal"};
        tableModelKeranjang = new DefaultTableModel(colKeranjang, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableKeranjang = new JTable(tableModelKeranjang);
        formatTable(tableKeranjang);

        JButton btnHapusItem = new JButton("🗑️ Hapus Item Terpilih");
        styleButton(btnHapusItem, COLOR_DANGER);
        btnHapusItem.addActionListener(e -> {
            int selectedRow = tableKeranjang.getSelectedRow();
            if (selectedRow >= 0) {
                hapusDariKeranjang(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih barang di keranjang yang ingin dihapus!", "Petunjuk", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panelKeranjang.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);
        panelKeranjang.add(btnHapusItem, BorderLayout.SOUTH);

        mainPanel.add(panelProduk);
        mainPanel.add(panelKeranjang);
        return mainPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout(15, 15));
        footer.setBorder(new EmptyBorder(10, 20, 20, 20));
        footer.setBackground(COLOR_BG);

        JPanel panelHitung = new JPanel(new GridLayout(3, 2, 10, 8));
        panelHitung.setBackground(COLOR_BG);

        JLabel lblTextTotal = new JLabel("TOTAL BELANJA:");
        lblTextTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalHarga.setForeground(COLOR_PRIMARY);

        JLabel lblTextBayar = new JLabel("UANG DIBAYAR (Rp):");
        lblTextBayar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtBayar = new JTextField();
        txtBayar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtBayar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                hitungKembalian();
            }
        });

        JLabel lblTextKembali = new JLabel("KEMBALIAN:");
        lblTextKembali.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblKembalian.setForeground(Color.DARK_GRAY);

        panelHitung.add(lblTextTotal);
        panelHitung.add(lblTotalHarga);
        panelHitung.add(lblTextBayar);
        panelHitung.add(txtBayar);
        panelHitung.add(lblTextKembali);
        panelHitung.add(lblKembalian);

        JButton btnSelesai = new JButton("✅ SELESAI TRANSAKSI & CETAK STRUK");
        btnSelesai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        styleButton(btnSelesai, COLOR_PRIMARY);
        btnSelesai.setPreferredSize(new Dimension(300, 50));
        btnSelesai.addActionListener(e -> prosesTransaksi());

        footer.add(panelHitung, BorderLayout.CENTER);
        footer.add(btnSelesai, BorderLayout.EAST);

        return footer;
    }

    private void refreshTableProduk() {
        tableModelProduk.setRowCount(0);
        for (Produk p : daftarProduk) {
            tableModelProduk.addRow(new Object[]{
                    p.getId(), p.getNama(), rupiahFormat.format(p.getHarga()), p.getStok()
            });
        }
    }

    private void tambahKeKeranjang(Produk p) {
        if (p.getStok() <= 0) {
            JOptionPane.showMessageDialog(this, "Stok barang ini habis!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ada = false;
        for (CartItem item : keranjang) {
            if (item.getProduk().getId().equals(p.getId())) {
                if (item.getJumlah() + 1 > p.getStok()) {
                    JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item.setJumlah(item.getJumlah() + 1);
                ada = true;
                break;
            }
        }

        if (!ada) {
            keranjang.add(new CartItem(p, 1));
        }

        updateTampilanKeranjang();
    }

    private void hapusDariKeranjang(int index) {
        keranjang.remove(index);
        updateTampilanKeranjang();
    }

    private void updateTampilanKeranjang() {
        tableModelKeranjang.setRowCount(0);
        double total = 0;

        for (CartItem item : keranjang) {
            tableModelKeranjang.addRow(new Object[]{
                    item.getProduk().getNama(),
                    item.getJumlah(),
                    rupiahFormat.format(item.getProduk().getHarga()),
                    rupiahFormat.format(item.getSubtotal())
            });
            total += item.getSubtotal();
        }

        lblTotalHarga.setText(rupiahFormat.format(total));
        hitungKembalian();
    }

    private double getTotalBelanja() {
        double total = 0;
        for (CartItem item : keranjang) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void hitungKembalian() {
        try {
            double total = getTotalBelanja();
            String inputBayar = txtBayar.getText().trim();
            if (inputBayar.isEmpty()) {
                lblKembalian.setText("Rp 0");
                return;
            }

            double bayar = Double.parseDouble(inputBayar);
            double kembalian = bayar - total;

            if (kembalian < 0) {
                lblKembalian.setText("Uang Kurang!");
                lblKembalian.setForeground(COLOR_DANGER);
            } else {
                lblKembalian.setText(rupiahFormat.format(kembalian));
                lblKembalian.setForeground(COLOR_PRIMARY);
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Input Angka!");
            lblKembalian.setForeground(COLOR_DANGER);
        }
    }

    private void prosesTransaksi() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double total = getTotalBelanja();
            double bayar = Double.parseDouble(txtBayar.getText().trim());

            if (bayar < total) {
                JOptionPane.showMessageDialog(this, "Uang pembayaran kurang!", "Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (CartItem item : keranjang) {
                Produk p = item.getProduk();
                p.setStok(p.getStok() - item.getJumlah());
            }

            StringBuilder struk = new StringBuilder();
            struk.append("========== STRUK myPOS ==========\n");
            for (CartItem item : keranjang) {
                struk.append(item.getProduk().getNama())
                     .append(" x").append(item.getJumlah())
                     .append(" = ").append(rupiahFormat.format(item.getSubtotal()))
                     .append("\n");
            }
            struk.append("------------------------------------\n");
            struk.append("Total    : ").append(rupiahFormat.format(total)).append("\n");
            struk.append("Bayar    : ").append(rupiahFormat.format(bayar)).append("\n");
            struk.append("Kembali  : ").append(rupiahFormat.format(bayar - total)).append("\n");
            struk.append("====================================\n");
            struk.append("       Terima Kasih Belanja!       ");

            JOptionPane.showMessageDialog(this, new JTextArea(struk.toString()), "Transaksi Berhasil", JOptionPane.INFORMATION_MESSAGE);

            keranjang.clear();
            txtBayar.setText("");
            updateTampilanKeranjang();
            refreshTableProduk();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Masukkan nominal pembayaran yang valid!", "Error Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void formatTable(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOR_SECONDARY);
        table.getTableHeader().setForeground(Color.BLACK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    // MAIN METHOD UTAMA
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MyPOS().setVisible(true);
        });
    }
}