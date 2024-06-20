package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asegurola on 6/1/18.
 */

public class TimeOfDayData implements Serializable {
    @SerializedName("starts")
    private LocalTime mStarts;

    @SerializedName("ends")
    private LocalTime mEnds;

    @SerializedName("timeOfDay")
    private TimeOfDay mTimeOfDay;

    @SerializedName("messages")
    private List<String> mMessages = new ArrayList<>();

    public LocalTime getStarts() {
        return mStarts;
    }

    public void setStarts(LocalTime starts) {
        mStarts = starts;
    }

    public LocalTime getEnds() {
        return mEnds;
    }

    public void setEnds(LocalTime ends) {
        mEnds = ends;
    }

    public TimeOfDay getTimeOfDay() {
        return mTimeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        mTimeOfDay = timeOfDay;
    }

    public List<String> getMessages() {
        return mMessages;
    }

    public void setMessages(List<String> messages) {
        mMessages = messages;
    }
}
