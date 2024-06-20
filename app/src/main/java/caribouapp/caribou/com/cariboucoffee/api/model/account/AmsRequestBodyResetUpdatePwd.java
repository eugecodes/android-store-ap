package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;


/**
 * Created by jmsmuy on 1/30/18.
 */

public class AmsRequestBodyResetUpdatePwd {

    @SerializedName("token")
    private String mToken;

    @SerializedName("password")
    private String mPassword;

    public AmsRequestBodyResetUpdatePwd(String token, String newPassword) {
        mToken = token;
        mPassword = newPassword;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
