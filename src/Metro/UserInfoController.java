package Metro;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import javafx.application.Platform;
import javafx.stage.Stage;

public class UserInfoController {

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TableView<UserInfoData> userInfoTableView;
    @FXML
    private Label titleLabel;

    private final ObservableList<UserInfoData> userInfoDataList = FXCollections.observableArrayList();

    private Stage primaryStage;
    private Main mainApp;

    void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void initialize() {
        System.out.println("Initializing TableView");//For debugging
        // Clear any existing columns to avoid duplication
        userInfoTableView.getColumns().clear();

        TableColumn<UserInfoData, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(cd -> cd.getValue().firstNameProperty());

        TableColumn<UserInfoData, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(cd -> cd.getValue().lastNameProperty());

        TableColumn<UserInfoData, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cd -> cd.getValue().usernameProperty());

        TableColumn<UserInfoData, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cd -> cd.getValue().emailProperty());

        TableColumn<UserInfoData, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(cd -> cd.getValue().passwordProperty());

        userInfoTableView.getColumns().addAll(firstNameCol, lastNameCol, usernameCol, emailCol, passwordCol);
        userInfoTableView.setItems(userInfoDataList);

        userInfoTableView.setRowFactory(tv -> {
            TableRow<UserInfoData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    UserInfoData rowData = row.getItem();
                    populateFieldsForEdit(rowData);
                }
            });
            return row;
        });

        refreshButton.setOnAction(e -> loadUserInfoData());
        modifyButton.setOnAction(e -> editUserInfoRecord());
        deleteButton.setOnAction(e -> deleteUserInfoRecord());

        loadUserInfoData();
    }

    private void populateFieldsForEdit(UserInfoData userInfoData) {
        firstNameTextField.setText(userInfoData.getFirstName());
        lastNameTextField.setText(userInfoData.getLastName());
        usernameTextField.setText(userInfoData.getUsername());
        emailTextField.setText(userInfoData.getEmail());
        passwordTextField.setText(userInfoData.getPassword());
    }

    private void loadUserInfoData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<UserInfoData> loadedData = FXCollections.observableArrayList();
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try (Connection con = Database.getUserConnection();
                         PreparedStatement pst = con.prepareStatement("select * from java_users_database");
                         ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            loadedData.add(new UserInfoData(
                                    rs.getString("Fname"),
                                    rs.getString("Lname"),
                                    rs.getString("Username"),
                                    rs.getString("Email"),
                                    rs.getString("Pass")
                            ));
                        }
                    }
                    Platform.runLater(() -> userInfoDataList.setAll(loadedData));
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void editUserInfoRecord() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || !ValidationUtils.isValidUsername(username) || !ValidationUtils.isValidEmail(email) || !ValidationUtils.isValidPassword(password)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection con = Database.getUserConnection();
                     PreparedStatement pst = con.prepareStatement("update java_users_database set Fname=?, Lname=?, Email=?, Pass=? where Username=?")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pst.setString(1, firstName);
                    pst.setString(2, lastName);
                    pst.setString(3, email);
                    pst.setString(4, password);
                    pst.setString(5, username);
                    pst.executeUpdate();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Record updated successfully!");
                        alert.showAndWait();
                        loadUserInfoData();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void deleteUserInfoRecord() {
        String username = usernameTextField.getText();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection con = Database.getUserConnection();
                     PreparedStatement pst = con.prepareStatement("delete from java_users_database where Username = ?")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pst.setString(1, username);
                    pst.executeUpdate();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Record deleted successfully!");
                        alert.showAndWait();
                        loadUserInfoData();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public class UserInfoData {

    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty username;
    private final SimpleStringProperty email;
    private final SimpleStringProperty password;

    public UserInfoData(String firstName, String lastName, String username, String email, String password) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public String getPassword() {
        return password.get();
    }
    }
}
