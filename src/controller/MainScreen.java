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
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import model.*;
import util.Dialogs;
import util.Filtering;
import util.Time;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The controller class for the MainScreen view.
 *
 * @author Jonathan Hawranko
 */
public class MainScreen implements Initializable {
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
    public RadioButton AllAppts;
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

    // Reports
    public RadioButton ReportAppts;
    public RadioButton ReportContacts;
    public RadioButton ReportAdditional;

    /**
     * Initializes the Main Screen.
     * Loads all countries, divisions, customers, and appointments, and sets up the Combo Boxes and Tables.
     * <br /><br />
     * Uses a lambda function to add a change listener to the radio button ToggleGroups.
     * The lambda used for <i>apptRadios</i> determines which radio button has been selected,
     * and filters the appointments by current week or month.<br />
     * The lambda used for <i>CountryCombo</i> determines selected Country,
     * and filters <i>DivisionCombo</i> and <i>CustomerTable</i> by the selected Country.<br />
     * The lambda used for <i>DivisionCombo</i> determines selected Division,
     * and filters <i>CustomerTable</i> by selected Division.<br />
     * The lambdas used for <i>setCellFactory</i> handle formatting the data shown in that <i>TableCell</i>.<br />
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.countries = FXCollections.observableArrayList(JDBC.loadCountries());
            this.divisions = FXCollections.observableArrayList(JDBC.loadDivisions());
            this.customers = FXCollections.observableArrayList(JDBC.loadCustomers(this.divisions));
            this.appts = FXCollections.observableArrayList(JDBC.loadAppointments());
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            System.exit(1);
        }

        final ToggleGroup apptRadios = new ToggleGroup();
        Weekly.setToggleGroup(apptRadios);
        Monthly.setToggleGroup(apptRadios);
        AllAppts.setToggleGroup(apptRadios);
        apptRadios.selectedToggleProperty().addListener((ov, t, newToggle) -> {
            if (newToggle == Weekly)
                AppTable.setItems(Filtering.filterAppointmentsThisWeek(this.appts));
            else if (newToggle == Monthly)
                AppTable.setItems(Filtering.filterAppointmentsThisMonth(this.appts));
            else
                AppTable.setItems(this.appts);

        });

        CountryCombo.valueProperty().addListener((ov, t, newSelection) -> {
            final Country C = (Country) newSelection;
            DivisionCombo.setItems(Filtering.filterDivisionsByCountry(this.divisions, C));
            CustomerTable.setItems(Filtering.filterCustomersByCountryId(this.customers, C));
        });

        DivisionCombo.valueProperty().addListener(
                (ov, t, newSelection) -> CustomerTable.setItems(Filtering.filterCustomersByDivision(this.customers, (Division) newSelection)));

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
                    setText(Time.dateFormatter.format(item));
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
                    setText(Time.dateFormatter.format(item));
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
                    setText(Time.dateFormatter.format(item));
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
                    setText(Time.dateFormatter.format(item));
            }
        });
        CustLastUpdatedBy.setCellValueFactory(new PropertyValueFactory<Customer,String>("lastUpdatedBy"));
        CustDivision.setCellValueFactory(new PropertyValueFactory<Customer,String>("division"));

        final ToggleGroup reportRadios = new ToggleGroup();
        ReportAppts.setToggleGroup(reportRadios);
        ReportContacts.setToggleGroup(reportRadios);
        ReportAdditional.setToggleGroup(reportRadios);
    }

    /**
     * Sets the active user. This method hides the Login button, displays the current
     * username, and populates the Combo Boxes and Tables.
     *
     * @param user The logged-in user
     */
    public void initUser(User user) {
        this.user = user;
        LoginButton.setVisible(false);
        UserLabel.setVisible(true);
        UserLabel.setText(String.format("Logged in as %s.", user.getUserName()));

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

    /**
     * Checks for upcoming appointments. Checks the logged-in user's appointments for an appointment beginning within 15 minutes.
     */
    public void checkForUpcomingAppts() {
        if (this.user != null && this.appts != null && this.appts.size() > 0) {
            final ZonedDateTime now       = ZonedDateTime.now();
            final ZonedDateTime timeFrame = now.plusMinutes(15);
            String alertOutput = "You do not have any upcoming appointments within the next 15 minutes.";

            for (final Appointment A : this.appts) {
                if (A.getUserId() != this.user.getUserId())
                    continue;
                if ((A.getStart().isAfter(now) || A.getStart().isEqual(now))
                        && A.getStart().isBefore(timeFrame)) {
                    alertOutput = String.format("Customer ID %d has an appointment starting at %s on %s, %s.",
                            A.getCustomerId(),
                            A.getStart().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            A.getStart().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                            A.getStart().toLocalDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
                    break;
                }
            }

            Dialogs.alertUser(
                    Alert.AlertType.INFORMATION,
                    "Upcoming Appointments",
                    "Upcoming Appointments",
                    alertOutput);
        }
    }

    /**
     * Shows the Login Screen.
     *
     * @param actionEvent
     */
    public void onLoginAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(new Scene(loader.load()));
            stage.show();

            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears any filters applied to the Customer Table.
     *
     * @param actionEvent
     */
    public void onClearFiltersAction(ActionEvent actionEvent) {
        CountryCombo.getSelectionModel().clearSelection();
        DivisionCombo.getSelectionModel().clearSelection();

        DivisionCombo.setItems(this.divisions);
        CustomerTable.setItems(this.customers);
    }

    // Customer Actions

    /**
     * Shows the Customer Screen.
     * If <b>customer</b> is null, user can create a new Customer.
     * Otherwise, the form is populated with the customer's data for editing.
     *
     * @param customer The customer to edit, or null
     */
    public void showCustomerScreen(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerScreen.fxml"));
            Stage stage = new Stage(StageStyle.UNDECORATED);
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

    /**
     * Handles the <b>New</b> customer button action.
     *
     * @see #showCustomerScreen(Customer)
     * @param actionEvent
     */
    public void onNewCustomerAction(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        showCustomerScreen(null);
    }

    /**
     * Handles the <b>Edit</b> customer button action.
     *
     * @see #showCustomerScreen(Customer)
     * @param actionEvent
     */
    public void onEditCustomerAction(ActionEvent actionEvent) {
        Customer toEdit = (Customer) CustomerTable.getSelectionModel().getSelectedItem();
        if (toEdit == null) {
            Dialogs.alertUser(
                    Alert.AlertType.ERROR,
                    "Edit Customer",
                    "Edit Customer",
                    "You must select a customer to edit.");
            return;
        }
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        showCustomerScreen(toEdit);
    }

    /**
     * Handles the <b>Delete</b> customer button action.
     * Deletes a customer and appointments.
     * <br /><br />
     * A lambda function is used to add each appointment to delete
     * to a new <b>ArrayList</b>.<br />
     * A second lambda function is used to remove those appointments
     * from the <i>appts</i> list.<br />
     *
     * @param actionEvent
     */
    public void onDeleteCustomerAction(ActionEvent actionEvent) {
        Customer toDelete = (Customer) CustomerTable.getSelectionModel().getSelectedItem();
        if (toDelete == null) {
            Dialogs.alertUser(
                    Alert.AlertType.ERROR,
                    "Delete Customer",
                    "Delete Customer",
                    "You must select a customer to delete.");
            return;
        }

        // promp user, asking if they're sure
        final boolean confirm = Dialogs.promptUser("Delete Customer and all associated Appointments?",
                "Are you sure you want to delete this customer and all of their scheduled appointments?");
        if (!confirm)
            return;

        // delete customer and associated appointments
        try {
            JDBC.deleteCustomerAndAppointments(toDelete);
            customers.remove(toDelete);

            List<Appointment> deleteAppts = new ArrayList<>();
            appts.forEach(appt -> {
                if (appt.getCustomerId() == toDelete.getCustomerId())
                    deleteAppts.add(appt);
            });
            deleteAppts.forEach(appt -> {
                appts.remove(appt);
            });

            Dialogs.alertUser(
                    Alert.AlertType.INFORMATION,
                    "Customer Deleted",
                    "Customer Deleted",
                    toDelete.getName() + " and their associated appointments have been deleted.");
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Delete Customer", "Delete Customer", sqle.getMessage());
        }
    }

    // Appointment Actions

    /**
     * Shows the Appointment Screen.
     * If <b>appointment</b> is null, user can create a new appointment for the given customer.
     * Otherwise, the form is populated with the customer's appointment data for editing.
     *
     * A lambda function is used to create a <b>Predicate</b> by which a new <b>FilteredList</b> of appointments is generated.
     *
     * @param customer The customer with the appointment
     * @param appointment The appointment to edit, or null
     */
    public void showAppointmentScreen(Customer customer, Appointment appointment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AppointmentScreen.fxml"));
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(new Scene(loader.load()));
            AppointmentScreen controller = loader.getController();
            controller.setUser(this.user);
            if (customer != null) {
                controller.setCustomer(customer);
                controller.setCustomerAppointments(this.appts.filtered(appt -> appt.getCustomerId() == customer.getCustomerId()));
            }
            if (appointment != null) {
                controller.setAppointment(appointment);
                controller.setCustomerAppointments(this.appts.filtered(appt -> appt.getCustomerId() == appointment.getCustomerId()));
            }
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the <b>Add</b> appointment button action.
     *
     * @see #showAppointmentScreen(Customer, Appointment)
     * @param actionEvent
     */
    public void onAddApptAction(ActionEvent actionEvent) {
        Customer customer = (Customer) CustomerTable.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Dialogs.alertUser(
                    Alert.AlertType.ERROR,
                    "Add Appointment",
                    "Customer Not Selected",
                    "You must select a customer to add an appointment for.");
            return;
        }
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        showAppointmentScreen(customer, null);
    }

    /**
     * Handles the <b>Edit</b> appointment button action.
     * 
     * @see #showAppointmentScreen(Customer, Appointment)
     * @param actionEvent
     */
    public void onEditApptAction(ActionEvent actionEvent) {
        Appointment toEdit = (Appointment) AppTable.getSelectionModel().getSelectedItem();
        if (toEdit == null) {
            Dialogs.alertUser(
                    Alert.AlertType.ERROR,
                    "Edit Appointment",
                    "Edit Appointment",
                    "You must select an appointment to edit.");
            return;
        }
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        showAppointmentScreen(null, toEdit);
    }

    /**
     * Handles the <b>Delete</b> appointment button action.
     * Deletes a customer's appointment.
     *
     * @param actionEvent
     */
    public void onDeleteApptAction(ActionEvent actionEvent) {
        Appointment toDelete = (Appointment) AppTable.getSelectionModel().getSelectedItem();
        if (toDelete == null) {
            Dialogs.alertUser(
                    Alert.AlertType.ERROR,
                    "Delete Appointment",
                    "Delete Appointment",
                    "You must select an appointment to delete.");
            return;
        } else {
            final boolean confirm = Dialogs.promptUser("Delete Appointment", String.format("Are you sure you want to delete the appointment with ID %d?", toDelete.getApptId()));
            if (!confirm)
                return;
        }
        try {
            JDBC.deleteAppointment(toDelete);
            this.appts.remove(toDelete);
            Dialogs.alertUser(
                    Alert.AlertType.INFORMATION,
                    "Appointment Cancelled",
                    "Appointment Cancelled",
                            String.format("The following appointment has been cancelled:\nID: %d \tType: %s", toDelete.getApptId(), toDelete.getType()));
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Delete Appointment", "Delete Appointment", sqle.getMessage());
        }
    }

    /**
     * Shows a modal window containing a generated report's output.
     * 
     * @param title The title of the modal window
     * @param report The report to display
     * @param width The width, in pixels, of the modal window
     * @param height The height, in pixels, of the modal window
     */
    public void showReportWindow(String title, String report, int width, int height) {
        TextArea reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setText(report);
        reportOutput.setWrapText(true);
        reportOutput.setLayoutX(10);
        reportOutput.setLayoutY(10);
        reportOutput.setPrefWidth(width-20);
        reportOutput.setMinWidth(400);
        reportOutput.setPrefHeight(height-20);
        reportOutput.setMinHeight(400);
        reportOutput.setFont(Font.font("Monospaced", 16));

        StackPane reportLayout = new StackPane();
        reportLayout.getChildren().add(reportOutput);

        Scene reportScene = new Scene(reportLayout, width, height);
        Stage reportWindow = new Stage();
        reportWindow.setTitle(title);
        reportWindow.setScene(reportScene);
        reportWindow.setMinWidth(420);
        reportWindow.setMinHeight(450);

        reportWindow.show();
    }

    /**
     * Handles generating a report based on the selected radio button,
     * and displaying it in a modal window.
     * 
     * @see #showReportWindow(String, String, int, int) 
     * @param actionEvent
     */
    public void onGenerateReportAction(ActionEvent actionEvent) {
        final String windowTitle, report;
        final int width, height;
        if (ReportAppts.isSelected()) {
            windowTitle = "Total of Customer Appointments";
            try {
                report = JDBC.generateApptReport();
                width = 800;
                height = 600;
            } catch (SQLException sqle) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Error Generating Report", "Error Generating Report", "There was an error in generating the requested report.");
                return;
            }
        }
        else if (ReportContacts.isSelected()) {
            windowTitle = "Schedule for each Contact";
            try {
                report = JDBC.generateContactsReport();
                width = 1500;
                height = 600;
            } catch (SQLException sqle) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Error Generating Report", "Error Generating Report", "There was an error in generating the requested report.");
                return;
            }
        }
        else if (ReportAdditional.isSelected()) {
            windowTitle = "Additional Report";
            report = JDBC.generateCustomReport();
            width = 800;
            height = 600;
        }
        else {
            Dialogs.alertUser(Alert.AlertType.WARNING, "Generate Report", "Select report type",
                    "You must first choose a type of report to generate.");
            return;
        }
        showReportWindow(windowTitle, report, width, height);
    }
}
