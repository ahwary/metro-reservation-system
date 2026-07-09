package Metro;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ManagerController {

    private Stage primaryStage;
    private Main mainApp;

    // No-argument constructor
    public ManagerController() {
        // Constructor remains empty
    }

    // Method to set the primaryStage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Method to set the mainApp
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    // Method to handle the AddMetro button action
    @FXML
    private void handleAddMetro(ActionEvent event) {
        if (mainApp != null && primaryStage != null) {
            try {
                mainApp.switchToAddMetro();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Main application instance or primary stage is null.");
        }
    }

    @FXML
    private void handleReservation() {
    if (mainApp != null && primaryStage != null) {
        try {
            mainApp.switchToAdminR(); // Switch to AdminR.fxml
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        System.err.println("Main application instance or primary stage is null.");
    }
    }

    @FXML
    private void handleUserInfo() {
    if (mainApp != null && primaryStage != null) {
        try {
            mainApp.switchToUserInfo(); // Switch to UserInfo.fxml
        } catch (Exception e) {
            e.printStackTrace();
        }
        } else {
            System.err.println("Main application instance or primary stage is null.");
        }
    }
}
