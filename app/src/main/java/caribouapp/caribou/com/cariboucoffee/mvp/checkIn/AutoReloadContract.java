package caribouapp.caribou.com.cariboucoffee.mvp.checkIn;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.AutoReloadModel;

/**
 * Created by jmsmuy on 11/30/17.
 */

public interface AutoReloadContract {

    interface View extends AccountContract.View {

        void setupMoneyAmounts(List<BigDecimal> moneyToAddAmounts, List<BigDecimal> moneyToAddDropdownAmounts,
                               List<BigDecimal> moneyThresholdAddAmounts, List<BigDecimal> moneyThresholdDropdownAmounts);

        void setupMoneyAmountsSelected(BigDecimal selectedMoneyToAdd, BigDecimal selectedThresholdValue);

        boolean tnCErrorEnabled(boolean enabled);

        boolean addThisAmountErrorEnabled(boolean enabled);

        boolean thresholdAmountErrorEnabled(boolean enabled);

        void showAddPayment(boolean show);

        void settingsApplied(BigDecimal threshold, BigDecimal amountToAdd);

        void goToAddPayment(String token);

        void goToTermsAndConditions();
    }

    interface Presenter extends MvpPresenter {

        void setModel(AutoReloadModel model);

        void setMoneyAddValue(BigDecimal amount);

        void setMoneyThresholdValue(BigDecimal amount);

        void configureAutoreload();

        void setTermsAndConditions(boolean value);

        void setUseCardOnFile(boolean cardOnFile);

        void navigateToAddPayment();

        void updateModel();

        void termsAndConditionsClicked();

        void loadMoneyAmounts();
    }
}

