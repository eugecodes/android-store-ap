package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by asegurola on 6/1/18.
 */

public class TimeOfDayTimeRanges implements Serializable {
    @SerializedName("ranges")
    private List<TimeOfDayData> mRanges;

    public List<TimeOfDayData> getRanges() {
        return mRanges;
    }

    public void setRanges(List<TimeOfDayData> ranges) {
        mRanges = ranges;
    }
}
