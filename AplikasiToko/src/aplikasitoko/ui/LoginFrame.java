package aplikasitoko.ui;

import aplikasitoko.data.DbConnection;
import aplikasitoko.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField tfUsername;
    private JPasswordField tfPassword;

    public LoginFrame() {
        setTitle("Login - Aplikasi Toko");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 380);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        panel.setBackground(new Color(240, 248, 255)); 

        JLabel lblShopName = new JLabel("TOKO BERKAH JAYA");
        lblShopName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblShopName.setForeground(new Color(0, 122, 255)); 
        lblShopName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Silakan Login");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.DARK_GRAY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        tfUsername = new JTextField(20);
        tfPassword = new JPasswordField(20);
        
        Dimension maxDim = new Dimension(Integer.MAX_VALUE, 35);
        tfUsername.setMaximumSize(maxDim);
        tfPassword.setMaximumSize(maxDim);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 122, 255)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setMaximumSize(maxDim);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> doLogin());

        // --- SOLUSI AGAR BISA ENTER ---
        // Mengatur agar btnLogin otomatis ditekan ketika user memencet tombol Enter
        this.getRootPane().setDefaultButton(btnLogin);

        panel.add(lblShopName);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(25));
        
        JLabel lblUser = new JLabel("Username");
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblUser);
        panel.add(tfUsername);
        
        panel.add(Box.createVerticalStrut(15));
        
        JLabel lblPass = new JLabel("Password");
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblPass);
        panel.add(tfPassword);
        
        panel.add(Box.createVerticalStrut(25));
        panel.add(btnLogin);

        add(panel);
    }

    private void doLogin() {
        String user = tfUsername.getText();
        String pass = new String(tfPassword.getPassword());

        try (Connection c = DbConnection.getConnection(); 
             PreparedStatement p = c.prepareStatement("SELECT * FROM tb_user WHERE username=? AND password=?")) {
            p.setString(1, user);
            p.setString(2, pass);
            ResultSet r = p.executeQuery();

            if (r.next()) {
                User loggedInUser = new User();
                loggedInUser.setIdUser(r.getInt("id_user"));
                loggedInUser.setUsername(r.getString("username"));
                loggedInUser.setNamaLengkap(r.getString("nama_lengkap"));
                loggedInUser.setLevel(r.getString("level"));

                dispose();
                new MainFrame(loggedInUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password Salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Koneksi Database Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}