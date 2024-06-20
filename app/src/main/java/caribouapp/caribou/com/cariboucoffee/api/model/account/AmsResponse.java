package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsResponse extends ResponseWithHeader {

    @SerializedName("getResult")
    private AmsResult mResult;

    @SerializedName("existsResult")
    private String existsResult;

    public AmsResult getResult() {
        return mResult;
    }

    public void setResult(AmsResult result) {
        mResult = result;
    }

    public String getExistsResult() {
        return existsResult;
    }

    public void setExistsResult(String existsResult) {
        this.existsResult = existsResult;
    }

}
