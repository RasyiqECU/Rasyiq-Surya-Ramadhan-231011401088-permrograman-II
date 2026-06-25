package aplikasitoko.ui.panel;

import aplikasitoko.data.DbConnection;
import aplikasitoko.model.User;
import aplikasitoko.ui.Theme;
import aplikasitoko.ui.UIFactory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*; // IMPORT BARU UNTUK VALIDASI KEYBOARD
import java.sql.*;

public class CustomerPanel extends JPanel {

    private JTextField tfId, tfNama, tfTelepon;
    private JTextArea taAlamat;
    private JTable table;
    private DefaultTableModel tableModel;
    private final User currentUser;

    public CustomerPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(15, 15)); setOpaque(false); setBorder(new EmptyBorder(20, 20, 20, 20));
        add(UIFactory.headerLabel("Master Data Customer"), BorderLayout.NORTH);

        JPanel tableCard = UIFactory.card(); tableCard.setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID Customer", "Nama Customer", "Alamat", "Telepon"}, 0);
        table = new JTable(tableModel); table.setRowHeight(Theme.ROW_HEIGHT);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(tableCard, BorderLayout.CENTER); add(buildFormPanel(), BorderLayout.EAST); 
        
        refreshData();

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                tfId.setText(tableModel.getValueAt(r, 0).toString());
                tfNama.setText(tableModel.getValueAt(r, 1).toString());
                taAlamat.setText(tableModel.getValueAt(r, 2).toString());
                tfTelepon.setText(tableModel.getValueAt(r, 3).toString());
            }
        });
    }

    private boolean isAdmin() {
        return currentUser != null && "Admin".equalsIgnoreCase(currentUser.getLevel());
    }

    private JPanel buildFormPanel() {
        JPanel formCard = UIFactory.card(); formCard.setPreferredSize(new Dimension(320, 0)); formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        tfId = UIFactory.textField(20); tfId.setEditable(false); tfId.setBackground(new Color(240, 240, 240));
        
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
                    JOptionPane.showMessageDialog(CustomerPanel.this, "Nama Customer hanya boleh berisi huruf dan spasi!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        tfTelepon = UIFactory.textField(20);
        // --- VALIDASI TELEPON (HANYA ANGKA) ---
        tfTelepon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Jika karakter BUKAN angka dan BUKAN tombol backspace/delete -> Blokir!
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Membatalkan ketikan
                    Toolkit.getDefaultToolkit().beep(); // (Opsional) Bunyi peringatan
                    JOptionPane.showMessageDialog(CustomerPanel.this, "Nomor Telepon hanya boleh berisi angka!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        taAlamat = new JTextArea(3, 20); 
        taAlamat.setLineWrap(true);       
        taAlamat.setWrapStyleWord(true);  
        taAlamat.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        
        JScrollPane scrollAlamat = new JScrollPane(taAlamat);
        scrollAlamat.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); 

        Dimension maxDim = new Dimension(Integer.MAX_VALUE, 35);
        tfId.setMaximumSize(maxDim); tfNama.setMaximumSize(maxDim); tfTelepon.setMaximumSize(maxDim);
        
        scrollAlamat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75)); 

        formCard.add(UIFactory.formLabel("ID Customer (Otomatis)")); formCard.add(tfId); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Nama Customer")); formCard.add(tfNama); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Alamat")); formCard.add(scrollAlamat); formCard.add(Box.createVerticalStrut(10));
        formCard.add(UIFactory.formLabel("Telepon")); formCard.add(tfTelepon); formCard.add(Box.createVerticalStrut(15));

        JButton btnTambah = UIFactory.primaryButton("Tambah"); JButton btnEdit = UIFactory.outlineButton("Edit");
        JButton btnHapus = UIFactory.dangerButton("Hapus"); JButton btnBatal = UIFactory.outlineButton("Batal");
        
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); pBtn.setOpaque(false); pBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pBtn.add(btnTambah); pBtn.add(btnEdit);
        // Tombol Hapus hanya tampil untuk Admin
        if (isAdmin()) {
            pBtn.add(btnHapus);
        }
        pBtn.add(btnBatal); formCard.add(pBtn);

        btnTambah.addActionListener(e -> executeTambah()); btnEdit.addActionListener(e -> executeEdit());
        btnHapus.addActionListener(e -> hapusData()); btnBatal.addActionListener(e -> resetForm());
        return formCard;
    }

    private void generateNextId() {
        try (Connection c = DbConnection.getConnection(); 
             Statement s = c.createStatement(); 
             ResultSet r = s.executeQuery("SELECT MAX(id_customer) AS max_id FROM tb_customer")) {
            
            if (r.next()) {
                String maxId = r.getString("max_id");
                if (maxId != null && maxId.startsWith("CST")) {
                    int idNum = Integer.parseInt(maxId.substring(3));
                    idNum++;
                    tfId.setText(String.format("CST%03d", idNum));
                } else {
                    tfId.setText("CST001");
                }
            } else {
                tfId.setText("CST001");
            }
        } catch (Exception e) { 
            e.printStackTrace();
            tfId.setText("CST001"); 
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0); 
        resetForm(); 
        
        try (Connection c = DbConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT * FROM tb_customer")) {
            while (r.next()) tableModel.addRow(new Object[]{r.getString("id_customer"), r.getString("nama_customer"), r.getString("alamat"), r.getString("telepon")});
        } catch (Exception e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() { 
        tfNama.setText(""); taAlamat.setText(""); tfTelepon.setText(""); 
        table.clearSelection(); 
        generateNextId();
    }

    private void executeTambah() {
        if (tfNama.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Customer tidak boleh kosong!");
            return;
        }
        
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("INSERT INTO tb_customer (id_customer, nama_customer, alamat, telepon) VALUES (?, ?, ?, ?)")) {
            p.setString(1, tfId.getText());
            p.setString(2, tfNama.getText()); 
            p.setString(3, taAlamat.getText()); 
            p.setString(4, tfTelepon.getText()); 
            p.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Data berhasil ditambah!");
            refreshData(); 
        } catch (Exception ex) { 
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambah data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeEdit() {
        if(tfId.getText().isEmpty()) return;
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE tb_customer SET nama_customer=?, alamat=?, telepon=? WHERE id_customer=?")) {
            p.setString(1, tfNama.getText()); 
            p.setString(2, taAlamat.getText()); 
            p.setString(3, tfTelepon.getText()); 
            p.setString(4, tfId.getText()); 
            p.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!");
            refreshData(); 
        } catch (Exception ex) { 
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if(tfId.getText().isEmpty()) return;
        try (Connection c = DbConnection.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM tb_customer WHERE id_customer=?")) {
            p.setString(1, tfId.getText()); 
            p.executeUpdate(); 
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            refreshData(); 
        } catch (Exception e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}