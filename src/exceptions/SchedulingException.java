package exceptions;

/**
 * Class representing a Scheduling Exception.  A Scheduling Exception is
 * thrown whenever a scheduling conflict occurs with an Appointment.
 *
 * @author Jonathan Hawranko
 */
public class SchedulingException extends Exception {
    private String reason;

    /**
     * Constuctor for a SchedulingException.
     *
     * @param message message detailing what caused the exception
     * @param reason the reason for the exception
     */
    public SchedulingException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }
}
