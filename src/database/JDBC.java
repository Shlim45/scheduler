package database;

import model.Customer;
import model.User;

import java.sql.*;

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
        Statement S = connection.createStatement();
        ResultSet R = S.executeQuery(query);
        return R;
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
            insert.setInt(7, customer.getDivisionId());

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
            update.setInt(6, customer.getDivisionId());
            update.setInt(7, customer.getCustomerId());

            update.executeUpdate();
        }
    }
}
