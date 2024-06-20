package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTimeConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmsmuy on 5/2/18.
 */

public class YextOpenHours {

    private Map<Integer, YextTimePeriod> mOpenHours;

    @SerializedName("monday")
    private YextTimeIntervals mMondayTimePeriods;
    @SerializedName("tuesday")
    private YextTimeIntervals mTuesdayTimePeriods;
    @SerializedName("wednesday")
    private YextTimeIntervals mWednesdayTimePeriods;
    @SerializedName("thursday")
    private YextTimeIntervals mThursdayTimePeriods;
    @SerializedName("friday")
    private YextTimeIntervals mFridayTimePeriods;
    @SerializedName("saturday")
    private YextTimeIntervals mSaturdayTimePeriods;
    @SerializedName("sunday")
    private YextTimeIntervals mSundayTimePeriods;
    @SerializedName("holidayHours")
    private List<YextDate> mHolidayHours;

    public Map<Integer, YextTimePeriod> getOpenHours() {
        return mOpenHours;
    }

    public void setOpenHours(Map<Integer, YextTimePeriod> openHours) {
        mOpenHours = openHours;
    }

    public void populateOpenHours() {
        mOpenHours = new HashMap<>();
        mOpenHours.put(DateTimeConstants.MONDAY, fromTimeIntervalsToPeriods(mMondayTimePeriods));
        mOpenHours.put(DateTimeConstants.TUESDAY, fromTimeIntervalsToPeriods(mTuesdayTimePeriods));
        mOpenHours.put(DateTimeConstants.WEDNESDAY, fromTimeIntervalsToPeriods(mWednesdayTimePeriods));
        mOpenHours.put(DateTimeConstants.THURSDAY, fromTimeIntervalsToPeriods(mThursdayTimePeriods));
        mOpenHours.put(DateTimeConstants.FRIDAY, fromTimeIntervalsToPeriods(mFridayTimePeriods));
        mOpenHours.put(DateTimeConstants.SATURDAY, fromTimeIntervalsToPeriods(mSaturdayTimePeriods));
        mOpenHours.put(DateTimeConstants.SUNDAY, fromTimeIntervalsToPeriods(mSundayTimePeriods));

    }

    private YextTimePeriod fromTimeIntervalsToPeriods(YextTimeIntervals yextTimeIntervals) {
        YextTimePeriod timePeriod = new YextTimePeriod();
        if (yextTimeIntervals == null) {
            timePeriod.setClosed(true);
            return timePeriod;
        }
        timePeriod.setClosed(yextTimeIntervals.isClose());
        if (yextTimeIntervals.isClose()) {
            return timePeriod;
        }
        timePeriod.setStartTime(yextTimeIntervals.getYextTimePeriods().get(0).getStartTime());
        timePeriod.setFinishTime(yextTimeIntervals.getYextTimePeriods().get(0).getFinishTime());
        return timePeriod;
    }

    public List<YextDate> getHolidayHours() {
        return mHolidayHours;
    }

    public void setHolidayHours(List<YextDate> holidayHours) {
        mHolidayHours = holidayHours;
    }
}
