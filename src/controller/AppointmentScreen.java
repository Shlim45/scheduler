package controller;

import database.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Appointment;
import model.Contact;
import model.User;
import util.Dialogs;

import java.io.IOException;
import java.net.URL;
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
    private Appointment appointment;
    private ObservableList<Contact> contacts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contacts = FXCollections.observableArrayList(JDBC.loadContacts());
    }

    public void setUser(User user) { this.user = user; }

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

        if (contacts != null) {
            ContactCombo.setItems(this.contacts);

            if (appointment != null) {
                Contact C = contacts.filtered(contact -> contact.getName().equals(appointment.getContact())).get(0);
                ContactCombo.getSelectionModel().select(C);
            }
        }
    }

    public void onSubmitAction(ActionEvent actionEvent) {
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
}
