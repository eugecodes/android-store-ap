package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.math.BigDecimal;


/**
 * Created by jmsmuy on 11/28/17.
 */

public class AddFundsModel extends BaseFundsModel {

    private BigDecimal mNewBalance;
    private boolean mMinimumAmountToAddAlert;

    @Bindable
    public BigDecimal getNewBalance() {
        return mNewBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        mNewBalance = newBalance;
        notifyPropertyChanged(BR.newBalance);
    }

    @Bindable
    public boolean isMinimumAmountToAddAlert() {
        return mMinimumAmountToAddAlert;
    }

    public void setMinimumAmountToAddAlert(boolean minimumAmountToAddAlert) {
        mMinimumAmountToAddAlert = minimumAmountToAddAlert;
        notifyPropertyChanged(BR.minimumAmountToAddAlert);
    }
}
