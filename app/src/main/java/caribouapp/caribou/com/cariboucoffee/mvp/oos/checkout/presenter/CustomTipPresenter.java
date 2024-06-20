package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.CustomTipContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CustomTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PercentageTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.RoundUpTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;
import caribouapp.caribou.com.cariboucoffee.order.Order;

public class CustomTipPresenter extends BasePresenter<CustomTipContract.View> implements CustomTipContract.Presenter {

    private static final BigDecimal MAX_TIP_VALUE = new BigDecimal(100);
    private Order mOrder;

    public CustomTipPresenter(CustomTipContract.View view, Order order) {
        super(view);
        mOrder = order;
    }

    @Override
    public void checkValidCustomTip(BigDecimal customTip) {
        if (customTip.compareTo(MAX_TIP_VALUE) <= 0) {
            getView().applyValidCustomTip(customTip);
        } else {
            getView().setMaxValueTipError();
        }
    }

    @Override
    public void selectedTippingOption(TippingOption tippingOption) {
        if (tippingOption == null) {
            return;
        }
        //TODO replace instaceof with getDescription of itemOption when we add it
        if (tippingOption instanceof RoundUpTipOption) {
            getView().setRoundUpSelected();
        } else if (tippingOption instanceof PercentageTipOption) {
            getView().setPercentageOptionSelected((PercentageTipOption) tippingOption);
        } else if (tippingOption instanceof CustomTipOption) {
            getView().setCustomTip(tippingOption.calculateTip(mOrder));
        }
    }

    @Override
    public void loadTipOptions() {
        List<PercentageTipOption> tippingOptions = new ArrayList<>();
        for (int tipOptionPercentage : AppConstants.TIP_OPTION_PERCENTAGE) {
            tippingOptions.add(createPercentageTipOption(new BigDecimal(tipOptionPercentage)));
        }
        getView().showPercentageOptions(tippingOptions);
        getView().showRoundUpOption(new RoundUpTipOption());
    }

    private PercentageTipOption createPercentageTipOption(BigDecimal amount) {
        return new PercentageTipOption(amount);
    }
}
