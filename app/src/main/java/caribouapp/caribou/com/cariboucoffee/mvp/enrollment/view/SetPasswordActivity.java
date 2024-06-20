package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivitySetPasswordBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CredentialsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.PasswordUtil;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SetPasswordContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.SetPasswordPresenter;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import icepick.Icepick;
import icepick.State;

/**
 * Created by gonzalo.gelos on 1/5/18.
 */

public class SetPasswordActivity extends BaseActivity<ActivitySetPasswordBinding> implements SetPasswordContract.View {


    @State
    CredentialsModel mModel;
    private SetPasswordContract.Presenter mPresenter;

    public static Intent createIntent(Context context, String email, String telephone, boolean userHasBirthday) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra(AppConstants.EXTRA_SET_EMAIL, email);
        intent.putExtra(AppConstants.EXTRA_SET_TELEPHONE, telephone);
        intent.putExtra(AppConstants.EXTRA_SET_BIRTHDAY, userHasBirthday);
        return intent;
    }

    public static Intent createChangePasswordIntent(Context context, boolean isChangePassword) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra(AppConstants.EXTRA_IS_CHANGE_PASSWORD, isChangePassword);
        return intent;
    }

    private boolean isChangePassword() {
        return getIntent().getBooleanExtra(AppConstants.EXTRA_IS_CHANGE_PASSWORD, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (mModel == null) {
            mModel = new CredentialsModel();
        }
        getBinding().contentIncluded.setModel(mModel);
        SetPasswordPresenter presenter = new SetPasswordPresenter(this, mModel);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        mModel.setChangePassword(isChangePassword());

        UIUtil.setPasswordCharVisible(getBinding().contentIncluded.tilPassword);


        getBinding().contentIncluded.btnSetPassword.setOnClickListener(view -> mPresenter.setPassword());
        getBinding().contentIncluded.etPasswordConfirm.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_GO)) {
                mPresenter.setPassword();
            }
            return false;
        });
        getBinding().contentIncluded.tilPassword.setHint(mModel.isChangePassword()
                ? getResources().getString(R.string.new_password) : getResources().getString(R.string.password));

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (!isChangePassword()) {
            getBinding().tb.getNavigationIcon()
                    .setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primaryMidDarkColor), PorterDuff.Mode.SRC_ATOP);
        }
        prepareLayoutForChangePassword();

        if (getIntent() == null || getIntent().getData() == null) {
            return;
        }
        mPresenter.setTokenUri(getIntent().getData());
        getBinding().contentIncluded.btnSetPassword.setText(R.string.reset_password);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_password;
    }

    @Override
    public boolean passwordErrorEnable(List<PasswordUtil.PasswordHint> hints) {
        return setErrors(hints, getBinding().contentIncluded.tilPassword);
    }

    @Override
    public boolean currentPasswordErrorEnable(List<PasswordUtil.PasswordHint> hints) {
        return setErrors(hints, getBinding().contentIncluded.tilCurrentPassword);
    }

    @Override
    public boolean confirmPasswordErrorEnable(boolean enabled) {
        return setError(enabled, getString(R.string.password_and_confirm_do_not_match), getBinding().contentIncluded.tilPasswordConfirm);
    }

    @Override
    protected AppScreen getScreenName() {
        return isChangePassword() ? AppScreen.CHANGE_PASSWORD : null;
    }

    public void prepareLayoutForChangePassword() {
        getBinding().tbTitle.setVisibility(isChangePassword() ? View.VISIBLE : View.GONE);
        getBinding().tb.setBackgroundResource(isChangePassword() ? R.drawable.appbar_primary_background : R.color.transparent);
        if (isChangePassword()) {
            getBinding().contentIncluded.btnSetPassword.setText(R.string.submit);
            getBinding().contentIncluded.btnSetPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.primary_button));
        }
    }

    @Override
    public void goToSignIn(int message) {
        Intent finishingIntent = new Intent(this, SignInActivity.class);
        finishingIntent.setAction(AppConstants.ACTION_PASSWORD_RESET);
        finishingIntent.putExtra(AppConstants.EXTRA_MESSAGE, message);
        finishingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(finishingIntent);
        finish();
    }

    @Override
    public void goToAccount() {
        setResult(RESULT_OK);
        finish();
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

    @Override
    public void showWarning(String message) {
        super.showWarning(message);
        mPresenter.logError(message);
    }
}
