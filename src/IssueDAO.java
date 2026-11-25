import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class IssueDAO {

    public boolean issueBook(int bookId, int memberId,
                             LocalDate dueDate) {

        String checkSql =
                "SELECT available_copies FROM books WHERE book_id = ?";

        String issueSql =
                "INSERT INTO book_issues "
                + "(book_id, member_id, issue_date, due_date, "
                + "fine, status) VALUES (?, ?, ?, ?, 0, 'Issued')";

        String updateSql =
                "UPDATE books SET available_copies = available_copies - 1 "
                + "WHERE book_id = ? AND available_copies > 0";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement check =
                             con.prepareStatement(checkSql)) {
                    check.setInt(1, bookId);

                    try (java.sql.ResultSet rs = check.executeQuery()) {
                        if (!rs.next() || rs.getInt("available_copies") < 1) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement update =
                             con.prepareStatement(updateSql)) {
                    update.setInt(1, bookId);

                    if (update.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement issue =
                             con.prepareStatement(issueSql)) {
                    issue.setInt(1, bookId);
                    issue.setInt(2, memberId);
                    issue.setDate(3, Date.valueOf(LocalDate.now()));
                    issue.setDate(4, Date.valueOf(dueDate));
                    issue.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
            return false;
        }
    }
    public java.util.List<String[]> getAllIssues() {
    java.util.List<String[]> issues = new java.util.ArrayList<>();

    String sql = "SELECT issue_id, book_id, member_id, " +
                 "issue_date, due_date, return_date, status " +
                 "FROM book_issues ORDER BY issue_id DESC";

    try (Connection con = DBConnection.getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            issues.add(new String[] {
                String.valueOf(rs.getInt("issue_id")),
                String.valueOf(rs.getInt("book_id")),
                String.valueOf(rs.getInt("member_id")),
                String.valueOf(rs.getDate("issue_date")),
                String.valueOf(rs.getDate("due_date")),
                rs.getDate("return_date") == null
                    ? "Not Returned"
                    : String.valueOf(rs.getDate("return_date")),
                rs.getString("status")
            });
        }

    } catch (SQLException e) {
        System.out.println("Error loading issued books: " + e.getMessage());
    }

    return issues;
}
}