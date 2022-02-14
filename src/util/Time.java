package util;

import javafx.scene.control.Alert;
import model.Appointment;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Time {
    public static ZonedDateTime toUTC(ZonedDateTime localTime) {
        return ZonedDateTime.ofInstant(localTime.toInstant(), ZoneId.of("UTC"));
    }

    public static ZonedDateTime toLocalTime(ZonedDateTime utcTime) {
        return ZonedDateTime.ofInstant(utcTime.toInstant(), ZoneId.systemDefault());
    }

    public static ZonedDateTime toLocalTime(Timestamp utcTime) {
        ZonedDateTime utcZTD = utcTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
        return ZonedDateTime.ofInstant(utcZTD.toInstant(), ZoneId.systemDefault());
    }

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


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
            // TODO(jon): alert user to error...
            return "";
        }

        return hr + ':' + min;
    }

    public static boolean isWithinBusinessHours(ZonedDateTime toCheck) {
        toCheck = ZonedDateTime.ofInstant(toCheck.toInstant(), ZoneId.of("America/New_York"));
        return (toCheck.getHour() >= 8 && toCheck.getHour() <= 22);
    }

    public static boolean timeOverlaps(Appointment a, Appointment b) {
        // ensure both are in local time
        ZonedDateTime aStart = toLocalTime(a.getStart());
        ZonedDateTime aEnd   = toLocalTime(a.getEnd());
        ZonedDateTime bStart = toLocalTime(b.getStart());
        ZonedDateTime bEnd   = toLocalTime(b.getEnd());


        if (aStart.isBefore(bStart) && aEnd.isAfter(bStart)) {
            // a runs into b
            return true;
        }
        else if (bStart.isBefore(aStart) && bEnd.isAfter(aStart)) {
            // b runs into a
            return true;
        }
        else if (aStart.isEqual(bStart)) {
            // a and b occur at same time
            return true;
        }

        return false;
    }

    public static boolean hasSchedulingErrors(Appointment toCheck, List<Appointment> custAppts) {
        if (toCheck.getStart().isAfter(toCheck.getEnd())) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Scheduling Error", "Start Time before End Time",
                    "This appointment's start time must be before its end time.");
            return true;
        }
        else if (toCheck.getStart().isEqual(toCheck.getEnd())) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Scheduling Error", "Start Time same as End Time",
                    "This appointment's end time must be after its start time.");
            return true;
        }
        else if (toCheck.getStart().isBefore(ZonedDateTime.now())) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Scheduling Error", "Invalid Start Date",
                    "This appointment's start date has already passed.");
            return true;
        }
        else if (!Time.isWithinBusinessHours(toCheck.getStart())) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Scheduling Error", "Outside of Business Hours",
                    "This appointment's start time is outside of business hours (8:00 a.m. - 10:00 p.m. EST).");
            return true;
        }
        else if (!Time.isWithinBusinessHours(toCheck.getEnd())) {
            Dialogs.alertUser(Alert.AlertType.ERROR, "Scheduling Error", "Outside of Business Hours",
                    "This appointment's end time is outside of business hours (8:00 a.m. - 10:00 p.m. EST).");
            return true;
        }

        // check all customer appointments for overlap
        AtomicBoolean overlap = new AtomicBoolean(false);
        custAppts.forEach(a -> {
            if (a.getApptId() != toCheck.getApptId() && Time.timeOverlaps(a, toCheck)) {
                Dialogs.alertUser(Alert.AlertType.ERROR, "Scheduling Error", "Scheduling Conflict",
                        "This appointment's time overlaps with an existing appointment.");
                overlap.set(true);
            }
        });

        return overlap.get();
    }
}
