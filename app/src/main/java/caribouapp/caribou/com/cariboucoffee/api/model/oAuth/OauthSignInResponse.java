package caribouapp.caribou.com.cariboucoffee.api.model.oAuth;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andressegurola on 12/12/17.
 */

public class OauthSignInResponse {

    @SerializedName("token")
    private String mToken;
    @SerializedName("aud")
    private String mAud;

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getAud() {
        return mAud;
    }

    public void setAud(String aud) {
        mAud = aud;
    }
}
