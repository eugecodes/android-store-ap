package caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter;

import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuRewardsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuRewardsFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.PreSelectedReward;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class MenuRewardsFragmentPresenter extends OOSFlowPresenter<MenuContract.Fragments.Rewards.View>
        implements MenuContract.Fragments.Rewards.Presenter {

    private static final String TAG = MenuRewardsFragment.class.getSimpleName();

    private MenuRewardsModel mModel;

    public MenuRewardsFragmentPresenter(MenuContract.Fragments.Rewards.View view, MenuRewardsModel model) {
        super(view);
        mModel = model;
    }

    @Override
    public void rewardClicked(RewardItemModel rewardItemModel) {
        if (rewardItemModel.isSelectionEnabled()
                && rewardItemModel.isOmsMobileEligible()
                && rewardItemModel.isRedeemed()) {
            getOrderService().setPreSelectedReward(new PreSelectedReward(rewardItemModel));
            if (getView() != null) {
                getView().showNewPreselectedRewardSet();
            }
        } else if (rewardItemModel.isSelectedReward()) {
            getOrderService().clearPreSelectedReward();
        }
        updateRewards();
    }

    @Override
    public void setModel(RewardsData data) {
        mModel.calculateRewardList(false, data.getAvailableRewards(), StreamSupport.stream(data.getRedeemedRewards())
                .filter(rewardItemModel -> !rewardItemModel.isAutoApply()).collect(Collectors.toList()));
    }

    private void updateRewards() {
        if (getView() == null || mModel.getOrder() == null) {
            return;
        }
        updateUsableStatus();
    }

    /**
     * Gets the current pre selected reward
     * If this is null, it means there is no reward selected, we update all rewards to be available for use
     * else it will be the id from the selected reward, and we update all rewards but the selected not to
     * be available, and the selected to be removable
     */
    private void updateUsableStatus() {
        PreSelectedReward orderReward = mModel.getOrder().getPreSelectedReward();
        Integer selectedRewardId = orderReward == null ? null : orderReward.getRewardId();

        for (RewardModel rewardModel : mModel.getRewardsOrderedList()) {
            if (rewardModel instanceof RewardItemModel) {
                RewardItemModel rewardItemModel = (RewardItemModel) rewardModel;
                rewardItemModel.setSelectionEnabled(selectedRewardId == null);
                rewardItemModel.setSelectedReward(rewardItemModel.getRewardId().equals(selectedRewardId));
            }
        }

        getView().showCards(mModel.getRewardsOrderedList());
    }

    @Override
    protected void setOrder(Order data) {
        mModel.setOrder(data);
        updateRewards();
    }
}
