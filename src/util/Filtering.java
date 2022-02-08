package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Customer;
import model.Division;

import java.util.List;

public abstract class Filtering {
    public static ObservableList<Division> filterDivisionsByCountry(List<Division> dList, Country country) {
        ObservableList<Division> filteredDivisions = FXCollections.observableArrayList();

        if (country == null)
            return filteredDivisions;

        dList.forEach(div -> {
            if (div.getCountryId() == country.getCountryId())
                filteredDivisions.add(div);
        });

        return filteredDivisions;
    }

    public static ObservableList<Customer> filterCustomersByCountryId(List<Customer> cList, Country country) {
        if (country == null)
            return FXCollections.observableArrayList(cList);

        ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();

        cList.forEach(c -> {
            if (c.getDivision().getCountryId() == country.getCountryId())
                filteredCustomers.add(c);
        });

        return filteredCustomers;
    }

    public static ObservableList<Customer> filterCustomersByDivision(List<Customer> cList, Division division) {
        ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();

        if (division == null)
            return filteredCustomers;

        cList.forEach(c -> {
            if (c.getDivision().getDivisionId() == division.getDivisionId())
                filteredCustomers.add(c);
        });

        return filteredCustomers;
    }
}
