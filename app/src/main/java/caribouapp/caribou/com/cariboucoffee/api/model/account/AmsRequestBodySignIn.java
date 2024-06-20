package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsRequestBodySignIn {

    @SerializedName("credentials")
    private AmsCredentials mCredentials;

    public AmsRequestBodySignIn(String email, String password) {
        mCredentials = new AmsCredentials(email, password);
    }

    public AmsCredentials getCredentials() {
        return mCredentials;
    }

    public void setCredentials(AmsCredentials credentials) {
        mCredentials = credentials;
    }

}
