package caribouapp.caribou.com.cariboucoffee.api.model.oAuth;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andressegurola on 12/12/17.
 */

public class OauthCredentials {
    @SerializedName("credentials")
    private OauthUserPassword mOauthUserPassword;

    public OauthUserPassword getOauthUserPassword() {
        return mOauthUserPassword;
    }

    public void setOauthUserPassword(OauthUserPassword oauthUserPassword) {
        mOauthUserPassword = oauthUserPassword;
    }
}
