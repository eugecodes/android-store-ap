package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;

/**
 * Created by gonzalo.gelos on 2/15/18.
 */

public class AmsRequestUpdateUser extends AmsRequest {

    @SerializedName("updatePersonalInfo")
    private AmsRequestBodyUpdateUser mRequestBodyUpdateUser;

    @SerializedName("uid")
    private String mUserUid;

    public AmsRequestUpdateUser(String firstName, String lastName, StateEnum state, String city,
                                String zipCode, String email, String telephone, AmsPreferences amsPreferences,
                                LocalDate birthday, String uid) {
        mRequestBodyUpdateUser = new AmsRequestBodyUpdateUser(firstName, lastName, state, city,
                zipCode, email, telephone, amsPreferences, birthday);
        mUserUid = uid;
    }

    public AmsRequestBodyUpdateUser getRequestBodyUpdateUser() {
        return mRequestBodyUpdateUser;
    }

    public void setRequestBodyUpdateUser(AmsRequestBodyUpdateUser requestBodyUpdateUser) {
        mRequestBodyUpdateUser = requestBodyUpdateUser;
    }

    public String getUserUids() {
        return mUserUid;
    }

    public void setUserUids(String userUids) {
        mUserUid = userUids;
    }
}
