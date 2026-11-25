import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);
    private final JComboBox<String> roleBox =
            new JComboBox<>(new String[]{"ADMIN", "USER"});

    private final Color navy = new Color(23, 38, 75);
    private final Color teal = new Color(7, 141, 163);
    private final Color blue = new Color(40, 117, 199);
    private final Color background = new Color(243, 245, 248);
    private final Color textColor = new Color(45, 55, 72);

    private JButton createAccountButton;

    public LoginFrame() {
        setTitle("BookGrid - Login");
        setSize(520, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(navy);
        header.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel heading = new JLabel("BookGrid");
        heading.setFont(new Font("SansSerif", Font.BOLD, 30));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("LIBRARY MANAGEMENT SYSTEM");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(190, 215, 235));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(heading);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        JPanel card = new JPanel(new BorderLayout(15, 20));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(222, 228, 236)),
                new EmptyBorder(25, 28, 25, 28)));

        JLabel formTitle = new JLabel("Welcome Back");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        formTitle.setForeground(navy);

        JLabel instruction = new JLabel("Sign in to continue to your dashboard");
        instruction.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instruction.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(formTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(instruction);

        JPanel form = new JPanel(new GridLayout(3, 2, 12, 16));
        form.setOpaque(false);

        addFormLabel(form, "Login as:");
        styleComboBox(roleBox);
        form.add(roleBox);

        addFormLabel(form, "Email:");
        styleField(emailField);
        form.add(emailField);

        addFormLabel(form, "Password:");
        styleField(passwordField);
        form.add(passwordField);

        JButton loginButton = createButton("LOGIN", teal, Color.WHITE);

        createAccountButton = createButton(
                "CREATE ACCOUNT", blue, Color.WHITE);
        createAccountButton.setVisible(false);

        loginButton.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());

        createAccountButton.addActionListener(e -> {
            dispose();
            new CreateAccountFrame().setVisible(true);
        });

        roleBox.addActionListener(e -> {
            boolean isUser =
                    "USER".equals(roleBox.getSelectedItem());
            createAccountButton.setVisible(isUser);
            packButtonPanelRefresh();
        });

        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 10));
        buttons.setOpaque(false);
        buttons.add(loginButton);
        buttons.add(createAccountButton);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(titlePanel, BorderLayout.NORTH);
        center.add(form, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        card.add(center, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(background);
        content.setBorder(new EmptyBorder(22, 30, 25, 30));
        content.add(card, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void packButtonPanelRefresh() {
        revalidate();
        repaint();
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(textColor);
        panel.add(label);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(textColor);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(205, 215, 226)),
                new EmptyBorder(7, 9, 7, 9)));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(textColor);
        combo.setBorder(BorderFactory.createLineBorder(
                new Color(205, 215, 226)));
    }

    private JButton createButton(
            String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 12, 11, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleBox.getSelectedItem();

        if (email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(
                    this, "Enter both email and password.");
            return;
        }

        String sql = "SELECT password_hash, role, member_id, full_name "
                + "FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(
                            this, "Invalid email, password, or role.");
                    return;
                }

                if (!selectedRole.equals(rs.getString("role"))
                        || !PasswordUtil.verifyPassword(
                                password, rs.getString("password_hash"))) {
                    JOptionPane.showMessageDialog(
                            this, "Invalid email, password, or role.");
                    return;
                }

                int memberId = rs.getInt("member_id");
                String fullName = rs.getString("full_name");

                if ("ADMIN".equals(selectedRole)) {
                    JOptionPane.showMessageDialog(
                            this, "Admin login successful!");
                    dispose();
                    Main.main(new String[0]);
                } else {
                    if (memberId <= 0) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Your account is not linked to a member record.");
                        return;
                    }

                    dispose();
                    UserDashboard dashboard =
                            new UserDashboard(memberId, fullName);
                    dashboard.setVisible(true);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this, "Login error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new LoginFrame().setVisible(true));
    }
}