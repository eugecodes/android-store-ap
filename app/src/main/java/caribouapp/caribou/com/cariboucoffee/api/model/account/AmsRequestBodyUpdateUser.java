package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;

/**
 * Created by gonzalo.gelos on 2/20/18.
 */

public class AmsRequestBodyUpdateUser {

    @SerializedName("personalInfo")
    private AmsPersonalInfo mAmsPersonalInfo;

    @SerializedName("preferences")
    private AmsPreferences mAmsPreferences;


    public AmsRequestBodyUpdateUser(String firstName, String lastName, StateEnum state, String city,
                                    String zipCode, String email, String telephone, AmsPreferences amsPreferences,
                                    LocalDate birthday) {
        mAmsPersonalInfo = new AmsPersonalInfo(firstName, lastName, state, city, zipCode, telephone, email, birthday);
        mAmsPreferences = amsPreferences;
    }

    public AmsPersonalInfo getAmsPersonalInfo() {
        return mAmsPersonalInfo;
    }

    public void setAmsPersonalInfo(AmsPersonalInfo amsPersonalInfo) {
        mAmsPersonalInfo = amsPersonalInfo;
    }

    public AmsPreferences getAmsPreferences() {
        return mAmsPreferences;
    }

    public void setAmsPreferences(AmsPreferences amsPreferences) {
        mAmsPreferences = amsPreferences;
    }
}
