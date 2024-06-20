package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 1/23/18.
 */

public class ResponseAvailableRewardsData {

    @SerializedName("id")
    private Integer mRewardId;

    @SerializedName("code")
    private String mCode;

    @SerializedName("criteria")
    private ResponseAvailableRewardsCriteria mCriteria;

    public Integer getRewardId() {
        return mRewardId;
    }

    public void setRewardId(Integer rewardId) {
        mRewardId = rewardId;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public ResponseAvailableRewardsCriteria getCriteria() {
        return mCriteria;
    }

    public void setCriteria(ResponseAvailableRewardsCriteria criteria) {
        mCriteria = criteria;
    }
}
