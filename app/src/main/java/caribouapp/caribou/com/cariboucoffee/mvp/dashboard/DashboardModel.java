package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.CurbsideStatusEnum;
import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by jmsmuy on 9/29/17.
 */
@Singleton
public class DashboardModel extends BaseObservable implements Serializable {

    private static final String TAG = "DashboardModel";

    private String mFirstName;

    private String mTitle;

    private Integer mPerksPoints;

    private boolean mLoadPoints;

    private boolean mContinueOrderMode;

    private Integer mCartItemCount;

    private TimeOfDayTimeRanges mRanges;

    private boolean mOrderNowLoading;

    private boolean mUserLoggedIn;

    private boolean mIsDailyTriviaActive;

    private String mCurbsidePickupTime;

    private List<String> mTriviaMessages;

    private String mCurbsideIamHereMessage;

    private String mCurbsideLocationPhone;

    private CurbsideStatusEnum mCurbsideIamHereState = CurbsideStatusEnum.NONE;

    @Inject
    public DashboardModel() {

    }

    @Bindable
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    @Bindable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public Integer getPerksPoints() {
        return mPerksPoints;
    }

    public void setPerksPoints(Integer perksPoints) {
        mPerksPoints = perksPoints;
        notifyPropertyChanged(BR.perksPoints);
    }

    @Bindable
    public boolean isLoadPoints() {
        return mLoadPoints;
    }

    public void setLoadPoints(boolean loadPoints) {
        mLoadPoints = loadPoints;
        notifyPropertyChanged(BR.loadPoints);
    }

    @Bindable
    public boolean isContinueOrderMode() {
        return mContinueOrderMode;
    }

    public void setContinueOrderMode(boolean continueOrderMode) {
        mContinueOrderMode = continueOrderMode;
        notifyPropertyChanged(BR.continueOrderMode);
    }

    @Bindable
    public Integer getCartItemCount() {
        return mCartItemCount;
    }

    public void setCartItemCount(Integer cartItemCount) {
        mCartItemCount = cartItemCount;
        notifyPropertyChanged(BR.cartItemCount);
    }

    public TimeOfDayTimeRanges getRanges() {
        return mRanges;
    }

    public void setRanges(TimeOfDayTimeRanges ranges) {
        mRanges = ranges;
    }

    public TimeOfDayData getLoggedOutMessage() {
        for (TimeOfDayData timeOfDayData : mRanges.getRanges()) {
            if (timeOfDayData.getTimeOfDay().equals(TimeOfDay.LoggedOut)) {
                return timeOfDayData;
            }
        }
        return null;
    }

    public TimeOfDayData calculateCurrentTimeOfDay(Clock clock) {
        LocalTime currentTime = clock.getCurrentTime();
        for (TimeOfDayData timeOfDayData : mRanges.getRanges()) {
            if (timeOfDayData.getStarts().compareTo(currentTime) <= 0
                    && (currentTime.compareTo(timeOfDayData.getEnds()) < 0)
                    || timeOfDayData.getEnds().compareTo(LocalTime.MIDNIGHT) == 0) {
                return timeOfDayData;
            }
        }

        return null;
    }

    @Bindable
    public boolean isOrderNowLoading() {
        return mOrderNowLoading;
    }

    public void setOrderNowLoading(boolean orderNowLoading) {
        mOrderNowLoading = orderNowLoading;
        notifyPropertyChanged(BR.orderNowLoading);
    }

    @Bindable
    public boolean isUserLoggedIn() {
        return mUserLoggedIn;
    }

    public void setUserLoggedIn(boolean isUserLoggedIn) {
        mUserLoggedIn = isUserLoggedIn;
        notifyPropertyChanged(BR.userLoggedIn);
    }

    public void reset() {
        mFirstName = null;
        mTitle = null;
        mPerksPoints = null;
        mLoadPoints = false;
        mContinueOrderMode = false;
        mCartItemCount = null;
        mOrderNowLoading = false;
        mUserLoggedIn = false;
        mIsDailyTriviaActive = false;
        mCurbsideIamHereState = CurbsideStatusEnum.NONE;
        notifyChange();
    }

    public boolean isDailyTriviaActive() {
        return mIsDailyTriviaActive;
    }

    public void setDailyTriviaActive(boolean dailyTriviaActive) {
        mIsDailyTriviaActive = dailyTriviaActive;
    }

    public List<String> getTriviaMessages() {
        return mTriviaMessages;
    }

    public void setTriviaMessages(List<String> triviaMessages) {
        mTriviaMessages = triviaMessages;
    }

    @Bindable
    public String getCurbsidePickupTime() {
        return mCurbsidePickupTime;
    }

    public void setCurbsidePickupTime(String curbsidePickupTime) {
        mCurbsidePickupTime = curbsidePickupTime;
        notifyPropertyChanged(BR.curbsidePickupTime);
    }

    @Bindable
    public String getCurbsideIamHereMessage() {
        return mCurbsideIamHereMessage;
    }

    public void setCurbsideIamHereMessage(String curbsideIamHereMessage) {
        mCurbsideIamHereMessage = curbsideIamHereMessage;
        notifyPropertyChanged(BR.curbsideIamHereMessage);
    }

    @Bindable
    public void setCurbsideIamHereState(CurbsideStatusEnum curbsideIamHereState) {
        this.mCurbsideIamHereState = curbsideIamHereState;
        Log.d(TAG, "curbsideIamHere State: " + curbsideIamHereState);
        notifyPropertyChanged(BR.curbsideIamHereState);
    }

    @Bindable
    public CurbsideStatusEnum getCurbsideIamHereState() {
        return mCurbsideIamHereState;
    }

    @Bindable
    public String getCurbsideLocationPhone() {
        return mCurbsideLocationPhone;
    }

    public void setCurbsideLocationPhone(String curbsideLocationPhone) {
        mCurbsideLocationPhone = curbsideLocationPhone;
        notifyPropertyChanged(BR.curbsideLocationPhone);
    }
}
