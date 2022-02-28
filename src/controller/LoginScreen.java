package controller;

import database.JDBC;
import exceptions.LoginException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import util.Dialogs;
import util.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

/**
 * The controller for the LoginScreen view.
 *
 * @author Jonathan Hawranko
 */
public class LoginScreen implements Initializable {
    public Label     Header;
    public Label     User;
    public Label     Pass;
    public TextField Username;
    public TextField Password;
    public Button    Login;
    public Label     Location;
    public Label     Information;
    private ResourceBundle rb;

    /**
     * Initializes the Login Screen.
     * Determines the users ZoneId and Locale, loads the appropriate
     * resource files, changes the language of all labels and
     * displays the users Time Zone.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
//            Locale.setDefault(Locale.FRENCH);
            rb = ResourceBundle.getBundle("prop/LoginScreen", Locale.getDefault());
            Header.setText(rb.getString("header"));
            User.setText(rb.getString("username"));
            Pass.setText(rb.getString("password"));
            Login.setText(rb.getString("button"));
            Login.setText(rb.getString("button"));
            Location.setText(rb.getString("location") + zoneId.getId().replaceAll("_"," "));
        }
        catch (MissingResourceException mre) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "Missing Resource", mre.getMessage());
        }

    }

    /**
     * Called when user presses ENTER key on the form.  Displays a warning
     * to the user if a username or password is not entered.
     *
     * Submits form if all fields filled.
     *
     * @see #onLoginAction(ActionEvent)
     * @param actionEvent
     */
    public void onEnterAction(ActionEvent actionEvent) {
        Information.setText("");
        final Object aSource = actionEvent.getSource();
        if (aSource == Username) {
            if (Username.getText().length() == 0)
                Information.setText(rb.getString("nouser"));
            else if (Password.getText().length() > 0)
                onLoginAction(actionEvent);
            else
                Password.requestFocus();
        }
        else if (aSource == Password) {
            if (Password.getText().length() == 0)
                Information.setText(rb.getString("nopass"));
            else
                onLoginAction(actionEvent);
        }
    }

    private void showMainWindow(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));

        Stage stage = new Stage();
        stage.setScene(
                new Scene(loader.load())
        );
        stage.setTitle("Appointment Scheduler");

        MainScreen controller = loader.getController();
        controller.initUser(user);

        stage.show();
        controller.checkForUpcomingAppts();
    }

    /**
     * Checks the database for the username/password combination.  If found,
     * creates a new User object, passes it to the Main Screen and closes
     * the Login Screen.
     *
     * @param actionEvent
     */
    public void onLoginAction(ActionEvent actionEvent) {
        Information.setText("");
        final String uName = Username.getText();
        final String pass = Password.getText();

        if (uName.length() <= 0 || pass.length() <= 0) {
            Information.setText(rb.getString("nouserpass"));
            Username.requestFocus();
            return;
        }

        Logger logger = new Logger();

        User user = null;
        try(ResultSet R = JDBC.queryConnection("SELECT User_ID, User_Name, Password " // , Create_Date, Created_By, Last_Update, Last_Updated_By
                + "FROM users WHERE User_Name='" + uName + "' AND Password='" + pass + "';")) {
            if (R.next()) {
                int    userId        = R.getInt("User_ID");
                String username      = R.getString("User_Name");
                String password      = R.getString("Password");

                user = new User(userId, username, password);
            }
            else {
                throw new LoginException(rb.getString("invalid"));
            }
        }
        catch (SQLException sqle) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "SQL Error", "SQL Error", sqle.getMessage());
        }
        catch (LoginException le) {
            Information.setText(le.getMessage());
            logger.logUserLoginAttempt(uName, false);
        }
        catch (NullPointerException npe) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Error", "No Database Connection", npe.getMessage());
            System.exit(1);
        }
        finally {
            if (user != null) {
                logger.logUserLoginAttempt(uName, true);
                try {
                    showMainWindow(user);
                    ((Node) actionEvent.getSource()).getScene().getWindow().hide();
                }
                catch (IOException ioe) {
                    Dialogs.alertUser(Alert.AlertType.ERROR, "Error loading view", "Failed to Load View", ioe.getMessage());
                    System.exit(2);
                }
            }
        }
    }
}
