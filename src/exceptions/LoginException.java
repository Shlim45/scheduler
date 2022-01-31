package exceptions;

/**
 * A Login Exception is thrown whenever a user provides an invalid
 * username/password combination.
 */
public class LoginException extends Exception {
    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }

    public LoginException() {
        super();
    }
}
