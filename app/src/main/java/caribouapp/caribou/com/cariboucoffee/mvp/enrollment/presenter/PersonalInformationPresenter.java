package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import org.joda.time.Years;

import java.util.Locale;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.JoinLoginType;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPersonalInfo;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPreferences;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestCreateUser;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestUpdateUser;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponseCreateUser;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.StateEnum;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.PasswordUtil;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.PersonalInformationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.GeolocationServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationInfo;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.ValidationUtils;
import retrofit2.Response;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.PERSONAL_INFORMATION_INVALID_PARAMS;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.VALID_EMAIL_ADDRESS_REGEX;

/**
 * Created by jmsmuy on 1/8/18.
 */

public class PersonalInformationPresenter extends SaveSignInDataPresenter<PersonalInformationContract.View>
        implements PersonalInformationContract.Presenter {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int MINIMUM_AGE_REQUIRED = 13;

    @Inject
    AmsApi mAmsApi;

    @Inject
    Clock mClock;

    @Inject
    EventLogger mEventLogger;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    GeolocationServices mGeolocationServices;

    @Inject
    UserServices mUserServices;
    private final PersonalInformationModel mModel;
    private final Observable.OnPropertyChangedCallback mOnPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (getView() == null || (!mModel.isEditPersonalInformation() && mModel.isFirstAttempt())) {
                return;
            }

            if (propertyId == BR.firstName) {
                getView().firstNameErrorEnabled(!checkFirstName());
            } else if (propertyId == BR.lastName) {
                getView().lastNameErrorEnabled(!checkLastName());
            } else if (propertyId == BR.zipCode) {
                getView().zipErrorEnabled(!checkZipCode());
            }
        }
    };
    private final PersonalInformationConfigurator mConfigurator;
    private SetPasswordPresenter mPasswordPresenter;

    public PersonalInformationPresenter(PersonalInformationContract.View view, PersonalInformationModel model,
                                        PersonalInformationConfigurator configurator) {
        super(view);
        mModel = model;
        mModel.addOnPropertyChangedCallback(mOnPropertyChangedCallback);
        mConfigurator = configurator;
    }

    public PersonalInformationPresenter(PersonalInformationContract.View view, PersonalInformationModel model,
                                        PersonalInformationConfigurator configurator, SetPasswordPresenter passwordPresenter) {
        this(view, model, configurator);
        mPasswordPresenter = passwordPresenter;

    }

    public void setAmsApi(AmsApi amsApi) {
        mAmsApi = amsApi;
    }

    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public void setGeolocationServices(GeolocationServices geolocationServices) {
        mGeolocationServices = geolocationServices;
    }

    public void setClock(Clock clock) {
        mClock = clock;
    }

    @Override
    public void detachView() {
        mModel.removeOnPropertyChangedCallback(mOnPropertyChangedCallback);
        super.detachView();
    }

    @Override
    public void loadExistingData() {
        if (mModel.isEditPersonalInformation() || mModel.isWelcomeBackUser()) {
            mUserAccountService.getProfileDataWithCache(new BaseViewResultCallback<Void>(getView()) {

                @Override
                protected void onSuccessViewUpdates(Void data) {
                    setUserDataToModel();
                }

                @Override
                protected void onFailView(int errorCode, String errorMessage) {
                    onSuccessViewUpdates(null);
                }
            });
        }
    }

    private void setUserDataToModel() {
        mModel.setFirstName(mUserServices.getFirstName() == null ? "" : mUserServices.getFirstName());
        mModel.setLastName(mUserServices.getLastName() == null ? "" : mUserServices.getLastName());
        mModel.setCity(mUserServices.getCity() == null ? "" : mUserServices.getCity());
        mModel.setState(mUserServices.getState());
        mModel.setZipCode(mUserServices.getZip() == null ? "" : mUserServices.getZip());
        mModel.setEmail(mUserServices.getEmail() == null ? "" : mUserServices.getEmail());
        mModel.setPhoneNumber(mUserServices.getPhoneNumber() == null ? "" : mUserServices.getPhoneNumber());
        mModel.setBirthday(mUserServices.getBirthday());

        mConfigurator.onPersonalInformationModelAvailable(mModel);
    }

    private boolean checkZipCode() {
        return ValidationUtils.isValidZipCode(mModel.getZipCode());
    }

    private boolean checkAgeRequirement() {
        return Years.yearsBetween(mModel.getBirthday(), mClock.getCurrentDateTime().toLocalDate()).getYears() >= MINIMUM_AGE_REQUIRED;
    }

    private boolean checkMail() {
        if (mModel.isEditPersonalInformation()) {
            String email = mModel.getEmail();
            return email != null && !email.isEmpty() && email.toUpperCase(Locale.US).matches(VALID_EMAIL_ADDRESS_REGEX);
        }
        return true;
    }

    private boolean checkPhone() {
        return !mModel.isEditPersonalInformation() || ValidationUtils.isValidPhoneNumber(mModel.getPhoneNumber());
    }

    private String getPhoneWithoutSymbols() {
        return StringUtils.toPhoneNumberWithoutSymbols(mModel.getPhoneNumber());
    }

    private boolean checkLastName() {
        return mModel.getLastName() != null && !mModel.getLastName().isEmpty();
    }

    private boolean checkFirstName() {
        return mModel.getFirstName() != null && !mModel.getFirstName().isEmpty();
    }

    private boolean checkTermsNConditions() {
        return mModel.isEditPersonalInformation() || mModel.isTermsNConditions();
    }

    @Override
    public void createNewUser() {
        validateAndContinue(new BaseViewResultCallback<LocationInfo>(getView()) {
            @Override
            protected void onSuccessViewUpdates(LocationInfo locationInfo) {
                mAmsApi.createUser(
                        new AmsRequestCreateUser(mModel.getFirstName(), mModel.getLastName(),
                                mModel.getState(), mModel.getCity(), mModel.getZipCode(),
                                mModel.getEmail(), getPhoneWithoutSymbols(),
                                getView().getPassword(), buildAmsPreferences(),
                                mModel.getBirthday()))
                        .enqueue(
                                new BaseViewRetrofitErrorMapperCallback<AmsResponseCreateUser>(getView()) {
                                    @Override
                                    protected void onSuccessViewUpdates(Response<AmsResponseCreateUser> response) {
                                        JoinLoginType joinLoginType = mModel.isEnrolledViaGoogle() ? JoinLoginType.GOOGLE : JoinLoginType.EMAIL;
                                        AmsResponseCreateUser body = response.body();
                                        saveSignInData(body.getToken(), body.getUid());
                                        mEventLogger.logEnrollStepCompleted(joinLoginType, AppScreen.PERSONAL_INFO);
                                        mEventLogger.logEnrollCompleted(joinLoginType);
                                        mEventLogger.logCompletedRegistration();
                                    }

                                    @Override
                                    protected ErrorMessageMapper buildErrorMessageMapper() {
                                        return mErrorMessageMapper;
                                    }
                                });
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                if (errorCode != PERSONAL_INFORMATION_INVALID_PARAMS) {
                    super.onFailView(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void updateUser() {
        validateAndContinue(
                new BaseViewResultCallback<LocationInfo>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(LocationInfo locationInfo) {
                        AmsPreferences amsPreferences = buildAmsPreferences();
                        AmsRequestUpdateUser updateUser =
                                new AmsRequestUpdateUser(mModel.getFirstName(), mModel.getLastName(), mModel.getState(),
                                        mModel.getCity(), mModel.getZipCode(), mModel.getEmail(), getPhoneWithoutSymbols(),
                                        amsPreferences, mModel.getBirthday(), mUserServices.getUid());

                        mAmsApi.updateUser(updateUser)
                                .enqueue(new BaseViewRetrofitErrorMapperCallback<ResponseWithHeader>(getView()) {
                                    @Override
                                    protected void onSuccessViewUpdates(Response<ResponseWithHeader> response) {
                                        mUserServices.setPreferences(amsPreferences);
                                        AmsPersonalInfo personalInfo = new AmsPersonalInfo(mModel.getFirstName(), mModel.getLastName(),
                                                mModel.getState(), mModel.getCity(), mModel.getZipCode(),
                                                mModel.getPhoneNumber(), mModel.getEmail(), mModel.getBirthday());
                                        mUserServices.setPersonalInfo(personalInfo);
                                        getView().setUpdateUserInformationResult(mModel);
                                    }
                                });
                    }

                    @Override
                    protected void onFailView(int errorCode, String errorMessage) {
                        if (errorCode != PERSONAL_INFORMATION_INVALID_PARAMS) {
                            super.onFailView(errorCode, errorMessage);
                        }
                    }
                }
        );
    }

    private AmsPreferences buildAmsPreferences() {
        AmsPreferences amsPreferences = new AmsPreferences();
        amsPreferences
                .setAmsPreferencesBrandSpecific(
                        AppUtils.getBrand(), mModel.isMarketingMails(), mModel.isCateringMails());
        return amsPreferences;
    }

    private void validateAndContinue(ResultCallback<LocationInfo> resultCallback) {
        mModel.setFirstAttempt(false);

        boolean errorFound = getView().firstNameErrorEnabled(!checkFirstName())
                | getView().lastNameErrorEnabled(!checkLastName())
                | getView().zipErrorEnabled(!checkZipCode())
                | getView().emailErrorEnabled(!checkMail())
                | getView().telephoneErrorEnabled(!checkPhone())
                | (checkPassword())
                | (checkPasswordConfirm())
                | getView().tNCErrorEnabled(!checkTermsNConditions())
                | (!mModel.isBirthdayAlreadyDefined()
                && (getView().birthdayErrorEnabled(mModel.getBirthday() == null)
                || getView().ageRequirementErrorEnable(!checkAgeRequirement())));

        if (errorFound) {
            resultCallback.onFail(PERSONAL_INFORMATION_INVALID_PARAMS, "Invalid params");
            return;
        }

        mGeolocationServices.getCityAndStateFromZipcode(mModel.getZipCode(), new ResultCallbackWrapper<LocationInfo>(resultCallback) {

            @Override
            public void onSuccess(LocationInfo locationInfo) {
                mModel.setCity(locationInfo.getCity());
                mModel.setState(StateEnum.getFromName(locationInfo.getState()));
                resultCallback.onSuccess(locationInfo);
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Log.e(TAG, new LogErrorException("Zipcode lookup error: " + mModel.getZipCode()));
                getView().zipErrorEnabled(true);
                super.onFail(PERSONAL_INFORMATION_INVALID_PARAMS, errorMessage);
            }
        });
    }

    private boolean checkPassword() {
        return mPasswordPresenter.isPasswordActive()
                && mPasswordPresenter.getView().passwordErrorEnable(
                PasswordUtil.validatePassword(getView().getPassword(), false));
    }

    private boolean checkPasswordConfirm() {
        return mPasswordPresenter.isPasswordActive()
                && mPasswordPresenter.getView().confirmPasswordErrorEnable(
                !PasswordUtil.validatePasswordConfirm(getView().getPassword(), getView().getPasswordConfirm()));

    }

    @Override
    public void logEnrollError(String errorMessage) {
        if (mModel.isEditPersonalInformation()) {
            // Ignore account edit use case
            return;
        }
        mEventLogger.logEnrollmentError(AppScreen.PERSONAL_INFO, errorMessage);
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
    public void stopGuestFlowAndStartLoyaltyUserFlow() {
        mUserServices.setGuestUserFlowStarted(false);
    }

}
