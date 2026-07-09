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

public class AdminRController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField dStationTextField;
    @FXML
    private TextField mStationTextField;
    @FXML
    private TextField mNumberTextField;
    @FXML
    private TextField mNameTextField;
    @FXML
    private TextField mPriceTextField;
    @FXML
    private TextField dateTextField;
    @FXML
    private TextField ticketsTextField;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TableView<ReservationData> reservationDetailsTableView;
    @FXML
    private Label titleLabel;

    private final ObservableList<ReservationData> reservationDataList = FXCollections.observableArrayList();

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
        reservationDetailsTableView.getColumns().clear();

        TableColumn<ReservationData, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cd -> cd.getValue().nameProperty());

        TableColumn<ReservationData, String> dStationCol = new TableColumn<>("Departure Station");
        dStationCol.setCellValueFactory(cd -> cd.getValue().dStationProperty());

        TableColumn<ReservationData, String> mStationCol = new TableColumn<>("Middle Station");
        mStationCol.setCellValueFactory(cd -> cd.getValue().mStationProperty());

        TableColumn<ReservationData, String> mNumberCol = new TableColumn<>("Metro Number");
        mNumberCol.setCellValueFactory(cd -> cd.getValue().mNumberProperty());

        TableColumn<ReservationData, String> mNameCol = new TableColumn<>("Metro Name");
        mNameCol.setCellValueFactory(cd -> cd.getValue().mNameProperty());

        TableColumn<ReservationData, String> mPriceCol = new TableColumn<>("Price");
        mPriceCol.setCellValueFactory(cd -> cd.getValue().mPriceProperty());

        TableColumn<ReservationData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cd -> cd.getValue().dateProperty());

        TableColumn<ReservationData, String> ticketsCol = new TableColumn<>("Tickets");
        ticketsCol.setCellValueFactory(cd -> cd.getValue().ticketsProperty());

        reservationDetailsTableView.getColumns().addAll(nameCol, dStationCol, mStationCol, mNumberCol, mNameCol, mPriceCol, dateCol, ticketsCol);
        reservationDetailsTableView.setItems(reservationDataList);

        reservationDetailsTableView.setRowFactory(tv -> {
            TableRow<ReservationData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ReservationData rowData = row.getItem();
                    populateFieldsForEdit(rowData);
                }
            });
            return row;
        });

        refreshButton.setOnAction(e -> loadReservationData());
        modifyButton.setOnAction(e -> editReservationRecord());
        deleteButton.setOnAction(e -> deleteReservationRecord());

        loadReservationData();
    }

    private void populateFieldsForEdit(ReservationData reservationData) {
        nameTextField.setText(reservationData.getName());
        dStationTextField.setText(reservationData.getdStation());
        mStationTextField.setText(reservationData.getmStation());
        mNumberTextField.setText(reservationData.getmNumber());
        mNameTextField.setText(reservationData.getmName());
        mPriceTextField.setText(reservationData.getmPrice());
        dateTextField.setText(reservationData.getDate());
        ticketsTextField.setText(reservationData.getTickets());
    }

    private void loadReservationData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<ReservationData> loadedData = FXCollections.observableArrayList();
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try (Connection con = Database.getMetroConnection();
                         PreparedStatement pst = con.prepareStatement("select * from reservation");
                         ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            loadedData.add(new ReservationData(
                                    rs.getString("name"),
                                    rs.getString("dstation"),
                                    rs.getString("mstation"),
                                    rs.getString("mnumber"),
                                    rs.getString("mname"),
                                    rs.getString("mprice"),
                                    rs.getString("date"),
                                    rs.getString("tickets")
                            ));
                        }
                    }
                    Platform.runLater(() -> reservationDataList.setAll(loadedData));
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void editReservationRecord() {
        String name = nameTextField.getText();
        String dStation = dStationTextField.getText();
        String mStation = mStationTextField.getText();
        String mNumber = mNumberTextField.getText();
        String mName = mNameTextField.getText();
        String mPrice = mPriceTextField.getText();
        String date = dateTextField.getText();
        String tickets = ticketsTextField.getText();

        if (name.isEmpty() || dStation.isEmpty() || mStation.isEmpty() || mNumber.isEmpty() || mName.isEmpty() || mPrice.isEmpty() || date.isEmpty() || tickets.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection con = Database.getMetroConnection();
                     PreparedStatement pst = con.prepareStatement("update reservation set dstation=?, mstation=?, mnumber=?, mname=?, mprice=?, date=?, tickets=? where name=?")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pst.setString(1, dStation);
                    pst.setString(2, mStation);
                    pst.setString(3, mNumber);
                    pst.setString(4, mName);
                    pst.setString(5, mPrice);
                    pst.setString(6, date);
                    pst.setString(7, tickets);
                    pst.setString(8, name);
                    pst.executeUpdate();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Record updated successfully!");
                        alert.showAndWait();
                        loadReservationData();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void deleteReservationRecord() {
        String name = nameTextField.getText();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection con = Database.getMetroConnection();
                     PreparedStatement pst = con.prepareStatement("delete from reservation where name = ?")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pst.setString(1, name);
                    pst.executeUpdate();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Record deleted successfully!");
                        alert.showAndWait();
                        loadReservationData();
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

    public static class ReservationData {

        private final SimpleStringProperty name;
        private final SimpleStringProperty dStation;
        private final SimpleStringProperty mStation;
        private final SimpleStringProperty mNumber;
        private final SimpleStringProperty mName;
        private final SimpleStringProperty mPrice;
        private final SimpleStringProperty date;
        private final SimpleStringProperty tickets;

        public ReservationData(String name, String dStation, String mStation, String mNumber, String mName, String mPrice, String date, String tickets) {
            this.name = new SimpleStringProperty(name);
            this.dStation = new SimpleStringProperty(dStation);
            this.mStation = new SimpleStringProperty(mStation);
            this.mNumber = new SimpleStringProperty(mNumber);
            this.mName = new SimpleStringProperty(mName);
            this.mPrice = new SimpleStringProperty(mPrice);
            this.date = new SimpleStringProperty(date);
            this.tickets = new SimpleStringProperty(tickets);
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public SimpleStringProperty dStationProperty() {
            return dStation;
        }

        public String getdStation() {
            return dStation.get();
        }

        public void setdStation(String dStation) {
            this.dStation.set(dStation);
        }

        public SimpleStringProperty mStationProperty() {
            return mStation;
        }

        public String getmStation() {
            return mStation.get();
        }

        public void setmStation(String mStation) {
            this.mStation.set(mStation);
        }

        public SimpleStringProperty mNumberProperty() {
            return mNumber;
        }

        public String getmNumber() {
            return mNumber.get();
        }

        public void setmNumber(String mNumber) {
            this.mNumber.set(mNumber);
        }

        public SimpleStringProperty mNameProperty() {
            return mName;
        }

        public String getmName() {
            return mName.get();
        }

        public void setmName(String mName) {
            this.mName.set(mName);
        }

        public SimpleStringProperty mPriceProperty() {
            return mPrice;
        }

        public String getmPrice() {
            return mPrice.get();
        }

        public void setmPrice(String mPrice) {
            this.mPrice.set(mPrice);
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        public SimpleStringProperty ticketsProperty() {
            return tickets;
        }

        public String getTickets() {
            return tickets.get();
        }

        public void setTickets(String tickets) {
            this.tickets.set(tickets);
        }
    }
}

