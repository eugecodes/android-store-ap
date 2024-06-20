package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gonzalo.gelos on 2/20/18.
 */

public class AmsRequestUpdatePassword extends AmsRequest {

    @SerializedName("pwUpdate")
    private AmsRequestBodyUpdatePassword mRequestBodyUpdatePassword;

    @SerializedName("uid")
    private String mUserUid;

    public AmsRequestUpdatePassword(String newPassword, String currentPassword, String userUid) {
        mRequestBodyUpdatePassword = new AmsRequestBodyUpdatePassword(currentPassword, newPassword);
        mUserUid = userUid;
    }

    public AmsRequestBodyUpdatePassword getRequestBodyUpdatePassword() {
        return mRequestBodyUpdatePassword;
    }

    public void setRequestBodyUpdatePassword(AmsRequestBodyUpdatePassword requestBodyUpdatePassword) {
        mRequestBodyUpdatePassword = requestBodyUpdatePassword;
    }

    public String getUserUid() {
        return mUserUid;
    }

    public void setUserUid(String userUid) {
        mUserUid = userUid;
    }
}
