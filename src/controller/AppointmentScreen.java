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

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class AppointmentScreen implements Initializable {
    public TextField ApptId;
    public TextField ApptTitle;
    public TextField ApptDesc;
    public TextField ApptLocation;
    public ComboBox ContactCombo;
    public TextField ApptType;
    public DatePicker StartDate;
    public TextField StartTime;
    public DatePicker EndDate;
    public TextField EndTime;
    public TextField ApptCustomerId;
    public TextField ApptUserId;

    private User user;
    private Customer customer;
    private Appointment appointment;
    private ObservableList<Contact> contacts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contacts = FXCollections.observableArrayList(JDBC.loadContacts());
    }

    public void setUser(User user) { this.user = user; }
    public void setCustomer(Customer customer) {
        this.customer = customer;
        populateFields();
    }

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

    public void onEnterAction(ActionEvent actionEvent) {
        final String title    = ApptTitle.getText();
        final String desc     = ApptDesc.getText();
        final String location = ApptLocation.getText();
        final String type     = ApptType.getText();
        final Contact contact = (Contact) ContactCombo.getSelectionModel().getSelectedItem();
        final ZonedDateTime start;
        final ZonedDateTime end;
        final int custId;
        final int userId;

        if (title.length() == 0)
            ApptTitle.requestFocus();
        else if (desc.length() == 0)
            ApptDesc.requestFocus();
        else if (location.length() == 0)
            ApptLocation.requestFocus();
        else if (contact == null)
            ContactCombo.requestFocus();
        else if (type.length() == 0)
            ApptType.requestFocus();
        else {
            final ZoneId local = ZoneId.systemDefault();

            try {
                start = ZonedDateTime.of(StartDate.getValue(), LocalTime.parse(StartTime.getText()), local);
            }
            catch (DateTimeParseException dtpe) {
                StartTime.requestFocus();
                return;
            }

            try {
                end = ZonedDateTime.of(EndDate.getValue(), LocalTime.parse(EndTime.getText()), local);
            }
            catch (DateTimeParseException dtpe) {
                EndTime.requestFocus();
                return;
            }

            // TODO(jon): customer and user ID validation?
            try {
                custId = Integer.parseInt(ApptCustomerId.getText());
            } catch(NumberFormatException nfe) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Invalid Customer ID", "Invalid Customer ID", "Customer ID must be an integer.");
                ApptCustomerId.requestFocus();
                return;
            }

            try {
                userId = Integer.parseInt(ApptUserId.getText());
            } catch(NumberFormatException nfe) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Invalid User ID", "Invalid User ID", "User ID must be an integer.");
                ApptUserId.requestFocus();
                return;
            }

            onSubmitAction(actionEvent);
        }
    }

    public void onSubmitAction(ActionEvent actionEvent) {
        final boolean confirm = Dialogs.promptUser("Submit changes?",
                "Are you sure you want to submit the appointment?");
        if (confirm) {
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            showAppointmentsWindow();
        }
    }

    public void onCancelAction(ActionEvent actionEvent) {
        final boolean confirm = Dialogs.promptUser("Lose all changes?",
                "Are you sure you want to cancel and lose all changes?");
        if (confirm) {
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            showAppointmentsWindow();
        }
    }

    private void showAppointmentsWindow() {
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

    public void onDateAction(ActionEvent actionEvent) {
        // TODO(jon): Ensure end time is after start time, duration makes sense, etc.
        if (actionEvent.getSource() == StartDate)
            StartTime.requestFocus();
        else if (actionEvent.getSource() == EndDate)
            EndTime.requestFocus();
    }

    private String timeFormatting(String time) {
        if (time.indexOf(':') == -1)
            time = time + ":00";
        String hr = time.substring(0, time.indexOf(':'));
        String min = time.substring(time.indexOf(':')+1);
        try {
            if (Integer.parseInt(hr) < 10 && hr.charAt(0) != '0')
                hr = '0' + hr;
            if (Integer.parseInt(min) < 10 && min.charAt(0) != '0')
                min = '0' + min;
        }
        catch (NumberFormatException nfe) {
            // TODO(jon): alert user to error...
            return "";
        }

       return hr + ':' + min;
    }

    public void onTimeAction(ActionEvent actionEvent) {
        if (actionEvent.getSource() == StartTime)
            StartTime.setText(timeFormatting(StartTime.getText()));
        else if (actionEvent.getSource() == EndTime)
            EndTime.setText(timeFormatting(EndTime.getText()));

        onEnterAction(actionEvent);
    }
}
