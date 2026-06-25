package aplikasitoko.ui.panel;

import aplikasitoko.data.DbConnection;
import aplikasitoko.ui.Theme;
import aplikasitoko.ui.UIFactory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*; // IMPORT UNTUK VALIDASI KEYBOARD
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class BarangPanel extends JPanel {

    private JTextField tfId, tfNama, tfSatuan, tfHarga, tfStok;
    private JComboBox<String> cbKategori;
    private JTable table;
    private DefaultTableModel tableModel;

    private String formatRp(double angka) {
        return NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(angka).replace(",00", "");
    }
    
    private double parseRp(String rpText) {
        try { return Double.parseDouble(rpText.replaceAll("[^\\d]", "")); } catch (Exception e) { return 0; }
    }

    public BarangPanel() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(UIFactory.headerLabel("Master Data Barang"), BorderLayout.NORTH);

        JPanel tableCard = UIFactory.card();
        tableCard.setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Kategori", "Nama Barang", "Satuan", "Harga Jual", "Stok"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(Theme.ROW_HEIGHT);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(tableCard, BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.EAST);

        refreshData();

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                tfId.setText(tableModel.getValueAt(r, 0).toString());
                cbKategori.setSelectedItem(tableModel.getValueAt(r, 1).toString());
                tfNama.setText(tableModel.getValueAt(r, 2).toString());
                tfSatuan.setText(tableModel.getValueAt(r, 3).toString());
                tfHarga.setText(String.valueOf((int) parseRp(tableModel.getValueAt(r, 4).toString())));
                tfStok.setText(tableModel.getValueAt(r, 5).toString());
            }
        });
    }

    private JPanel buildFormPanel() {
        JPanel formCard = UIFactory.card();
        formCard.setPreferredSize(new Dimension(320, 0)); 
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        tfId = UIFactory.textField(20); 
        tfId.setEditable(false); 
        tfId.setBackground(new Color(240, 240, 240)); 
        
        cbKategori = new JComboBox<>();
        
        // --- VALIDASI NAMA BARANG (TIDAK BOLEH ANGKA) ---
        tfNama = UIFactory.textField(20);
        tfNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Jika karakter BUKAN huruf, BUKAN spasi, dan BUKAN tombol backspace/delete -> Blokir!
                if (!Character.isLetter(c) && !Character.isWhitespace(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Membatalkan ketikan
                    Toolkit.getDefaultToolkit().beep(); // (Opsional) Bunyi peringatan
                    JOptionPane.showMessageDialog(BarangPanel.this, "Nama Barang hanya boleh berisi huruf dan spasi!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        tfSatuan = UIFactory.textField(20);
        tfSatuan.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Jika karakter BUKAN huruf, BUKAN spasi, dan BUKAN tombol backspace/delete -> Blokir!
                if (!Character.isLetter(c) && !Character.isWhitespace(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Membatalkan ketikan
                    Toolkit.getDefaultToolkit().beep(); // (Opsional) Bunyi peringatan
                    JOptionPane.showMessageDialog(BarangPanel.this, "satuan hanya boleh berisi huruf dan spasi!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // --- VALIDASI HARGA JUAL (HANYA BOLEH ANGKA) ---
        tfHarga = UIFactory.textField(20);
        tfHarga.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Jika BUKAN angka dan BUKAN tombol backspace/delete, maka batalkan/blokir
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(BarangPanel.this, "Harga Jual hanya boleh berisi angka!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // --- VALIDASI STOK (HANYA BOLEH ANGKA) ---
        tfStok = UIFactory.textField(20);
        tfStok.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Jika BUKAN angka dan BUKAN tombol backspace/delete, maka batalkan/blokir
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(BarangPanel.this, "Stok hanya boleh berisi angka!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        Dimension maxDim = new Dimension(Integer.MAX_VALUE, 35);
        tfId.setMaximumSize(maxDim); cbKategori.setMaximumSize(maxDim); tfNama.setMaximumSize(maxDim);
        tfSatuan.setMaximumSize(maxDim); tfHarga.setMaximumSize(maxDim); tfStok.setMaximumSize(maxDim);

        formCard.add(UIFactory.formLabel("ID Barang (Otomatis)")); formCard.add(tfId); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Kategori")); formCard.add(cbKategori); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Nama Barang")); formCard.add(tfNama); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Satuan")); formCard.add(tfSatuan); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Harga Jual")); formCard.add(tfHarga); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Stok")); formCard.add(tfStok); formCard.add(Box.createVerticalStrut(15));

        JButton btnTambah = UIFactory.primaryButton("Tambah"); JButton btnEdit = UIFactory.outlineButton("Edit");
        JButton btnHapus = UIFactory.dangerButton("Hapus"); JButton btnBatal = UIFactory.outlineButton("Batal");
        
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); pBtn.setOpaque(false); pBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBtn.add(btnTambah); pBtn.add(btnEdit); pBtn.add(btnHapus); pBtn.add(btnBatal); formCard.add(pBtn);

        btnTambah.addActionListener(e -> executeTambah()); btnEdit.addActionListener(e -> executeEdit());
        btnHapus.addActionListener(e -> doDelete()); btnBatal.addActionListener(e -> resetForm());
        return formCard;
    }

    private void generateNextId() {
        try (Connection c = DbConnection.getConnection(); 
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT MAX(id_barang) AS max_id FROM tb_barang")) {
            
            if (r.next()) {
                String lastId = r.getString("max_id");
                if (lastId != null && lastId.startsWith("BRG")) {
                    int num = Integer.parseInt(lastId.substring(3));
                    num++;
                    tfId.setText(String.format("BRG%03d", num));
                } else {
                    tfId.setText("BRG001");
                }
            } else {
                tfId.setText("BRG001");
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            tfId.setText("BRG001");
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0); 
        resetForm();
        
        cbKategori.removeAllItems(); cbKategori.addItem("-- Pilih Kategori --");
        try (Connection c = DbConnection.getConnection(); Statement s = c.createStatement()) {
            ResultSet rk = s.executeQuery("SELECT id_kategori, nama_kategori FROM tb_kategori");
            while (rk.next()) cbKategori.addItem(rk.getString(1) + " - " + rk.getString(2));
            
            ResultSet r = s.executeQuery("SELECT b.id_barang, k.nama_kategori, b.nama_barang, b.satuan, b.harga_jual, b.stok FROM tb_barang b LEFT JOIN tb_kategori k ON b.id_kategori = k.id_kategori");
            while (r.next()) {
                tableModel.addRow(new Object[]{r.getString(1), r.getString(2), r.getString(3), r.getString(4), formatRp(r.getDouble(5)), r.getInt(6)});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void resetForm() {
        tfNama.setText(""); tfSatuan.setText(""); tfHarga.setText(""); tfStok.setText(""); table.clearSelection();
        if (cbKategori.getItemCount() > 0) cbKategori.setSelectedIndex(0);
        generateNextId(); 
    }

    private void executeTambah() {
        if(tfNama.getText().isEmpty() || tfHarga.getText().isEmpty() || tfStok.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, Harga, dan Stok wajib diisi!");
            return;
        }
        
        String newId = tfId.getText(); 
        
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("INSERT INTO tb_barang (id_barang, id_kategori, nama_barang, satuan, harga_jual, stok) VALUES(?,?,?,?,?,?)")) {
            int idKat = cbKategori.getSelectedIndex() > 0 ? Integer.parseInt(cbKategori.getSelectedItem().toString().split(" - ")[0]) : 1;
            p.setString(1, newId); p.setInt(2, idKat); p.setString(3, tfNama.getText()); p.setString(4, tfSatuan.getText()); p.setDouble(5, Double.parseDouble(tfHarga.getText())); p.setInt(6, Integer.parseInt(tfStok.getText()));
            p.executeUpdate(); JOptionPane.showMessageDialog(this, "Barang Ditambahkan!"); refreshData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void executeEdit() {
        if(tfId.getText().isEmpty()) return;
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE tb_barang SET id_kategori=?, nama_barang=?, satuan=?, harga_jual=?, stok=? WHERE id_barang=?")) {
            int idKat = cbKategori.getSelectedIndex() > 0 ? Integer.parseInt(cbKategori.getSelectedItem().toString().split(" - ")[0]) : 1;
            p.setInt(1, idKat); p.setString(2, tfNama.getText()); p.setString(3, tfSatuan.getText()); p.setDouble(4, Double.parseDouble(tfHarga.getText())); p.setInt(5, Integer.parseInt(tfStok.getText())); p.setString(6, tfId.getText());
            p.executeUpdate(); JOptionPane.showMessageDialog(this, "Barang Diupdate!"); refreshData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void doDelete() {
        if(tfId.getText().isEmpty()) return;
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM tb_barang WHERE id_barang=?")) {
            p.setString(1, tfId.getText()); p.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang Dihapus!"); refreshData();
        } catch (Exception e) { e.printStackTrace(); }
    }
}