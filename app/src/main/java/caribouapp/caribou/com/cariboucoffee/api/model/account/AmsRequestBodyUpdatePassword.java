package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gonzalo.gelos on 2/20/18.
 */

public class AmsRequestBodyUpdatePassword {

    @SerializedName("currentPassword")
    private String mCurrentPassword;

    @SerializedName("newPassword")
    private String mNewPassword;

    public AmsRequestBodyUpdatePassword(String currentPassword, String newPassword) {
        mCurrentPassword = currentPassword;
        mNewPassword = newPassword;
    }

    public String getCurrentPassword() {
        return mCurrentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        mCurrentPassword = currentPassword;
    }

    public String getNewPassword() {
        return mNewPassword;
    }

    public void setNewPassword(String newPassword) {
        mNewPassword = newPassword;
    }
}
