package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsRequestSignIn extends AmsRequest {

    @SerializedName("get")
    private AmsRequestBodySignIn mRequestSignInBody;

    public AmsRequestSignIn(String email, String password) {
        mRequestSignInBody = new AmsRequestBodySignIn(email, password);
    }

    public AmsRequestBodySignIn getRequestSignInBody() {
        return mRequestSignInBody;
    }

    public void setRequestSignInBody(AmsRequestBodySignIn requestSignInBody) {
        mRequestSignInBody = requestSignInBody;
    }

}
