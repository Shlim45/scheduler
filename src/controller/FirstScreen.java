package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FirstScreen implements Initializable {
    public Label  UserLabel;
    public Button LoginButton;
    private User  user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("First Screen initialized.");
    }

    public void initUser(User user) {
        this.user = user;
        LoginButton.setVisible(false);
        UserLabel.setVisible(true);
        UserLabel.setText("Logged in as " + user.getUserName());
    }

    public void onLoginAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Please log in");
            stage.setScene(new Scene(loader.load()));
            stage.show();

            ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
