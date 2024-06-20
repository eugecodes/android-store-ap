package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PercentageTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.RoundUpTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;

public interface CustomTipContract {

    interface View extends MvpView {

        void setCustomTip(BigDecimal tippingValue);

        void setRoundUpSelected();

        void setPercentageOptionSelected(PercentageTipOption tippingOption);

        void applyValidCustomTip(BigDecimal customTip);

        void setMaxValueTipError();

        void showPercentageOptions(List<PercentageTipOption> percentageTipOptions);

        void showRoundUpOption(RoundUpTipOption roundUpTipOption);
    }

    interface Presenter extends MvpPresenter {

        void checkValidCustomTip(BigDecimal customTip);

        void selectedTippingOption(TippingOption tippingOption);

        void loadTipOptions();
    }
}
