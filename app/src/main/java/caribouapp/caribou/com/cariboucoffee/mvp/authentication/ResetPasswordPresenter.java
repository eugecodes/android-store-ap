package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import java.util.Locale;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestResetPwd;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import retrofit2.Response;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.VALID_EMAIL_ADDRESS_REGEX;

/**
 * Created by jmsmuy on 10/23/17.
 */

public class ResetPasswordPresenter extends BasePresenter<ResetPasswordContract.View> implements ResetPasswordContract.Presenter {

    private static final String TAG = ResetPasswordPresenter.class.getSimpleName();
    private final CredentialsModel mModel;

    @Inject
    AmsApi mAmsApi;

    public ResetPasswordPresenter(ResetPasswordContract.View view) {
        super(view);
        mModel = new CredentialsModel();
        getView().setModel(mModel);
    }

    @Override
    public void resetPassword() {
        // Checks email sintax
        if (!checkMail(mModel.getEmail())) {
            getView().emailErrorEnabled(true);
            return;
        }

        getView().resetPasswordEnabled(false);
        mAmsApi.sendResetPasswordMail(new AmsRequestResetPwd(mModel.getEmail())).enqueue(new BaseViewRetrofitCallback<AmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(Response<AmsResponse> response) {
                getView().endView(R.string.email_sent, mModel.getEmail());
            }

            @Override
            protected void onFail(Response<AmsResponse> response) {
                // Error on the actual reset password call
                Log.e(TAG, new LogErrorException("Error, unsuccessful password reset call"));
                if (getView() == null) {
                    return;
                }
                if (response.code() == AppConstants.HTTP_TOO_MANY_ATTEMPTS) {
                    getView().showErrorTooManyAttempts();
                } else {
                    getView().showErrorResetPassword();
                }
                getView().resetPasswordEnabled(true);
            }

            @Override
            protected void onError(Throwable throwable) {
                super.onError(throwable);
                if (getView() == null) {
                    return;
                }
                getView().resetPasswordEnabled(true);
            }
        });
    }

    private boolean checkMail(String mail) {
        return mail != null && !mail.isEmpty() && mail.toUpperCase(Locale.US).matches(VALID_EMAIL_ADDRESS_REGEX);
    }

    @Override
    public boolean checkMail() {
        return checkMail(mModel.getEmail());
    }
}
