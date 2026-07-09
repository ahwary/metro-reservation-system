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

public class AddMetroController {

    @FXML
    private TextField mNoTextField;
    @FXML
    private TextField mNameTextField;
    @FXML
    private TextField mLineTextField;
    @FXML
    private TextField mDStationTextField;
    @FXML
    private TextField mAStationTextField;
    @FXML
    private TextField mPriceTextField;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TableView<MetroData> metroDetailsTableView;
    @FXML
    private Label titleLabel;

    private final ObservableList<MetroData> metroDataList = FXCollections.observableArrayList();

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
        metroDetailsTableView.getColumns().clear();

        TableColumn<MetroData, String> metroNumberCol = new TableColumn<>("Metro Number");
        metroNumberCol.setCellValueFactory(cd -> cd.getValue().metroNumberProperty());

        TableColumn<MetroData, String> metroNameCol = new TableColumn<>("Metro Name");
        metroNameCol.setCellValueFactory(cd -> cd.getValue().metroNameProperty());

        TableColumn<MetroData, String> lineNumberCol = new TableColumn<>("Line Number");
        lineNumberCol.setCellValueFactory(cd -> cd.getValue().lineNumberProperty());

        TableColumn<MetroData, String> departureStationCol = new TableColumn<>("Departure Station");
        departureStationCol.setCellValueFactory(cd -> cd.getValue().departureStationProperty());

        TableColumn<MetroData, String> arrivalStationCol = new TableColumn<>("Arrival Station");
        arrivalStationCol.setCellValueFactory(cd -> cd.getValue().arrivalStationProperty());

