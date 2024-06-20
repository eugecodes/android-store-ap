package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivitySignUpBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SignUpPresenterContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.SignUpPresenter;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_GOOGLE_SIGN_IN;

public class SignUpActivity extends BaseActivity<ActivitySignUpBinding>
        implements SignUpPresenterContract.View, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private SignUpPresenterContract.Presenter mPresenter;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().tb.getNavigationIcon()
                .setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primaryMidDarkColor), PorterDuff.Mode.SRC_ATOP);


        SignUpPresenter signUpPresenter = new SignUpPresenter(this);
        SourceApplication.get(this).getComponent().inject(signUpPresenter);
        mPresenter = signUpPresenter;


        boolean googleEnabled = mPresenter.isGoogleSignInEnabled();
        if (googleEnabled) {
            setupGoogleSignIn();
        }
        getBinding().contentIncluded.btnSignUpWithEmail.setVisibility(googleEnabled ? View.VISIBLE : View.INVISIBLE);
        getBinding().contentIncluded.tvOr.setVisibility(googleEnabled ? View.VISIBLE : View.INVISIBLE);
        getBinding().contentIncluded.btnSignUpWithEmail.setOnClickListener((view) -> mPresenter.doEmailSignup());
        getBinding().contentIncluded.tvGoToSignIn.setOnClickListener(view -> goToSignIn());
    }

    private void goToSignIn() {
        startActivity(SignInActivity.createIntent(this));
        finish();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getBinding().contentIncluded.btnGoogleSignUp.setEnabled(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        getBinding().contentIncluded.btnGoogleSignUp.setEnabled(false);
                    }
                })
                .build();

        getBinding().contentIncluded.btnGoogleSignUp.setEnabled(false);
        getBinding().contentIncluded.btnGoogleSignUp.setOnClickListener(view -> {
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                return;
            }
            mGoogleApiClient.clearDefaultAccountAndReconnect();
            startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), REQUEST_CODE_GOOGLE_SIGN_IN);
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String errorMessage = connectionResult.getErrorMessage();
        Log.e(TAG, new LogErrorException("Google sign up error: " + errorMessage));
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = getString(R.string.unknown_error);
        }
        showWarning(errorMessage);
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.SIGN_UP_1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN && resultCode == RESULT_OK && data != null) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        // Fills the email and phone text views
        if (result.getSignInAccount() == null
                || result.getSignInAccount().getEmail() == null
                || result.getSignInAccount().getEmail().isEmpty()) {
            return;
        }
        mPresenter.doGoogleSignup(result.getSignInAccount().getEmail());
    }

    @Override
    public void goToPersonalInfoSignUpScreen(String googleEmail) {
        finish();
        startActivity(SignUpPersonalInfoActivity.createIntent(this, googleEmail != null, googleEmail));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
