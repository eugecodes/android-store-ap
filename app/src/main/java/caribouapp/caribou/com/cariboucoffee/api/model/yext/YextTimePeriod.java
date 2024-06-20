package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalTime;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.util.DateUtil;

/**
 * Created by jmsmuy on 4/27/18.
 */

public class YextTimePeriod implements Serializable {

    public static final String YEXT_OPEN_HOURS_CLOSED_KEYWORD = "closed";

    @SerializedName("start")
    private String mStartTime;

    @SerializedName("end")
    private String mFinishTime;

    @SerializedName("closed")
    private boolean mClosed = false;

    public LocalTime getStartTime() {
        return new LocalTime(mStartTime);
    }

    public void setStartTime(LocalTime startTime) {
        mStartTime = startTime.toString();
    }

    public LocalTime getFinishTime() {
        return new LocalTime(mFinishTime);
    }

    public void setFinishTime(LocalTime finishTime) {
        mFinishTime = finishTime.toString();
    }

    @Override
    public String toString() {
        if (mClosed) {
            return YEXT_OPEN_HOURS_CLOSED_KEYWORD;
        }
        return DateUtil.formatHour(mStartTime) + ":" + DateUtil.formatHour(mFinishTime);
    }

    public void setClosed(boolean closed) {
        mClosed = closed;
    }

    public boolean isClosed() {
        return mClosed;
    }
}
