package util;

import java.io.*;
import java.time.ZonedDateTime;

/**
 * Class used for Logging.  Logs all login activity to a file on local storage.
 *
 * @author Jonathan Hawranko
 */
public class Logger implements Logging {
    /**
     * Name of file to store log activity locally.
     */
    public static final String filename = "login_activity.txt";

    public Logger() { super(); }
    public Logger(String filename) {}

    /**
     * Writes to a file on local storage who attempted to log in,
     * and whether it was successful or not.
     *
     * @see #filename
     * @param username username of User
     * @param success true if successful login, false otherwise
     */
    @Override
    public void logUserLoginAttempt(String username, boolean success) {
        final String output = String.format("%s login attempt for user '%s' at %s\r\n",
                success ? "Successful" : "Failed",
                username,
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
                System.err.println("Unable to open file " + filename);
                return;
            }
        } else {
            try {
                file.createNewFile();
                fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
            }
            catch (IOException ioe) {
                System.err.println("Unable to create file " + filename);
                return;
            }
        }
        pw.write(output);
        pw.close();
    }
}