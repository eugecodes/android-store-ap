package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import java.net.HttpURLConnection;
import java.util.Locale;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.JoinLoginType;
import caribouapp.caribou.com.cariboucoffee.api.OAuthAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInContract.Presenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.SaveSignInDataPresenter;
import retrofit2.Response;

/**
 * Created by jmsmuy on 10/18/17.
 */

public class SignInPresenter extends SaveSignInDataPresenter<SignInContract.View> implements Presenter {

    private static final String TAG = SignInPresenter.class.getSimpleName();
    private final CredentialsModel mCredentials;
    @Inject
    OAuthAPI mOAuthAPI;
    @Inject
    SettingsServices mSettingsServices;
    @Inject
    EventLogger mEventLogger;
    @Inject
    AppDataStorage mAppDataStorage;
    @Inject
    UserServices mUserServices;

    public SignInPresenter(SignInContract.View view) {
        super(view);
        mCredentials = new CredentialsModel();
        getView().setModel(mCredentials);


        mCredentials.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (getView() == null) {
                    return;
                }
                if (propertyId == BR.email) {
                    getView().emailErrorEnabled(!checkMail());
                } else if (propertyId == BR.password) {
                    getView().passwordErrorEnabled(PasswordUtil.validatePassword(mCredentials.getPassword(), false));
                }
            }
        });
    }

    @Override
    public void checkCredentials(String googleAccessToken) {
        boolean errors =
                getView().emailErrorEnabled(!checkMail())
                        | (googleAccessToken == null
                        && getView().passwordErrorEnabled(
                        PasswordUtil.validatePassword(mCredentials.getPassword(), false)));
        if (errors) {
            return;
        }
        boolean googleSignIn = googleAccessToken != null;

        mOAuthAPI.authenticate(new OauthSignInRequest(mCredentials.getEmail(),
                !googleSignIn ? mCredentials.getPassword() : null,
                googleAccessToken,
                !googleSignIn ? null : AppConstants.GOOGLE_PROVIDER))
                .enqueue(new BaseViewRetrofitCallback<OauthSignInResponse>(getView()) {

                    @Override
                    protected void onSuccessViewUpdates(Response<OauthSignInResponse> response) {
                        mEventLogger.logSignInCompleted(googleAccessToken == null ? JoinLoginType.EMAIL : JoinLoginType.GOOGLE);
                        OauthSignInResponse oAuthSignInResponse = response.body();
                        saveSignInData(oAuthSignInResponse.getToken(), oAuthSignInResponse.getAud());
                    }

                    @Override
                    protected void onFail(Response<OauthSignInResponse> response) {
                        if (getView() == null) {
                            return;
                        }

                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED || response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                            if (googleSignIn) {
                                getView().goToSignUpWithGoogle(mCredentials.getEmail());
                            } else {
                                getView().showErrorSignIn();
                            }
                        } else if (response.code() == AppConstants.HTTP_TOO_MANY_ATTEMPTS) {
                            getView().showErrorTooManyAttemptsSignIn();
                        } else {
                            getView().showWarning(R.string.unknown_error);
                        }
                    }
                });
    }

    @Override
    public void setUpGoogleSignIn(String email, String googleAccessToken) {
        mCredentials.setEmail(email);
        checkCredentials(googleAccessToken);
    }

    private boolean checkMail() {
        String email = mCredentials.getEmail();
        return email != null
                && email.length() > 0
                && email.toUpperCase(Locale.US).matches(AppConstants.VALID_EMAIL_ADDRESS_REGEX);
    }

    public boolean isGoogleSignInEnabled() {
        return mSettingsServices.getGoogleSignIn();
    }

    @Override
    public void joinNowClicked() {
        mEventLogger.logEnrollStarted();
        getView().goToJoinNow();
    }

    @Override
    public boolean isGuestFlowActiveAfterLogin() {
        /** in this case we checked loggedIn user first because we are going to allow
         * user sign In first and then going to reset flag for guest user.
         */
        if (mUserServices.isUserLoggedIn()) {
            return mUserServices.isGuestUserFlowStarted();
        }
        return false;
    }

    @Override
    public boolean isThisGuestFlow() {
        return mUserServices.isGuestUserFlowStarted();
    }

    @Override
    public void stopGuestFlowAndStartLoyaltyUserFlow() {
        mUserServices.setGuestUserFlowStarted(false);
    }

}
