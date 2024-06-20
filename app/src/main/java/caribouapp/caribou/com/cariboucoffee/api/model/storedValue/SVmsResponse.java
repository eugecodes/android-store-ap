package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;

/**
 * Created by jmsmuy on 11/8/17.
 */

public class SVmsResponse extends ResponseWithHeader {

    @SerializedName("getBalanceResult")
    private SVmsBalanceResult mBalanceResult;

    public SVmsBalanceResult getBalanceResult() {
        return mBalanceResult;
    }

    public void setBalanceResult(SVmsBalanceResult balanceResult) {
        mBalanceResult = balanceResult;
    }
}
