package caribouapp.caribou.com.cariboucoffee.mvp.enrollment;

import androidx.annotation.StringRes;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

public interface DupeCheckContract {
    interface View extends MvpView {

        boolean emailErrorEnabled(boolean value);

        boolean telephoneErrorEnabled(boolean value);

        void showEmailAlreadyRegistered(@StringRes int messageStringId, boolean enrolledViaGoogle);

        void goToPersonalInformation(boolean enrolledViaGoogle, String email, String phone, boolean userHasBirthday);

        void showPhoneAlreadyRegistered(@StringRes int messageStringId, String maskedEmail);
    }

    interface Presenter extends MvpPresenter {

        boolean checkMail();

        boolean checkPhone();

        void dupeCheck();

        void logError(String errorMessage);
    }
}
