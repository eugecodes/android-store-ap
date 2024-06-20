package caribouapp.caribou.com.cariboucoffee.api.model.oAuth;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;

/**
 * Created by andressegurola on 12/12/17.
 */

public class OauthSignInRequest {
    // TODO: Remove mSourceApp param when service stops using it
    @SerializedName("sourceApp")
    private String mSourceApp = BuildConfig.SOURCE_APP;

    @SerializedName("authenticate")
    private OauthCredentials mOauthCredentials;

    public OauthSignInRequest() {
    }

    public OauthSignInRequest(String email, String password, String token, String provider) {
        mOauthCredentials = new OauthCredentials();
        mOauthCredentials.setOauthUserPassword(new OauthUserPassword(email, password, token, provider));
    }
}
