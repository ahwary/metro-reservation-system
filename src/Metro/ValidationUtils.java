package Metro;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        // Simple email validation regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidPassword(String password) {
        // Password validation (e.g., minimum length)
        return password.length() >= 8;
    }

    public static boolean isValidUsername(String username) {
        // Username validation (e.g., minimum length)
        return username.length() >= 5;
    }
}
