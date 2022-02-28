package exceptions;

/**
 * Class representing a Login Exception.  A Login Exception is
 * thrown whenever a user provides an invalid username/password
 * combination.
 *
 * @author Jonathan Hawranko
 */
public class LoginException extends Exception {
    /**
     * Constuctor for a LoginException.
     *
     * @param message message detailing what caused the exception
     */
    public LoginException(String message) {
        super(message);
    }
}
