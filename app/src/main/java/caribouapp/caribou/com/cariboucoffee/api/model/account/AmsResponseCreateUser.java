package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;

/**
 * Created by jmsmuy on 1/12/18.
 */

public class AmsResponseCreateUser extends ResponseWithHeader {

    @SerializedName("token")
    private String mToken;

    @SerializedName("aud")
    private String mUid;

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }
}
