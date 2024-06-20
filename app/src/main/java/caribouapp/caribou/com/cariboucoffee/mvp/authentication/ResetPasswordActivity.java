package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.core.content.ContextCompat;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityResetPasswordBinding;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by jmsmuy on 10/23/17.
 */

public class ResetPasswordActivity extends BaseActivity<ActivityResetPasswordBinding> implements ResetPasswordContract.View {


    private ResetPasswordContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ResetPasswordPresenter presenter = new ResetPasswordPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        getBinding().btnResetPassword.setOnClickListener(view -> {
            UIUtil.hideKeyboard(ResetPasswordActivity.this);
            mPresenter.resetPassword();
        });

        // This checks the mail is valid as well as sending it to the presenter
        getBinding().etEmail.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_GO) {
                UIUtil.hideKeyboard(ResetPasswordActivity.this);
                getBinding().btnResetPassword.performClick();
                return true;
            }
            return false;
        });

        getBinding().btnResetPassword.requestFocus();

        getBinding().etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailErrorEnabled(!(editable.toString().isEmpty() || mPresenter.checkMail()));
            }
        });

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().tb.getNavigationIcon()
                .setColorFilter(ContextCompat.getColor(getApplicationContext(),
                        R.color.primaryMidDarkColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.FORGOT_PASSWORD;
    }

    @Override
    public void setModel(CredentialsModel model) {
        getBinding().setModel(model);
    }

    @Override
    public void endView(int stringResource, String text) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppConstants.EXTRA_MESSAGE, StringUtils.format(getString(stringResource), text));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void showErrorResetPassword() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.oops)
                .setMessage(R.string.error_sending_email)
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss()).show();
    }

    @Override
    public void showErrorTooManyAttempts() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.oops)
                .setMessage(R.string.error_too_many_reset_password_attempts)
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss()).show();
    }

    @Override
    public void emailErrorEnabled(boolean value) {
        if (value) {
            getBinding().tilEmail.setError(getString(R.string.email_not_valid));
        } else {
            getBinding().tilEmail.setErrorEnabled(false);
        }
    }

    @Override
    public void resetPasswordEnabled(boolean enabled) {
        getBinding().pbResettingPwd.setVisibility(enabled ? View.INVISIBLE : View.VISIBLE);
        getBinding().btnResetPassword.setEnabled(enabled);
        getBinding().etEmail.setEnabled(enabled);
    }
}
