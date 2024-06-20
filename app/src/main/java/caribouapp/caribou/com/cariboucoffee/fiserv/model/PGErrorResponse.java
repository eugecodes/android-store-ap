package caribouapp.caribou.com.cariboucoffee.fiserv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Updated by Swapnil on 22/10/22
 */

public class PGErrorResponse {

    public static final int PG_500_ERROR = 500;
    public static final int PG_400_ERROR = 400;

    public static final String PRICE_OVER_LIMIT = "PRICE_OVER_LIMIT";
    public static final String PAYMENT_REJECTED = "PAYMENT_REJECTED";
    public static final String PAYMENT_DECLINED = "PAYMENT_DECLINED";

    @SerializedName("errorCode")
    private String mErrorCode;

    public String getmErrorCode() {
        return mErrorCode;
    }

    public void setmErrorCode(String mErrorCode) {
        this.mErrorCode = mErrorCode;
    }
}
