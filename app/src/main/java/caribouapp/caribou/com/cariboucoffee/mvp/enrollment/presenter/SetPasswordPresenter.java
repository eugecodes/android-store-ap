package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import android.net.Uri;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestResetPwd;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestUpdatePassword;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CredentialsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.PasswordUtil;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SetPasswordContract;
import retrofit2.Response;

/**
 * Created by gonzalo.gelos on 1/5/18.
 */

public class SetPasswordPresenter extends BasePresenter<SetPasswordContract.View>
        implements SetPasswordContract.Presenter {

    @Inject
    AmsApi mAmsApi;

    @Inject
    UserServices mUserServices;

    @Inject
    EventLogger mEventLogger;

    @Inject
    AppDataStorage mAppDataStorage;

    private final CredentialsModel mModel;
    private boolean mResetPasswordMode = false;
    private String mToken;

    public SetPasswordPresenter(SetPasswordContract.View view, CredentialsModel model) {
        super(view);
        mModel = model;
        mModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (getView() == null) {
                    return;
                }
                if (propertyId == BR.password) {
                    getView().passwordErrorEnable(
                            PasswordUtil
                                    .validatePassword(mModel.getPassword(), false));
                }
                if (propertyId == BR.currentPassword && mModel.isChangePassword()) {
                    getView().currentPasswordErrorEnable(
                            PasswordUtil
                                    .validatePassword(mModel.getCurrentPassword(), false));
                }
                if (propertyId == BR.passwordConfirm && mModel.isChangePassword()) {
                    getView().confirmPasswordErrorEnable(false);
                }
            }
        });
    }

    private void cleanErrors() {
        getView().passwordErrorEnable(null);
        getView().currentPasswordErrorEnable(null);
        getView().confirmPasswordErrorEnable(false);
    }

    private void showErrors(List<PasswordUtil.PasswordHint> hintsPassword,
                            List<PasswordUtil.PasswordHint> hintsCurrentPassword,
                            boolean hintsConfirmPassword) {
        getView().passwordErrorEnable(hintsPassword);
        getView().currentPasswordErrorEnable(hintsCurrentPassword);
        getView().confirmPasswordErrorEnable(hintsConfirmPassword);
    }

    public void setPassword() {
        cleanErrors();

        List<PasswordUtil.PasswordHint> hintsPassword = PasswordUtil.validatePassword(mModel.getPassword(), true);
        List<PasswordUtil.PasswordHint> hintsCurrentPassword = PasswordUtil.validatePassword(mModel.getCurrentPassword(), false);
        boolean hintsConfirmPassword = !PasswordUtil.validatePasswordConfirm(mModel.getPassword(), mModel.getPasswordConfirm());

        if (getView().passwordErrorEnable(hintsPassword)
                || (mModel.isChangePassword()
                && (getView().currentPasswordErrorEnable(hintsCurrentPassword)
                || getView().confirmPasswordErrorEnable(hintsConfirmPassword)))) {
            showErrors(hintsPassword, hintsCurrentPassword, hintsConfirmPassword);
            return;
        }

        if (mModel.isChangePassword()) {
            // Change password flow
            mAmsApi.updatePassword(
                    new AmsRequestUpdatePassword(mModel.getPassword(),
                            mModel.getCurrentPassword(), mUserServices.getUid()))
                    .enqueue(new BaseViewRetrofitErrorMapperCallback<ResponseWithHeader>(getView()) {
                        @Override
                        protected void onSuccessViewUpdates(Response<ResponseWithHeader> response) {
                            getView().goToAccount();
                        }
                    });
        } else if (mResetPasswordMode) {
            // Reset password flow
            mAmsApi.resetPassword(new AmsRequestResetPwd(mToken, mModel.getPassword()))
                    .enqueue(new BaseViewRetrofitErrorMapperCallback<AmsResponse>(getView()) {
                        @Override
                        protected void onSuccessViewUpdates(Response<AmsResponse> response) {
                            getView().goToSignIn(R.string.success_resetting_password);
                        }

                        @Override
                        protected void onFail(Response<AmsResponse> response) {
                            if (getView() == null) {
                                return;
                            }
                            getView().goToSignIn(R.string.error_message_reset_password);
                        }
                    });
        }
    }

    @Override
    public void setTokenUri(Uri uri) {
        mResetPasswordMode = true;
        mToken = getTokenFromUri(uri);
    }

    @Override
    public void logError(String errorMessage) {
        mEventLogger.logEnrollmentError(AppScreen.SET_PASSWORD, errorMessage);
    }

    @Override
    public boolean isPasswordActive() {
        return mModel.isNewPassword() || mModel.isChangePassword();
    }

    /**
     * The uri has the following format
     * https://shared-dev.caribouperks.com/passwordreset?token=30a302b2-ecea-49b1-9838-861561994c7e
     * This method only gets the attribute "token" from the uri and returns it
     *
     * @param uri
     * @return
     */
    private String getTokenFromUri(Uri uri) {
        Pattern p = Pattern.compile("(.*?)(token=)(.[^&]*)");
        Matcher matcher = p.matcher(uri.toString());
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(3);
    }
}
