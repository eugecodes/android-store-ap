package caribouapp.caribou.com.cariboucoffee.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsMobileEligibleReward;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;

public class RewardsData implements Serializable {

    private static final String TAG = RewardsData.class.getSimpleName();

    private List<RewardItemModel> mAvailableRewards = new ArrayList<>();
    private List<RewardItemModel> mRedeemedRewards = new ArrayList<>();
    private List<OmsMobileEligibleReward> mOmsMobileEligibleRewards = new ArrayList<>();
    private Map<Integer, CmsReward> mRewardsContent = new HashMap<>();


    public List<RewardItemModel> getRedeemedRewards() {
        return mRedeemedRewards;
    }

    public List<RewardItemModel> getAvailableRewards() {
        return mAvailableRewards;
    }

    public List<OmsMobileEligibleReward> getOmsMobileEligibleRewards() {
        return mOmsMobileEligibleRewards;
    }

    public Map<Integer, CmsReward> getRewardsContent() {
        return mRewardsContent;
    }

    public void setRewardsContent(Map<Integer, CmsReward> rewardsContent) {
        mRewardsContent = rewardsContent;
    }
}
