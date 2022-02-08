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
import javafx.util.StringConverter;
import model.*;
import util.Filtering;
import util.TimeConversion;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentsScreen implements Initializable {
    private User       user;
    private ObservableList<Appointment> appts;
    private ObservableList<Customer>    customers;
    private ObservableList<Country>     countries;
    private ObservableList<Division>    divisions;

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
        populateCountries();
        populateDivisions();
        populateCustomers();
        populateAppointments();

        CountryCombo.valueProperty().addListener((ov, prevSelection, newSelection) -> {
            final Country C = (Country) newSelection;
            DivisionCombo.setItems(Filtering.filterDivisionsByCountry(this.divisions, C));
            CustomerTable.setItems(Filtering.filterCustomersByCountryId(this.customers, C));
        });

        DivisionCombo.valueProperty().addListener((ov, prevSelection, newSelection) -> {
            CustomerTable.setItems(Filtering.filterCustomersByDivision(this.customers, (Division) newSelection));
        });

        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ID.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("apptId"));
        Title.setCellValueFactory(new PropertyValueFactory<Appointment,String>("title"));
        Desc.setCellValueFactory(new PropertyValueFactory<Appointment,String>("desc"));
        Location.setCellValueFactory(new PropertyValueFactory<Appointment,String>("location"));
        Contact.setCellValueFactory(new PropertyValueFactory<Appointment,String>("contact"));
        Type.setCellValueFactory(new PropertyValueFactory<Appointment,String>("type"));
        Start.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("start"));
        Start.setCellFactory(tableColumn -> new TableCell<Appointment, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                    setText(null);
                else
                    setText(dateFormatter.format(item));
            }
        });
        End.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("end"));
        End.setCellFactory(tableColumn -> new TableCell<Appointment, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                    setText(null);
                else
                    setText(dateFormatter.format(item));
            }
        });
        CustomerID.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("customerId"));
        UserID.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("userId"));

        CustID.setCellValueFactory(new PropertyValueFactory<Customer,Integer>("customerId"));
        CustName.setCellValueFactory(new PropertyValueFactory<Customer,String>("name"));
        CustAddress.setCellValueFactory(new PropertyValueFactory<Customer,String>("address"));
        CustPostal.setCellValueFactory(new PropertyValueFactory<Customer,String>("postalCode"));
        CustPhone.setCellValueFactory(new PropertyValueFactory<Customer,String>("phone"));
        CustCreatedOn.setCellValueFactory(new PropertyValueFactory<Customer, ZonedDateTime>("createDate"));
        CustCreatedOn.setCellFactory(tableColumn -> new TableCell<Customer, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                    setText(null);
                else
                    setText(dateFormatter.format(item));
            }
        });
        CustCreatedBy.setCellValueFactory(new PropertyValueFactory<Customer,String>("createdBy"));

        CustLastUpdate.setCellValueFactory(new PropertyValueFactory<Customer, ZonedDateTime>("lastUpdate"));
        CustLastUpdate.setCellFactory(tableColumn -> new TableCell<Customer, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                    setText(null);
                else
                    setText(dateFormatter.format(item));
            }
        });
        CustLastUpdatedBy.setCellValueFactory(new PropertyValueFactory<Customer,String>("lastUpdatedBy"));
        CustDivision.setCellValueFactory(new PropertyValueFactory<Customer,String>("division"));
    }

    public void initUser(User user) {
        this.user = user;
        LoginButton.setVisible(false);
        UserLabel.setVisible(true);
        UserLabel.setText("Logged in as " + user.getUserName());

        CountryCombo.setItems(this.countries);
        CountryCombo.setConverter(new StringConverter<Country>() {

            @Override
            public String toString(Country country) {
                return country.getCountry();
            }

            @Override
            public Country fromString(String s) {
                for (final Country C : countries)
                    if (C.getCountry().equalsIgnoreCase(s))
                        return C;
                return null;
            }
        });

        DivisionCombo.setItems(this.divisions);
        DivisionCombo.setConverter(new StringConverter<Division>() {

            @Override
            public String toString(Division division) {
                return division.getDivision();
            }

            @Override
            public Division fromString(String s) {
                for (final Division D : divisions)
                    if (D.getDivision().equalsIgnoreCase(s))
                        return D;
                return null;
            }
        });

        AppTable.setItems(this.appts);

        CustomerTable.setItems(this.customers);
    }

    public void populateCountries() {
        this.countries = FXCollections.observableArrayList();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.countries")) {
            while (R.next()) {
                Country C = new Country(R.getInt("Country_ID"));
                C.setCountry(R.getString("Country"));

                Timestamp created = R.getTimestamp("Create_Date");
                C.setCreateDate(TimeConversion.toLocalTime(created));
                C.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                C.setLastUpdate(TimeConversion.toLocalTime(updated));
                C.setLastUpdatedBy(R.getString("Last_Updated_By"));

                this.countries.add(C);
            }
        }
        catch (SQLException sql) {
            // TODO
            System.err.println(sql.getMessage());
        }
    }

    public void populateDivisions() {
        this.divisions = FXCollections.observableArrayList();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.first_level_divisions")) {
            while (R.next()) {
                Division D = new Division(R.getInt("Division_ID"));
                D.setDivision(R.getString("Division"));

                Timestamp created = R.getTimestamp("Create_Date");
                D.setCreateDate(TimeConversion.toLocalTime(created));
                D.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                D.setLastUpdate(TimeConversion.toLocalTime(updated));
                D.setLastUpdatedBy(R.getString("Last_Updated_By"));

                D.setCountryId(R.getInt("Country_ID"));

                this.divisions.add(D);
            }
        }
        catch (SQLException sql) {
            // TODO
            System.err.println(sql.getMessage());
        }
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
                C.setCreateDate(TimeConversion.toLocalTime(created));
                C.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                C.setLastUpdate(TimeConversion.toLocalTime(updated));
                C.setLastUpdatedBy(R.getString("Last_Updated_By"));

                final String divName = R.getString("Division");
                Division D = divisions.filtered(div -> div.getDivision().equals(divName)).get(0);
                C.setDivision(D);

                this.customers.add(C);
            }
        }
        catch (SQLException sql) {
            // TODO(jon): Handle error
            System.err.println(sql.getMessage());
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
                A.setStart(TimeConversion.toLocalTime(start));

                Timestamp end = R.getTimestamp("End");
                A.setEnd(TimeConversion.toLocalTime(end));

                Timestamp created = R.getTimestamp("Create_Date");
                A.setCreateDate(TimeConversion.toLocalTime(created));
                A.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                A.setLastUpdate(TimeConversion.toLocalTime(updated));
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

    public void onClearFiltersAction(ActionEvent actionEvent) {
        CountryCombo.getSelectionModel().clearSelection();
        DivisionCombo.getSelectionModel().clearSelection();

        DivisionCombo.setItems(this.divisions);
        CustomerTable.setItems(this.customers);
    }

    public void showCustomerScreen(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerScreen.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Customer Information");
            stage.setScene(new Scene(loader.load()));
            CustomerScreen controller = loader.getController();
            controller.setUser(this.user);
            controller.passCountriesAndDivisions(this.countries, this.divisions);
            controller.setCustomer(customer);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onNewCustomerAction(ActionEvent actionEvent) {
        showCustomerScreen(null);
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void onEditCustomerAction(ActionEvent actionEvent) {
        showCustomerScreen((Customer) CustomerTable.getSelectionModel().getSelectedItem());
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    private boolean promptUser(String header, String message) {
        Alert prompt = new Alert(Alert.AlertType.CONFIRMATION);
        prompt.setTitle("Confirm");
        prompt.setHeaderText(header);
        prompt.setContentText(message);

        Optional<ButtonType> response = prompt.showAndWait();
        return response.get() == ButtonType.OK;
    }

    public void onDeleteCustomerAction(ActionEvent actionEvent) {
        // promp user, asking if they're sure
        final boolean confirm = promptUser("Delete Customer and all associated Appointments?",
                "Are you sure you want to delete this customer and all of their scheduled appointments?");
        if (!confirm)
            return;

        // delete customer and associated appointments
        Customer toDelete = (Customer) CustomerTable.getSelectionModel().getSelectedItem();
        if (toDelete == null) {
            // TODO(jon): ask user delete who?
            return;
        }

        try {
            JDBC.deleteCustomer(toDelete);
            customers.remove(toDelete);
            appts = appts.filtered(z -> z.getCustomerId() != toDelete.getCustomerId());
        }
        catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }
}
