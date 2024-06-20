package caribouapp.caribou.com.cariboucoffee.mvp.enrollment;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

public interface SignUpPresenterContract {

    interface View extends MvpView {
        void goToPersonalInfoSignUpScreen(String googleEmail);
    }

    interface Presenter extends MvpPresenter {
        boolean isGoogleSignInEnabled();

        void doEmailSignup();

        void doGoogleSignup(String email);
    }
}
