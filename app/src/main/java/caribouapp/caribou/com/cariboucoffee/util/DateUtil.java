package caribouapp.caribou.com.cariboucoffee.util;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import caribouapp.caribou.com.cariboucoffee.common.Clock;

/**
 * Created by jmsmuy on 5/3/18.
 */

public final class DateUtil {

    private static final String HOUR_PATTERN = "HH:mm";
    private static final String HOUR_PATTERN_AMPM = "h:mm a";
    public static final String DATE_FORMAT_WITH_LOCAL_TIMEZONE = "YYYY-MM-dd'T'HH:mm:ssZZ";

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormat.forPattern(HOUR_PATTERN);
    private static final DateTimeFormatter AMPM_HOUR_FORMATTER = DateTimeFormat.forPattern(HOUR_PATTERN_AMPM);
    private static final DateTimeFormatter LOCAL_TIMEZONE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_WITH_LOCAL_TIMEZONE);


    private DateUtil() {
    }

    /**
     * Returns true if targetTime is between or is equal to timeLimit1 and timeLimit2
     *
     * @param targetTime
     * @param timeLimit1
     * @param timeLimit2
     * @return
     */
    public static boolean timeBetween(LocalTime targetTime, LocalTime timeLimit1, LocalTime timeLimit2) {
        LocalTime firstLimit;
        LocalTime secondLimit;
        if (timeLimit1.isBefore(timeLimit2)) {
            firstLimit = timeLimit1;
            secondLimit = timeLimit2;
        } else {
            firstLimit = timeLimit2;
            secondLimit = timeLimit1;
        }
        return !targetTime.isBefore(firstLimit) && !targetTime.isAfter(secondLimit);
    }

    /**
     * Returns the number of minutes from currentTime until targetTime
     * If currentTime is after targetTime returns -1
     *
     * @param clock
     * @param targetTime
     * @return
     */
    public static long minutesUntil(Clock clock, LocalTime targetTime) {
        LocalTime currentTime = clock.getCurrentTime();
        if (targetTime.isBefore(currentTime)) {
            return -1;
        }
        return Minutes.minutesBetween(currentTime, targetTime).getMinutes();
    }

    public static String formatHour(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return HOUR_FORMATTER.print(new LocalTime(dateTime));
    }

    public static String formatHour(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        return HOUR_FORMATTER.print(new LocalTime(dateTime));
    }

    public static String formatHourAMPM(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return AMPM_HOUR_FORMATTER.print(new LocalTime(dateTime));
    }

    public static String formatHourAMPM(LocalTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return AMPM_HOUR_FORMATTER.print(dateTime);
    }

    public static String formatLocalTimezone(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return LOCAL_TIMEZONE_FORMATTER.print(dateTime);
    }

    public static boolean isBeforeToday(Clock clock, LocalDate date) {
        return date.isBefore(clock.getCurrentDateTime().toLocalDate());
    }
}
