package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by gonzalogelos on 9/6/18.
 */

public class RewardsCardModel extends BaseObservable implements Serializable {

    private BigDecimal mBalance;
    private String mCardNumber;
    private Boolean mAutoReloadEnabled;
    private BigDecimal mIncrementAmount;
    private BigDecimal mThresholdAmount;

    @Bindable
    public BigDecimal getBalance() {
        return mBalance;
    }

    public void setBalance(BigDecimal balance) {
        mBalance = balance;
        notifyPropertyChanged(BR.balance);
    }


    public String getCardNumber() {
        return mCardNumber;
    }

    @Bindable
    public String getLastFourCardDigit() {
        if (getCardNumber() != null) {
            return getCardNumber().substring(getCardNumber().length() - 4);
        }
        return null;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
        notifyPropertyChanged(BR.lastFourCardDigit);
        notifyPropertyChanged(BR.cardNumber);
    }

    @Bindable
    public Boolean getAutoReloadEnabled() {
        return mAutoReloadEnabled;
    }

    public void setAutoReloadEnabled(Boolean autoReloadEnabled) {
        mAutoReloadEnabled = autoReloadEnabled;
        notifyPropertyChanged(BR.autoReloadEnabled);
    }

    public BigDecimal getIncrementAmount() {
        return mIncrementAmount;
    }

    public void setIncrementAmount(BigDecimal incrementAmount) {
        mIncrementAmount = incrementAmount;
    }

    public BigDecimal getThresholdAmount() {
        return mThresholdAmount;
    }

    public void setThresholdAmount(BigDecimal thresholdAmount) {
        mThresholdAmount = thresholdAmount;
    }

}
