package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SaveSignInDataContract;

/**
 * Created by jmsmuy on 10/18/17.
 */

public interface SignInContract {

    interface View extends SaveSignInDataContract.View {

        boolean emailErrorEnabled(boolean enabled);

        boolean passwordErrorEnabled(List<PasswordUtil.PasswordHint> hints);

        void setModel(CredentialsModel model);

        void goToJoinNow();

        void showErrorSignIn();

        void showErrorTooManyAttemptsSignIn();

        void goToSignUpWithGoogle(String email);
    }

    interface Presenter extends SaveSignInDataContract.Presenter {

        void checkCredentials(String googleAccessToken);

        void setUpGoogleSignIn(String email, String accessToken);

        boolean isGoogleSignInEnabled();

        void joinNowClicked();

        boolean isGuestFlowActiveAfterLogin();

        boolean isThisGuestFlow();

        void stopGuestFlowAndStartLoyaltyUserFlow();


    }
}
