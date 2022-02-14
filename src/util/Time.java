package util;

import model.Appointment;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

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
}
