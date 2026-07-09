package Metro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage; // Set the primaryStage

        // Load the Login.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        // Set the mainApp instance in the LoginController
        LoginController controller = loader.getController();
        controller.setMainApp(this);
        controller.setPrimaryStage(primaryStage); // Set primaryStage in LoginController

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // Method to switch to the manager window
    public void switchToManager() throws Exception {
        // Load the Manager.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Manager.fxml"));
        Parent root = loader.load();

        // Set the mainApp instance in the ManagerController
        ManagerController controller = loader.getController();
        controller.setMainApp(this);
        controller.setPrimaryStage(primaryStage); // Set primaryStage in ManagerController

        primaryStage.setTitle("Manager"); // Change the title
        primaryStage.setScene(new Scene(root, 400, 300)); // Set an initial size
        primaryStage.show();
    }

    // Method to switch to the AddMetro window
    public void switchToAddMetro() throws Exception {
        // Load the AddMetro.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddMetro.fxml"));
        Parent root = loader.load();

        // Get the controller after the root has been loaded
        AddMetroController controller = loader.getController();
        controller.setMainApp(this);
        controller.setPrimaryStage(this.primaryStage); // Use the primaryStage from the Main class
        controller.initialize();

        Scene scene = new Scene(root);
        this.primaryStage.setScene(scene); // Set the new scene on the primaryStage
        this.primaryStage.setTitle("Add Metro"); // Change the title
        this.primaryStage.show();
    }
    // Method to switch to the AdminR window
    public void switchToAdminR() throws Exception {
    // Load the AdminR.fxml file
    FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminR.fxml"));
    Parent root = loader.load();

    // Set the mainApp instance in the AdminRController
    AdminRController controller = loader.getController();
    controller.setMainApp(this);
    controller.setPrimaryStage(primaryStage); // Set primaryStage in AdminRController

    primaryStage.setTitle("Admin Reservations"); // Change the title
    primaryStage.setScene(new Scene(root, 800, 600)); // Set an appropriate size
    primaryStage.show();
    }
    // Method to switch to the UserInfo window
    public void switchToUserInfo() throws Exception {
    // Load the UserInfo.fxml file
    FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInfo.fxml"));
    Parent root = loader.load();

    // Set the mainApp instance in the UserInfoController
    UserInfoController controller = loader.getController();
    controller.setMainApp(this);
    controller.setPrimaryStage(primaryStage); // Set primaryStage in UserInfoController

    primaryStage.setTitle("User Info"); // Change the title
    primaryStage.setScene(new Scene(root, 800, 600)); // Set an appropriate size
    primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
