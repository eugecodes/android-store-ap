package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CmsHomeMessage {

    @SerializedName("lateNight")
    private List<String> mLateNight;

    @SerializedName("night")
    private List<String> mNight;

    @SerializedName("evening")
    private List<String> mEvening;

    @SerializedName("day")
    private List<String> mDay;

    @SerializedName("morning")
    private List<String> mMorning;

    @SerializedName("loggedOut")
    private List<String> mLoggedOut;

    @SerializedName("trivia")
    private List<String> mTriviaMessages;

    public List<String> getLateNight() {
        return mLateNight;
    }

    public void setLateNight(List<String> lateNight) {
        mLateNight = lateNight;
    }

    public List<String> getNight() {
        return mNight;
    }

    public void setNight(List<String> night) {
        mNight = night;
    }

    public List<String> getEvening() {
        return mEvening;
    }

    public void setEvening(List<String> evening) {
        mEvening = evening;
    }

    public List<String> getDay() {
        return mDay;
    }

    public void setDay(List<String> day) {
        mDay = day;
    }

    public List<String> getMorning() {
        return mMorning;
    }

    public void setMorning(List<String> morning) {
        mMorning = morning;
    }

    public List<String> getLoggedOut() {
        return mLoggedOut;
    }

    public void setLoggedOut(List<String> loggedOut) {
        mLoggedOut = loggedOut;
    }

    public List<String> getTriviaMessages() {
        return mTriviaMessages;
    }

    public void setTriviaMessages(List<String> triviaMessages) {
        mTriviaMessages = triviaMessages;
    }
}
