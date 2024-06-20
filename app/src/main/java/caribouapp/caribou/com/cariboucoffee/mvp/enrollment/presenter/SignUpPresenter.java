package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SignUpPresenterContract;

public class SignUpPresenter extends BasePresenter<SignUpPresenterContract.View> implements SignUpPresenterContract.Presenter {

    @Inject
    SettingsServices mSettingsServices;

    public SignUpPresenter(SignUpPresenterContract.View view) {
        super(view);
    }


    @Override
    public boolean isGoogleSignInEnabled() {
        return mSettingsServices.getGoogleSignIn();
    }

    @Override
    public void doEmailSignup() {
        getView().goToPersonalInfoSignUpScreen(null);
    }

    @Override
    public void doGoogleSignup(String email) {
        getView().goToPersonalInfoSignUpScreen(email);
    }
}
