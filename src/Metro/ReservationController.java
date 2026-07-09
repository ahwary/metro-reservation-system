package Metro;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ReservationController {

    @FXML
    private TextField txtname;

    @FXML
    private ComboBox<String> txtdstation;

    @FXML
    private ComboBox<String> txtastation;

    @FXML
    private TextField txtmnumber;

    @FXML
    private TextField txtmline;

    @FXML
    private TextField txtprice;

    @FXML
    private DatePicker txtdate;

    @FXML
    private TextField txtticket;

    @FXML
    private Button btnReserve;

    @FXML
    private Button btnSearch;

    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;

    // Initialize method called after FXML components are injected
    public void initialize() {
        System.out.println("Initializing...");
        Connect();
        Platform.runLater(this::DepartureStation);
        Platform.runLater(this::ArrivalStation);
    }

    // Method to establish database connection
    public void Connect() {
        try {
            System.out.println("Connecting to database...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = Database.getMetroConnection();
            System.out.println("Database connection established successfully");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ReservationController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error establishing database connection: " + ex.getMessage());
        }
    }

    // Method to populate the Arrival Station ComboBox
    public void ArrivalStation() {
        if (con == null) {
            showAlert("Database Error", "Database connection is not available.");
            return;
        }
        try {
            System.out.println("Fetching arrival stations...");
            pst = con.prepareStatement("SELECT DISTINCT m_astation FROM metro_details");
            rs = pst.executeQuery();
            txtastation.getItems().clear();
            System.out.println("Arrival Stations:");
            while (rs.next()) {
                String station = rs.getString(1);
                System.out.println("Arrival Station: " + station); // Debugging statement
                txtastation.getItems().add(station);
            }
            if (txtastation.getItems().isEmpty()) {
                System.out.println("No arrival stations found in database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving arrival stations: " + e.getMessage());
        }
    }

    // Method to populate the Departure Station ComboBox
    public void DepartureStation() {
        if (con == null) {
            showAlert("Database Error", "Database connection is not available.");
            return;
        }
        try {
            System.out.println("Fetching departure stations...");
            pst = con.prepareStatement("SELECT DISTINCT m_dstation FROM metro_details");
            rs = pst.executeQuery();
            txtdstation.getItems().clear();
            System.out.println("Departure Stations:");
            while (rs.next()) {
                String station = rs.getString(1);
                System.out.println("Departure Station: " + station); // Debugging statement
                txtdstation.getItems().add(station);
            }
            if (txtdstation.getItems().isEmpty()) {
                System.out.println("No departure stations found in database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving departure stations: " + e.getMessage());
        }
    }

    // Method to load metro details based on selected stations
    @FXML
    public void load() {
        String dstation = txtdstation.getValue();
        String astation = txtastation.getValue();
        if (dstation == null || astation == null) {
            showAlert("Error", "Please select both Departure and Arrival stations.");
            return;
        }

        try {
            System.out.println("Loading metro details...");
            pst = con.prepareStatement("SELECT * FROM metro_details WHERE m_dstation = ? AND m_astation = ?");
            pst.setString(1, dstation);
            pst.setString(2, astation);
            rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("Metro details found.");
                txtmnumber.setText(rs.getString("metro_no"));
                txtmline.setText(rs.getString("m_name"));
                txtprice.setText(rs.getString("m_price"));
            } else {
                showAlert("Record Not Found", "No records found for the selected stations.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading metro details: " + e.getMessage());
        }
    }

    // Method to reserve a ticket
    @FXML
    public void reserve() {
        String name = txtname.getText();
        String dstation = txtdstation.getValue();
        String mstation = txtastation.getValue();
        String mnumber = txtmnumber.getText();
        String mline = txtmline.getText();
        String mprice = txtprice.getText();
        if (txtdate.getValue() == null) {
            showAlert("Error", "Please select a date.");
            return;
        }
        String date = txtdate.getValue().toString();
        String tickets = txtticket.getText();

        try {
            System.out.println("Reserving ticket...");
            pst = con.prepareStatement("INSERT INTO reservation (name, dstation, mstation, mnumber, mname, mprice, date, tickets) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setString(1, name);
            pst.setString(2, dstation);
            pst.setString(3, mstation);
            pst.setString(4, mnumber);
            pst.setString(5, mline);
            pst.setString(6, mprice);
            pst.setString(7, date);
            pst.setString(8, tickets);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected == 1) {
                showAlert("Success", "Reservation successful!");
            } else {
                showAlert("Error", "Error inserting data into the database.");
            }
        } catch (SQLException e) {
            showAlert("Error", "Error inserting data into the database: " + e.getMessage());
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    void setMainApp(Main aThis) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void setPrimaryStage(Stage primaryStage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
