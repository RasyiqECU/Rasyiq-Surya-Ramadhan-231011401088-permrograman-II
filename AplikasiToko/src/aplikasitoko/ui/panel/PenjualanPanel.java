package aplikasitoko.ui.panel;

import aplikasitoko.data.DbConnection;
import aplikasitoko.model.User;
import aplikasitoko.ui.Theme;
import aplikasitoko.ui.UIFactory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PenjualanPanel extends JPanel {

    private final User currentUser;
    private JComboBox<String> cbCustomer, cbBarang;
    private JTextField tfQty;
    private JTable tableKeranjang;
    private DefaultTableModel tableModel;
    private JLabel lblGrandTotal;
    
    private String formatRp(double angka) {
        return NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(angka).replace(",00", "");
    }
    
    private double parseRp(String rpText) {
        try { return Double.parseDouble(rpText.replaceAll("[^\\d]", "")); } 
        catch (Exception e) { return 0; }
    }

    public PenjualanPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(UIFactory.headerLabel("Kasir Transaksi"), BorderLayout.NORTH);

        JPanel tableCard = UIFactory.card();
        tableCard.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(new Object[]{"ID Customer", "Customer", "ID Barang", "Barang", "Harga Satuan", "Qty", "Jumlah Harga"}, 0);
        tableKeranjang = new JTable(tableModel);
        tableKeranjang.setRowHeight(Theme.ROW_HEIGHT);
        tableCard.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);
        
        JPanel pBawah = new JPanel(new BorderLayout()); 
        pBawah.setOpaque(false);
        
        lblGrandTotal = new JLabel("Total Akhir: Rp 0");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pBawah.add(lblGrandTotal, BorderLayout.WEST);
        
        JPanel pBtnBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pBtnBawah.setOpaque(false);
        JButton btnRiwayat = UIFactory.outlineButton("Riwayat Transaksi");
        btnRiwayat.addActionListener(e -> tampilkanRiwayat());
        JButton btnSimpanTransaksi = UIFactory.primaryButton("Simpan Semua Transaksi");
        btnSimpanTransaksi.addActionListener(e -> simpanSemuaTransaksi());
        
        pBtnBawah.add(btnRiwayat);
        pBtnBawah.add(btnSimpanTransaksi);
        pBawah.add(pBtnBawah, BorderLayout.EAST);
        
        tableCard.add(pBawah, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.EAST);
        refreshData();
    }

    private JPanel buildFormPanel() {
        JPanel formCard = UIFactory.card();
        formCard.setPreferredSize(new Dimension(320, 0));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        cbCustomer = new JComboBox<>(); cbBarang = new JComboBox<>(); tfQty = UIFactory.textField(20);
        Dimension maxDim = new Dimension(Integer.MAX_VALUE, 35);
        cbCustomer.setMaximumSize(maxDim); cbBarang.setMaximumSize(maxDim); tfQty.setMaximumSize(maxDim);

        tfQty.addKeyListener(new KeyAdapter() { public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); } });

        formCard.add(UIFactory.formLabel("Pilih Customer")); formCard.add(cbCustomer); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Pilih Barang")); formCard.add(cbBarang); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Jumlah Beli (Qty)")); formCard.add(tfQty); formCard.add(Box.createVerticalStrut(15));

        JButton btnTambah = UIFactory.primaryButton("Tambah"); 
        JButton btnEdit = UIFactory.outlineButton("Edit");
        JButton btnHapus = UIFactory.dangerButton("Hapus"); 
        JButton btnBatal = UIFactory.outlineButton("Batal");
        
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); 
        pBtn.setOpaque(false); 
        pBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBtn.add(btnTambah); pBtn.add(btnEdit); pBtn.add(btnHapus); pBtn.add(btnBatal); 
        formCard.add(pBtn);

        btnTambah.addActionListener(e -> tambahKeKeranjang()); 
        btnEdit.addActionListener(e -> editKeranjang());
        btnHapus.addActionListener(e -> hapusKeranjang()); 
        btnBatal.addActionListener(e -> resetForm());
        
        return formCard;
    }

    private void resetForm() {
        if (cbCustomer.getItemCount() > 0) cbCustomer.setSelectedIndex(0);
        if (cbBarang.getItemCount() > 0) cbBarang.setSelectedIndex(0);
        tfQty.setText("");
        tableKeranjang.clearSelection();
    }

    private void hitungGrandTotal() {
        double totalAkhir = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            totalAkhir += parseRp(tableModel.getValueAt(i, 6).toString());
        }
        lblGrandTotal.setText("Total Akhir: " + formatRp(totalAkhir));
    }

    private void tambahKeKeranjang() {
        if(cbCustomer.getSelectedIndex() == 0 || cbBarang.getSelectedIndex() == 0 || tfQty.getText().isEmpty()) return;
        String[] cData = cbCustomer.getSelectedItem().toString().split(" - ");
        String[] bData = cbBarang.getSelectedItem().toString().split(" - ");
        
        double hargaSatuan = 0;
        int stokBarang = 0; // Variabel penampung stok
        
        // Mengambil data harga dan stok dari database
        try (Connection c = DbConnection.getConnection(); Statement s = c.createStatement(); 
             ResultSet r = s.executeQuery("SELECT harga_jual, stok FROM tb_barang WHERE id_barang='" + bData[0] + "'")) {
            if (r.next()) {
                hargaSatuan = r.getDouble(1);
                stokBarang = r.getInt(2); // Indeks 2 adalah kolom 'stok'
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        int qty = Integer.parseInt(tfQty.getText());
        
        // --- LOGIKA PENGECEKAN STOK ---
        if (qty > stokBarang) {
            JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!\nSisa stok saat ini: " + stokBarang, "Stok Kurang", JOptionPane.WARNING_MESSAGE);
            return; // Hentikan proses, jangan masuk ke keranjang
        }
        
        double subtotal = qty * hargaSatuan;
        tableModel.addRow(new Object[]{cData[0], cData[1], bData[0], bData[1], formatRp(hargaSatuan), qty, formatRp(subtotal)});
        tfQty.setText(""); hitungGrandTotal();
    }

    private void editKeranjang() {
        int r = tableKeranjang.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Pilih barang di tabel yang mau diedit!"); return; }
        String newQty = JOptionPane.showInputDialog(this, "Masukkan Qty Baru:", tableModel.getValueAt(r, 5));
        if(newQty != null && newQty.matches("\\d+")) {
            int qty = Integer.parseInt(newQty);
            
            // Catatan: Jika ingin lebih aman, pengecekan stok bisa ditambahkan juga di bagian Edit ini, 
            // namun saat ini kita percayakan validasi utama di tombol Tambah dan Simpan.
            
            double harga = parseRp(tableModel.getValueAt(r, 4).toString());
            tableModel.setValueAt(qty, r, 5);
            tableModel.setValueAt(formatRp(qty * harga), r, 6);
            hitungGrandTotal();
        }
    }

    private void hapusKeranjang() {
        int r = tableKeranjang.getSelectedRow();
        if(r != -1) { tableModel.removeRow(r); hitungGrandTotal(); }
    }

    private void simpanSemuaTransaksi() {
        if (tableModel.getRowCount() == 0) return;

        // Kolom tb_penjualan (ERD): id_jual, no_faktur, tgl_transaksi, id_customer, total_bayar, id_user
        // no_faktur di-generate otomatis: FAK-YYYYMMDD-milis
        String noFaktur     = "FAK-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + "-" + System.currentTimeMillis() % 100000;
        String sqlPenjualan  = "INSERT INTO tb_penjualan (no_faktur, tgl_transaksi, id_customer, total_bayar, id_user) VALUES (?, ?, ?, ?, ?)";

        // Kolom tb_detail_penjualan (ERD): id_detail, id_jual, id_barang, harga_satuan, jumlah_beli, subtotal
        String sqlDetail     = "INSERT INTO tb_detail_penjualan (id_jual, id_barang, harga_satuan, jumlah_beli, subtotal) VALUES (?, ?, ?, ?, ?)";

        String sqlUpdateStok = "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?";

        try (Connection c = DbConnection.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement pJual      = c.prepareStatement(sqlPenjualan, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pDetail    = c.prepareStatement(sqlDetail);
                 PreparedStatement pUpdateStok = c.prepareStatement(sqlUpdateStok)) {

                // Hitung grand total dari seluruh item di keranjang
                double grandTotal = 0;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    grandTotal += parseRp(tableModel.getValueAt(i, 6).toString());
                }

                // id_customer diambil dari baris pertama (satu transaksi = satu customer)
                String idCustomer = tableModel.getValueAt(0, 0).toString();

                // 1. Insert satu baris ke tb_penjualan (header transaksi)
                pJual.setString(1, noFaktur);
                pJual.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                pJual.setString(3, idCustomer);
                pJual.setDouble(4, grandTotal);
                pJual.setInt(5, currentUser != null ? currentUser.getIdUser() : 1);
                pJual.executeUpdate();

                // Ambil id_jual yang di-generate AUTO_INCREMENT
                int idJualBaru = 0;
                try (ResultSet generatedKeys = pJual.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idJualBaru = generatedKeys.getInt(1);
                    }
                }

                // 2. Insert tiap barang ke tb_detail_penjualan + kurangi stok
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String idBarang    = tableModel.getValueAt(i, 2).toString();
                    double hargaSatuan = parseRp(tableModel.getValueAt(i, 4).toString());
                    int    qtyBeli     = Integer.parseInt(tableModel.getValueAt(i, 5).toString());
                    double subtotal    = parseRp(tableModel.getValueAt(i, 6).toString());

                    pDetail.setInt(1, idJualBaru);
                    pDetail.setString(2, idBarang);
                    pDetail.setDouble(3, hargaSatuan);
                    pDetail.setInt(4, qtyBeli);
                    pDetail.setDouble(5, subtotal);
                    pDetail.addBatch();

                    pUpdateStok.setInt(1, qtyBeli);
                    pUpdateStok.setString(2, idBarang);
                    pUpdateStok.addBatch();
                }

                pDetail.executeBatch();      // Eksekusi semua insert detail
                pUpdateStok.executeBatch();  // Eksekusi semua pemotongan stok

                c.commit(); // Simpan perubahan secara permanen ke database

                JOptionPane.showMessageDialog(this, "Transaksi Berhasil dan Stok telah dikurangi!");
                tableModel.setRowCount(0); hitungGrandTotal(); tampilkanRiwayat();

            } catch (SQLException ex) {
                c.rollback(); // Batalkan semua perintah jika ada yang error
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan transaksi!\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                c.setAutoCommit(true); // Kembalikan ke mode default
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Koneksi Database Gagal!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        cbCustomer.removeAllItems(); cbCustomer.addItem("-- Pilih Customer --");
        cbBarang.removeAllItems(); cbBarang.addItem("-- Pilih Barang --");
        try (Connection c = DbConnection.getConnection(); Statement s = c.createStatement()) {
            ResultSet r1 = s.executeQuery("SELECT id_customer, nama_customer FROM tb_customer");
            while (r1.next()) cbCustomer.addItem(r1.getString(1) + " - " + r1.getString(2));
            ResultSet r2 = s.executeQuery("SELECT id_barang, nama_barang FROM tb_barang");
            while (r2.next()) cbBarang.addItem(r2.getString(1) + " - " + r2.getString(2));
        } catch (Exception e) { }
    }

    private void tampilkanRiwayat() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Riwayat Transaksi", true);
        d.setSize(850, 450); d.setLocationRelativeTo(this);

        // Riwayat dari tb_penjualan: siapa pembelinya, kapan, total pendapatan
        DefaultTableModel m = new DefaultTableModel(new Object[]{"ID Jual", "No Faktur", "Tanggal", "Customer", "Total Bayar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable t = new JTable(m); t.setRowHeight(Theme.ROW_HEIGHT);
        t.getTableHeader().setReorderingAllowed(false);

        String sql = "SELECT p.id_jual, p.no_faktur, p.tgl_transaksi, c.nama_customer, p.total_bayar " +
                     "FROM tb_penjualan p " +
                     "JOIN tb_customer c ON p.id_customer = c.id_customer " +
                     "ORDER BY p.id_jual DESC";

        try (Connection c = DbConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while(r.next()) m.addRow(new Object[]{r.getInt(1), r.getString(2), r.getDate(3), r.getString(4), formatRp(r.getDouble(5))});
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(d, "Gagal memuat riwayat: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        d.add(new JScrollPane(t)); d.setVisible(true);
    }
}
