import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDashboard extends JFrame {

    private final int memberId;
    private final String fullName;

    private final Color navy = new Color(24, 39, 70);
    private final Color teal = new Color(0, 137, 123);

    public UserDashboard(int memberId, String fullName) {
        this.memberId = memberId;
        this.fullName = fullName;

        setTitle("BookGrid - User Dashboard");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(15, 20));
        main.setBackground(new Color(242, 246, 250));
        main.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel heading = new JLabel("BookGrid");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);

        JLabel welcome = new JLabel("Welcome, " + fullName);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 21));
        welcome.setForeground(navy);

        JLabel description = new JLabel(
                "Browse books and check your library records.");
        description.setForeground(Color.DARK_GRAY);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(navy);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        header.add(heading, BorderLayout.WEST);

        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.add(welcome);
        intro.add(Box.createVerticalStrut(8));
        intro.add(description);

        JButton viewBooks = new JButton("View Books");
        JButton myIssues = new JButton("My Issued Books");
        JButton logout = new JButton("Logout");

        styleButton(viewBooks, teal);
        styleButton(myIssues, navy);
        styleButton(logout, new Color(105, 116, 132));

        JPanel buttons = new JPanel(new GridLayout(1, 3, 15, 15));
        buttons.setOpaque(false);
        buttons.add(viewBooks);
        buttons.add(myIssues);
        buttons.add(logout);

        JPanel content = new JPanel(new BorderLayout(15, 25));
        content.setOpaque(false);
        content.add(intro, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);

        main.add(header, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        viewBooks.addActionListener(e -> showBooks());
        myIssues.addActionListener(e -> showMyIssues());
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(
                    () -> new LoginFrame().setVisible(true));
        });

        setContentPane(main);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showBooks() {
        String sql = "SELECT book_id, title, author, category, "
                + "available_copies FROM books";

        String[] columns = {
            "Book ID", "Title", "Author", "Category", "Available Copies"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getInt("available_copies")
                });
            }

            showTable("Available Library Books", model);

        } catch (Exception ex) {
            showError("Could not load books: " + ex.getMessage());
        }
    }

    private void showMyIssues() {
        String sql = "SELECT i.issue_id, b.title, i.issue_date, "
                + "i.due_date, i.return_date, i.status "
                + "FROM book_issues i "
                + "JOIN books b ON i.book_id = b.book_id "
                + "WHERE i.member_id = ? "
                + "ORDER BY i.issue_id DESC";

        String[] columns = {
            "Issue ID", "Book Title", "Issue Date",
            "Due Date", "Return Date", "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("issue_id"),
                        rs.getString("title"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")
                    });
                }
            }

            showTable("My Issued Books", model);

        } catch (Exception ex) {
            showError("Could not load your records: " + ex.getMessage());
        }
    }

    private void showTable(String title, DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(27);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setBackground(navy);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);

        JFrame window = new JFrame("BookGrid - " + title);
        window.add(scrollPane);
        window.setSize(850, 400);
        window.setLocationRelativeTo(this);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "BookGrid",
                JOptionPane.ERROR_MESSAGE);
    }
}