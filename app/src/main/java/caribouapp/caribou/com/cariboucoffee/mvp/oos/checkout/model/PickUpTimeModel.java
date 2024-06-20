package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalTime;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.util.DateUtil;

/**
 * Created by jmsmuy on 12/07/18.
 */

public class PickUpTimeModel implements Serializable {

    @SerializedName("asap")
    private boolean mAsap;

    @SerializedName("pickUpTime")
    private LocalTime mPickUpTime;

    public PickUpTimeModel(LocalTime pickUpTime, boolean asap) {
        mPickUpTime = pickUpTime;
        mAsap = asap;
    }

    public PickUpTimeModel(LocalTime currentPickUpTime) {
        mPickUpTime = currentPickUpTime;
    }

    public PickUpTimeModel(boolean isAsap) {
        mAsap = isAsap;
    }

    public boolean isAsap() {
        return mAsap;
    }

    public void setAsap(boolean asap) {
        mAsap = asap;
    }

    public LocalTime getPickUpTime() {
        return mPickUpTime;
    }

    public void setPickUpTime(LocalTime pickUpTime) {
        mPickUpTime = pickUpTime;
    }

    @Override
    public String toString() {
        return mPickUpTime == null ? null : DateUtil.formatHourAMPM(mPickUpTime);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PickUpTimeModel compareObject = (PickUpTimeModel) obj;
        if (compareObject.isAsap() && this.isAsap()) {
            return true;
        }
        return compareObject.getPickUpTime().compareTo(mPickUpTime) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
