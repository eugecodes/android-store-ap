package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class YextTimeIntervals {

    @SerializedName("openIntervals")
    private ArrayList<YextTimePeriod> mYextTimePeriods;

    @SerializedName("isClosed")
    private boolean mIsClose;

    public ArrayList<YextTimePeriod> getYextTimePeriods() {
        return mYextTimePeriods;
    }

    public void setYextTimePeriods(ArrayList<YextTimePeriod> yextTimePeriods) {
        mYextTimePeriods = yextTimePeriods;
    }

    public boolean isClose() {
        return mIsClose;
    }

    public void setClose(boolean close) {
        mIsClose = close;
    }
}
