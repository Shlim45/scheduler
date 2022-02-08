package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Division;

import java.util.List;

public abstract class Filtering {
    public static ObservableList<Division> filterDivisionsByCountry(List<Division> D, Country C) {
        ObservableList<Division> filteredDivisions = FXCollections.observableArrayList();

        D.forEach(div -> {
            if (div.getCountryId() == C.getCountryId())
                filteredDivisions.add(div);
        });

        return filteredDivisions;
    }
}
