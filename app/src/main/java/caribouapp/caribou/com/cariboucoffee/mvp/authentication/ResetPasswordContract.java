package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by jmsmuy on 10/23/17.
 */

public interface ResetPasswordContract {

    interface View extends MvpView {

        void setModel(CredentialsModel model);

        void endView(int stringResource, String text);

        void showErrorResetPassword();

        void showErrorTooManyAttempts();

        void emailErrorEnabled(boolean value);

        void resetPasswordEnabled(boolean enabled);
    }

    interface Presenter extends MvpPresenter {

        void resetPassword();

        boolean checkMail();
    }

}
