package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.ACTION_PASSWORD_RESET;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.EXTRA_MESSAGE;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_GOOGLE_SIGN_IN;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_SIGN_UP;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivitySignInBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.SignUpActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.SignUpPersonalInfoActivity;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by jmsmuy on 10/18/17.
 */

public class SignInActivity extends BaseActivity<ActivitySignInBinding> implements SignInContract.View,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SignInActivity.class.getSimpleName();
    private static final int FORGOT_PASSWORD_CODE = 1;
    @Inject
    UserServices mUserServices;
    private SignInContract.Presenter mPresenter;
    private GoogleApiClient mGoogleApiClient;

    public static Intent createIntent(Context context) {
        return new Intent(context, SignInActivity.class);
    }

    public static Intent createIntent(Context context, String message) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.putExtra(AppConstants.EXTRA_MESSAGE, message);
        return intent;
    }

    public static Intent createIntent(Context context, Intent afterSignInIntent) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.putExtra(AppConstants.EXTRA_INTENT, afterSignInIntent);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() != null && intent.getAction().equals(ACTION_PASSWORD_RESET)) {
            if (mUserServices == null) {
                SourceApplication.get(this).getComponent().inject(this);
            }
            mUserServices.signOut();
            showWarning(intent.getIntExtra(EXTRA_MESSAGE, R.string.error_message_reset_password));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SignInPresenter presenter = new SignInPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        setGoogleSignInStatus(mPresenter.isGoogleSignInEnabled());

        getBinding().contentIncluded.btnSignIn.setOnClickListener(view -> {
            mPresenter.checkCredentials(null);
            UIUtil.hideKeyboard(this);
        });

        // This checks credentials as well as hiding the keyboard
        getBinding().contentIncluded.etPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT) {
                UIUtil.hideKeyboard(SignInActivity.this);
                mPresenter.checkCredentials(null);
                return true;
            }
            return false;
        });

        UIUtil.setPasswordCharVisible(getBinding().contentIncluded.tilPassword);

        getBinding().contentIncluded.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                getBinding().contentIncluded.btnSignIn.performClick();
            }
            return false;
        });

        // This is to show complete hints in email and password when creating activity
        getBinding().contentIncluded.ivCaribouPerks.requestFocus();

        getBinding().contentIncluded.tvForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, ResetPasswordActivity.class);
            startActivityForResult(intent, FORGOT_PASSWORD_CODE);
        });

        getBinding().contentIncluded.tvGoToSignUp.setOnClickListener(view -> goToJoinNow());

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().tb.getNavigationIcon()
                .setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primaryMidDarkColor), PorterDuff.Mode.SRC_ATOP);

        setupGoogleSignIn();
        if (getIntent() == null) {
            return;
        }
        onNewIntent(getIntent());
        showExtraMessage(getIntent());
    }

    private void setGoogleSignInStatus(boolean googleSignInStatus) {
        getBinding().contentIncluded.btnSignInGoogle.setVisibility(googleSignInStatus ? View.VISIBLE : View.INVISIBLE);
        getBinding().contentIncluded.tvSignGoogle.setVisibility(googleSignInStatus ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean emailErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilEmail.setError(getString(R.string.email_not_valid));
        } else {
            getBinding().contentIncluded.tilEmail.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.LOG_IN;
    }

    @Override
    public boolean passwordErrorEnabled(List<PasswordUtil.PasswordHint> hints) {

        boolean errorEnabled = !hints.isEmpty();
        if (errorEnabled) {
            StringBuilder stringBuilder = new StringBuilder();
            String separator = getString(R.string.hint_separator);
            for (PasswordUtil.PasswordHint hint : hints) {
                stringBuilder.append(separator).append(" ").append(getString(hint.getHintRes())).append(" ");
            }
            getBinding().contentIncluded.tilPassword.setError(stringBuilder.toString());
        } else {
            getBinding().contentIncluded.tilPassword.setErrorEnabled(false);
        }
        return errorEnabled;
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getBinding().contentIncluded.btnSignInGoogle.setEnabled(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        getBinding().contentIncluded.btnSignInGoogle.setEnabled(false);
                    }
                })
                .build();
        getBinding().contentIncluded.btnSignInGoogle.setEnabled(false);
        getBinding().contentIncluded.btnSignInGoogle.setOnClickListener(view -> {
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                return;
            }
            mGoogleApiClient.clearDefaultAccountAndReconnect();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();

        if (mGoogleApiClient != null) {
            try {
                mGoogleApiClient.disconnect();
            } catch (RuntimeException e) {
                Log.e(TAG, new LogErrorException("Error disconnecting Google Api Client", e));
            }
        }
    }

    private Intent getAfterSignInIntent() {
        return getIntent().getExtras() != null ? (Intent) getIntent().getExtras().get(AppConstants.EXTRA_INTENT) : null;
    }

    @Override
    public void goToDashboard() {
        if (mPresenter.isGuestFlowActiveAfterLogin()) {
            mPresenter.stopGuestFlowAndStartLoyaltyUserFlow();
        } else {
            Intent intent = MainActivity.createIntent(this, getAfterSignInIntent());
            intent.putExtra(AppConstants.EXTRA_IS_FROM_SIGN_IN, true);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void setModel(CredentialsModel model) {
        getBinding().contentIncluded.setModel(model);
    }

    @Override
    public void goToJoinNow() {
        startActivityForResult(new Intent(SignInActivity.this, SignUpActivity.class), REQUEST_CODE_SIGN_UP);
        if (mPresenter.isThisGuestFlow()) {
            finish();
        }

    }

    @Override
    public void showErrorSignIn() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.oops)
                .setMessage(R.string.bad_credentials)
                .setNeutralButton(R.string.log_in, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.sign_up, (dialog, which) -> goToJoinNow())
                .setPositiveButton(R.string.cancel, (dialog, which) -> finish()).show();
    }

    @Override
    public void showErrorTooManyAttemptsSignIn() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.oops)
                .setMessage(R.string.error_too_many_sign_in_attempts)
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss()).show();
    }

    @Override
    public void goToSignUpWithGoogle(String email) {
        startActivity(SignUpPersonalInfoActivity.createIntent(this, true, email));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN && resultCode == RESULT_OK && data != null) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        } else if (requestCode == FORGOT_PASSWORD_CODE && resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK, data);
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showExtraMessage(Intent data) {
        String message = data.getStringExtra(AppConstants.EXTRA_MESSAGE);
        if (message == null) {
            return;
        }
        showMessage(message);
    }

    /**
     * This method should reside in the presenter, but while we don't actually do anything with
     * the data returned, it stays here for clarity
     *
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        if (result == null) {
            showWarning(R.string.error_while_signing_in);
            return;
        }
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            new GetAccessTokenAsyncTask(mPresenter, this, this).execute(result);
        } else {
            Log.e(TAG, new LogErrorException(StringUtils.format("Result status: %s", result.getStatus())));
        }
    }

    private static class GetAccessTokenAsyncTask extends AsyncTask<GoogleSignInResult, Void, String> {

        private final WeakReference<SignInContract.Presenter> mPresenter;
        private final WeakReference<Context> mContext;
        private final WeakReference<SignInContract.View> mView;
        private String mEmail = null;

        GetAccessTokenAsyncTask(SignInContract.Presenter presenter, SignInContract.View view, Context context) {
            mPresenter = new WeakReference<>(presenter);
            mContext = new WeakReference<>(context);
            mView = new WeakReference<>(view);
        }

        @Override
        protected String doInBackground(GoogleSignInResult... googleSignInResults) {
            Context context;
            if ((context = mContext.get()) == null) {
                return null;
            }

            String accessToken = null;
            try {
                mEmail = googleSignInResults[0].getSignInAccount().getEmail();
                Account accountDetails = new Account(mEmail, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                accessToken = GoogleAuthUtil.getToken(context, accountDetails, "oauth2:profile email");
            } catch (Exception e) {
                if (mView.get() == null) {
                    return null;
                }
                mView.get().showError(e);
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(String accessToken) {
            SignInContract.View view;
            SignInContract.Presenter presenter;
            if ((view = mView.get()) == null) {
                return;
            }
            if ((presenter = mPresenter.get()) == null || accessToken == null) {
                view.showWarning(R.string.error_while_signing_in);
                return;
            }
            presenter.setUpGoogleSignIn(mEmail, accessToken);
        }

    }
}
