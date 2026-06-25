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

public class PenggunaPanel extends JPanel {

    private JTextField tfId, tfUsername, tfNama;
    private JPasswordField tfPassword;
    private JComboBox<String> cbLevel;
    private JTable table;
    private DefaultTableModel tableModel;

    public PenggunaPanel() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(UIFactory.headerLabel("Manajemen Data Pengguna"), BorderLayout.NORTH);

        JPanel tableCard = UIFactory.card();
        tableCard.setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Nama Lengkap", "Level Akses"}, 0);
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
                tfUsername.setText(tableModel.getValueAt(r, 1).toString());
                tfPassword.setText(""); 
                tfNama.setText(tableModel.getValueAt(r, 2).toString());
                cbLevel.setSelectedItem(tableModel.getValueAt(r, 3).toString());
            }
        });
    }

    private JPanel buildFormPanel() {
        JPanel formCard = UIFactory.card();
        formCard.setPreferredSize(new Dimension(320, 0));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        tfId = UIFactory.textField(20); tfId.setEditable(false);
        tfId.setBackground(new Color(240, 240, 240));

        tfUsername = UIFactory.textField(20);
        // --- VALIDASI USERNAME (TIDAK BOLEH SPASI) ---
        tfUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Memeriksa apakah karakter yang diketik BUKAN huruf kecil, BUKAN angka, BUKAN titik, dan BUKAN underscore
                if (!(Character.isLowerCase(c) || Character.isDigit(c) || c == '.' || c == '_')) {
                    e.consume(); // Blokir karakter yang tidak sesuai
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(PenggunaPanel.this, 
                        "Username hanya boleh menggunakan huruf kecil, angka, titik (.), dan underscore (_)", 
                        "Input Tidak Valid", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        tfPassword = new JPasswordField(20);
        
        tfNama = UIFactory.textField(20);
        // --- VALIDASI NAMA (HANYA HURUF DAN SPASI) ---
        tfNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Jika karakter BUKAN huruf, BUKAN spasi, dan BUKAN tombol backspace/delete -> Blokir!
                if (!Character.isLetter(c) && !Character.isWhitespace(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Membatalkan ketikan
                    Toolkit.getDefaultToolkit().beep(); // (Opsional) Bunyi peringatan
                    JOptionPane.showMessageDialog(PenggunaPanel.this, "Nama Lengkap hanya boleh berisi huruf dan spasi!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cbLevel = new JComboBox<>(new String[]{"Admin", "Petugas"});

        Dimension maxDim = new Dimension(Integer.MAX_VALUE, 35);
        tfId.setMaximumSize(maxDim); tfUsername.setMaximumSize(maxDim); tfPassword.setMaximumSize(maxDim);
        tfNama.setMaximumSize(maxDim); cbLevel.setMaximumSize(maxDim);

        formCard.add(UIFactory.formLabel("ID User (Otomatis)")); formCard.add(tfId); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Username")); formCard.add(tfUsername); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Password")); formCard.add(tfPassword); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Nama Lengkap")); formCard.add(tfNama); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Level Akses")); formCard.add(cbLevel); formCard.add(Box.createVerticalStrut(15));

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
        try (Connection c = DbConnection.getConnection(); Statement s = c.createStatement(); 
             ResultSet r = s.executeQuery("SELECT * FROM tb_user ORDER BY id_user ASC")) {
            while (r.next()) tableModel.addRow(new Object[]{r.getInt("id_user"), r.getString("username"), r.getString("nama_lengkap"), r.getString("level")});
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        tfId.setText(""); tfUsername.setText(""); tfPassword.setText(""); tfNama.setText(""); table.clearSelection();
    }

    private void executeTambah() {
        String pwd = new String(tfPassword.getPassword());
        if(tfUsername.getText().isEmpty() || tfNama.getText().isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data tidak boleh ada yang kosong!");
            return;
        }
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("INSERT INTO tb_user (username, nama_lengkap, level, password) VALUES (?, ?, ?, ?)")) {
            p.setString(1, tfUsername.getText()); p.setString(2, tfNama.getText()); p.setString(3, cbLevel.getSelectedItem().toString()); p.setString(4, pwd);
            p.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "User Berhasil Ditambahkan!"); 
            refreshData();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Gagal menambah data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeEdit() {
        if(tfId.getText().isEmpty()) return;
        String pwd = new String(tfPassword.getPassword());
        String sql = pwd.isEmpty() ? "UPDATE tb_user SET username=?, nama_lengkap=?, level=? WHERE id_user=?" : "UPDATE tb_user SET username=?, nama_lengkap=?, level=?, password=? WHERE id_user=?";
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, tfUsername.getText()); p.setString(2, tfNama.getText()); p.setString(3, cbLevel.getSelectedItem().toString());
            if (pwd.isEmpty()) { p.setInt(4, Integer.parseInt(tfId.getText())); } 
            else { p.setString(4, pwd); p.setInt(5, Integer.parseInt(tfId.getText())); }
            p.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "User Berhasil Diupdate!"); 
            refreshData();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if(tfId.getText().isEmpty()) return;
        
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus user ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM tb_user WHERE id_user=?")) {
                p.setInt(1, Integer.parseInt(tfId.getText())); p.executeUpdate(); 
                JOptionPane.showMessageDialog(this, "User Berhasil Dihapus!"); 
                refreshData();
            } catch (Exception e) { 
                e.printStackTrace(); 
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}