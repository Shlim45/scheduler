package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Country;
import model.Customer;
import model.Division;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Class used for filtering Lists into smaller Lists.
 *
 * @author Jonathan Hawranko
 */
public abstract class Filtering {
    /**
     * Filters a list of <code>Division</code>s by a specific <code>Country</code>.
     * <br><br>
     * A lambda function is used to iterate over and filter <b>dList</b> by
     * <b>country</b>'s <i>countryId</i>.<br>
     *
     * @param dList list of Divisions
     * @param country Country to filter by
     * @return filtered list of Divisions
     */
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

    /**
     * Filters a list of <code>Customer</code>s by a specific <code>Country</code>.
     * <br><br>
     * A lambda function is used to iterate over and filter <b>cList</b> by
     * <b>country</b>'s <i>countryId</i>.<br>
     *
     * @param cList list of Customers
     * @param country Country to filter by
     * @return filtered list of Customers
     */
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

    /**
     * Filters a list of <code>Customer</code>s by a specific <code>Division</code>.
     * <br><br>
     * A lambda function is used to iterate over and filter <b>dList</b> by
     * <b>division</b>'s <i>divisionId</i>.<br>
     *
     * @param cList list of Customers
     * @param division Division to filter by
     * @return filtered list of Customers
     */
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

    /**
     * Filters a list of <code>Appointment</code>s by appointments starting within the current week.
     * <br><br>
     * A lambda function is used to iterate over and filter <b>appts</b> by
     * appointments starting this week.<br>
     *
     * @param appts list of Appointments
     * @return Appointments starting in current week
     */
    public static ObservableList<Appointment> filterAppointmentsThisWeek(List<Appointment> appts) {
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

        if (appts == null)
            return filteredAppointments;

        ZonedDateTime lastWeek = ZonedDateTime.now().minusWeeks(1);
        appts.forEach(appt -> {
            if (appt.getStart().isAfter(lastWeek))
                filteredAppointments.add(appt);
        });

        return filteredAppointments;
    }

    /**
     * Filters a list of <code>Appointment</code>s by appointments starting within the current month.
     * <br><br>
     * A lambda function is used to iterate over and filter <b>appts</b> by
     * appointments starting this month.<br>
     *
     * @param appts list of Appointments
     * @return Appointments starting in current month
     */
    public static ObservableList<Appointment> filterAppointmentsThisMonth(List<Appointment> appts) {
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

        if (appts == null)
            return filteredAppointments;

        ZonedDateTime lastMonth = ZonedDateTime.now().minusMonths(1);
        appts.forEach(appt -> {
            if (appt.getStart().isAfter(lastMonth))
                filteredAppointments.add(appt);
        });

        return filteredAppointments;
    }
}
