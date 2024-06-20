package caribouapp.caribou.com.cariboucoffee.mvp.checkIn;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.AddFundsModel;

/**
 * Created by jmsmuy on 11/24/17.
 */

public interface AddFundsContract {

    interface View extends MvpView {

        boolean amountToAddErrorEnabled(boolean enabled);

        void showAddPayment(boolean show);

        void finishedSuccessfulAddFunds(BigDecimal newBalance, BigDecimal addedAmount);

        void goToAddPayment(String token);

        void showInsufficientAlertDialog();

        void setupMoneyItems(List<BigDecimal> checkoutAddFundsValues, List<BigDecimal> dropdownMoneyAmounts);
    }

    interface Presenter extends MvpPresenter {

        void init();

        void setMoneyValue(BigDecimal value);

        void setModel(AddFundsModel addFundsModel);

        void setUseCardOnFile(boolean cardOnFile);

        void addFundsOneTime(String token);

        void addFundsFromCardOnFile(String token);

        void navigateToAddPayment();

        List<BigDecimal> getCheckoutAddFundsValues(BigDecimal amountNeededForOrder);

        void loadMoneyItems();
    }

}
