package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;

/**
 * Created by jmsmuy on 1/11/18.
 */

public class AmsResponsePreEnrollment extends ResponseWithHeader {

    @SerializedName("preEnrollmentResult")
    private AmsResponsePreEnrollmentResult mResult;

    public AmsResponsePreEnrollmentResult getResult() {
        return mResult;
    }

    public void setResult(AmsResponsePreEnrollmentResult result) {
        mResult = result;
    }
}
