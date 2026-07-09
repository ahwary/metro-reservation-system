package Metro;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button signupButton;

    @FXML
    void cancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void loginButtonAction(ActionEvent event) {
        String usernameValue;
        String passwordValue;
        String passDb = null;
        int notFound = 0;

        try (Connection con = Database.getUserConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM java_users_database WHERE Username = ?")) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            if ("".equals(username.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username is required");
            } else if ("".equals(password.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Password is required");
            } else {
                usernameValue = username.getText();
                passwordValue = password.getText();

                pst.setString(1, usernameValue);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        passDb = rs.getString("Pass");
                        notFound = 1;
                    }
                }
                if (notFound == 1 && passwordValue.equals(passDb)) {
                    if ("Admin".equals(usernameValue) && "123".equals(passwordValue)) {
                        mainApp.switchToManager();
                    } else {
                        navigateToReservation(); // Navigate to Reservation page for non-admin users
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Re-Enter","Incorrect username or password");
                }

                username.setText("");
                password.setText("");
            }
        } catch (Exception e) {
            System.out.println("Error! " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void navigateToReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Reservation");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void signupButtonAction(ActionEvent event) {
        try {
            Parent signUpRoot = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
            Scene signUpScene = new Scene(signUpRoot);
            Stage signUpStage = new Stage();
            signUpStage.setScene(signUpScene);
            signUpStage.setTitle("Sign Up");
            signUpStage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void initialize() {
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Login.fxml'.";
    }
}
