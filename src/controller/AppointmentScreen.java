package controller;

import database.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.*;
import util.Dialogs;
import util.Time;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * The controller class for the AppointmentScreen view.
 *
 * @author Jonathan Hawranko
 */
public class AppointmentScreen implements Initializable {
    public TextField  ApptId;
    public TextField  ApptTitle;
    public TextField  ApptDesc;
    public TextField  ApptLocation;
    public ComboBox   ContactCombo;
    public TextField  ApptType;
    public DatePicker StartDate;
    public TextField  StartTime;
    public DatePicker EndDate;
    public TextField  EndTime;
    public TextField  ApptCustomerId;
    public TextField  ApptUserId;

    private User                        user;
    private Customer                    customer;
    private Appointment                 appointment;
    private ObservableList<Appointment> customerAppts;
    private ObservableList<Contact>     contacts;

    /**
     * Initializes the Appointment Screen.
     * If creating a new appointment, the customer ID field is populated.  If modifying
     * an existing appointment, all fields are populated.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contacts = FXCollections.observableArrayList(JDBC.loadContacts());
    }

    /**
     * Sets the active user. This is the user who will be tied to the creation or
     * modification of the appointment.
     *
     * @param user The logged-in user
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Sets the customer to whom this appointment pertains.
     *
     * @param customer The appointment's customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        populateFields();
    }

    /**
     * Sets the list of existing appointments for the customer. This is used in
     * checking for scheduling conflicts.
     *
     * @param appts The customer's existing appointments
     */
    public void setCustomerAppointments(ObservableList<Appointment> appts) {
        this.customerAppts = appts;
    }

    /**
     * Sets the appointment to modify.  Pre-populates all fields on the form
     * with data.
     *
     * @param appt
     */
    public void setAppointment(Appointment appt) {
        this.appointment = appt;
        populateFields();
    }

    private void populateFields() {
        if (appointment != null) {
            ApptId.setText(Integer.toString(appointment.getApptId()));
            ApptTitle.setText(appointment.getTitle());
            ApptDesc.setText(appointment.getDesc());
            ApptLocation.setText(appointment.getLocation());
            ApptType.setText(appointment.getType());
            ApptCustomerId.setText(Integer.toString(appointment.getCustomerId()));
            ApptUserId.setText(Integer.toString(appointment.getUserId()));

            StartDate.setValue(appointment.getStart().toLocalDate());
            StartTime.setText(appointment.getStart().toLocalTime().toString());
            EndDate.setValue(appointment.getEnd().toLocalDate());
            EndTime.setText(appointment.getEnd().toLocalTime().toString());
        }
        else if (customer != null) {
            ApptCustomerId.setText(Integer.toString(customer.getCustomerId()));
            ApptUserId.setText(Integer.toString(user.getUserId()));
        }

        if (contacts != null) {
            ContactCombo.setItems(this.contacts);

            if (appointment != null) {
                Contact C = contacts.filtered(contact -> contact.getName().equals(appointment.getContact())).get(0);
                ContactCombo.getSelectionModel().select(C);
            }
        }
    }

    private Appointment createAppointmentObject() {
        Appointment appt = new Appointment();
        if (this.appointment != null)
            appt.setApptId(this.appointment.getApptId());
        appt.setTitle(ApptTitle.getText());
        appt.setDesc(ApptDesc.getText());
        appt.setLocation(ApptLocation.getText());
        appt.setType(ApptType.getText());
        appt.setStart(ZonedDateTime.of(StartDate.getValue(), LocalTime.parse(StartTime.getText()), ZoneId.systemDefault()));
        appt.setEnd(ZonedDateTime.of(EndDate.getValue(), LocalTime.parse(EndTime.getText()), ZoneId.systemDefault()));
        appt.setCustomerId(Integer.parseInt(ApptCustomerId.getText()));
        appt.setUserId(Integer.parseInt(ApptUserId.getText()));
        Contact contact = (Contact) ContactCombo.getSelectionModel().getSelectedItem();
        appt.setContactId(contact.getContactId());
        appt.setContact(contact.getName());
        return appt;
    }

