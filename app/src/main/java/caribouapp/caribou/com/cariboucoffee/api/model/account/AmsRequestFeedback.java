package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 2/23/18.
 */

public class AmsRequestFeedback extends AmsRequest {

    //Required
    @SerializedName("emailAddress")
    private String mEmail;

    //Required
    @SerializedName("contactPhone")
    private String mMobile;

    @SerializedName("firstName")
    private String mFirstName;

    @SerializedName("lastName")
    private String mLastName;

    @SerializedName("feedbackRating")
    private String mFeedbackRating;

    //Required
    @SerializedName("feedbackText")
    private String mFeedbackText;

    @SerializedName("appInfo")
    private AmsAppInfo mAmsAppInfo;

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String mobile) {
        mMobile = mobile;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getFeedbackRating() {
        return mFeedbackRating;
    }

    public void setFeedbackRating(String feedbackRating) {
        mFeedbackRating = feedbackRating;
    }

    public String getFeedbackText() {
        return mFeedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        mFeedbackText = feedbackText;
    }

    public AmsAppInfo getAmsAppInfo() {
        return mAmsAppInfo;
    }

    public void setAmsAppInfo(AmsAppInfo amsAppInfo) {
        mAmsAppInfo = amsAppInfo;
    }
}
