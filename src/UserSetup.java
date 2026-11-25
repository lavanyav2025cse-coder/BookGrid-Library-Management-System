import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserSetup {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Create Login Email: ");
            String email = sc.nextLine().trim();

            System.out.print("Create Password (minimum 8 characters): ");
            String password = sc.nextLine();

            if (email.isBlank() || password.length() < 8) {
                System.out.println(
                        "Enter a valid email and a password of at least 8 characters.");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {

                String memberSql =
                        "SELECT name FROM members WHERE member_id = ?";

                String memberName;

                try (PreparedStatement ps =
                        con.prepareStatement(memberSql)) {
                    ps.setInt(1, memberId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("Member ID not found.");
                            return;
                        }
                        memberName = rs.getString("name");
                    }
                }

                String sql = "INSERT INTO users "
                        + "(full_name, email, password_hash, role, member_id) "
                        + "VALUES (?, ?, ?, 'USER', ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, memberName);
                    ps.setString(2, email);
                    ps.setString(3, PasswordUtil.hashPassword(password));
                    ps.setInt(4, memberId);

                    ps.executeUpdate();
                    System.out.println("User account created successfully!");
                }
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            System.out.println(
                    "This email is already registered. Try another email.");
        } catch (Exception ex) {
            System.out.println("Setup failed: " + ex.getMessage());
        }
    }
}