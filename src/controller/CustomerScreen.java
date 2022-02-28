package controller;

import database.JDBC;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Country;
import model.Customer;
import model.Division;
import model.User;
import util.Dialogs;
import util.Filtering;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * The controller class for the CustomerScreen view.
 *
 * @author Jonathan Hawranko
 */
public class CustomerScreen implements Initializable {
    public TextField CustomerID;
    public TextField Name;
    public TextField Address;
    public TextField Postal;
    public TextField Phone;
    public Label     HeaderLabel;
    public ComboBox  CountryCombo;
    public ComboBox  DivisionCombo;

    private Customer                 customer;
    private User                     user;
    private ObservableList<Country>  countries;
    private ObservableList<Division> divisions;

    /**
     * Initializes the Customer Screen.  Applies an action listener to
     * the Countries combo box, filtering divisions when a Country is
     * selected.
     * <br /><br />
     * A lambda function is used to add a change listener to <i>CountryCombo</i>,
     * which handles filtering <i>DivisionCombo</i> choices by the selected Country.<br />
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CountryCombo.valueProperty().addListener(
                (ov, prevSelection, newSelection) -> DivisionCombo.setItems(Filtering.filterDivisionsByCountry(divisions, (Country) newSelection)));
    }

    /**
     * Sets the customer to modify.  Pre-populates all fields with Customer data.
     * @param customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        populateFields();
    }

    /**
     * Sets the active user.  This is the user who will be tied to the creation or
     * modification of the customer.
     *
     * @param user The logged-in user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets the available countries and top-level divisions.
     *
     * @param C The list of Countries
     * @param D The list of Top-Level Divisions
     */
    public void passCountriesAndDivisions(ObservableList<Country> C, ObservableList<Division> D) {
        this.countries = C;
        this.divisions = D;
    }

    /**
     * Populates all fields on the form with the Customer's data.
     * <br /><br />
     * A lambda function is used to filter <i>countries</i> List to the customer's
     * country, and pre-select that country in <i>CountryCombo</i>.<br />
     */
    private void populateFields() {
        if (customer != null) {
            HeaderLabel.setText("Modify Customer Information");

            CustomerID.setText(Integer.toString(customer.getCustomerId()));
            Name.setText(customer.getName());
            Address.setText(customer.getAddress());
            Postal.setText(customer.getPostalCode());
            Phone.setText(customer.getPhone());
        }

        if (countries != null) {
            CountryCombo.setItems(this.countries);
            CountryCombo.setConverter(new StringConverter<Country>() {

                @Override
                public String toString(Country country) {
                    if (country == null)
                        return "";
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

            if (customer != null) {
                Country C = countries.filtered(country -> country.getCountryId() == customer.getDivision().getCountryId()).get(0);
                CountryCombo.getSelectionModel().select(C);
            }
        }

        if (divisions != null) {
            Country C = (Country) CountryCombo.getSelectionModel().getSelectedItem();
            DivisionCombo.setItems(Filtering.filterDivisionsByCountry(this.divisions, C));
            DivisionCombo.setConverter(new StringConverter<Division>() {

                @Override
                public String toString(Division division) {
                    if (division == null)
                        return "";
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
            if (customer != null)
                DivisionCombo.getSelectionModel().select(customer.getDivision());
        }
    }

    /**
     * Called when user presses ENTER key on the form.  Checks for next empty
     * value and requests focus on that textbox.  
     * Submits form if all fields filled.
     *
     * @see #onSubmitAction(ActionEvent) 
     * @param actionEvent
     */
    public void onEnterAction(ActionEvent actionEvent) {
        final String name       = Name.getText();
        final String address    = Address.getText();
        final String postal     = Postal.getText();
        final String phone      = Phone.getText();
        final Country  country  = (Country) CountryCombo.getSelectionModel().getSelectedItem();
        final Division division = (Division) DivisionCombo.getSelectionModel().getSelectedItem();

        if (name.length() == 0)
            Name.requestFocus();
        else if (address.length() == 0)
            Address.requestFocus();
        else if (postal.length() == 0)
            Postal.requestFocus();
        else if (phone.length() == 0)
            Phone.requestFocus();
        else if (country == null)
            CountryCombo.requestFocus();
        else if (division == null)
            DivisionCombo.requestFocus();
        else
            onSubmitAction(actionEvent);
    }

    /**
     * Submits the customer to the database.  Prompts the user for confirmation.
     *
     * @param actionEvent
     */
    public void onSubmitAction(ActionEvent actionEvent) {
        final boolean newCustomer = this.customer == null;
        final String header = newCustomer ? "Create new customer?" : "Submit changes to customer?";
        final String message = newCustomer
                ? "Are you sure you want to create a new customer with this information?"
                : "Are you sure you want to modify this customer's information?";
        final boolean confirm = Dialogs.promptUser(header, message);
        if (!confirm)
            return;

        final String name = Name.getText();
        final String address = Address.getText();
        final String postal = Postal.getText();
        final String phone = Phone.getText();
        final Division division = (Division) DivisionCombo.getSelectionModel().getSelectedItem();

        if (name.length() == 0 || address.length() == 0 || postal.length() == 0
                || phone.length() == 0)
            return;

        if (newCustomer)
            this.customer = new Customer();

        this.customer.setName(name);
        this.customer.setAddress(address);
        this.customer.setPostalCode(postal);
        this.customer.setPhone(phone);
        this.customer.setDivision(division);
        try {
            if (newCustomer)
                JDBC.insertCustomer(this.user, this.customer);
            else
                JDBC.updateCustomer(this.user, this.customer);
        }
        catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
            return;
        }

        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        showMainWindow();
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
}
