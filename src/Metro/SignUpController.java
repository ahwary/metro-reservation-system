package Metro;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignUpController {

    @FXML
    private TextField firstname;

    @FXML
    private TextField lastname;

    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    void SignUpBtnActionPerformed(ActionEvent event) {
        String Fname, Lname, Username, Email, Pass;

        try (Connection con = Database.getUserConnection();
             PreparedStatement pst = con.prepareStatement(
                     "INSERT INTO java_users_database(Fname, Lname, Username, Email, Pass) VALUES (?, ?, ?, ?, ?)")) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Validate input fields
            if ("".equals(firstname.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "First Name is required");
                return;
            } else if ("".equals(lastname.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Last Name is required");
                return;
            } else if (!ValidationUtils.isValidUsername(username.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username must be at least 5 characters long");
                return;
            } else if (!ValidationUtils.isValidEmail(email.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Email format");
                return;
            } else if (!ValidationUtils.isValidPassword(password.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 8 characters long");
                return;
            }

            // Proceed if all fields are valid
            Fname = firstname.getText();
            Lname = lastname.getText();
            Username = username.getText();
            Email = email.getText();
            Pass = password.getText();

            pst.setString(1, Fname);
            pst.setString(2, Lname);
            pst.setString(3, Username);
            pst.setString(4, Email);
            pst.setString(5, Pass);
            pst.executeUpdate();
            firstname.setText("");
            lastname.setText("");
            username.setText("");
            email.setText("");
            password.setText("");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Congratulations!", "New account has been created successfully");
        } catch (Exception e) {
            System.out.println("Error!" + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // Original showAlert method
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Overloaded showAlert method with an additional headerText parameter
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
