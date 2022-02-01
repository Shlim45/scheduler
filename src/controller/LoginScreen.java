package controller;

import database.JDBC;
import exceptions.LoginException;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginScreen implements Initializable {
    public TextField Username;
    public TextField Password;
    public Label     Location;
    public Label     Information;
    private Locale   L;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        L = Locale.getDefault();
        String labelText = "Your language is set to " + L.getLanguage();
        Location.setText(labelText);
    }

    public void onLoginAction(ActionEvent actionEvent) {
        Information.setText("");
        final String uName = Username.getText();
        final String pass = Password.getText();

        if (uName.length() <= 0 || pass.length() <= 0) {
            Information.setText("Enter a username and password.");
            return;
        }

        User user = null;
        try(ResultSet R = JDBC.queryConnection("SELECT User_ID, User_Name, Password, Create_Date, Created_By, Last_Update, Last_Updated_By "
                + "FROM client_schedule.users WHERE User_Name='" + uName + "' AND Password='" + pass + "';")) {
            if (R.next()) {
                int    userId        = R.getInt("User_ID");
                String username      = R.getString("User_Name");
                String password      = R.getString("Password");
                Date   createDate    = R.getDate("Create_Date");
                String createdBy     = R.getString("Created_By");
                Time   lastUpdate    = R.getTime("Last_Update");
                String lastUpdatedBy = R.getString("Last_Updated_By");

                user = new User(userId, username, password, createDate, createdBy, lastUpdate, lastUpdatedBy);
            }
            else {
                throw new LoginException("Invalid username/password.");
            }
        }
        catch (SQLException sql) {
            // TODO(jon): Handle error
            System.err.println(sql.getMessage());
        }
        catch (LoginException le) {
            Information.setText(le.getMessage());
        }
        finally {
            Information.setText("Logged in as " + user.getUserName());
        }
    }
}
