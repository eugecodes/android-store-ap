package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.AmountTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by fernando on 9/29/20.
 */
public final class OptionFactory {

    private Context mContext;

    private static OptionFactory mInstance = null;

    private OptionFactory() {
    }

    private OptionFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public static OptionFactory getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OptionFactory(context);
        }
        return mInstance;
    }

    public OptionItem<BigDecimal> createMoneyOption(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return new OptionItem<>(amount,
                StringUtils.formatMoneyAmount(mContext, amount),
                StringUtils.contentDescriptionMoneyAmount(mContext, amount));
    }

    public OptionItem<TippingOption> createOptionItemFromTippingOption(TippingOption tippingOption) {
        if (tippingOption instanceof AmountTipOption) {
            AmountTipOption amountTipOption = (AmountTipOption) tippingOption;
            BigDecimal predefinedTipAmount = amountTipOption.getPredefinedTipAmount();
            OptionItem<TippingOption> optionItem =
                    new OptionItem<>(amountTipOption,
                            StringUtils.formatMoneyAmount(mContext, predefinedTipAmount), null);
            return optionItem;
        } else {
            throw new RuntimeException("Unsupported tip option");
        }
    }
}
