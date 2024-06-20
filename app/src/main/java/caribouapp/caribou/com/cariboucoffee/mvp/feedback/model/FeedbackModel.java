package caribouapp.caribou.com.cariboucoffee.mvp.feedback.model;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

/**
 * Created by jmsmuy on 2/20/18.
 */

public class FeedbackModel extends BaseObservable implements Serializable {

    private static final Integer MAX_FEEDBACK_CHARS = 500;

    private Integer mStars = null;
    private String mFeedback = "";
    private boolean mConfirmedPressed = false;

    @Bindable
    public Integer getStars() {
        return mStars;
    }

    public void setStars(Integer stars) {
        mStars = stars;
        notifyPropertyChanged(BR.stars);
    }

    @Bindable
    public String getFeedback() {
        return mFeedback;
    }

    public void setFeedback(String feedback) {
        mFeedback = feedback;
        notifyPropertyChanged(BR.feedback);
        notifyPropertyChanged(BR.feedbackCharactersLeft);
        notifyPropertyChanged(BR.errorFeedbackText);
    }

    @Bindable
    public int getFeedbackCharactersLeft() {
        if (mFeedback == null) {
            return MAX_FEEDBACK_CHARS;
        }
        return MAX_FEEDBACK_CHARS - mFeedback.length();
    }

    @Bindable
    public boolean getErrorFeedbackText() {
        return TextUtils.isEmpty(mFeedback);
    }

    @Bindable
    public boolean isConfirmedPressed() {
        return mConfirmedPressed;
    }

    public void setConfirmedPressed(boolean confirmedPressed) {
        mConfirmedPressed = confirmedPressed;
        notifyPropertyChanged(BR.confirmedPressed);
    }

    @Bindable
    public static Integer getMaxFeedbackChars() {
        return MAX_FEEDBACK_CHARS;
    }
}
