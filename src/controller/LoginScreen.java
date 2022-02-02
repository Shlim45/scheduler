package controller;

import database.JDBC;
import exceptions.LoginException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

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
            System.err.println(mre.getMessage());
        }

    }

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AppointmentsScreen.fxml"));

        Stage stage = new Stage();
        stage.setScene(
                new Scene(loader.load())
        );
        stage.setTitle("Appointment Scheduler");

        AppointmentsScreen controller = loader.getController();
        controller.initUser(user);

        stage.show();
    }

    public void onLoginAction(ActionEvent actionEvent) {
        Information.setText("");
        final String uName = Username.getText();
        final String pass = Password.getText();

        if (uName.length() <= 0 || pass.length() <= 0) {
            Information.setText(rb.getString("nouserpass"));
            Username.requestFocus();
            return;
        }

        User user = null;
        try(ResultSet R = JDBC.queryConnection("SELECT User_ID, User_Name, Password " // , Create_Date, Created_By, Last_Update, Last_Updated_By
                + "FROM client_schedule.users WHERE User_Name='" + uName + "' AND Password='" + pass + "';")) {
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
        catch (SQLException sql) {
            // TODO(jon): Handle error
            System.err.println(sql.getMessage());
        }
        catch (LoginException le) {
            Information.setText(le.getMessage());
        }
        finally {
            if (user != null) {
                try {
                    showMainWindow(user);
                    ((Node) actionEvent.getSource()).getScene().getWindow().hide();
                }
                catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
            }
        }
    }
}
