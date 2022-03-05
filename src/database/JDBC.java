package database;

import javafx.scene.control.Alert;
import model.*;
import util.Dialogs;
import util.Time;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains methods for accessing the database.
 * The JDBC class contains methods for database operations such as
 * opening and closing a connection, querying, and specific results.
 *
 * @author Jonathan Hawranko
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface

    /**
     * Opens a connection to the database.
     */
    public static void openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
        }
        catch(Exception e)
        {
            String errMsg = String.format("DB connection error, driver: '%s' host: '%s' db-name: '%s' username: '%s' password: '%s'\n",
                    driver, location, databaseName, userName, password);
            System.err.println(errMsg + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Closes the open connection to the database.
     */
    public static void closeConnection() {
        try {
            connection.close();
        }
        catch(Exception e)
        {
            System.err.println("Close Database Connection Error " + e.getMessage());
        }
    }

    /**
     * Creates a <b>Statement</b> object, queries the database, and returns the <b>ResultSet</b>.
     *
     * @param query The SQL statement for the desired database query.
     * @return a <b>ResultSet</b> with the results of the query
     * @throws SQLException on SQL syntax error
     */
    public static ResultSet queryConnection(String query) throws SQLException {
        try {
            Statement S = connection.createStatement();
            ResultSet R = S.executeQuery(query);
            return R;
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }
    }

    /**
     * Loads all Customers from the database.
     * <br /><br />
     * A lambda function is used to filter the list of <i>Divisions</i>
     * by the currently loaded Customer's Division.  The filtered result
     * is stored in a <i>Collector</i>, and used to assign the <i>division</i>
     * field on the <i>Customer</i>.<br />
     *
     * @param divisions A list of all <b>Division</b>s
     * @return A List of <b>Customer</b> objects
     */
    public static List<Customer> loadCustomers(List<Division> divisions) {
        final List<Customer> customers = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM customers "
                +"LEFT JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID")) {
            while (R.next()) {
                Customer C = new Customer(R.getInt("Customer_ID"));
                C.setName(R.getString("Customer_Name"));
                C.setAddress(R.getString("Address"));
                C.setPostalCode(R.getString("Postal_Code"));
                C.setPhone(R.getString("Phone"));

                Timestamp created = R.getTimestamp("Create_Date");
                C.setCreateDate(Time.toLocalTime(created));
                C.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                C.setLastUpdate(Time.toLocalTime(updated));
                C.setLastUpdatedBy(R.getString("Last_Updated_By"));

                final String divName = R.getString("Division");
                Division D = divisions.stream()
                        .filter(div -> div.getDivision().equals(divName))
                        .collect(Collectors.toList()).get(0);
                C.setDivision(D);

                customers.add(C);
            }
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "SQL Error", "SQL Error", sqle.getMessage());
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }

        return customers;
    }

    /**
     * Adds a new Customer to the database.
     *
     * @param user The User creating the new Customer
     * @param customer The new Customer
     * @throws SQLException on SQL syntax error
     */
    public static void insertCustomer(User user, Customer customer) throws SQLException {
        final String newCustomer = "INSERT INTO customers "
                +"(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) "
                +"VALUES (?,?,?,?,NOW(),?,NOW(),?,?)";
        try (PreparedStatement insert = connection.prepareStatement(newCustomer)) {
            insert.setString(1, customer.getName());
            insert.setString(2, customer.getAddress());
            insert.setString(3, customer.getPostalCode());
            insert.setString(4, customer.getPhone());
            insert.setString(5, user.getUserName());
            insert.setString(6, user.getUserName());
            insert.setInt(7, customer.getDivision().getDivisionId());

            insert.executeUpdate();
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
        }
    }

    /**
     * Updates a customer in the database.
     *
     * @param user The User updating the Customer
     * @param customer The Customer to update
     * @throws SQLException on SQL syntax error
     */
    public static void updateCustomer(User user, Customer customer) throws SQLException {
        final String editCustomer = "UPDATE customers "
                +"SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = NOW(), Last_Updated_By = ?, Division_ID = ? "
                +"WHERE Customer_ID = ?";
        try (PreparedStatement update = connection.prepareStatement(editCustomer)) {
            update.setString(1, customer.getName());
            update.setString(2, customer.getAddress());
            update.setString(3, customer.getPostalCode());
            update.setString(4, customer.getPhone());
            update.setString(5, user.getUserName());
            update.setInt(6, customer.getDivision().getDivisionId());
            update.setInt(7, customer.getCustomerId());

            update.executeUpdate();
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
        }
    }

    /**
     * Deletes a customer and any associated appointments.
     *
     * @param customer The Customer to delete
     * @throws SQLException On SQL syntax error
     */
    public static void deleteCustomerAndAppointments(Customer customer) throws SQLException {
        final String deleteAppointments = "DELETE FROM appointments WHERE Customer_ID=?";
        try (PreparedStatement delete = connection.prepareStatement(deleteAppointments)) {
            delete.setInt(1, customer.getCustomerId());
            delete.executeUpdate();
        }

        final String deleteCustomer = "DELETE FROM customers WHERE Customer_ID=?";
        try (PreparedStatement delete = connection.prepareStatement(deleteCustomer)) {
            delete.setInt(1, customer.getCustomerId());
            delete.executeUpdate();
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
        }
    }

    /**
     * Loads all Countries from the database.
     *
     * @return A List of <b>Country</b> objects
     */
    public static List<Country> loadCountries() {
        List<Country> countries = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM countries")) {
            while (R.next()) {
                Country C = new Country(R.getInt("Country_ID"));
                C.setCountry(R.getString("Country"));

                Timestamp created = R.getTimestamp("Create_Date");
                C.setCreateDate(Time.toLocalTime(created));
                C.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                C.setLastUpdate(Time.toLocalTime(updated));
                C.setLastUpdatedBy(R.getString("Last_Updated_By"));

                countries.add(C);
            }
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "SQL Error", "SQL Error", sqle.getMessage());
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }

        return countries;
    }

    /**
     * Loads all Divisions from the database.
     *
     * @return A List of <b>Division</b> objects
     */
    public static List<Division> loadDivisions() {
        List<Division> divisions = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM first_level_divisions")) {
            while (R.next()) {
                Division D = new Division(R.getInt("Division_ID"));
                D.setDivision(R.getString("Division"));

                Timestamp created = R.getTimestamp("Create_Date");
                D.setCreateDate(Time.toLocalTime(created));
                D.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                D.setLastUpdate(Time.toLocalTime(updated));
                D.setLastUpdatedBy(R.getString("Last_Updated_By"));

                D.setCountryId(R.getInt("Country_ID"));

                divisions.add(D);
            }
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "SQL Error", "SQL Error", sqle.getMessage());
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }

        return divisions;
    }

    /**
     * Loads all Contacts from the database.
     *
     * @return A List of <b>Contact</b> objects
     */
    public static List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM contacts")) {
            while (R.next()) {
                Contact C = new Contact(R.getInt("Contact_ID"));
                C.setName(R.getString("Contact_Name"));
                C.setEmail(R.getString("Email"));

                contacts.add(C);
            }
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "SQL Error", "SQL Error", sqle.getMessage());
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }

        return contacts;
    }

    /**
     * Loads all Appointments from the database.
     *
     * @return A List of <b>Appointment</b> objects
     */
    public static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM appointments "
                +"LEFT JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID;")) {
            while (R.next()) {
                Appointment A = new Appointment(R.getInt("Appointment_ID"));
                A.setTitle(R.getString("Title"));
                A.setDesc(R.getString("Description"));
                A.setLocation(R.getString("Location"));
                A.setType(R.getString("Type"));

                Timestamp start = R.getTimestamp("Start");
                A.setStart(Time.toLocalTime(start));

                Timestamp end = R.getTimestamp("End");
                A.setEnd(Time.toLocalTime(end));

                Timestamp created = R.getTimestamp("Create_Date");
                A.setCreateDate(Time.toLocalTime(created));
                A.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                A.setLastUpdate(Time.toLocalTime(updated));
                A.setLastUpdatedBy(R.getString("Last_Updated_By"));

                A.setCustomerId(R.getInt("Customer_ID"));
                A.setUserId(R.getInt("User_ID"));
                A.setContactId(R.getInt("Contact_ID"));
                A.setContact(R.getString("Contact_Name"));

                appointments.add(A);
            }
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "SQL Error", "SQL Error", sqle.getMessage());
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }

        return appointments;
    }

    /**
     * Adds a new appointment to the database.
     *
     * @param user The user creating the new Appointment
     * @param appt The new Appointment
     * @throws SQLException On SQL syntax error
     */
    public static void insertAppointment(User user, Appointment appt) throws SQLException {
        final String newAppointment = "INSERT INTO appointments "
                +"(Title, Description, Location, Type, Start, End, "
                +"Create_Date, Created_By, Last_Update, Last_Updated_By, "
                +"Customer_ID, User_ID, Contact_ID) "
                +"VALUES (?,?,?,?,?,?,NOW(),?,NOW(),?,?,?,?)";
        try (PreparedStatement insert = connection.prepareStatement(newAppointment)) {
            insert.setString(1, appt.getTitle());
            insert.setString(2, appt.getDesc());
            insert.setString(3, appt.getLocation());
            insert.setString(4, appt.getType());
            insert.setTimestamp(5, Timestamp.valueOf(Time.toUTC(appt.getStart()).toLocalDateTime()));
            insert.setTimestamp(6, Timestamp.valueOf(Time.toUTC(appt.getEnd()).toLocalDateTime()));
            insert.setString(7, user.getUserName());
            insert.setString(8, user.getUserName());
            insert.setInt(9, appt.getCustomerId());
            insert.setInt(10, appt.getUserId());
            insert.setInt(11, appt.getContactId());

            insert.executeUpdate();
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
        }
    }

    /**
     * Updates an appointment in the database.
     *
     * @param user The User updating the Appointment
     * @param appt The Appointment to update
     * @throws SQLException On SQL syntax error
     */
    public static void updateAppointment(User user, Appointment appt) throws SQLException {
        final String editAppointment = "UPDATE appointments "
                +"SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? "
                +"WHERE Appointment_ID = ?";
        try (PreparedStatement update = connection.prepareStatement(editAppointment)) {
            update.setString(1, appt.getTitle());
            update.setString(2, appt.getDesc());
            update.setString(3, appt.getLocation());
            update.setString(4, appt.getType());
            update.setTimestamp(5, Timestamp.valueOf(Time.toUTC(appt.getStart()).toLocalDateTime()));
            update.setTimestamp(6, Timestamp.valueOf(Time.toUTC(appt.getEnd()).toLocalDateTime()));
            update.setString(7, user.getUserName());
            update.setInt(8, appt.getCustomerId());
            update.setInt(9, appt.getUserId());
            update.setInt(10, appt.getContactId());
            update.setInt(11, appt.getApptId());

            update.executeUpdate();
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
        }
    }

    /**
     * Deletes an appointment from the database.
     *
     * @param appt The Appointment to delete
     * @throws SQLException On SQL syntax error
     */
    public static void deleteAppointment(Appointment appt) throws SQLException {
        final String deleteAppointment = "DELETE FROM appointments WHERE Appointment_ID=?";
        try (PreparedStatement delete = connection.prepareStatement(deleteAppointment)) {
            delete.setInt(1, appt.getApptId());
            delete.executeUpdate();
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
        }
    }

    // reports

    /**
     * Generates a report with the Count, Type, and Month of all appointments.
     *
     * @return The report output
     * @throws SQLException On SQL syntax error
     */
    public static String generateApptReport() throws SQLException {
        final StringBuilder report = new StringBuilder();
        report.append("Total number of customer appointments by Type and Month:\n\n");

        try(ResultSet R = JDBC.queryConnection("SELECT COUNT(*) AS Count, Type, MONTHNAME(Start) AS Month FROM appointments GROUP BY Type, Month")) {
            while (R.next()) {
                final int count = R.getInt("Count");
                final String type = R.getString("Type");
                final String month = R.getString("Month");
                report.append(String.format("There %s %d %s appointment%s in %s.\n\n", count == 1 ? "is" : "are",count, type, count==1?"":"s", month));
            }
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }

        return report.toString();
    }

    /**
     * Generates a report with each Contact's appointments.
     *
     * @return The report output
     * @throws SQLException On SQL syntax error
     */
    public static String generateContactsReport() throws SQLException {
        final StringBuilder report = new StringBuilder();
        report.append("Schedules for each Contact:\n\n");

        final String outputFormat = "%-20s %-8s %-20s %-20s %-20s %-20s %-20s %s";
        report.append(String.format(outputFormat, "Contact", "Appt. ID", "Title", "Type", "Description", "Start (Local Time)", "End (Local Time)", "Customer ID\n"));
        report.append("**************************************************************************************************************************************************\n");
        try(ResultSet R = JDBC.queryConnection("SELECT contacts.Contact_ID, Contact_Name, Appointment_ID, Title, Type, Description, Start, End, Customer_ID FROM appointments "
                +"RIGHT JOIN contacts ON contacts.Contact_ID = appointments.Contact_ID "
                +"ORDER BY Contact_Name, Start")) {
            int prevContactId = -1;
            while (R.next()) {
                final int contactId = R.getInt("Contact_ID");
                boolean sameContact = prevContactId == contactId;
                if (!sameContact)
                    report.append('\n');
                report.append(String.format(outputFormat,
                        sameContact ? "" : R.getString("Contact_Name"),
                        R.getInt("Appointment_ID"),
                        R.getString("Title"),
                        R.getString("Type"),
                        R.getString("Description"),
                        Time.toLocalTime(R.getTimestamp("Start")).format(Time.dateFormatter),
                        Time.toLocalTime(R.getTimestamp("End")).format(Time.dateFormatter),
                        R.getInt("Customer_ID"))).append('\n');
                prevContactId = contactId;
            }
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            return null;
        }

        return report.toString();
    }

    /**
     * Generates a custom report.
     *
     * @return The report output
     */
    public static String generateCustomReport() {
        final StringBuilder report = new StringBuilder();
        // TODO(jon): Generate a report of each User's Customers, with some detail of appointments or location.
        report.append("Placeholder for a custom additional report.");
        return report.toString();
    }
}
