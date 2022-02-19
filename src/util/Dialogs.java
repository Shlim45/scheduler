package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Class used for showing Dialog boxes to the user.
 *
 * @author Jonathan Hawranko
 */
public abstract class Dialogs {
    /**
     * Prompts the User for a response.  An alert will show
     * to the user with the <code>header</code> and <code>message</code>, and
     * the user will either <b>confirm</b> or <b>cancel</b>.  Prompts are blocking,
     * meaning the program will pause until the User responds to the prompt.
     *
     * @param header Header displayed on Alert
     * @param message Message displayed on Alert
     * @return true if confirmed, false otherwise
     */
    public static boolean promptUser(String header, String message) {
        Alert prompt = new Alert(Alert.AlertType.CONFIRMATION);
        prompt.setTitle("Confirm");
        prompt.setHeaderText(header);
        prompt.setContentText(message);

        Optional<ButtonType> response = prompt.showAndWait();
        return response.get() == ButtonType.OK;
    }

    /**
     * Alerts the user.  An alert can be of any <code>AlertType</code>.  Alerts will
     * not be blocking, meaning the program will continue running while the Alert is
     * displayed.
     *
     * @param type <code>AlertType</code> of the Alert
     * @param title Title shown on Alert
     * @param header Header displayed on Alert
     * @param message Message displayed on Alert
     */
    public static void alertUser(Alert.AlertType type, String title, String header, String message) {
        Alert prompt = new Alert(type);
        prompt.setTitle(title);
        prompt.setHeaderText(header);
        prompt.setContentText(message);
        prompt.show();
    }
}
