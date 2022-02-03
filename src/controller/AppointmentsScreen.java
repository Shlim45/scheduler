package controller;

import database.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AppointmentsScreen implements Initializable {
    private User       user;
    private ObservableList<Appointment> appts;
    private ObservableList<Customer>    customers;

    public Label       UserLabel;
    public Button      LoginButton;
    
    // Appointments Table
    public RadioButton Weekly;
    public RadioButton Monthly;
    public TableView   AppTable;
    public TableColumn ID;
    public TableColumn Title;
    public TableColumn Desc;
    public TableColumn Location;
    public TableColumn Contact;
    public TableColumn Type;
    public TableColumn Start;
    public TableColumn End;
    public TableColumn CustomerID;
    public TableColumn UserID;

    // Customers Table
    public ComboBox CountryCombo;
    public ComboBox DivisionCombo;
    public TableView CustomerTable;
    public TableColumn CustID;
    public TableColumn CustName;
    public TableColumn CustAddress;
    public TableColumn CustPostal;
    public TableColumn CustPhone;
    public TableColumn CustCreatedOn;
    public TableColumn CustCreatedBy;
    public TableColumn CustLastUpdate;
    public TableColumn CustLastUpdatedBy;
    public TableColumn CustDivision;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ID.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("apptId"));
        Title.setCellValueFactory(new PropertyValueFactory<Appointment,String>("title"));
        Desc.setCellValueFactory(new PropertyValueFactory<Appointment,String>("desc"));
        Location.setCellValueFactory(new PropertyValueFactory<Appointment,String>("location"));
        Contact.setCellValueFactory(new PropertyValueFactory<Appointment,String>("contact"));
        Type.setCellValueFactory(new PropertyValueFactory<Appointment,String>("type"));
        Start.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("start"));
        End.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("end"));
        CustomerID.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("customerId"));
        UserID.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("userId"));

        CustID.setCellValueFactory(new PropertyValueFactory<Customer,Integer>("customerId"));
        CustName.setCellValueFactory(new PropertyValueFactory<Customer,String>("name"));
        CustAddress.setCellValueFactory(new PropertyValueFactory<Customer,String>("address"));
        CustPostal.setCellValueFactory(new PropertyValueFactory<Customer,String>("postalCode"));
        CustPhone.setCellValueFactory(new PropertyValueFactory<Customer,String>("phone"));
        CustCreatedOn.setCellValueFactory(new PropertyValueFactory<Customer, LocalDateTime>("createDate"));
        CustCreatedBy.setCellValueFactory(new PropertyValueFactory<Customer,String>("createdBy"));
        CustLastUpdate.setCellValueFactory(new PropertyValueFactory<Customer, LocalDateTime>("lastUpdate"));
        CustLastUpdatedBy.setCellValueFactory(new PropertyValueFactory<Customer,String>("lastUpdatedBy"));
        CustDivision.setCellValueFactory(new PropertyValueFactory<Customer,String>("division"));
    }

    public void initUser(User user) {
        this.user = user;
        LoginButton.setVisible(false);
        UserLabel.setVisible(true);
        UserLabel.setText("Logged in as " + user.getUserName());

        populateAppointments();
        populateCustomers();
    }

    public void populateCustomers() {
        this.customers = FXCollections.observableArrayList();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.customers "
                +"LEFT JOIN client_schedule.first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID")) {
            while (R.next()) {
                Customer C = new Customer(R.getInt("Customer_ID"));
                C.setName(R.getString("Customer_Name"));
                C.setAddress(R.getString("Address"));
                C.setPostalCode(R.getString("Postal_Code"));
                C.setPhone(R.getString("Phone"));

                Timestamp created = R.getTimestamp("Create_Date");
                C.setCreateDate(created.toLocalDateTime());
                C.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                C.setLastUpdate(updated.toLocalDateTime().toLocalTime());
                C.setLastUpdatedBy(R.getString("Last_Updated_By"));

                C.setDivisionId(R.getInt("Division_ID"));
                C.setDivision(R.getString("Division"));

                this.customers.add(C);
            }
        }
        catch (SQLException sql) {
            // TODO(jon): Handle error
            System.err.println(sql.getMessage());
        }
        finally {
            CustomerTable.setItems(this.customers);
        }
    }

    public void populateAppointments() {
        this.appts = FXCollections.observableArrayList();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.appointments "
                +"LEFT JOIN client_schedule.contacts ON appointments.Contact_ID = contacts.Contact_ID;")) {
            while (R.next()) {
                Appointment A = new Appointment(R.getInt("Appointment_ID"));
                A.setTitle(R.getString("Title"));
                A.setDesc(R.getString("Description"));
                A.setLocation(R.getString("Location"));
                A.setType(R.getString("Type"));

                Timestamp start = R.getTimestamp("Start");
                A.setStart(start.toLocalDateTime());

                Timestamp end = R.getTimestamp("End");
                A.setEnd(end.toLocalDateTime());

                Timestamp created = R.getTimestamp("Create_Date");
                A.setCreateDate(created.toLocalDateTime());
                A.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                A.setLastUpdate(updated.toLocalDateTime().toLocalTime());
                A.setLastUpdatedBy(R.getString("Last_Updated_By"));

                A.setCustomerId(R.getInt("Customer_ID"));
                A.setUserId(R.getInt("User_ID"));
                A.setContactId(R.getInt("Contact_ID"));
                A.setContact(R.getString("Contact_Name"));

                this.appts.add(A);
            }
        }
        catch (SQLException sql) {
            // TODO(jon): Handle error
            System.err.println(sql.getMessage());
        }
        finally {
            AppTable.setItems(this.appts);
        }
    }

    public void onLoginAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Please log in");
            stage.setScene(new Scene(loader.load()));
            stage.show();

            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterByCountry(ActionEvent actionEvent) {
    }

    public void filterByDivision(ActionEvent actionEvent) {
    }
}
