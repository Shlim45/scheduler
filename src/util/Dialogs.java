package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class Dialogs {
    public static boolean promptUser(String header, String message) {
        Alert prompt = new Alert(Alert.AlertType.CONFIRMATION);
        prompt.setTitle("Confirm");
        prompt.setHeaderText(header);
        prompt.setContentText(message);

        Optional<ButtonType> response = prompt.showAndWait();
        return response.get() == ButtonType.OK;
    }
    public static void alertUser(Alert.AlertType type, String title, String header, String message) {
        Alert prompt = new Alert(type);
        prompt.setTitle(title);
        prompt.setHeaderText(header);
        prompt.setContentText(message);

        prompt.show();
    }
}