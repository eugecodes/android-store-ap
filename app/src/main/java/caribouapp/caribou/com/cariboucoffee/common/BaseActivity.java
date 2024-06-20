package caribouapp.caribou.com.cariboucoffee.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.google.android.material.snackbar.Snackbar;
import com.newrelic.agent.android.NewRelic;
import com.urbanairship.UAirship;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.api.CheckCaptivePortalApi;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.view.CaptivePortalWebActivity;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import icepick.Icepick;
import icepick.State;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by asegurola on 8/31/16.
 */

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements MvpView {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final int SNACKBAR_DELAY_PER_TEXT_LINE = 2400;
    @State
    boolean mIsCaptivePortal = false;
    private int mLoadingCounter = 0;
    private boolean mActive;
    private Handler mHandler;
    private Runnable mHideLoading;
    private T mBinding;
    private InjectionHelper mInjectionHelper;
    private Snackbar mCurrentSnackbar;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mActive = true;
        mInjectionHelper = new InjectionHelper();
        SourceApplication.get(this).getComponent().inject(mInjectionHelper);
        mHandler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppScreen appScreen = getScreenName();
        if (appScreen == null) {
            return;
        }
        trackScreen(appScreen);

    }

    private void trackScreen(AppScreen appScreen) {
        try {
            trackUAScreen(appScreen);
        } catch (RuntimeException e) {
            Log.w(TAG, "Fail to send Urban Airship track screen:" + appScreen.getScreenValue());
        }
        try {
            trackNewRelicScreen(appScreen);
        } catch (RuntimeException e) {
            Log.w(TAG, "Fail to send New relic track screen:" + appScreen.getScreenValue());
        }
    }

    private void trackUAScreen(AppScreen appScreen) {
        UAirship.shared().getAnalytics().trackScreen(appScreen.getScreenValue());
    }

    private void trackNewRelicScreen(AppScreen appScreen) {
        if (NewRelic.isStarted()) {
            NewRelic.recordBreadcrumb(appScreen.getScreenValue());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        mActive = false;
        super.onDestroy();
    }

    @Override
    public void showWarning(@StringRes int messageResId) {
        showWarning(getString(messageResId));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void showWarning(@StringRes int messageResId, Object... args) {
        showWarning(getString(messageResId, args));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SourceApplication.get(this).setCurrentActivity(this);
        if (isCaptivePortalCheckEnabled()) {
            checkCaptivePortal();
        }
        setHomeContentDescription();
    }

    private void setHomeContentDescription() {
        final Toolbar toolbar = getBinding().getRoot().findViewById(R.id.tb);
        if (toolbar != null) {
            toolbar.setNavigationContentDescription(R.string.go_back_cd);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeActionContentDescription(R.string.go_back_cd);
        }
    }

    protected boolean isCaptivePortalCheckEnabled() {
        return true;
    }

    @Override
    public void showWarning(String message) {
        Log.w(TAG, StringUtils.format("Warning message: %s", message));
        showSnackbar(message);
    }

    public void showSnackbar(String message) {
        UIUtil.hideKeyboard(this);
        if (mCurrentSnackbar != null && mCurrentSnackbar.isShown()) {
            mCurrentSnackbar.dismiss();
            mCurrentSnackbar = null;
        }
        View rootSnackbarView = findViewById(R.id.cl_root);
        if (rootSnackbarView == null) {
            rootSnackbarView = findViewById(android.R.id.content);
        }
        Snackbar snackbar = Snackbar
                .make(rootSnackbarView, message, calculateDismissTimeout(message));

        // Set maximum lines for the error message to be displayed.
        View snackbarView = snackbar.getView();
        TextView tv = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setMaxLines(5);
        snackbarView.setOnClickListener(v -> snackbar.dismiss());

        snackbar.show();
        mCurrentSnackbar = snackbar;
    }

    private int calculateDismissTimeout(String message) {
        return Math.max(3000, Math.round(((float) message.length() / 40) * SNACKBAR_DELAY_PER_TEXT_LINE));
    }

    @Override
    public void showMessage(@StringRes int messageId) {
        showMessage(getString(messageId));
    }

    @Override
    public void showMessage(String message) {
        Log.i(TAG, StringUtils.format("Info message: %s", message));
        showSnackbar(message);
    }

    @Override
    public void showDebugDialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).show();
    }

    @Override
    public void showError(Throwable throwable) {
        Log.e(TAG, new LogErrorException("ErrorHandler", throwable));
        if (throwable instanceof UnknownHostException) {
            showWarning(R.string.connection_error);
        } else if (throwable instanceof SocketTimeoutException) {
            showWarning(R.string.request_timeout);
        } else {
            showWarning(R.string.unknown_error);
        }
    }

    public void showLoadingLayer() {
        showLoadingLayer(false);
    }

    public void showLoadingLayer(boolean showProcessingText) {
        Log.d(TAG, "+1 loading");
        runOnUiThread(() -> {
            mLoadingCounter++;
            if (mLoadingCounter == 1) {
                if (mHideLoading != null) {
                    mHandler.removeCallbacks(mHideLoading);
                }
                Log.d(TAG, "showLoadingLayer");
                setLoadingState(true);
            }
            if (showProcessingText && getLoadingView() != null) {
                getLoadingView().showProcessingText();
            }
        });
    }

    protected AppScreen getScreenName() {
        return null;
    }

    private LoadingView getLoadingView() {
        return ((LoadingView) findViewById(R.id.lv));
    }

    public void hideLoadingLayer() {
        Log.d(TAG, "-1 loading");
        runOnUiThread(() -> {
            mLoadingCounter--;
            if (mLoadingCounter < 0) {
                mLoadingCounter = 0;
            }
            if (mLoadingCounter == 0) {
                if (mHideLoading != null) {
                    mHandler.removeCallbacks(mHideLoading);
                }

                mHideLoading = () -> {
                    Log.d(TAG, "hideLoadingLayer");
                    setLoadingState(false);
                };
                mHandler.postDelayed(mHideLoading, 100);
            }
        });
    }

    public boolean isLoading() {
        return mLoadingCounter > 0;
    }

    @Override
    public boolean isActive() {
        return mActive;
    }

    protected void setLoadingState(boolean showing) {
        LoadingView loadingView = getLoadingView();
        if (loadingView != null) {
            if (showing) {
                loadingView.loadLoadingScreen();
            } else {
                loadingView.unloadLoadingScreen();
            }
        }
    }

    public T getBinding() {
        return mBinding;
    }

    private void checkCaptivePortal() {


        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (isConnectedToWiFi(connManager)) {
            mInjectionHelper.getCheckCaptivePortalApi().checkInternetConnectivity().enqueue(
                    new Callback<ResponseBody>() {
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (!isActive()) {
                                return;
                            }
                            if (isPortal(response.code()) || hasBody(response.body())) {
                                //We are in a captive portal so we show the AlertDialog to the user so
                                //he can login in captive portal
                                showCaptivePortalDialog();
                                mIsCaptivePortal = true;
                            } else if (response.code() == 204 && mIsCaptivePortal) {
                                // We are not int captive portal, but before we were in one and already accept terms and condition
                                AppUtils.restartAppAtActivity(BaseActivity.this);
                                mIsCaptivePortal = false;
                            } else {
                                //We are not in captive portal and we have internet. Expected behaviour with wifi or mobile data
                                mIsCaptivePortal = false;
                            }

                        }

                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            mIsCaptivePortal = false;
                            Log.e(TAG, new LogErrorException("OnFailure", t));
                        }
                    }
            );
        }


    }

    private boolean isPortal(int responseCode) {
        return responseCode != 204 && (responseCode >= 200) && (responseCode <= 399);
    }

    private boolean isConnectedToWiFi(ConnectivityManager connectivityManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
        } else {
            return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        }
    }

    private boolean hasBody(ResponseBody body) {
        return body != null && body.contentLength() > 0;
    }

    private void showCaptivePortalDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.captive_portal_title)
                .setMessage(getString(R.string.captive_portal_body))
                .setNeutralButton(R.string.captive_portal_accept, (dialog, which) -> openBrowserForCaptivePortal())
                .setCancelable(false)
                .show();
    }

    private void openBrowserForCaptivePortal() {
        Intent captivePortalIntent = new Intent(this, CaptivePortalWebActivity.class);
        startActivity(captivePortalIntent);
    }

    public static class InjectionHelper {
        @Inject
        CheckCaptivePortalApi mCheckCaptivePortalApi;

        public CheckCaptivePortalApi getCheckCaptivePortalApi() {
            return mCheckCaptivePortalApi;
        }

        public void setCheckCaptivePortalApi(CheckCaptivePortalApi checkCaptivePortalApi) {
            mCheckCaptivePortalApi = checkCaptivePortalApi;
        }
    }
}
