package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class FirstScreen implements Initializable {
    public Label TheLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("First Screen initialized.");
    }

    public void onButtonAction(ActionEvent actionEvent) {
        TheLabel.setText("Thanks for clicking!");
    }
}
