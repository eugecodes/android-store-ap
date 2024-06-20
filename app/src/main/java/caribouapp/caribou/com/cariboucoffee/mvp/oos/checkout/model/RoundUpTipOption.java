package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import caribouapp.caribou.com.cariboucoffee.order.Order;

public class RoundUpTipOption extends TippingOption implements Serializable {

    public static final String ROUND_UP_TIP_OPTION_DESCRIPTION = "Round_up";

    @Override
    public BigDecimal calculateTip(Order order) {
        BigDecimal totalRoundUp = order.getTotalWithoutTip().setScale(0, RoundingMode.UP);
        BigDecimal differenceToRoundUp = totalRoundUp.subtract(order.getTotalWithoutTip());
        if (differenceToRoundUp.compareTo(BigDecimal.ZERO) == 0) {
            differenceToRoundUp = differenceToRoundUp.add(BigDecimal.ONE);
        }
        return differenceToRoundUp;
    }

    @Override
    public String getDescription() {
        return ROUND_UP_TIP_OPTION_DESCRIPTION;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof RoundUpTipOption;
    }

    @Override
    public int hashCode() {
        return RoundUpTipOption.class.getName().hashCode();
    }
}
