package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsResult {

    @SerializedName("uid")
    private String mUid;

    @SerializedName("personalInfo")
    private AmsPersonalInfo mPersonalInfo;

    @SerializedName("preferences")
    private AmsPreferences mPreferences;

    @SerializedName("billingInfo")
    private AmsBillingInformation mAmsBillingInformation;

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public AmsPersonalInfo getPersonalInfo() {
        return mPersonalInfo;
    }

    public void setPersonalInfo(AmsPersonalInfo personalInfo) {
        mPersonalInfo = personalInfo;
    }

    public AmsPreferences getPreferences() {
        return mPreferences;
    }

    public void setPreferences(AmsPreferences preferences) {
        mPreferences = preferences;
    }

    public AmsBillingInformation getAmsBillingInformation() {
        return mAmsBillingInformation;
    }

    public void setAmsBillingInformation(AmsBillingInformation amsBillingInformation) {
        mAmsBillingInformation = amsBillingInformation;
    }
}
