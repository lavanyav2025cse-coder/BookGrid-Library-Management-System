import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnDAO {
    public Double getFineForReturn(int issueId) {
    String sql = "SELECT due_date FROM book_issues "
            + "WHERE issue_id = ? AND status = 'Issued'";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, issueId);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }

            LocalDate dueDate =
                    rs.getDate("due_date").toLocalDate();

            long lateDays = Math.max(
                    0, ChronoUnit.DAYS.between(
                            dueDate, LocalDate.now()));

            return lateDays * 5.0;
        }

    } catch (SQLException e) {
        System.out.println("Error checking fine: "
                + e.getMessage());
        return null;
    }
}
    public boolean returnBook(int issueId) {
        String findSql =
                "SELECT book_id, due_date FROM book_issues "
                + "WHERE issue_id = ? AND status = 'Issued'";

        String returnSql =
                "UPDATE book_issues SET return_date = ?, "
                + "status = 'Returned', fine = ? "
                + "WHERE issue_id = ? AND status = 'Issued'";

        String stockSql =
                "UPDATE books SET available_copies = available_copies + 1 "
                + "WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int bookId;
                LocalDate dueDate;

                try (PreparedStatement ps = con.prepareStatement(findSql)) {
                    ps.setInt(1, issueId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        bookId = rs.getInt("book_id");
                        dueDate = rs.getDate("due_date").toLocalDate();
                    }
                }

                LocalDate today = LocalDate.now();
                long lateDays = Math.max(
                        0, ChronoUnit.DAYS.between(dueDate, today));
                double fine = lateDays * 5.0;

                try (PreparedStatement ps = con.prepareStatement(returnSql)) {
                    ps.setDate(1, java.sql.Date.valueOf(today));
                    ps.setDouble(2, fine);
                    ps.setInt(3, issueId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
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
            System.out.println("Error returning book: " + e.getMessage());
            return false;
        }
    }
}