    /**
     * Called when user presses ENTER key on the form.  Checks for next empty
     * value and requests focus on that textbox.  Performs input validation
     * on ID and time fields.  
     * Submits form if all fields filled.
     *
     * @see #onSubmitAction(ActionEvent) 
     * @param actionEvent
     */
    public void onEnterAction(ActionEvent actionEvent) {
        if (ApptTitle.getText().length() == 0) {
            ApptTitle.requestFocus();
            return;
        } else if (ApptDesc.getText().length() == 0) {
            ApptDesc.requestFocus();
            return;
        } else if (ApptLocation.getText().length() == 0) {
            ApptLocation.requestFocus();
            return;
        } else if (ContactCombo.getSelectionModel().getSelectedItem() == null) {
            ContactCombo.requestFocus();
            return;
        } else if (ApptType.getText().length() == 0) {
            ApptType.requestFocus();
            return;
        } else if (StartDate.getValue() == null) {
            StartDate.requestFocus();
            return;
        } else if (StartTime.getText().length() == 0) {
            StartTime.requestFocus();
            return;
        } else if (EndDate.getValue() == null) {
            EndDate.requestFocus();
            return;
        } else if (EndTime.getText().length() == 0) {
            EndTime.requestFocus();
            return;
        } else {
            // TODO(jon): customer and user ID validation?
            try {
                Integer.parseInt(ApptCustomerId.getText());
            } catch(NumberFormatException nfe) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Invalid Customer ID", "Invalid Customer ID", "Customer ID must be an integer.");
                ApptCustomerId.requestFocus();
                return;
            }

            try {
                Integer.parseInt(ApptUserId.getText());
            } catch(NumberFormatException nfe) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Invalid User ID", "Invalid User ID", "User ID must be an integer.");
                ApptUserId.requestFocus();
                return;
            }
        }

        if (StartTime.getText().length() > 0) {
            try {
                final String formatted = Time.timeFormatting(StartTime.getText());
                StartTime.setText(formatted);
                LocalTime.parse(formatted);
            }
            catch (DateTimeParseException dtpe) {
                StartTime.requestFocus();
                return;
            }
        }
        else if (EndTime.getText().length() > 0) {
            try {
                final String formatted = Time.timeFormatting(EndTime.getText());
                EndTime.setText(formatted);
                LocalTime.parse(formatted);
            }
            catch (DateTimeParseException dtpe) {
                EndTime.requestFocus();
                return;
            }
        }

        onSubmitAction(actionEvent);
    }

    /**
     * Submits the appointment to the database. Prompts the user for confirmation.
     *
     * @param actionEvent
     */
    public void onSubmitAction(ActionEvent actionEvent) {
        final Appointment appt = createAppointmentObject();

        if (Time.hasSchedulingErrors(appt, this.customerAppts))
            return;

        final boolean confirm = Dialogs.promptUser("Submit appointment?",
                "Are you sure you want to submit the appointment?");
        if (confirm) {
            try {
                if (this.appointment == null)
                    JDBC.insertAppointment(this.user, appt);
                else
                    JDBC.updateAppointment(this.user, appt);
            }
            catch (SQLException sqle) {
                System.err.println(sqle.getSQLState() + sqle.getMessage());
                return;
            }
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            showMainWindow();
        }
    }

    /**
     * Cancels operation and closes window.  Prompts the user for confirmation.
     *
     * @param actionEvent
     */
    public void onCancelAction(ActionEvent actionEvent) {
        final boolean confirm = Dialogs.promptUser("Lose all changes?",
                "Are you sure you want to cancel and lose all changes?");
        if (confirm) {
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            showMainWindow();
        }
    }

    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Appointment Scheduler");

            MainScreen controller = loader.getController();
            controller.initUser(this.user);

            stage.show();
        }
        catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

    }

    /**
     * Called when a Start/End date is changed. If an entered start date
     * is after an end date, the end date is changed to the start date.
     * If an entered end date is before a start date, the start date is
     * changed to the end date.
     *
     * @param actionEvent
     */
    public void onDateAction(ActionEvent actionEvent) {
        if (actionEvent.getSource() == StartDate) {
            if (EndDate.getValue() == null
                    || (StartDate.getValue() != null && EndDate.getValue().isBefore(StartDate.getValue())))
                EndDate.setValue(StartDate.getValue());
            StartTime.requestFocus();
        } else if (actionEvent.getSource() == EndDate) {
            if (StartDate.getValue() == null
                    || (EndDate.getValue() != null && StartDate.getValue().isAfter(EndDate.getValue())))
                StartDate.setValue(EndDate.getValue());
            EndTime.requestFocus();
        }
    }

    /**
     * Called when a Start/End time is changed. Checks for and applies
     * proper formatting.  Alerts user if format cannot be implied.
     *
     * @param actionEvent
     */
    public void onTimeAction(ActionEvent actionEvent) {
        if (actionEvent.getSource() == StartTime) {
            try {
                StartTime.setText(Time.timeFormatting(StartTime.getText()));
                LocalTime.parse(StartTime.getText());
            }
            catch (DateTimeException dte) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Time Format Error", "Invalid Start Time",
                        "This appointment's start time must be in the 24-hour format of hh:mm (i.e. 18:30).");
                StartTime.requestFocus();
                StartTime.selectAll();
                return;
            }
        } else if (actionEvent.getSource() == EndTime) {
            try {
                EndTime.setText(Time.timeFormatting(EndTime.getText()));
                LocalTime.parse(EndTime.getText());
            }
            catch (DateTimeException dte) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Time Format Error", "Invalid End Time",
                        "This appointment's end time must be in the 24-hour format of hh:mm (i.e. 18:30).");
                EndTime.requestFocus();
                EndTime.selectAll();
                return;
            }
        }

        onEnterAction(actionEvent);
    }
}
