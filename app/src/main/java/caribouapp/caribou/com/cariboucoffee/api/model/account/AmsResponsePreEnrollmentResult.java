package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.common.EnumPreEnrollmentResponse;

/**
 * Created by jmsmuy on 1/11/18.
 */

public class AmsResponsePreEnrollmentResult {

    @SerializedName("status")
    private EnumPreEnrollmentResponse mStatus;

    @SerializedName("hasDateOfBirth")
    private boolean mDateOfBirth;

    @SerializedName("maskedEmailForPhoneNumber")
    private String mMaskedEmailForPhoneNumber;

    public EnumPreEnrollmentResponse getStatus() {
        return mStatus;
    }

    public void setStatus(EnumPreEnrollmentResponse status) {
        mStatus = status;
    }

    public boolean isDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(boolean dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    public String getMaskedEmailForPhoneNumber() {
        return mMaskedEmailForPhoneNumber;
    }

    public void setMaskedEmailForPhoneNumber(String maskedEmailForPhoneNumber) {
        mMaskedEmailForPhoneNumber = maskedEmailForPhoneNumber;
    }
}
