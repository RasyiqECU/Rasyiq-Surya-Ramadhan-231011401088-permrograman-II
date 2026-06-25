package aplikasitoko.ui.panel;

import aplikasitoko.data.DbConnection;
import aplikasitoko.model.User;
import aplikasitoko.ui.Theme;
import aplikasitoko.ui.UIFactory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class DashboardPanel extends JPanel {

    private final User currentUser;
    private JLabel lblTotalBarang, lblTotalCustomer, lblTotalTransaksi;

    public DashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. Bagian Atas: Ucapan Selamat Datang & Deskripsi Toko
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel lblWelcome = UIFactory.headerLabel("Dashboard Utama");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        
        JLabel lblUser = new JLabel("Selamat datang kembali, " + (currentUser != null ? currentUser.getNamaLengkap() : "Pengguna") + "!");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUser.setForeground(Color.GRAY);
        
        JLabel lblDesc = new JLabel("Toko Berkah Jaya — Solusi Aplikasi Toko Serbaguna Terintegrasi.");
        lblDesc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDesc.setForeground(new Color(0x3b82f6)); // Warna aksen biru
        
        headerPanel.add(lblWelcome);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblUser);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblDesc);
        
        add(headerPanel, BorderLayout.NORTH);

        // 2. Bagian Tengah: Susunan Kartu Informasi Ringkasan (Stats Card)
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        statsContainer.setOpaque(false);

        // Membuat 3 kartu dengan skema warna flat modern
        JPanel cardBarang = createStatsCard("Total Produk", "0 Items", new Color(0x1e3a8a));   // Biru Tua
        JPanel cardCustomer = createStatsCard("Total Pelanggan", "0 Orang", new Color(0x15803d)); // Hijau
        JPanel cardTransaksi = createStatsCard("Total Transaksi", "0 Sukses", new Color(0xb45309)); // Oranye

        statsContainer.add(cardBarang);
        statsContainer.add(cardCustomer);
        statsContainer.add(cardTransaksi);

        // Tempatkan kartu di panel tengah dengan pembungkus agar ukurannya proporsional
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(statsContainer, BorderLayout.NORTH);
        
        add(centerWrapper, BorderLayout.CENTER);

        // Picu pemuatan statistik dari database
        refreshData();
    }

    // Fungsi pembentuk struktur UI Card agar mirip dengan dashboard acuan
    private JPanel createStatsCard(String title, String defaultValue, Color bgGradient) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgGradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(0, 140));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblValue = new JLabel(defaultValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        // Simpan referensi JLabel ke variabel kelas agar bisa di-update nilainya kemudian
        if (title.contains("Produk")) {
            lblTotalBarang = lblValue;
        } else if (title.contains("Pelanggan")) {
            lblTotalCustomer = lblValue;
        } else if (title.contains("Transaksi")) {
            lblTotalTransaksi = lblValue;
        }

        return card;
    }

    // Fungsi untuk menghitung row database secara realtime
    public void refreshData() {
        try (Connection c = DbConnection.getConnection()) {
            // Hitung Barang
            try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM tb_barang")) {
                if (r.next()) lblTotalBarang.setText(r.getInt(1) + " Produk");
            }
            // Hitung Customer
            try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM tb_customer")) {
                if (r.next()) lblTotalCustomer.setText(r.getInt(1) + " Orang");
            }
            // Hitung Transaksi
            try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM tb_penjualan")) {
                if (r.next()) lblTotalTransaksi.setText(r.getInt(1) + " Sukses");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}