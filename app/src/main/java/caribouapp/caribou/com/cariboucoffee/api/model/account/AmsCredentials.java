package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsCredentials {

    @SerializedName("userName")
    private String mUserName;

    @SerializedName("password")
    private String mPassword;

    public AmsCredentials(String email, String password) {
        mUserName = email;
        mPassword = password;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

}
