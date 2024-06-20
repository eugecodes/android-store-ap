package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;

/**
 * Created by jmsmuy on 1/12/18.
 */

public class AmsRequestCreateUser extends AmsRequest {

    @SerializedName("create")
    private AmsRequestBodyCreateUser mRequestBodyCreateUser;

    public AmsRequestCreateUser(String firstName, String lastName, StateEnum state, String city,
                                String zipCode, String email, String telephone, String password,
                                AmsPreferences amsPreferences, LocalDate birthday) {
        mRequestBodyCreateUser = new AmsRequestBodyCreateUser(firstName, lastName, state, city,
                zipCode, email, telephone, password, amsPreferences, birthday);
    }

    public AmsRequestBodyCreateUser getRequestBodyCreateUser() {
        return mRequestBodyCreateUser;
    }

    public void setRequestBodyCreateUser(AmsRequestBodyCreateUser requestBodyCreateUser) {
        mRequestBodyCreateUser = requestBodyCreateUser;
    }
}
