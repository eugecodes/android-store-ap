package caribouapp.caribou.com.cariboucoffee.common;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardBottomButtonModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardHeaderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardModel;

public class BaseRewardsModel extends BaseObservable implements Serializable {

    private static final String TAG = BaseRewardsModel.class.getSimpleName();

    private List<RewardModel> mRewardsOrderedList = new ArrayList<>();

    public void calculateRewardList(boolean addHeaders,
                                    List<RewardItemModel> availableRewards, List<RewardItemModel> redeemedRewards) {
        mRewardsOrderedList.clear();
        List<RewardItemModel> sortedAvailableRewards = new ArrayList<>(availableRewards);
        Collections.sort(sortedAvailableRewards,
                (RewardItemModel reward1, RewardItemModel reward2) ->
                        reward1.getPoints().compareTo(reward2.getPoints()));

        List<RewardItemModel> sortedRedeemedRewards = new ArrayList<>(redeemedRewards);
        Collections.sort(sortedRedeemedRewards, (reward1, reward2) ->
                reward1.getEndingDate().compareTo(reward2.getEndingDate()));

        if (sortedRedeemedRewards.size() > 0) {
            mRewardsOrderedList.addAll(sortedRedeemedRewards); // Redeemed rewards
            if (addHeaders) {
                mRewardsOrderedList.add(0, new RewardHeaderModel(R.string.ready_to_use)); // Redeemed header
            } else {
                mRewardsOrderedList.add(new RewardBottomButtonModel(R.string.no_using_reward_today));
            }
        }
        if (sortedAvailableRewards.size() > 0) {
            if (addHeaders) {
                mRewardsOrderedList.add(new RewardHeaderModel(R.string.redeem_it)); // Available header
            }
            mRewardsOrderedList.addAll(sortedAvailableRewards); // Available rewards
        }

        notifyPropertyChanged(BR.rewardsOrderedList);
    }

    @Bindable
    public List<RewardModel> getRewardsOrderedList() {
        return mRewardsOrderedList;
    }

}
