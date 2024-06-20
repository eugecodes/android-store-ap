package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalTime;

import java.io.Serializable;

/**
 * Created by jmsmuy on 10/10/17.
 */

public class LocationScheduleModel implements Serializable {

    public static final int WEEK_DAY_HOLIDAYS = 10;

    @SerializedName("weekDay")
    private int mWeekDay;

    @SerializedName("opens")
    private LocalTime mOpens;

    @SerializedName("closes")
    private LocalTime mCloses;

    public LocationScheduleModel(int weekDay) {
        mWeekDay = weekDay;
    }

    public int getWeekDay() {
        return mWeekDay;
    }

    public void setWeekDay(int weekDay) {
        mWeekDay = weekDay;
    }

    public LocalTime getOpens() {
        return mOpens;
    }

    public void setOpens(LocalTime opens) {
        mOpens = opens;
    }

    public LocalTime getCloses() {
        return mCloses;
    }

    public void setCloses(LocalTime closes) {
        mCloses = closes;
    }

    public boolean isClosed() {
        return mOpens == null && mCloses == null;
    }
}
