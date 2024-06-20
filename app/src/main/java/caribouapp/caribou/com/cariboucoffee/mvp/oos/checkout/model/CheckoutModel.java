package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.order.Order;

public class CheckoutModel extends BaseObservable implements Serializable {

    private RewardBannerModel mRewardBannerModel;

    private Order mOrder;
    private BigDecimal mRewardsCardBalance;
    private boolean mBounceRequired = false;

    @Bindable
    public Order getOrder() {
        return mOrder;
    }

    @Bindable
    public RewardBannerModel getRewardBannerModel() {
        return mRewardBannerModel;
    }

    public void setRewardBannerModel(RewardBannerModel rewardBannerModel) {
        mRewardBannerModel = rewardBannerModel;
        notifyPropertyChanged(BR.rewardBannerModel);
        notifyPropertyChanged(BR.shouldShowBanner);
    }

    public void setOrder(Order order) {
        mOrder = order;
        notifyPropertyChanged(BR.order);
        notifyPropertyChanged(BR.shouldShowBanner);
    }

    @Bindable
    public BigDecimal getRewardsCardBalance() {
        return mRewardsCardBalance;
    }

    public void setRewardsCardBalance(BigDecimal rewardsCardBalance) {
        mRewardsCardBalance = rewardsCardBalance;
        notifyPropertyChanged(BR.rewardsCardBalance);
    }

    public boolean hasEnoughFounds() {
        return getRewardsCardBalance().compareTo(getOrder().getTotalWithTip()) >= 0;
    }

    @Bindable
    public boolean isShouldShowBanner() {
        if (mRewardBannerModel == null) {
            return false;
        }

        // Return true if all the rewards applied are auto applied rewards
        return mOrder.isAllAutoAppliedDiscountLines();
    }

    public boolean isBounceRequired() {
        return mBounceRequired;
    }

    public void setBounceRequired(boolean bounceRequired) {
        mBounceRequired = bounceRequired;
    }
}
