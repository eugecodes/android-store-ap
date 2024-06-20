package caribouapp.caribou.com.cariboucoffee.api.model.oAuth;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andressegurola on 12/12/17.
 */

public class OauthUserPassword {

    @SerializedName("userName")
    private String mUser;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("oauthToken")
    private String mOauthToken;

    @SerializedName("oauthProvider")
    private String mOauthProvider;

    public OauthUserPassword() {
    }

    public OauthUserPassword(String email, String password, String token, String provider) {
        mUser = email;
        mPassword = password;
        mOauthToken = token;
        mOauthProvider = provider;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getOauthToken() {
        return mOauthToken;
    }

    public void setOauthToken(String oauthToken) {
        mOauthToken = oauthToken;
    }

    public String getOauthProvider() {
        return mOauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        mOauthProvider = oauthProvider;
    }
}
