package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;


/**
 * Created by jmsmuy on 4/27/18.
 */

public class YextDate implements Serializable {

    @SerializedName("date")
    private LocalDate mDate;

    @SerializedName("openIntervals")
    private List<YextTimePeriod> mTimePeriod;

    @SerializedName("isRegularHours")
    private boolean mIsRegularHours;

    @SerializedName("isClosed")
    private boolean mIsClosed;

    public LocalDate getDate() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        mDate = date;
    }

    public List<YextTimePeriod> getTimePeriod() {
        return mTimePeriod;
    }

    public void setTimePeriod(List<YextTimePeriod> timePeriod) {
        mTimePeriod = timePeriod;
    }

    public boolean isRegularHours() {
        return mIsRegularHours;
    }

    public void setRegularHours(boolean regularHours) {
        mIsRegularHours = regularHours;
    }

    public boolean isClosed() {
        return mIsClosed;
    }

    public void setClosed(boolean closed) {
        mIsClosed = closed;
    }
}
