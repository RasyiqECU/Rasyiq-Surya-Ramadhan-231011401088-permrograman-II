package aplikasitoko.ui.panel;

import aplikasitoko.data.DbConnection;
import aplikasitoko.ui.Theme;
import aplikasitoko.ui.UIFactory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*; // IMPORT BARU UNTUK VALIDASI KEYBOARD
import java.sql.*;

public class KategoriPanel extends JPanel {

    private JTextField tfId, tfNamaKategori;
    private JTable table;
    private DefaultTableModel tableModel;

    public KategoriPanel() {
        setLayout(new BorderLayout(15, 15)); setOpaque(false); setBorder(new EmptyBorder(20, 20, 20, 20));
        add(UIFactory.headerLabel("Master Data Kategori"), BorderLayout.NORTH);

        JPanel tableCard = UIFactory.card(); tableCard.setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID Kategori", "Nama Kategori"}, 0);
        table = new JTable(tableModel); table.setRowHeight(Theme.ROW_HEIGHT);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(tableCard, BorderLayout.CENTER); add(buildFormPanel(), BorderLayout.EAST); refreshData();

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                tfId.setText(tableModel.getValueAt(r, 0).toString());
                tfNamaKategori.setText(tableModel.getValueAt(r, 1).toString());
            }
        });
    }

    private JPanel buildFormPanel() {
        JPanel formCard = UIFactory.card(); formCard.setPreferredSize(new Dimension(320, 0)); formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        tfId = UIFactory.textField(20); tfId.setEditable(false); tfId.setBackground(new Color(240, 240, 240));
        
        tfNamaKategori = UIFactory.textField(20);
        // --- VALIDASI NAMA KATEGORI (HANYA HURUF DAN SPASI) ---
        tfNamaKategori.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Jika karakter BUKAN huruf, BUKAN spasi, dan BUKAN tombol backspace/delete -> Blokir!
                if (!Character.isLetter(c) && !Character.isWhitespace(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Membatalkan ketikan
                    Toolkit.getDefaultToolkit().beep(); // (Opsional) Bunyi peringatan
                    JOptionPane.showMessageDialog(KategoriPanel.this, "Nama Kategori hanya boleh berisi huruf dan spasi!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        Dimension maxDim = new Dimension(Integer.MAX_VALUE, 35); 
        tfId.setMaximumSize(maxDim); tfNamaKategori.setMaximumSize(maxDim);

        formCard.add(UIFactory.formLabel("ID Kategori (Otomatis)")); formCard.add(tfId); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Nama Kategori")); formCard.add(tfNamaKategori); formCard.add(Box.createVerticalStrut(15));

        JButton btnTambah = UIFactory.primaryButton("Tambah"); JButton btnEdit = UIFactory.outlineButton("Edit");
        JButton btnHapus = UIFactory.dangerButton("Hapus"); JButton btnBatal = UIFactory.outlineButton("Batal");
        
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); pBtn.setOpaque(false); pBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBtn.add(btnTambah); pBtn.add(btnEdit); pBtn.add(btnHapus); pBtn.add(btnBatal); formCard.add(pBtn);

        btnTambah.addActionListener(e -> executeTambah()); btnEdit.addActionListener(e -> executeEdit());
        btnHapus.addActionListener(e -> hapusData()); btnBatal.addActionListener(e -> resetForm());
        return formCard;
    }

    public void refreshData() {
        tableModel.setRowCount(0); resetForm();
        try (Connection c = DbConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT * FROM tb_kategori")) {
            while (r.next()) tableModel.addRow(new Object[]{r.getInt("id_kategori"), r.getString("nama_kategori")});
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() { tfId.setText(""); tfNamaKategori.setText(""); table.clearSelection(); }

    private void executeTambah() {
        if(tfNamaKategori.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kategori tidak boleh kosong!");
            return;
        }
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("INSERT INTO tb_kategori (nama_kategori) VALUES (?)")) {
            p.setString(1, tfNamaKategori.getText()); p.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Kategori Berhasil Ditambahkan!");
            refreshData();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Gagal menambah data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeEdit() {
        if(tfId.getText().isEmpty()) return;
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE tb_kategori SET nama_kategori=? WHERE id_kategori=?")) {
            p.setString(1, tfNamaKategori.getText()); p.setInt(2, Integer.parseInt(tfId.getText())); p.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Kategori Berhasil Diupdate!");
            refreshData();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if(tfId.getText().isEmpty()) return;
        
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus kategori ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM tb_kategori WHERE id_kategori=?")) {
                p.setInt(1, Integer.parseInt(tfId.getText())); p.executeUpdate(); 
                JOptionPane.showMessageDialog(this, "Kategori Berhasil Dihapus!");
                refreshData();
            } catch (Exception e) { 
                e.printStackTrace(); 
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}