        TableColumn<MetroData, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cd -> cd.getValue().priceProperty());

        metroDetailsTableView.getColumns().addAll(metroNumberCol, metroNameCol, lineNumberCol, departureStationCol, arrivalStationCol, priceCol);
        metroDetailsTableView.setItems(metroDataList);

        metroDetailsTableView.setRowFactory(tv -> {
            TableRow<MetroData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    MetroData rowData = row.getItem();
                    populateFieldsForEdit(rowData);
                }
            });
            return row;
        });

        refreshButton.setOnAction(e -> loadMetroData());
        addButton.setOnAction(e -> addMetroRecord());
        editButton.setOnAction(e -> editMetroRecord());
        deleteButton.setOnAction(e -> deleteMetroRecord());

        loadMetroData();
    }

    private void populateFieldsForEdit(MetroData metroData) {
        mNoTextField.setText(metroData.getMetroNumber());
        mNameTextField.setText(metroData.getMetroName());
        mLineTextField.setText(metroData.getlineNumber());
        mDStationTextField.setText(metroData.getdepartureStation());
        mAStationTextField.setText(metroData.getarrivalStation());
        mPriceTextField.setText(metroData.getprice());
    }

    private void loadMetroData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<MetroData> loadedData = FXCollections.observableArrayList();
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try (Connection con = Database.getMetroConnection();
                         PreparedStatement pst = con.prepareStatement("select * from metro_details");
                         ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            loadedData.add(new MetroData(
                                    rs.getString("metro_no"),
                                    rs.getString("m_name"),
                                    rs.getString("m_lineno"),
                                    rs.getString("m_dstation"),
                                    rs.getString("m_astation"),
                                    rs.getString("m_price")
                            ));
                        }
                    }
                    Platform.runLater(() -> metroDataList.setAll(loadedData));
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void addMetroRecord() {
        String mno = mNoTextField.getText();
        String mname = mNameTextField.getText();
        String mline = mLineTextField.getText();
        String md = mDStationTextField.getText();
        String ma = mAStationTextField.getText();
        String mprice = mPriceTextField.getText();

        if (mno.isEmpty() || mname.isEmpty() || mline.isEmpty() || md.isEmpty() || ma.isEmpty() || mprice.isEmpty()) {
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
                     PreparedStatement pst = con.prepareStatement("insert into metro_details(metro_no,m_name,m_lineno,m_dstation,m_astation,m_price) values(?,?,?,?,?,?)")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pst.setString(1, mno);
                    pst.setString(2, mname);
                    pst.setString(3, mline);
                    pst.setString(4, md);
                    pst.setString(5, ma);
                    pst.setString(6, mprice);
                    pst.executeUpdate();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Record added successfully!");
                        alert.showAndWait();
                        loadMetroData();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void editMetroRecord() {
        String mno = mNoTextField.getText();
        String mname = mNameTextField.getText();
        String mline = mLineTextField.getText();
        String md = mDStationTextField.getText();
        String ma = mAStationTextField.getText();
        String mprice = mPriceTextField.getText();

        if (mno.isEmpty() || mname.isEmpty() || mline.isEmpty() || md.isEmpty() || ma.isEmpty() || mprice.isEmpty()) {
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
                     PreparedStatement pst = con.prepareStatement("update metro_details set m_name=?, m_lineno=?, m_dstation=?, m_astation=?, m_price=? where metro_no=?")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pst.setString(1, mname);
                    pst.setString(2, mline);
                    pst.setString(3, md);
                    pst.setString(4, ma);
                    pst.setString(5, mprice);
                    pst.setString(6, mno);
                    pst.executeUpdate();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Record updated successfully!");
                        alert.showAndWait();
                        loadMetroData();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void deleteMetroRecord() {
        String mno = mNoTextField.getText();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection con = Database.getMetroConnection();
                     PreparedStatement pst = con.prepareStatement("delete from metro_details where metro_no = ?")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pst.setString(1, mno);
                    pst.executeUpdate();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Record deleted successfully!");
                        alert.showAndWait();
                        loadMetroData();
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

    public class MetroData {

        public static class MetroDataProperties {
            private final SimpleStringProperty metroNumber;
            private final SimpleStringProperty metroName;
            private final SimpleStringProperty lineNumber;
            private final SimpleStringProperty departureStation;
            private final SimpleStringProperty arrivalStation;
            private final SimpleStringProperty price;

            public MetroDataProperties(String metroNumber, String metroName, String lineNumber, String departureStation, String arrivalStation, String price) {
                this.metroNumber = new SimpleStringProperty(metroNumber);
                this.metroName = new SimpleStringProperty(metroName);
                this.lineNumber = new SimpleStringProperty(lineNumber);
                this.departureStation = new SimpleStringProperty(departureStation);
                this.arrivalStation = new SimpleStringProperty(arrivalStation);
                this.price = new SimpleStringProperty(price);
            }

            // Getters follow

            public SimpleStringProperty metroNumberProperty() {
                return metroNumber;
            }

            public String getMetroNumber() {
                return metroNumber.get();
            }

            public void setMetroNumber(String metroNumber) {
                this.metroNumber.set(metroNumber);
            }

            public SimpleStringProperty metroNameProperty() {
                return metroName;
            }

            public String getMetroName() {
                return metroName.get();
            }

            public void setMetroName(String metroName) {
                this.metroName.set(metroName);
            }

            public SimpleStringProperty lineNumberProperty() {
                return lineNumber;
            }

            public String getlineNumber() {
                return lineNumber.get();
            }

            public void setlineNumber(String lineNumber) {
                this.lineNumber.set(lineNumber);
            }

            public SimpleStringProperty departureStationProperty() {
                return departureStation;
            }

            public String getdepartureStation() {
                return departureStation.get();
            }

            public void setdepartureStation(String departureStation) {
                this.departureStation.set(departureStation);
            }

            public SimpleStringProperty arrivalStationProperty() {
                return arrivalStation;
            }

            public String getarrivalStation() {
                return arrivalStation.get();
            }

            public void setarrivalStation(String arrivalStation) {
                this.arrivalStation.set(arrivalStation);
            }

            public SimpleStringProperty priceProperty() {
                return price;
            }

            public String getprice() {
                return price.get();
            }

            public void setprice(String price) {
                this.price.set(price);
            }
        }

        private final MetroDataProperties properties;

        public MetroData(String metroNumber, String metroName, String lineNumber, String departureStation, String arrivalStation, String price) {
            properties = new MetroDataProperties(metroNumber, metroName, lineNumber, departureStation, arrivalStation, price);
        }

        // Getters follow

        public SimpleStringProperty metroNumberProperty() {
            return properties.metroNumberProperty();
        }

        public String getMetroNumber() {
            return properties.getMetroNumber();
        }

        public void setMetroNumber(String metroNumber) {
            properties.setMetroNumber(metroNumber);
        }

        public SimpleStringProperty metroNameProperty() {
            return properties.metroNameProperty();
        }

        public String getMetroName() {
            return properties.getMetroName();
        }

        public void setMetroName(String metroName) {
            properties.setMetroName(metroName);
        }

        public SimpleStringProperty lineNumberProperty() {
            return properties.lineNumberProperty();
        }

        public String getlineNumber() {
            return properties.getlineNumber();
        }

        public void setlineNumber(String lineNumber) {
            properties.setlineNumber(lineNumber);
        }

        public SimpleStringProperty departureStationProperty() {
            return properties.departureStationProperty();
        }

        public String getdepartureStation() {
            return properties.getdepartureStation();
        }

        public void setdepartureStation(String departureStation) {
            properties.setdepartureStation(departureStation);
        }

        public SimpleStringProperty arrivalStationProperty() {
            return properties.arrivalStationProperty();
        }

        public String getarrivalStation() {
            return properties.getarrivalStation();
        }

        public void setarrivalStation(String arrivalStation) {
            properties.setarrivalStation(arrivalStation);
        }

        public SimpleStringProperty priceProperty() {
            return properties.priceProperty();
        }

        public String getprice() {
            return properties.getprice();
        }

        public void setprice(String price) {
            properties.setprice(price);
        }
    }
}
