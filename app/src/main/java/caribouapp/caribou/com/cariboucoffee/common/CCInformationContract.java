package caribouapp.caribou.com.cariboucoffee.common;

import caribouapp.caribou.com.cariboucoffee.cybersource.TokenResponse;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by jmsmuy on 11/28/17.
 */

public interface CCInformationContract {

    interface View extends MvpView {

        boolean cvvUnknownErrorEnabled(boolean enabled);

        boolean cardNumberErrorEnabled(boolean enabled);

        boolean validCardNumberErrorEnabled(boolean enabled);

        boolean firstNameErrorEnabled(boolean enabled);

        boolean lastNameErrorEnabled(boolean enabled);

        boolean expirationMonthErrorEnabled(boolean enabled);

        boolean expirationYearErrorEnabled(boolean enabled);

        boolean cvvErrorEnabled(boolean enabled);

        boolean validCvvErrorEnabled(boolean enabled);

        boolean billingAddressErrorEnabled(boolean enabled);

        boolean zipErrorEnabled(boolean enabled);

        boolean cityErrorEnabled(boolean enabled);

        boolean stateErrorEnabled(boolean enabled);

        boolean hasOptionalReplaceCard();

        void finishAndSendResult(CCInformationModel model, TokenResponse data, boolean cardOnFile, boolean newCard);

        boolean cardTypeUnknownErrorEnabled(boolean enabled);
    }

    interface Presenter extends MvpPresenter {

        void setModel(CCInformationModel model);

        void addOrUpdateCard();

        void setUpForEditing();

        boolean hasUserGotCard();

        void setState(StateEnum stateEnum);

        void setToken(String token);

        void fillModel();

        void updateLocalBillingData();
    }
}
