package caribouapp.caribou.com.cariboucoffee.order;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;

public class PreSelectedReward implements Serializable {
    private int mRewardId;
    private String mRewardName;

    public PreSelectedReward() {
    }

    public PreSelectedReward(RewardItemModel rewardItemModel) {
        mRewardId = rewardItemModel.getRewardId();
        mRewardName = rewardItemModel.getName();
    }

    public int getRewardId() {
        return mRewardId;
    }

    public void setRewardId(int rewardId) {
        mRewardId = rewardId;
    }

    public String getRewardName() {
        return mRewardName;
    }

    public void setRewardName(String rewardName) {
        mRewardName = rewardName;
    }
}
