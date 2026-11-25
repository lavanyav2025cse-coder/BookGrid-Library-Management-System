import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set UI theme.");
        }

        Color navy = new Color(24, 39, 70);
        Color teal = new Color(0, 137, 123);
        Color background = new Color(242, 246, 250);
        Color darkText = new Color(35, 48, 68);
        Color white = Color.WHITE;

        JFrame frame = new JFrame("BookGrid | Library Management");
        frame.setSize(950, 650);
        frame.setMinimumSize(new Dimension(800, 550));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(background);

        // Top header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(navy);
        header.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel logo = new JLabel("BookGrid");
        logo.setFont(new Font("SansSerif", Font.BOLD, 29));
        logo.setForeground(white);

        JLabel subtitle = new JLabel("LIBRARY MANAGEMENT SYSTEM");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(190, 211, 232));

        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);
        brand.add(logo);
        brand.add(Box.createVerticalStrut(5));
        brand.add(subtitle);

        JLabel badge = new JLabel("LIBRARY PORTAL");
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(white);
        badge.setOpaque(true);
        badge.setBackground(teal);
        badge.setBorder(BorderFactory.createEmptyBorder(9, 13, 9, 13));

        header.add(brand, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        // Dashboard content
        JPanel center = new JPanel(new BorderLayout(0, 22));
        center.setBackground(background);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel welcome = new JLabel("Welcome to your dashboard");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 25));
        welcome.setForeground(navy);

        JLabel description = new JLabel(
                "Manage books, members, issues and library records.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(new Color(100, 113, 132));

        JPanel intro = new JPanel();
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.setOpaque(false);
        intro.add(welcome);
        intro.add(Box.createVerticalStrut(8));
        intro.add(description);

        JPanel menu = new JPanel(new GridLayout(4, 2, 18, 16));
        menu.setBackground(background);

        JButton addBook = new JButton("＋   Add Book");
        JButton viewBooks = new JButton("▤   View Books");
        JButton manageMembers = new JButton("♙   Manage Members");
        JButton issueBook = new JButton("↗   Issue Book");
        JButton returnBook = new JButton("↙   Return Book");
        JButton reports = new JButton("▥   Reports");
        JButton viewIssuedBooks = new JButton("☷   View Issued Books");
        JButton exit = new JButton("←   Exit");

        JButton[] buttons = {
            addBook, viewBooks, manageMembers, issueBook,
            returnBook, reports, viewIssuedBooks, exit
        };

        Color[] buttonColors = {
            teal,
            navy,
            new Color(52, 92, 138),
            new Color(0, 121, 107),
            new Color(63, 81, 120),
            new Color(37, 99, 130),
            new Color(46, 78, 104),
            new Color(105, 116, 132)
        };

        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];
            button.setFont(new Font("SansSerif", Font.BOLD, 16));
            button.setForeground(white);
            button.setBackground(buttonColors[i]);
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    buttonColors[i].darker(), 1),
                            BorderFactory.createEmptyBorder(18, 20, 18, 12)));
            menu.add(button);
        }

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(background);
        footer.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, new Color(218, 225, 234)));

        JLabel footerText = new JLabel("  BookGrid  |  Library Dashboard");
        footerText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footerText.setForeground(new Color(100, 113, 132));

        JLabel footerStatus = new JLabel("Ready to manage records  ");
        footerStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        footerStatus.setForeground(teal);

        footer.add(footerText, BorderLayout.WEST);
        footer.add(footerStatus, BorderLayout.EAST);
        footer.setBorder(BorderFactory.createCompoundBorder(
                footer.getBorder(),
                BorderFactory.createEmptyBorder(12, 8, 12, 8)));

        center.add(intro, BorderLayout.NORTH);
        center.add(menu, BorderLayout.CENTER);

        // Keep all existing button actions
        addBook.addActionListener(e -> openAddBookForm(frame));
        viewBooks.addActionListener(e -> showBooks(frame));
        manageMembers.addActionListener(e -> showMembersMenu(frame));
        issueBook.addActionListener(e -> openIssueBookForm(frame));
        returnBook.addActionListener(e -> returnBookForm(frame));
        reports.addActionListener(e -> showReports(frame));
        viewIssuedBooks.addActionListener(e -> showIssuedBooks(frame));
        exit.addActionListener(e -> frame.dispose());

        frame.add(header, BorderLayout.NORTH);
        frame.add(center, BorderLayout.CENTER);
        frame.add(footer, BorderLayout.SOUTH);
        frame.setVisible(true);
    });
}
    // Add Book
    private static void openAddBookForm(JFrame parent) {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField isbn = new JTextField();
        JTextField category = new JTextField();
        JTextField publisher = new JTextField();
        JTextField year = new JTextField();
        JTextField copies = new JTextField();

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));

        form.add(new JLabel("Book Title:"));
        form.add(title);
        form.add(new JLabel("Author:"));
        form.add(author);
        form.add(new JLabel("ISBN:"));
        form.add(isbn);
        form.add(new JLabel("Category:"));
        form.add(category);
        form.add(new JLabel("Publisher:"));
        form.add(publisher);
        form.add(new JLabel("Publication Year:"));
        form.add(year);
        form.add(new JLabel("Total Copies:"));
        form.add(copies);

        int result = JOptionPane.showConfirmDialog(
                parent, form, "Add New Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            if (title.getText().isBlank()
                    || author.getText().isBlank()
                    || copies.getText().isBlank()) {
                JOptionPane.showMessageDialog(parent,
                        "Enter book title, author and total copies.");
                return;
            }

            int publicationYear = year.getText().isBlank()
                    ? 0
                    : Integer.parseInt(year.getText().trim());

            int totalCopies =
                    Integer.parseInt(copies.getText().trim());

            if (totalCopies < 1) {
                JOptionPane.showMessageDialog(parent,
                        "Total copies must be at least 1.");
                return;
            }

            Book book = new Book(
                    title.getText().trim(),
                    author.getText().trim(),
                    isbn.getText().trim(),
                    category.getText().trim(),
                    publisher.getText().trim(),
                    publicationYear,
                    totalCopies);

            if (new BookDAO().addBook(book)) {
                JOptionPane.showMessageDialog(parent,
                        "Book added successfully!");
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Book could not be added. Check the Terminal.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Year and copies must be numbers.");
        }
    }

    // View Books
    private static void showBooks(JFrame parent) {
        List<String[]> books = new BookDAO().getAllBooks();

        String[] columns = {
            "Book ID", "Title", "Author",
            "Category", "Total Copies", "Available Copies"
        };

        JTable table = new JTable(
                books.toArray(new String[0][]), columns);
        table.setRowHeight(25);

        JFrame window = new JFrame("BookGrid - View Books");
        window.add(new JScrollPane(table));
        window.setSize(850, 400);
        window.setLocationRelativeTo(parent);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }

    // Manage Members menu
    private static void showMembersMenu(JFrame parent) {
        String[] options = {
            "Add Member", "View Members", "Cancel"
        };

        int choice = JOptionPane.showOptionDialog(
                parent,
                "Choose a member operation",
                "Manage Members",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            openAddMemberForm(parent);
        } else if (choice == 1) {
            showMembers(parent);
        }
    }

    // Add Member
    private static void openAddMemberForm(JFrame parent) {
        JTextField name = new JTextField();
        JTextField department = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));

        form.add(new JLabel("Member Name:"));
        form.add(name);
        form.add(new JLabel("Department:"));
        form.add(department);
        form.add(new JLabel("Email:"));
        form.add(email);
        form.add(new JLabel("Phone:"));
        form.add(phone);

        int result = JOptionPane.showConfirmDialog(
                parent, form, "Add New Member",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (name.getText().isBlank()) {
            JOptionPane.showMessageDialog(parent,
                    "Member name is required.");
            return;
        }

        Member member = new Member(
                name.getText().trim(),
                department.getText().trim(),
                email.getText().trim(),
                phone.getText().trim());

        if (new MemberDAO().addMember(member)) {
            JOptionPane.showMessageDialog(parent,
                    "Member added successfully!");
        } else {
            JOptionPane.showMessageDialog(parent,
                    "Could not add member. Check the Terminal.");
        }
    }

    // View Members
    private static void showMembers(JFrame parent) {
        List<String[]> members = new MemberDAO().getAllMembers();

        String[] columns = {
            "Member ID", "Name", "Department", "Email", "Phone"
        };

        JTable table = new JTable(
                members.toArray(new String[0][]), columns);
        table.setRowHeight(25);

        JFrame window = new JFrame("BookGrid - View Members");
        window.add(new JScrollPane(table));
        window.setSize(800, 400);
        window.setLocationRelativeTo(parent);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }

    // Issue Book
    private static void openIssueBookForm(JFrame parent) {
        JTextField bookId = new JTextField();
        JTextField memberId = new JTextField();

        JTextField dueDate = new JTextField(
                java.time.LocalDate.now().plusDays(14).toString());

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));

        form.add(new JLabel("Book ID:"));
        form.add(bookId);
        form.add(new JLabel("Member ID:"));
        form.add(memberId);
        form.add(new JLabel("Due Date (YYYY-MM-DD):"));
        form.add(dueDate);

        int result = JOptionPane.showConfirmDialog(
                parent, form, "Issue Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int bId = Integer.parseInt(bookId.getText().trim());
            int mId = Integer.parseInt(memberId.getText().trim());

            java.time.LocalDate due =
                    java.time.LocalDate.parse(
                            dueDate.getText().trim());

            if (!due.isAfter(java.time.LocalDate.now())) {
                JOptionPane.showMessageDialog(parent,
                        "Due date must be in the future.");
                return;
            }

            boolean success =
                    new IssueDAO().issueBook(bId, mId, due);

            if (success) {
                JOptionPane.showMessageDialog(parent,
                        "Book issued successfully!");
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Book unavailable or invalid Book ID. "
                        + "Check that the Member ID also exists.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Book ID and Member ID must be numbers.");
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Enter the due date as YYYY-MM-DD.");
        }
    }

    // Return Book
    private static void returnBookForm(JFrame parent) {
    String input = JOptionPane.showInputDialog(
            parent, "Enter the Issue ID of the book to return:");

    if (input == null || input.isBlank()) {
        return;
    }

    try {
        int issueId = Integer.parseInt(input.trim());

        ReturnDAO dao = new ReturnDAO();
        Double fine = dao.getFineForReturn(issueId);
        System.out.println("Fine check method called!");
        System.out.println("Fine amount: " + fine);

        if (fine == null) {
            JOptionPane.showMessageDialog(parent,
                    "Invalid Issue ID or book already returned.");
            return;
        }

        if (fine > 0) {
            int fineConfirm = JOptionPane.showConfirmDialog(
                    parent,
                    String.format(
                            "Fine amount: ₹%.2f%n"
                            + "Has the fine been collected?",
                            fine),
                    "Fine Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (fineConfirm != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(parent,
                        "Book return cancelled. "
                        + "Please collect the fine first.");
                return;
            }

        } else {
            int confirm = JOptionPane.showConfirmDialog(
                    parent,
                    "No fine is due. Confirm book return?",
                    "Return Book",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        boolean success = dao.returnBook(issueId);

        if (success) {
            JOptionPane.showMessageDialog(parent,
                    String.format(
                            "Book returned successfully!%nFine: ₹%.2f",
                            fine));
        } else {
            JOptionPane.showMessageDialog(parent,
                    "Unable to return book. Please check the Issue ID.");
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(parent,
                "Issue ID must be a number.");
    }
}

    // View Issued Books
    private static void showIssuedBooks(JFrame parent) {
        List<String[]> issues = new IssueDAO().getAllIssues();

        String[] columns = {
            "Issue ID", "Book ID", "Member ID",
            "Issue Date", "Due Date", "Return Date", "Status"
        };

        javax.swing.table.DefaultTableModel model =
                new javax.swing.table.DefaultTableModel(columns, 0);

        for (String[] issue : issues) {
            model.addRow(issue);
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 300));

        JOptionPane.showMessageDialog(
                parent, scrollPane, "View Issued Books",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Reports
    private static void showReports(JFrame parent) {
        String sql =
                "SELECT "
                + "(SELECT COUNT(*) FROM books) AS total_books, "
                + "(SELECT COALESCE(SUM(available_copies), 0) "
                + "FROM books) AS available_books, "
                + "(SELECT COUNT(*) FROM members) AS total_members, "
                + "(SELECT COUNT(*) FROM book_issues "
                + "WHERE status = 'Issued') AS books_issued, "
                + "(SELECT COUNT(*) FROM book_issues "
                + "WHERE status = 'Returned') AS books_returned, "
                + "(SELECT COALESCE(SUM(fine), 0) FROM book_issues "
                + "WHERE status = 'Returned') AS total_fine";

        try (Connection con = DBConnection.getConnection();
             java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                String report =
                        "LIBRARY REPORT\n\n"
                        + "Total Books: "
                        + rs.getInt("total_books") + "\n"
                        + "Available Copies: "
                        + rs.getInt("available_books") + "\n"
                        + "Total Members: "
                        + rs.getInt("total_members") + "\n"
                        + "Books Currently Issued: "
                        + rs.getInt("books_issued") + "\n"
                        + "Books Returned: "
                        + rs.getInt("books_returned") + "\n"
                        + "Total Fine: Rs. "
                        + rs.getDouble("total_fine");

                JOptionPane.showMessageDialog(
                        parent, report, "Library Reports",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Error loading reports: " + ex.getMessage());
        }
    }
}