package main;

import database.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentsScreen.fxml"));
        primaryStage.setTitle("Appointment Scheduler");
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
    }

    public static void main(String[] args) {
        JDBC.openConnection();

        launch(args);

        JDBC.closeConnection();
    }
}
