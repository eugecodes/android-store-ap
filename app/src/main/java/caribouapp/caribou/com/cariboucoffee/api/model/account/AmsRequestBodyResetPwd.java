package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsRequestBodyResetPwd {

    private static final String RESET_URL = BuildConfig.REWARDS_URL + "passwordreset?token=";

    @SerializedName("emailAddress")
    private String mEmail;

    @SerializedName("resetUrl")
    private String mResetUrl = RESET_URL;

    @SerializedName("brand")
    private String brand = BuildConfig.RESET_PW_BRAND;

    public AmsRequestBodyResetPwd(String email) {
        this.mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getResetUrl() {
        return mResetUrl;
    }

    public void setResetUrl(String resetUrl) {
        this.mResetUrl = resetUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

}
