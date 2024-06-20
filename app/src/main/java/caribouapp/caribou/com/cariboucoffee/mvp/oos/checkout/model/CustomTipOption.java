package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import caribouapp.caribou.com.cariboucoffee.order.Order;

public class CustomTipOption extends TippingOption implements Serializable {

    public static final String CUSTOM_TIP_OPTION_DESCRIPTION = "Custom";
    private BigDecimal mCustomTipAmount;

    public CustomTipOption(BigDecimal customTipAmount) {
        mCustomTipAmount = customTipAmount;
    }

    @Override
    public BigDecimal calculateTip(Order order) {
        return mCustomTipAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return CUSTOM_TIP_OPTION_DESCRIPTION;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CustomTipOption) {
            return mCustomTipAmount.equals(((CustomTipOption) obj).mCustomTipAmount);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (mCustomTipAmount != null) {
            return mCustomTipAmount.hashCode();
        }
        return super.hashCode();
    }

}
