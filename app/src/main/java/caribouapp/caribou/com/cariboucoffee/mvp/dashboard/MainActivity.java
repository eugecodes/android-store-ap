package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.DeeplinkParser;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.LocationAwareActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityMainBinding;

public class MainActivity extends LocationAwareActivity<ActivityMainBinding> {

    private static final String DEEPLINK_HOST = "deeplink";

    public static final String TAG = MainActivity.class.getSimpleName();

    public static Intent createIntent(Context context, Intent deeplinkIntent) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppConstants.EXTRA_INTENT, deeplinkIntent);
        return intent;
    }

    private Intent mDeeplinkIntent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private SplashFragment mSplashFragment;

    private DashboardFragment mDashboardFragment;

    private Intent getDeeplinkIntent(Intent intent) {
        return intent.getExtras() != null ? (Intent) intent.getExtras().get(AppConstants.EXTRA_INTENT) : null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        processDeeplinkIntent(getIntent());

        mSplashFragment = new SplashFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mSplashFragment).commit();
    }

    @Override
    protected void onLocationPermissionRequested() {
        setAskForLocationPermission(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent);
        processDeeplinkIntent(intent);
        processIntent(intent);
    }

    private boolean isDeeplink(Intent intent) {
        Log.d(TAG, "isDeeplink intent?: " + intent);
        return intent != null && intent.getData() != null && (DEEPLINK_HOST.equals(intent.getData().getHost())
                | getString(R.string.rewards_host).equals(intent.getData().getHost()));
    }

    private void processDeeplinkIntent(Intent intent) {
        Intent deeplinkIntent = getDeeplinkIntent(intent);
        if (deeplinkIntent != null) {
            intent.getExtras().remove(AppConstants.EXTRA_INTENT);
            intent = deeplinkIntent;
        }

        mDeeplinkIntent = isDeeplink(intent) ? intent : null;
    }

    private void processIntent(Intent intent) {
        if (isFromSignIn(intent)) {
            closeDrawer();
        }
    }

    private void closeDrawer() {
        if (mDashboardFragment != null && mDashboardFragment.isVisible()) {
            mDashboardFragment.closeDrawer();
        }
    }

    private boolean isFromSignIn(Intent intent) {
        return intent.getBooleanExtra(AppConstants.EXTRA_IS_FROM_SIGN_IN, false);
    }

    @Override
    public void onBackPressed() {
        if (mDashboardFragment != null && mDashboardFragment.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.post(this::executeDeeplink);
    }

    public void executeDeeplink() {
        if (mDeeplinkIntent == null || mDashboardFragment == null) {
            return;
        }
        DeeplinkParser deepLinkParser = new DeeplinkParser();
        SourceApplication.get(this).getComponent().inject(deepLinkParser);
        deepLinkParser.openDeepLink(this, mDeeplinkIntent.getData());

        mDeeplinkIntent = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mDashboardFragment == null) {
            Log.e(TAG, "Got location data before initializing dashboard.");
            return;
        }
        mDashboardFragment.onLocationChanged(location);
        unsubscribeFromLocationRequest();
    }

    public void startupFinished() {
        mDashboardFragment = new DashboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mDashboardFragment).commitAllowingStateLoss();
        executeDeeplink();
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.HOME_SCREEN;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION_SERVICES && mDashboardFragment != null) {
            mDashboardFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLocationFailed() {
        unsubscribeFromLocationRequest();
    }
}
