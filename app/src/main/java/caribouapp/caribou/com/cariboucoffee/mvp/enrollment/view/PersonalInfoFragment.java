package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import org.joda.time.LocalDate;

import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.common.BrandEnum;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.MaskEmailTransformationMethod;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentPersonalInformationBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CredentialsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.PasswordUtil;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.PersonalInformationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SetPasswordContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.BasePersonalInformationConfigurator;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.PersonalInformationConfigurator;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.PersonalInformationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.SetPasswordPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.view.TermsAndPrivacyActivity;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import icepick.Icepick;
import icepick.State;

@FragmentWithArgs
public class PersonalInfoFragment extends BaseFragment<FragmentPersonalInformationBinding>
        implements PersonalInformationContract.View, SetPasswordContract.View {

    private static final int DATE_PICKER_MONTH_DIFFERENCE = 1;
    @Inject
    Clock mClock;
    @Inject
    EventLogger mEventLogger;
    @State
    PersonalInformationModel mPersonalInformationModel;
    @State
    CredentialsModel mCredentialsModel;
    @Arg
    FormType mFormType;
    private PersonalInformationContract.Presenter mPersonalInfoPresenter;
    private SetPasswordContract.Presenter mSetPasswordPresenter;
    private PersonalInfoListener mListener;
    private PersonalInformationConfigurator mConfigurator;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal_information;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SourceApplication.get(context).getComponent().inject(this);

        if (getActivity() instanceof PersonalInfoListener) {
            mListener = (PersonalInfoListener) getActivity();
        }

        mConfigurator = (PersonalInformationConfigurator) getActivity().getIntent()
                .getSerializableExtra(AppConstants.PERSONAL_INFORMATION_CONFIGURATOR);
        if (mConfigurator == null) {
            mConfigurator = new BasePersonalInformationConfigurator();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mPersonalInformationModel == null) {
            mPersonalInformationModel = new PersonalInformationModel();
            mPersonalInformationModel.setEditPersonalInformation(mFormType == FormType.EDIT);
            mPersonalInformationModel.setWelcomeBackUser(isWelcomeBackUser());
            mCredentialsModel = new CredentialsModel();
        }

        SetPasswordPresenter setPasswordPresenter = new SetPasswordPresenter(this, mCredentialsModel);
        SourceApplication.get(getContext()).getComponent().inject(setPasswordPresenter);
        mSetPasswordPresenter = setPasswordPresenter;

        PersonalInformationPresenter personalInformationPresenter = new PersonalInformationPresenter(
                this, mPersonalInformationModel, mConfigurator, setPasswordPresenter
        );
        SourceApplication.get(getContext()).getComponent().inject(personalInformationPresenter);
        mPersonalInfoPresenter = personalInformationPresenter;

        getBinding().setConfigurator(mConfigurator);
        getBinding().setModel(mPersonalInformationModel);
        getBinding().setCredentialsModel(mCredentialsModel);

        prepareLayoutForEditPersonalInformation();

        initBirthdayField();

        getBinding().tvGoToTNC
                .setOnClickListener(v -> startActivity(new Intent(getContext(), TermsAndPrivacyActivity.class)));

        if (mPersonalInformationModel.isEditPersonalInformation()) {
            getBinding().etEmail.setTransformationMethod(new MaskEmailTransformationMethod());
        }

        getBinding().etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                getBinding().btnSubmit.performClick();
            }
            return false;
        });
        UIUtil.setPasswordCharVisible(getBinding().tilPassword);

        getBinding().btnSubmit.setOnClickListener(v -> mListener.onPersonalInfoSubmit());

        if (mFormType == FormType.EDIT || mFormType == FormType.WELCOME_BACK) {
            mPersonalInfoPresenter.loadExistingData();
        }
        if (AppUtils.getBrand() == BrandEnum.CBOU_BRAND || mFormType == FormType.EDIT) {
            getBinding().cbOptCateringMails.setVisibility(View.GONE);
        }
    }

    private void initBirthdayField() {
        getBinding().etBirthday.setInputType(InputType.TYPE_NULL);

        if (!mPersonalInformationModel.isEditPersonalInformation()) {
            getBinding().etBirthday.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    openBirthdayDialog();
                }
            });
            getBinding().etBirthday.setOnClickListener(v -> openBirthdayDialog());
        }
    }

    private void openBirthdayDialog() {
        LocalDate currentTime = mClock.getCurrentDateTime().toLocalDate();
        LocalDate pickerDate;
        if (mPersonalInformationModel.getBirthday() == null) {
            pickerDate = currentTime;
        } else {
            pickerDate = mPersonalInformationModel.getBirthday();
        }
        DatePickerDialog datePicker =
                new DatePickerDialog(getContext(),
                        AlertDialog.THEME_HOLO_LIGHT,
                        (view, year, month, dayOfMonth) ->
                                mPersonalInformationModel
                                        .setBirthday(
                                                new LocalDate(year, month + DATE_PICKER_MONTH_DIFFERENCE, dayOfMonth)), pickerDate.getYear(),
                        pickerDate.getMonthOfYear() - DATE_PICKER_MONTH_DIFFERENCE,
                        pickerDate.getDayOfMonth());
        datePicker.getDatePicker().setMaxDate(currentTime.toDateTimeAtStartOfDay().getMillis());
        datePicker.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPersonalInfoPresenter != null) {
            mPersonalInfoPresenter.detachView();
        }
        if (mSetPasswordPresenter != null) {
            mSetPasswordPresenter.detachView();
        }
    }

    @Override
    public boolean firstNameErrorEnabled(boolean enable) {
        return fieldErrorEnabled(getBinding().tilFirstName, R.string.first_name_error, enable);
    }

    @Override
    public boolean lastNameErrorEnabled(boolean enable) {
        return fieldErrorEnabled(getBinding().tilLastName, R.string.last_name_error, enable);
    }

    @Override
    public boolean zipErrorEnabled(boolean enable) {
        return fieldErrorEnabled(getBinding().tilZipCode, R.string.zip_error, enable);
    }

    @Override
    public boolean emailErrorEnabled(boolean enable) {
        return fieldErrorEnabled(getBinding().tilEmail, R.string.email_not_valid, enable);
    }

    @Override
    public boolean telephoneErrorEnabled(boolean enable) {
        return fieldErrorEnabled(getBinding().tilPhoneNumber, R.string.phone_not_valid, enable);
    }

    @Override
    public boolean tNCErrorEnabled(boolean enable) {
        if (enable) {
            mPersonalInfoPresenter.logEnrollError(getString(R.string.you_must_agree_with_t_n_c));
        }
        getBinding().tvSeeTNCError.setVisibility(enable ? View.VISIBLE : View.GONE);
        return enable;
    }

    @Override
    public boolean confirmPasswordErrorEnabled(boolean enable) {
        return fieldErrorEnabled(getBinding().tilPasswordConfirm, R.string.password_and_confirm_do_not_match, enable);
    }

    @Override
    public boolean ageRequirementErrorEnable(boolean enable) {
        return fieldErrorEnabled(getBinding().tilBirthday, R.string.error_message_age_requirement, enable);
    }

    public boolean birthdayErrorEnabled(boolean enabled) {
        return fieldErrorEnabled(getBinding().tilBirthday, R.string.birthday_error, enabled);
    }

    private boolean fieldErrorEnabled(TextInputLayout field, int errorResource, boolean enabled) {
        if (enabled) {
            String errorMessage = getString(errorResource);
            mPersonalInfoPresenter.logEnrollError(errorMessage);
            field.setError(errorMessage);
        } else {
            field.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public void goToDashboard() {
        if (mPersonalInfoPresenter.isGuestFlowActiveAfterLogin() && mFormType == FormType.CREATE) {
            mPersonalInfoPresenter.stopGuestFlowAndStartLoyaltyUserFlow();
            requireActivity().finish();
        } else {
            Intent intent = MainActivity.createIntent(getContext(), null);
            intent.putExtra(AppConstants.EXTRA_IS_FROM_SIGN_IN, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }


    }

    public void prepareLayoutForEditPersonalInformation() {
        getBinding().rlTitleContainer.setVisibility(mPersonalInformationModel.isEditPersonalInformation() ? View.VISIBLE : View.GONE);
    }

    public void setUpdateUserInformationResult(PersonalInformationModel model) {
        if (isWelcomeBackUser()) {
            getActivity().finish();
            startActivity(new Intent(getContext(), MainActivity.class));
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(AppConstants.EXTRA_PERSONAL_INFORMATION_MODEL, model);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public String getPassword() {
        return mCredentialsModel.getPassword();
    }

    @Override
    public String getPasswordConfirm() {
        return mCredentialsModel.getPasswordConfirm();
    }

    @Override
    public boolean passwordErrorEnable(List<PasswordUtil.PasswordHint> hints) {
        return setErrors(hints, getBinding().tilPassword);
    }

    @Override
    public boolean currentPasswordErrorEnable(List<PasswordUtil.PasswordHint> hints) {
        return setErrors(hints, getBinding().tilPasswordConfirm);
    }

    @Override
    public boolean confirmPasswordErrorEnable(boolean enabled) {
        return setError(enabled, getString(R.string.password_and_confirm_do_not_match), getBinding().tilPasswordConfirm);
    }

    @Override
    public void goToSignIn(int message) {
        // NO-OP
    }

    @Override
    public void goToAccount() {
        // NO-OP
    }

    private boolean setError(boolean errorEnabled, String error, TextInputLayout editTextWithError) {
        if (errorEnabled) {
            editTextWithError.setError(error);
        }
        editTextWithError.setErrorEnabled(errorEnabled);
        return errorEnabled;
    }

    private boolean setErrors(List<PasswordUtil.PasswordHint> hints, TextInputLayout editTextWithErrors) {
        boolean errorEnabled = hints != null && !hints.isEmpty();
        if (errorEnabled) {
            StringBuilder stringBuilder = new StringBuilder();
            String separator = getString(R.string.hint_separator);
            for (PasswordUtil.PasswordHint hint : hints) {
                stringBuilder.append(separator).append(" ").append(getString(hint.getHintRes())).append(" ");
            }
            editTextWithErrors.setError(stringBuilder.toString());
        } else {
            editTextWithErrors.setErrorEnabled(false);
        }
        return errorEnabled;
    }

    private boolean isWelcomeBackUser() {
        return mFormType == FormType.WELCOME_BACK;
    }

    public PersonalInformationContract.Presenter getPersonalInfoPresenter() {
        return mPersonalInfoPresenter;
    }

    public enum FormType {
        CREATE,
        EDIT,
        WELCOME_BACK
    }

    public interface PersonalInfoListener {
        void onPersonalInfoSubmit();
    }

}
