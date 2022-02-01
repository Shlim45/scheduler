package controller;

import database.JDBC;
import exceptions.LoginException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.ZoneId;
import java.util.*;

public class LoginScreen implements Initializable {
    public TextField Username;
    public TextField Password;
    public Label     Location;
    public Label     Information;
    private ZoneId   zoneId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        zoneId = ZoneId.systemDefault();
        Location.setText("Your location is " + zoneId.getId().replaceAll("_"," "));
    }

    public void onEnterAction(ActionEvent actionEvent) {
        Information.setText("");
        final Object aSource = actionEvent.getSource();
        if (aSource == Username) {
            if (Username.getText().length() == 0)
                Information.setText("Enter a username.");
            else if (Password.getText().length() > 0)
                onLoginAction(actionEvent);
            else
                Password.requestFocus();
        }
        else if (aSource == Password) {
            if (Password.getText().length() == 0)
                Information.setText("Enter a password.");
            else
                onLoginAction(actionEvent);
        }
    }

    public void onLoginAction(ActionEvent actionEvent) {
        Information.setText("");
        final String uName = Username.getText();
        final String pass = Password.getText();

        if (uName.length() <= 0 || pass.length() <= 0) {
            Information.setText("Enter a username and password.");
            Username.requestFocus();
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
            if (user != null) {
                Information.setText("Logged in as " + user.getUserName());
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FirstScreen.fxml"));

                    Stage stage = new Stage();
                    stage.setScene(
                            new Scene(loader.load())
                    );
                    stage.setTitle("Appointment Scheduler");

                    FirstScreen controller = loader.getController();
                    controller.initUser(user);

                    stage.show();
                }
                catch (IOException io) {
                    System.err.println(io.getMessage());
                }


                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }
        }
    }
}
