package database;

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
        try {
            Statement S = connection.createStatement();
            ResultSet R = S.executeQuery(query);
            return R;
        }
        catch (SQLException sql) {
//            throw sql;
            // TODO(jon): Let user know connection error.
            System.err.println(sql.getMessage());
        }
        return null;
    }


}
