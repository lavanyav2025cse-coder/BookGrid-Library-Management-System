import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books " +
                "(title, author, isbn, category, publisher, " +
                "publication_year, total_copies, available_copies) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getCategory());
            ps.setString(5, book.getPublisher());
            ps.setInt(6, book.getPublicationYear());
            ps.setInt(7, book.getTotalCopies());
            ps.setInt(8, book.getAvailableCopies());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getAllBooks() {
        List<String[]> books = new ArrayList<>();

        String sql = "SELECT book_id, title, author, category, " +
                "total_copies, available_copies FROM books";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(new String[]{
                    String.valueOf(rs.getInt("book_id")),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    String.valueOf(rs.getInt("total_copies")),
                    String.valueOf(rs.getInt("available_copies"))
                });
            }

        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }

        return books;
    }
}