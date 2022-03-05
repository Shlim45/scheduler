package util;

import javafx.scene.control.Alert;

import java.io.*;
import java.time.ZonedDateTime;

/**
 * Class used for Logging.  Logs all login activity to a file on local storage.
 *
 * @author Jonathan Hawranko
 */
public abstract class Logging {
    /**
     * Name of file to store log activity locally.
     */
    public static String filename = "login_activity.txt";

    /**
     * Writes to a file on local storage who attempted to log in,
     * and whether it was successful or not.
     *
     * @see #filename
     * @param username username of User
     * @param success true if successful login, false otherwise
     */
    public static void logUserLoginAttempt(String username, boolean success) {
        final String output = String.format("%s login attempt for username '%s' at %d (%s)\r\n",
                success ? "Successful" : "Failed",
                username,
                System.currentTimeMillis(),
                ZonedDateTime.now().format(Time.dateFormatter));

        // open file
        FileWriter fw;
        PrintWriter pw;
        File file = new File(filename);
        if (file.exists()) {
            try {
                fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
            }
            catch (IOException ioe) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "I/O Error", "Unable to open file '" + filename + "'", ioe.getMessage());
                return;
            }
        } else {
            try {
                file.createNewFile();
                fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
            }
            catch (IOException ioe) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "I/O Error", "Unable to create file '" + filename + "'", ioe.getMessage());
                return;
            }
        }
        pw.write(output);
        pw.close();
    }
}
