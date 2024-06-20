package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 1/23/18.
 */

public class SVmsRequestClaimReward {

    @SerializedName("rewardId")
    private int mRewardId;

    @SerializedName("code")
    private String mCode;

    @SerializedName("points")
    private String mPoints;

    public SVmsRequestClaimReward(int rewardId, String code, String points) {
        mRewardId = rewardId;
        mCode = code;
        mPoints = points;
    }

    public int getRewardId() {
        return mRewardId;
    }

    public void setRewardId(int rewardId) {
        mRewardId = rewardId;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getPoints() {
        return mPoints;
    }

    public void setPoints(String points) {
        mPoints = points;
    }
}
