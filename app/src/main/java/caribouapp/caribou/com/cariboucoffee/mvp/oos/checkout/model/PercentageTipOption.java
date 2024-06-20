package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import caribouapp.caribou.com.cariboucoffee.order.Order;

public class PercentageTipOption extends TippingOption implements Serializable {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private BigDecimal mPercentage;

    public PercentageTipOption(BigDecimal percentage) {
        mPercentage = percentage;
    }

    @Override
    public BigDecimal calculateTip(Order order) {
        return order.getSubtotal().multiply(mPercentage.divide(ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return mPercentage.toString() + "%";
    }

    public BigDecimal getPercentage() {
        return mPercentage;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof PercentageTipOption) {
            return mPercentage.equals(((PercentageTipOption) obj).mPercentage);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (mPercentage != null) {
            return mPercentage.hashCode();
        }
        return super.hashCode();
    }
}
