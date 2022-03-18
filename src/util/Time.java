package util;

import exceptions.SchedulingException;
import model.Appointment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class for working with Time.
 *
 * @author Jonathan Hawranko
 */
public abstract class Time {
    /**
     * Returns the proper date formatter used for the project.
     */
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Formats a String object representing Time.  Will enforce the format
     * of HH:mm, in 24-hour format.
     *
     * @param time String representation of time
     * @return formatted time
     */
    public static String timeFormatting(String time) {
        if (time.indexOf(':') == -1)
            time = time + ":00";
        String hr = time.substring(0, time.indexOf(':'));
        String min = time.substring(time.indexOf(':')+1);
        try {
            if (Integer.parseInt(hr) < 10 && hr.charAt(0) != '0')
                hr = '0' + hr;
            if (Integer.parseInt(min) < 10 && min.charAt(0) != '0')
                min = '0' + min;
        }
        catch (NumberFormatException nfe) {
            return "";
        }

        return hr + ':' + min;
    }

    /**
     * Checks if a Time is within the business hours of 8AM - 10PM EST.
     *
     * @param toCheck time to check
     * @return true if within business hours, false otherwise
     */
    public static boolean isWithinBusinessHours(LocalDateTime toCheck) {
        ZonedDateTime localTime = ZonedDateTime.of(toCheck, ZoneId.systemDefault());
        ZonedDateTime businessTime = localTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDateTime hoursCheck = businessTime.toLocalDateTime();
        return (hoursCheck.getHour() >= 8 && hoursCheck.getHour() <= 22);
    }

    /**
     * Checks if 2 appointments have any overlap in their Start and End times.
     *
     * @param a appointment to check for
     * @param b appointment to check against
     * @return true if overlap, false otherwise
     */
    public static boolean timeOverlaps(Appointment a, Appointment b) {
        if (a.getStart().isBefore(b.getStart()) && a.getEnd().isAfter(b.getStart())) {
            // a runs into b
            return true;
        }
        else if (b.getStart().isBefore(a.getStart()) && b.getEnd().isAfter(a.getStart())) {
            // b runs into a
            return true;
        }
        else if (a.getStart().isEqual(b.getStart())) {
            // a and b occur at same time
            return true;
        }

        return false;
    }

    /**
     * Checks if an Appointment has any scheduling errors.  Scheduling Errors include
     * Start time not before End time, invalid dates that already passed, outside of 
     * business hours, and appointment overlaps for the same customer.  If a
     * scheduling conflict exists, a <b>SchedulingException</b> is thrown.
     *
     * <br><br>
     * A lambda function is used to iterate over <b>custAppts</b>, checking for
     * appointments with overlapping times with the appointment <b>toCheck</b>.  If
     * an overlap is found, an <i>AtomicBoolean</i> is set true.  The <i>.forEach</i> method takes a
     * <i>Consumer</i>, which is executed against each item in the list.<br>
     * 
     * @see #isWithinBusinessHours(LocalDateTime)
     * @see #timeOverlaps(Appointment, Appointment)
     * @param toCheck the appointment to check
     * @param custAppts a list of the customers appointments
     * @throws SchedulingException if there is a scheduling conflict
     */
    public static void checkForSchedulingErrors(Appointment toCheck, List<Appointment> custAppts)  throws SchedulingException {
        if (toCheck.getStart().isAfter(toCheck.getEnd()))
            throw new SchedulingException("This appointment's start time must be before its end time.","Start Time before End Time");

        else if (toCheck.getStart().isEqual(toCheck.getEnd()))
            throw new SchedulingException("This appointment's end time must be after its start time.","Start Time same as End Time");

        else if (toCheck.getStart().isBefore(LocalDateTime.now()))
            throw new SchedulingException("This appointment's start date has already passed.","Invalid Start Date");

        else if (!Time.isWithinBusinessHours(toCheck.getStart()))
            throw new SchedulingException("This appointment's start time is outside of business hours (8:00 a.m. - 10:00 p.m. EST).","Outside of Business Hours");

        else if (!Time.isWithinBusinessHours(toCheck.getEnd()))
            throw new SchedulingException("This appointment's end time is outside of business hours (8:00 a.m. - 10:00 p.m. EST).","Outside of Business Hours");

        // check all customer appointments for overlap
        AtomicBoolean overlap = new AtomicBoolean(false);
        custAppts.forEach(a -> {
            if (a.getApptId() != toCheck.getApptId() && Time.timeOverlaps(a, toCheck))
                overlap.set(true);
        });

        if (overlap.get())
            throw new SchedulingException("This appointment's time overlaps with an existing appointment.","Scheduling Conflict");
    }
}
