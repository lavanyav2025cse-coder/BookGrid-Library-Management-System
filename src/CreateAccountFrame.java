import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateAccountFrame extends JFrame {

    private final JTextField memberIdField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmPasswordField =
            new JPasswordField(20);

    private final Color navy = new Color(23, 38, 75);
    private final Color teal = new Color(7, 141, 163);
    private final Color blue = new Color(40, 117, 199);
    private final Color background = new Color(243, 245, 248);
    private final Color textColor = new Color(45, 55, 72);

    public CreateAccountFrame() {
        setTitle("BookGrid - Create Account");
        setSize(620,600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(navy);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel heading = new JLabel("BookGrid");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("CREATE YOUR LIBRARY ACCOUNT");
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
                new EmptyBorder(22, 28, 22, 28)));

        JLabel formTitle = new JLabel("Create Your Account");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 21));
        formTitle.setForeground(navy);

        JLabel instruction = new JLabel(
                "Enter your registered member details");
        instruction.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instruction.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(formTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(instruction);

        JPanel form = new JPanel(new GridLayout(4, 2, 12, 20));
        form.setOpaque(false);

        addFormLabel(form, "Member ID:");
        styleField(memberIdField);
        form.add(memberIdField);

        addFormLabel(form, "Registered Email:");
        styleField(emailField);
        form.add(emailField);

        addFormLabel(form, "Create Password:");
        styleField(passwordField);
        form.add(passwordField);

        addFormLabel(form, "Confirm Password:");
        styleField(confirmPasswordField);
        form.add(confirmPasswordField);

        JButton createButton =
                createButton("CREATE ACCOUNT", teal, Color.WHITE);
        JButton backButton =
                createButton("BACK TO LOGIN", blue, Color.WHITE);

        createButton.addActionListener(e -> createAccount());
        confirmPasswordField.addActionListener(e -> createAccount());

        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 9));
        buttons.setOpaque(false);
        buttons.add(createButton);
        buttons.add(backButton);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(titlePanel, BorderLayout.NORTH);
        center.add(form, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        card.add(center, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(background);
        content.setBorder(new EmptyBorder(20, 30, 25, 30));
        content.add(card, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(textColor);
        panel.add(label);
    }

    private void styleField(JTextField field) {
    field.setFont(new Font("SansSerif", Font.PLAIN, 15));
    field.setForeground(textColor);
    field.setBackground(Color.WHITE);

    field.setPreferredSize(new Dimension(230, 42));
    field.setMinimumSize(new Dimension(180, 38));

    field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                    new Color(205, 215, 226)),
            new EmptyBorder(8, 10, 8, 10)));
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
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void createAccount() {
        String memberIdText = memberIdField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        if (memberIdText.isBlank() || email.isBlank()
                || password.isBlank() || confirmPassword.isBlank()) {
            JOptionPane.showMessageDialog(
                    this, "Please fill in all fields.");
            return;
        }

        int memberId;

        try {
            memberId = Integer.parseInt(memberIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Member ID must be a number.");
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(
                    this, "Password must contain at least 8 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                    this, "Passwords do not match.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String memberSql =
                    "SELECT name, email FROM members "
                    + "WHERE member_id = ? AND email = ?";

            String memberName;

            try (PreparedStatement ps =
                    con.prepareStatement(memberSql)) {
                ps.setInt(1, memberId);
                ps.setString(2, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Member ID and registered email do not match.");
                        return;
                    }

                    memberName = rs.getString("name");
                }
            }

            String checkSql =
                    "SELECT user_id FROM users "
                    + "WHERE email = ? OR member_id = ?";

            try (PreparedStatement ps =
                    con.prepareStatement(checkSql)) {
                ps.setString(1, email);
                ps.setInt(2, memberId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(
                                this,
                                "An account already exists for this email "
                                + "or member.");
                        return;
                    }
                }
            }

            String insertSql =
                    "INSERT INTO users "
                    + "(full_name, email, password_hash, role, member_id) "
                    + "VALUES (?, ?, ?, 'USER', ?)";

            try (PreparedStatement ps =
                    con.prepareStatement(insertSql)) {
                ps.setString(1, memberName);
                ps.setString(2, email);
                ps.setString(3, PasswordUtil.hashPassword(password));
                ps.setInt(4, memberId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Account created successfully! You can now log in.");

            dispose();
            new LoginFrame().setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this, "Account creation failed: " + ex.getMessage());
        }
    }
}