package util;

import java.io.*;
import java.time.ZonedDateTime;

public abstract class Tracking {
    public static final String filename = "login_activity.txt";

    public static void logUserLoginAttempt(String username, boolean success) {
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
