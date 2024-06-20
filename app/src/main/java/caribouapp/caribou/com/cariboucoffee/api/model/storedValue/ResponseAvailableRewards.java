package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jmsmuy on 1/23/18.
 */

public class ResponseAvailableRewards {

    @SerializedName("statusCode")
    private String mStatusCode;

    @SerializedName("data")
    private List<ResponseAvailableRewardsData> mData;

    public String getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(String statusCode) {
        mStatusCode = statusCode;
    }

    public List<ResponseAvailableRewardsData> getData() {
        return mData;
    }

    public void setData(List<ResponseAvailableRewardsData> data) {
        mData = data;
    }
}
