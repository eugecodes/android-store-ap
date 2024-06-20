package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import android.text.TextUtils;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.Locale;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.JoinLoginType;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestPreEnrollment;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponsePreEnrollment;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponsePreEnrollmentResult;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.DupeCheckContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.DupeCheckModel;
import retrofit2.Response;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.DEFAULT_TELEPHONE_LENGHT;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.VALID_EMAIL_ADDRESS_REGEX;

/**
 * Created by jmsmuy on 1/4/18.
 */

public class DupeCheckPresenter extends BasePresenter<DupeCheckContract.View> implements DupeCheckContract.Presenter {

    private static final String TAG = DupeCheckPresenter.class.getSimpleName();

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    AmsApi mAmsApi;

    @Inject
    EventLogger mEventLogger;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    ErrorMessageMapper mErrorMessageMapper;

    private final DupeCheckModel mModel;

    public DupeCheckPresenter(DupeCheckContract.View view, DupeCheckModel model) {
        super(view);
        mModel = model;
        mModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (mModel.isFirstSignInAttempt() || getView() == null) {
                    return;
                }
                if (propertyId == BR.email) {
                    getView().emailErrorEnabled(!checkMail());
                } else if (propertyId == BR.password) {
                    getView().telephoneErrorEnabled(!checkPhone());
                }
            }
        });
    }

    public void setAmsApi(AmsApi amsApi) {
        mAmsApi = amsApi;
    }

    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public void setErrorMessageMapper(ErrorMessageMapper errorMessageMapper) {
        mErrorMessageMapper = errorMessageMapper;
    }

    @Override
    public boolean checkMail() {
        String email = mModel.getEmail();
        return mModel.isFirstSignInAttempt() || email != null && !email.isEmpty() && email.toUpperCase(Locale.US).matches(VALID_EMAIL_ADDRESS_REGEX);
    }

    @Override
    public boolean checkPhone() {
        return mModel.isFirstSignInAttempt() || getPhoneWithoutSymbols().length() == DEFAULT_TELEPHONE_LENGHT;
    }

    private String getPhoneWithoutSymbols() {
        return TextUtils.isEmpty(mModel.getTelephone()) ? "" : mModel.getTelephone().replaceAll("[^0-9]", "");
    }

    @Override
    public void dupeCheck() {
        mModel.setFirstSignInAttempt(false);

        boolean errorDetected = getView().emailErrorEnabled(!checkMail()) || getView().telephoneErrorEnabled(!checkPhone());

        if (errorDetected) {
            return;
        }

        String telephoneWithoutSymbols = getPhoneWithoutSymbols();

        mAmsApi.preEnrollment(
                new AmsRequestPreEnrollment(
                        mModel.getEmail(), mModel.getTelephone().replace("-", ""),
                        mModel.isEnrolledViaGoogle()))
                .enqueue(new BaseViewRetrofitErrorMapperCallback<AmsResponsePreEnrollment>(getView()) {


                    @Override
                    protected ErrorMessageMapper buildErrorMessageMapper() {
                        return mErrorMessageMapper;
                    }

                    @Override
                    protected void onSuccessViewUpdates(Response<AmsResponsePreEnrollment> response) {
                        if (response.body().getResult() == null || response.body().getResult().getStatus() == null) {
                            onFail(response);
                            return;
                        }
                        AmsResponsePreEnrollmentResult result = response.body().getResult();
                        switch (response.body().getResult().getStatus()) {
                            case NOT_ENROLLED:
                                mModel.setPreEnrolledValidationPassed(true);
                                mEventLogger.logEnrollStepCompleted(mModel.isEnrolledViaGoogle()
                                        ? JoinLoginType.GOOGLE : JoinLoginType.EMAIL, AppScreen.SIGN_UP_2);

                                getView().goToPersonalInformation(mModel.isEnrolledViaGoogle(),
                                        mModel.getEmail(), telephoneWithoutSymbols, result.isDateOfBirth());
                                break;
                            case AUTOMATIC_PASSWORD_RESET:
                                int errorMessagePasswordResetId = R.string.you_are_already_registered_email;
                                mEventLogger.logEnrollmentError(AppScreen.SIGN_UP_2, errorMessagePasswordResetId);
                                getView().showEmailAlreadyRegistered(errorMessagePasswordResetId, mModel.isEnrolledViaGoogle());
                                break;
                            case ENROLLED_IN_THIS_PROGRAM:
                                int errorMessageAlreadyRegisteredId = R.string.you_are_already_registered;
                                mEventLogger.logEnrollmentError(AppScreen.SIGN_UP_2, errorMessageAlreadyRegisteredId);
                                getView().showEmailAlreadyRegistered(errorMessageAlreadyRegisteredId, mModel.isEnrolledViaGoogle());
                                break;
                            case ENROLLED_IN_DIFFERENT_PROGRAM:
                                int errorMessageDifferentProgramId = R.string.you_are_already_registered_different_program;
                                mEventLogger.logEnrollmentError(AppScreen.SIGN_UP_2, errorMessageDifferentProgramId);
                                getView().showEmailAlreadyRegistered(R.string.you_are_already_registered_different_program,
                                        mModel.isEnrolledViaGoogle());
                                break;
                            case PHONE_REGISTERED_DIFFERENT_EMAIL:
                                int errorMessagePhoneNumberId = R.string.phone_number_already_registered_with_other_email;
                                mEventLogger.logEnrollmentError(AppScreen.SIGN_UP_2, errorMessagePhoneNumberId);
                                getView().showPhoneAlreadyRegistered(R.string.phone_number_already_registered_with_other_email,
                                        result.getMaskedEmailForPhoneNumber());
                                break;
                        }
                    }
                });
    }

    @Override
    public void logError(String errorMessage) {
        mEventLogger.logEnrollmentError(AppScreen.PERSONAL_INFO, errorMessage);
    }

}
