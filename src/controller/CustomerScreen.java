package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Customer;

import java.net.URL;
import java.time.LocalDateTime;
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

    public void onEnterAction(ActionEvent actionEvent) {
    }

    public void onSubmitAction(ActionEvent actionEvent) {
    }

    public void onCancelAction(ActionEvent actionEvent) {
        // TODO(jon): Prompt if any changes?
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }
}
