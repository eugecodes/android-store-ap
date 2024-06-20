package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsRequestResetPwd extends AmsRequest {

    @SerializedName("pwReset")
    private AmsRequestBodyResetPwd mRequestResetPasswordBody;

    @SerializedName("pwResetUpdate")
    private AmsRequestBodyResetUpdatePwd mRequestBodyResetUpdatePwd;

    public AmsRequestResetPwd(String email) {
        mRequestResetPasswordBody = new AmsRequestBodyResetPwd(email);
    }

    public AmsRequestResetPwd(String token, String newPassword) {
        mRequestBodyResetUpdatePwd = new AmsRequestBodyResetUpdatePwd(token, newPassword);
    }

    public AmsRequestBodyResetUpdatePwd getAmsRequestBodyResetUpdatePwd() {
        return mRequestBodyResetUpdatePwd;
    }

    public void setAmsRequestBodyResetUpdatePwd(AmsRequestBodyResetUpdatePwd amsRequestBodyResetUpdatePwd) {
        mRequestBodyResetUpdatePwd = amsRequestBodyResetUpdatePwd;
    }

    public AmsRequestBodyResetPwd getRequestResetPasswordBody() {
        return mRequestResetPasswordBody;
    }

    public void setRequestResetPasswordBody(AmsRequestBodyResetPwd requestResetPasswordBody) {
        mRequestResetPasswordBody = requestResetPasswordBody;
    }

}
