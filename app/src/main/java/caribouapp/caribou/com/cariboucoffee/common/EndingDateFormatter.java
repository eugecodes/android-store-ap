package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

public class EndingDateFormatter {

    private static final int DAYS_IN_A_WEEK = 7;
    private static final int DAYS_IN_A_YEAR = 365;

    private Clock mClock;

    private String mDateFormat;

    private String mHoursDateFormat;

    private boolean mShowHours;

    public EndingDateFormatter(Clock clock, String dateFormat, String hoursDateFormat, boolean showHours) {
        mClock = clock;
        mDateFormat = dateFormat;
        mHoursDateFormat = hoursDateFormat;
        mShowHours = showHours;
    }

    public String format(Context context, DateTime endingDate) {

        DateTime currentTime = mClock.getCurrentDateTime();
        LocalDate currentDate = currentTime.toLocalDate();

        int diffDays = Days.daysBetween(currentDate, endingDate.toLocalDate()).getDays();

        if (diffDays == 0 && !mShowHours) { // TODAY
            return StringUtils.format(mDateFormat, context.getString(R.string.today));
        } else if (diffDays == 0) {
            return StringUtils.format(mHoursDateFormat, Hours.hoursBetween(currentTime, endingDate).getHours());
        } else if (diffDays < DAYS_IN_A_WEEK) { // MON-TUE-WED-THU-FRI-SAT-SUN
            return StringUtils.format(mDateFormat, (DateTimeFormat.forPattern("EEE")).withLocale(Locale.US).print(endingDate));
        } else if (diffDays < DAYS_IN_A_YEAR) { // Complete date without year
            return StringUtils.format(mDateFormat, (DateTimeFormat.forPattern("MMM d")).withLocale(Locale.US).print(endingDate));
        } else { // Complete date with year
            return StringUtils.format(mDateFormat, (DateTimeFormat.forPattern("MMM d yyyy")).withLocale(Locale.US).print(endingDate));
        }
    }
}
