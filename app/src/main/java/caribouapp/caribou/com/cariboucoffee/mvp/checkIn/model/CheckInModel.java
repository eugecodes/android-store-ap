package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.common.BaseRewardsModel;

/**
 * Created by jmsmuy on 11/15/17.
 */

public class CheckInModel extends BaseRewardsModel implements Serializable {

    private static final String TAG = CheckInModel.class.getSimpleName();

    private String mCardNumber = null;
    private BigDecimal mBalance = null;
    private BigDecimal mPoints;
    private boolean showMessageError = false;

    @Bindable
    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
        notifyPropertyChanged(BR.cardNumber);
    }

    @Bindable
    public BigDecimal getBalance() {
        return mBalance;
    }

    public void setBalance(BigDecimal balance) {
        mBalance = balance;
        notifyPropertyChanged(BR.balance);
    }

    @Bindable
    public BigDecimal getPoints() {
        return mPoints;
    }

    public void setPoints(BigDecimal points) {
        mPoints = points;
        notifyPropertyChanged(BR.points);
    }

    public void updateRedeemable(BigDecimal userPoints) {
        for (RewardModel rewardModel : getRewardsOrderedList()) {
            if (rewardModel instanceof RewardItemModel
                    && ((RewardItemModel) rewardModel).getPoints() != null) { // this is needed, redeemed rewards don't have points
                ((RewardItemModel) rewardModel).calculateBuyable(userPoints);
            }
        }
    }

    @Bindable
    public boolean isShowMessageError() {
        return showMessageError;
    }

    public void setShowMessageError(boolean showMessageError) {
        this.showMessageError = showMessageError;
        notifyPropertyChanged(BR.showMessageError);
    }
}
