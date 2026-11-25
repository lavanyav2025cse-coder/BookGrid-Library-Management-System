import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    public static String hashPassword(String password)
            throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);

        byte[] hash = md.digest(
                password.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(
            String password, String storedValue) throws Exception {

        String[] parts = storedValue.split(":");

        if (parts.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);

        byte[] actualHash = md.digest(
                password.getBytes(StandardCharsets.UTF_8));

        return MessageDigest.isEqual(expectedHash, actualHash);
    }
}