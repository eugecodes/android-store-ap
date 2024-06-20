package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import androidx.annotation.Nullable;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.order.Order;

/**
 * Created by fernando on 10/2/20.
 */
public class AmountTipOption extends TippingOption {

    private BigDecimal mPredefinedTipAmount;

    public AmountTipOption(BigDecimal customTipAmount) {
        mPredefinedTipAmount = customTipAmount;
    }

    public BigDecimal getPredefinedTipAmount() {
        return mPredefinedTipAmount;
    }

    @Override
    public BigDecimal calculateTip(Order order) {
        return mPredefinedTipAmount;
    }

    @Override
    public String getDescription() {
        return mPredefinedTipAmount.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AmountTipOption) {
            return mPredefinedTipAmount.equals(((AmountTipOption) obj).mPredefinedTipAmount);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (mPredefinedTipAmount != null) {
            return mPredefinedTipAmount.hashCode();
        }
        return super.hashCode();
    }
}
