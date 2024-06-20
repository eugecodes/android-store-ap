package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jmsmuy on 1/22/18.
 */

public class ResponseClaimedRewards {

    @SerializedName("statusCode")
    private int mStatusCode;

    @SerializedName("data")
    private List<ResponseClaimedRewardsData> mData;

    public List<ResponseClaimedRewardsData> getData() {
        return mData;
    }

    public void setData(List<ResponseClaimedRewardsData> data) {
        mData = data;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        mStatusCode = statusCode;
    }
}
