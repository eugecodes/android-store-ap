package caribouapp.caribou.com.cariboucoffee.api.serializers;

import org.joda.time.LocalTime;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;

/**
 * Created by jmsmuy on 5/2/18.
 */

public final class PeriodUtil {

    private PeriodUtil() {
    }

    public static YextTimePeriod getTimePeriodFromString(String periodString) {
        YextTimePeriod period = new YextTimePeriod();
        if (periodString == null) {
            periodString = "";
        }
        String[] splittedValue = periodString.split(":");
        if (splittedValue[0].equals(YextTimePeriod.YEXT_OPEN_HOURS_CLOSED_KEYWORD)
                || splittedValue[0].equals("")) {
            period.setClosed(true);
        } else {
            period.setStartTime(new LocalTime(Integer.valueOf(splittedValue[0]), Integer.valueOf(splittedValue[1])));
            period.setFinishTime(new LocalTime(Integer.valueOf(splittedValue[2]), Integer.valueOf(splittedValue[3])));
        }

        return period;
    }
}
