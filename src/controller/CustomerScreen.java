package controller;

import database.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CustomerScreen implements Initializable {
    public TextField CustomerID;
    public TextField Name;
    public TextField Address;
    public TextField Postal;
    public TextField Phone;
    public TextField CreateDate;
    public TextField CreatedBy;
    public TextField LastUpdate;
    public TextField LastUpdatedBy;
    public TextField DivisionID;
    public Label HeaderLabel;

    private Customer customer;
    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private void populateFields() {
        if (customer == null)
            return;

        HeaderLabel.setText("Modify Customer Information");

        CustomerID.setText(Integer.toString(customer.getCustomerId()));
        Name.setText(customer.getName());
        Address.setText(customer.getAddress());
        Postal.setText(customer.getPostalCode());
        Phone.setText(customer.getPhone());
        CreateDate.setText(customer.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        CreatedBy.setText(customer.getCreatedBy());
        LastUpdate.setText(customer.getLastUpdate().format(DateTimeFormatter.ofPattern("HH:mm")));
        LastUpdatedBy.setText(customer.getLastUpdatedBy());
        DivisionID.setText(Integer.toString(customer.getDivisionId()));
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        populateFields();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void onEnterAction(ActionEvent actionEvent) {
        final String name = Name.getText();
        final String address = Address.getText();
        final String postal = Postal.getText();
        final String phone = Phone.getText();
        final String divId = DivisionID.getText();

        if (name.length() == 0)
            Name.requestFocus();
        else if (address.length() == 0)
            Address.requestFocus();
        else if (postal.length() == 0)
            Postal.requestFocus();
        else if (phone.length() == 0)
            Phone.requestFocus();
        else if (divId.length() == 0)
            DivisionID.requestFocus();
        else
            onSubmitAction(actionEvent);
    }

    public void onSubmitAction(ActionEvent actionEvent) {
        final String name = Name.getText();
        final String address = Address.getText();
        final String postal = Postal.getText();
        final String phone = Phone.getText();
        final String divId = DivisionID.getText();

        if (name.length() == 0 || address.length() == 0 || postal.length() == 0
                || phone.length() == 0 || divId.length() == 0)
            return;

        if (this.customer == null) {
            // handle new customer

            this.customer = new Customer();
            this.customer.setName(name);
            this.customer.setAddress(address);
            this.customer.setPostalCode(postal);
            this.customer.setPhone(phone);
            try {
                this.customer.setDivisionId(Integer.parseInt(divId));
            }
            catch (NumberFormatException nfe) {
                System.err.println("Customer division ID is not a number.");
            }

            try {
                JDBC.insertCustomer(this.user, this.customer);
            }
            catch (SQLException sqle) {
                System.err.println(sqle.getSQLState() + sqle.getMessage());
                return;
            }
        }
        else {
            // handle update customer
            this.customer.setName(name);
            this.customer.setAddress(address);
            this.customer.setPostalCode(postal);
            this.customer.setPhone(phone);

            try {
                this.customer.setDivisionId(Integer.parseInt(divId));
            }
            catch (NumberFormatException nfe) {
                System.err.println("Customer division ID is not a number.");
            }

            try {
                JDBC.updateCustomer(this.user, this.customer);
            }
            catch (SQLException sqle) {
                System.err.println(sqle.getSQLState() + sqle.getMessage());
                return;
            }
        }

        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        showAppointmentsWindow();
    }

    public void onCancelAction(ActionEvent actionEvent) {
        // TODO(jon): Prompt if any changes?
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        showAppointmentsWindow();
    }

    private void showAppointmentsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AppointmentsScreen.fxml"));

            Stage stage = new Stage();
            stage.setScene(
                    new Scene(loader.load())
            );
            stage.setTitle("Appointment Scheduler");

            AppointmentsScreen controller = loader.getController();
            controller.initUser(this.user);

            stage.show();
        }
        catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

    }
}
