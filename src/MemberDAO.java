import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public boolean addMember(Member member) {
        String sql = "INSERT INTO members " +
                "(name, department, email, phone) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getDepartment());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getAllMembers() {
        List<String[]> members = new ArrayList<>();

        String sql = "SELECT member_id, name, department, email, phone "
                   + "FROM members";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                members.add(new String[]{
                    String.valueOf(rs.getInt("member_id")),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
            }

        } catch (SQLException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }

        return members;
    }
}