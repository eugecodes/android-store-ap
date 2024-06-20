package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 1/11/18.
 */

public class AmsRequestPreEnrollment extends AmsRequest {

    @SerializedName("emailAddress")
    private String mEmail;

    @SerializedName("phoneNumber")
    private String mPhone;

    @SerializedName("isEmailVerified")
    private boolean mEmailVerified;

    public AmsRequestPreEnrollment(String email, String phone, boolean emailVerified) {
        mEmail = email;
        mPhone = phone;
        mEmailVerified = emailVerified;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public boolean isEmailVerified() {
        return mEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        mEmailVerified = emailVerified;
    }
}
