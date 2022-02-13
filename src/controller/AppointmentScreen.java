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
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

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
    private ObservableList<Appointment> allAppointments;
    private ObservableList<Contact>     contacts;

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

    public void onSubmitAction(ActionEvent actionEvent) {
        final boolean confirm = Dialogs.promptUser("Submit changes?",
                "Are you sure you want to submit the appointment?");
        if (confirm) {
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            showMainWindow();
        }
    }

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

    public void onDateAction(ActionEvent actionEvent) {
        // TODO(jon): Ensure end time is after start time, duration makes sense, etc.
        if (actionEvent.getSource() == StartDate)
            StartTime.requestFocus();
        else if (actionEvent.getSource() == EndDate)
            EndTime.requestFocus();
    }

    public void onTimeAction(ActionEvent actionEvent) {
        if (actionEvent.getSource() == StartTime)
            StartTime.setText(Time.timeFormatting(StartTime.getText()));
        else if (actionEvent.getSource() == EndTime)
            EndTime.setText(Time.timeFormatting(EndTime.getText()));

        onEnterAction(actionEvent);
    }
}
