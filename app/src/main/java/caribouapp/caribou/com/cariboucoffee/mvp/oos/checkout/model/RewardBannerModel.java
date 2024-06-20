package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import java.io.Serializable;

public class RewardBannerModel implements Serializable {

    private int mRewardId;
    private String mDescription;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getRewardId() {
        return mRewardId;
    }

    public void setRewardId(int rewardId) {
        mRewardId = rewardId;
    }
}
