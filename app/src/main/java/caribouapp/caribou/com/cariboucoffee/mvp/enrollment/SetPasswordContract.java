package caribouapp.caribou.com.cariboucoffee.mvp.enrollment;

import android.net.Uri;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.PasswordUtil;

public interface SetPasswordContract {
    interface View extends MvpView {

        boolean passwordErrorEnable(List<PasswordUtil.PasswordHint> hints);

        boolean currentPasswordErrorEnable(List<PasswordUtil.PasswordHint> hints);

        boolean confirmPasswordErrorEnable(boolean enable);

        void goToSignIn(int message);

        void goToAccount();
    }

    interface Presenter extends MvpPresenter {

        void setPassword();

        void setTokenUri(Uri uri);

        void logError(String errorMessage);

        boolean isPasswordActive();
    }
}
