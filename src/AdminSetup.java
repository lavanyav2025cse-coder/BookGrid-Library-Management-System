import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class AdminSetup {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter Admin Name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter Admin Email: ");
            String email = sc.nextLine().trim();

            System.out.print("Create Admin Password: ");
            String password = sc.nextLine();

            if (name.isBlank() || email.isBlank()
                    || password.length() < 8) {
                System.out.println(
                    "Name and email are required. Password must have at least 8 characters.");
                return;
            }

            String passwordHash = PasswordUtil.hashPassword(password);

            String sql = "INSERT INTO users "
                    + "(full_name, email, password_hash, role) "
                    + "VALUES (?, ?, ?, 'ADMIN')";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, passwordHash);

                ps.executeUpdate();

                System.out.println("Admin account created successfully!");
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            System.out.println("This email already exists.");
        } catch (Exception ex) {
            System.out.println("Admin setup failed: " + ex.getMessage());
        } finally {
            sc.close();
        }
    }
}