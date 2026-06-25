package aplikasitoko.ui;

import aplikasitoko.model.User;
import aplikasitoko.ui.panel.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final User currentUser;
    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private JLabel     lblPageTitle;

    private BarangPanel    pBarang;
    private PenjualanPanel pPenjualan;
    private CustomerPanel  pCustomer;
    private KategoriPanel  pKategori;
    private PenggunaPanel  pPengguna;

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Toko Berkah Jaya – Sistem Penjualan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(buildTopbar(), BorderLayout.NORTH);

        cardLayout  = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.CONTENT_BG);

        pBarang    = new BarangPanel();
        pPenjualan = new PenjualanPanel(currentUser);
        pCustomer  = new CustomerPanel(currentUser);
        pKategori  = new KategoriPanel();
        pPengguna  = new PenggunaPanel();

        contentPanel.add(new DashboardPanel(currentUser), "Dashboard");
        contentPanel.add(pBarang,                         "Barang");
        contentPanel.add(pPenjualan,                      "Transaksi");
        contentPanel.add(pCustomer,                       "Customer");
        contentPanel.add(pKategori,                       "Kategori");
        contentPanel.add(pPengguna,                       "Pengguna");

        rightPanel.add(contentPanel, BorderLayout.CENTER);
        root.add(rightPanel, BorderLayout.CENTER);
        showPage("Dashboard");
    }

    private boolean isAdmin() {
        return currentUser != null && "Admin".equalsIgnoreCase(currentUser.getLevel());
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Theme.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        menuPanel.add(createMenuButton(" Dashboard", "Dashboard"));
        menuPanel.add(createMenuButton(" Kasir (Transaksi)", "Transaksi"));

        // Menu berikut hanya tampil untuk Admin
        if (isAdmin()) {
            menuPanel.add(createMenuButton(" Data Barang", "Barang"));
            menuPanel.add(createMenuButton(" Kategori", "Kategori"));
        }

        menuPanel.add(createMenuButton(" Data Customer", "Customer"));

        if (isAdmin()) {
            menuPanel.add(createMenuButton(" Manajemen User", "Pengguna"));
        }

        JPanel pUser = new JPanel();
        pUser.setLayout(new BoxLayout(pUser, BoxLayout.Y_AXIS));
        pUser.setOpaque(false);
        pUser.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblLoginAs = new JLabel("Login sebagai:");
        lblLoginAs.setForeground(Color.LIGHT_GRAY);
        lblLoginAs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoginAs.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        String roleUser = (currentUser != null) ? currentUser.getLevel() : "Admin";
        JLabel lblRole = new JLabel(roleUser.toUpperCase());
        lblRole.setForeground(Color.WHITE);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogout = UIFactory.dangerButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        pUser.add(lblLoginAs);
        pUser.add(Box.createVerticalStrut(3));
        pUser.add(lblRole);
        pUser.add(Box.createVerticalStrut(15));
        pUser.add(btnLogout);

        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(pUser, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(Theme.TOPBAR_BG);
        topbar.setPreferredSize(new Dimension(0, Theme.TOPBAR_HEIGHT));
        topbar.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));

        lblPageTitle = new JLabel("Dashboard");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPageTitle.setBorder(new EmptyBorder(0, 20, 0, 0));

        topbar.add(lblPageTitle, BorderLayout.WEST);
        return topbar;
    }

    private JButton createMenuButton(String title, String pageName) {
        JButton btn = new JButton(title);
        btn.setFont(Theme.FONT_NAV);
        btn.setForeground(Theme.SIDEBAR_TEXT);
        btn.setBackground(Theme.SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showPage(pageName));
        return btn;
    }

    private void showPage(String pageName) {
        cardLayout.show(contentPanel, pageName);
        if (lblPageTitle != null) lblPageTitle.setText(pageName);
        switch (pageName) {
            case "Dashboard": if (contentPanel.getComponent(0) instanceof DashboardPanel) ((DashboardPanel) contentPanel.getComponent(0)).refreshData(); break;
            case "Barang": if (pBarang != null) pBarang.refreshData(); break;
            case "Transaksi": if (pPenjualan != null) pPenjualan.refreshData(); break;
            case "Customer": if (pCustomer != null) pCustomer.refreshData(); break;
            case "Kategori": if (pKategori != null) pKategori.refreshData(); break;
            case "Pengguna": if (pPengguna != null) pPengguna.refreshData(); break;
        }
    }
}