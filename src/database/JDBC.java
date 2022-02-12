package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import util.TimeConversion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Database connection successful!");
        }
        catch(Exception e)
        {
            System.err.println("Error:" + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Database connection closed!");
        }
        catch(Exception e)
        {
            System.err.println("Error:" + e.getMessage());
        }
    }

    public static ResultSet queryConnection(String query) throws SQLException {
        try {
            Statement S = connection.createStatement();
            ResultSet R = S.executeQuery(query);
            return R;
        }
        catch (NullPointerException npe) {
            System.err.println(npe.getMessage());
            return null;
        }
    }

    public static List<Customer> loadCustomers(List<Division> divisions) {
        final List<Customer> customers = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.customers "
                +"LEFT JOIN client_schedule.first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID")) {
            while (R.next()) {
                Customer C = new Customer(R.getInt("Customer_ID"));
                C.setName(R.getString("Customer_Name"));
                C.setAddress(R.getString("Address"));
                C.setPostalCode(R.getString("Postal_Code"));
                C.setPhone(R.getString("Phone"));

                Timestamp created = R.getTimestamp("Create_Date");
                C.setCreateDate(TimeConversion.toLocalTime(created));
                C.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                C.setLastUpdate(TimeConversion.toLocalTime(updated));
                C.setLastUpdatedBy(R.getString("Last_Updated_By"));

                final String divName = R.getString("Division");
                Division D = divisions.stream()
                        .filter(div -> div.getDivision().equals(divName))
                        .collect(Collectors.toList()).get(0);
                C.setDivision(D);

                customers.add(C);
            }
        }
        catch (SQLException sql) {
            // TODO(jon): Handle error
            System.err.println(sql.getMessage());
        }

        return customers;
    }

    public static void insertCustomer(User user, Customer customer) throws SQLException {
        final String newCustomer = "INSERT INTO client_schedule.customers "
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
    }

    public static void updateCustomer(User user, Customer customer) throws SQLException {
        final String editCustomer = "UPDATE client_schedule.customers "
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
    }

    public static void deleteCustomer(Customer customer) throws SQLException {
        final String deleteAppointments = "DELETE FROM client_schedule.appointments WHERE Customer_ID=?";
        try (PreparedStatement delete = connection.prepareStatement(deleteAppointments)) {
            delete.setInt(1, customer.getCustomerId());
            delete.executeUpdate();
        }

        final String deleteCustomer = "DELETE FROM client_schedule.customers WHERE Customer_ID=?";
        try (PreparedStatement delete = connection.prepareStatement(deleteCustomer)) {
            delete.setInt(1, customer.getCustomerId());
            delete.executeUpdate();
        }
    }

    public static List<Country> loadCountries() {
        List<Country> countries = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.countries")) {
            while (R.next()) {
                Country C = new Country(R.getInt("Country_ID"));
                C.setCountry(R.getString("Country"));

                Timestamp created = R.getTimestamp("Create_Date");
                C.setCreateDate(TimeConversion.toLocalTime(created));
                C.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                C.setLastUpdate(TimeConversion.toLocalTime(updated));
                C.setLastUpdatedBy(R.getString("Last_Updated_By"));

                countries.add(C);
            }
        }
        catch (SQLException sql) {
            // TODO
            System.err.println(sql.getMessage());
        }

        return countries;
    }
    public static List<Division> loadDivisions() {
        List<Division> divisions = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.first_level_divisions")) {
            while (R.next()) {
                Division D = new Division(R.getInt("Division_ID"));
                D.setDivision(R.getString("Division"));

                Timestamp created = R.getTimestamp("Create_Date");
                D.setCreateDate(TimeConversion.toLocalTime(created));
                D.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                D.setLastUpdate(TimeConversion.toLocalTime(updated));
                D.setLastUpdatedBy(R.getString("Last_Updated_By"));

                D.setCountryId(R.getInt("Country_ID"));

                divisions.add(D);
            }
        }
        catch (SQLException sql) {
            // TODO
            System.err.println(sql.getMessage());
        }

        return divisions;
    }

    public static List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.contacts")) {
            while (R.next()) {
                Contact C = new Contact(R.getInt("Contact_ID"));
                C.setName(R.getString("Contact_Name"));
                C.setEmail(R.getString("Email"));

                contacts.add(C);
            }
        }
        catch (SQLException sql) {
            // TODO
            System.err.println(sql.getMessage());
        }

        return contacts;
    }

    public static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        try(ResultSet R = JDBC.queryConnection("SELECT * FROM client_schedule.appointments "
                +"LEFT JOIN client_schedule.contacts ON appointments.Contact_ID = contacts.Contact_ID;")) {
            while (R.next()) {
                Appointment A = new Appointment(R.getInt("Appointment_ID"));
                A.setTitle(R.getString("Title"));
                A.setDesc(R.getString("Description"));
                A.setLocation(R.getString("Location"));
                A.setType(R.getString("Type"));

                Timestamp start = R.getTimestamp("Start");
                A.setStart(TimeConversion.toLocalTime(start));

                Timestamp end = R.getTimestamp("End");
                A.setEnd(TimeConversion.toLocalTime(end));

                Timestamp created = R.getTimestamp("Create_Date");
                A.setCreateDate(TimeConversion.toLocalTime(created));
                A.setCreatedBy(R.getString("Created_By"));

                Timestamp updated = R.getTimestamp("Last_Update");
                A.setLastUpdate(TimeConversion.toLocalTime(updated));
                A.setLastUpdatedBy(R.getString("Last_Updated_By"));

                A.setCustomerId(R.getInt("Customer_ID"));
                A.setUserId(R.getInt("User_ID"));
                A.setContactId(R.getInt("Contact_ID"));
                A.setContact(R.getString("Contact_Name"));

                appointments.add(A);
            }
        }
        catch (SQLException sql) {
            // TODO(jon): Handle error
            System.err.println(sql.getMessage());
        }

        return appointments;
    }
}
