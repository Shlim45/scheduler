package util;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class TimeConversion {
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
}
