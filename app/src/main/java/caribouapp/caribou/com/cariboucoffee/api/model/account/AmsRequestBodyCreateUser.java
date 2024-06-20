package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;

/**
 * Created by jmsmuy on 1/12/18.
 */

public class AmsRequestBodyCreateUser extends AmsRequestBodyUpdateUser {

    @SerializedName("credentials")
    private AmsCredentials mAmsCredentials;

    public AmsRequestBodyCreateUser(String firstName, String lastName, StateEnum state, String city,
                                    String zipCode, String email, String telephone, String password,
                                    AmsPreferences amsPreferences,
                                    LocalDate birthday) {
        super(firstName, lastName, state, city, zipCode, email, telephone, amsPreferences, birthday);
        mAmsCredentials = new AmsCredentials(email, password);
    }

    public AmsCredentials getAmsCredentials() {
        return mAmsCredentials;
    }

    public void setAmsCredentials(AmsCredentials amsCredentials) {
        mAmsCredentials = amsCredentials;
    }
}
