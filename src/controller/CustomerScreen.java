package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Customer;

import java.net.URL;
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

    public void setCustomer(Customer customer) {
        this.customer = customer;
        // TODO(jon): Populate fields
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
