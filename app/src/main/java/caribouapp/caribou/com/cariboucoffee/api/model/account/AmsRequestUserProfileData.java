package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.api.model.StringWithNull;

/**
 * Created by jmsmuy on 11/23/17.
 */

public class AmsRequestUserProfileData extends AmsRequest {

    @SerializedName("uid")
    private String mUid;

    @SerializedName("get")
    private StringWithNull mGet = new StringWithNull(null); // TODO we should ask what this parameter is for!

    public AmsRequestUserProfileData(String uid) {
        mUid = uid;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public StringWithNull getGet() {
        return mGet;
    }

    public void setGet(StringWithNull get) {
        mGet = get;
    }
}
