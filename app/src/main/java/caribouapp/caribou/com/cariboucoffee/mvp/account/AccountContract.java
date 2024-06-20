package caribouapp.caribou.com.cariboucoffee.mvp.account;


import android.app.Activity;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CreditCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.TransactionModel;

/**
 * Created by jmsmuy on 11/21/17.
 */

public interface AccountContract {
    interface View extends MvpView {

    }

    interface CreditCardView extends MvpView {

        void setCardList(List<CreditCardModel> cardList);

        void updateCardList();

        void removeCard(CreditCardModel cardModel);

        void editCreditCard(CreditCardModel cardModel);

        void addCreditCard();
    }

    interface CreditCardPresenter extends MvpPresenter {

        void loadCards();

        void removeCard(CreditCardModel cardModel);
    }

    interface TransactionHistoryView extends MvpView {

        void displayTransactionHistory(List<TransactionModel> transactions);

        void displayEmptyTransactionHistory();
    }

    interface TransactionHistoryPresenter extends MvpPresenter {
        void loadTransactionHistory();
    }

    interface ProfileView extends MvpView {
        void showDeleteAccountFunctionality();

        void hideDeleteAccountFunctionality();
    }

    interface RewardsCardView extends MvpView {

        void setAutoReload(boolean enable, BigDecimal thresholdAmount, BigDecimal incrementAmount);

        void setCardNumber(String cardNumber);

        void goToAutoReloadSetting();
    }

    interface RewardsCardPresenter extends MvpPresenter {

        void loadData();

        void autoReloadClicked(boolean enabled);

        void setBalance(BigDecimal newBalance);

        void setAutoReload(boolean enabled);

        void updateCardNumber();
    }

    interface ProfilePresenter extends MvpPresenter {

        void loadData();

        void showMyAccountDeleteDialog(Activity activity);

        void enabledDeleteAccountButton();
    }
}
