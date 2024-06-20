package caribouapp.caribou.com.cariboucoffee.common;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_LOCATION_SERVICES;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.ViewDataBinding;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.urbanairship.UAirship;
import com.urbanairship.analytics.location.LocationEvent;

import java.lang.ref.WeakReference;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by andressegurola on 10/24/17.
 */

public abstract class LocationAwareActivity<T extends ViewDataBinding> extends BaseActivity<T> implements LocationAwareListener {

    private static final String TAG = LocationAwareActivity.class.getSimpleName();
    private static final long MAX_WAIT_TIME = 6000;

    private SafeLocationListener mSafeLocationListener;
    private boolean mAskForLocationPermission = true;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks mApiCallback = new GoogleApiClient.ConnectionCallbacks() {
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "Google API Client connected");
            tryToGetCurrentLocation();
        }

        public void onConnectionSuspended(int i) {
            Log.e(TAG, new LogErrorException("onConnectionSuspended i: " + i));
            showWarning(R.string.problems_getting_current_location);
        }
    };
    private GoogleApiClient.OnConnectionFailedListener mApiFailedListener = connectionResult -> {
        Log.e(TAG,
                new LogErrorException("onConnectionFailed connectionResult: "
                        + connectionResult.getErrorCode() + "-" + connectionResult.getErrorMessage()));
        showWarning(R.string.problems_getting_current_location);
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSafeLocationListener = new SafeLocationListener(this, new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getBinding() == null) {
            return;
        }
        Toolbar toolbar = getBinding().getRoot().findViewById(R.id.tb);
        if (toolbar != null) {
            toolbar.setNavigationContentDescription(R.string.go_back_cd);
        }
    }

    public void startCurrentLocationRequest() {
        if (!isGooglePlayServicesAvailable(this)) {
            return;
        }

        // Create an instance of GoogleAPIClient.
        createGoogleApi();
        if (checkLocationPermission()) {
            onLocationPermissionGranted();
        }
    }

    protected boolean checkLocationPermission() {
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isGooglePlayServicesAvailable(android.app.Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, AppConstants.REQUEST_CODE_CHECK_PLAY_SERVICES, dialog -> finish()).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                onLocationPermissionGranted();
            } else {
                onLocationPermissionDenied();
            }
        }
    }

    protected void onLocationPermissionDenied() {

    }

    protected void onLocationPermissionGranted() {
        Log.d(TAG, "onLocationPermissionGranted()");
        tryToGetCurrentLocation();
    }

    private void createGoogleApi() {
        if (mGoogleApiClient == null) {
            Log.d(TAG, "Building Google API Client");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            connectApi();
        }
    }

    protected void tryToGetCurrentLocation() {
        if (!checkLocationPermission()) {
            Log.d(TAG, "No Location permissions");
            if (mAskForLocationPermission) {
                Log.d(TAG, "Asking for Location permissions");
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.REQUEST_CODE_LOCATION_PERMISSION);
                onLocationPermissionRequested();
            }
            return;
        }
        if (mGoogleApiClient == null) {
            createGoogleApi();
            return;
        }

        if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Google API client not yet connected");
            return;
        }

        Log.d(TAG, "Fire requestLocationUpdates()");
        createLocationRequest();
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
                createLocationRequest(), mSafeLocationListener, Looper.myLooper());

    }

    protected void onLocationPermissionRequested() {
        // NO-OP
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest
                .create()
                .setPriority(AppUtils.isProductionBuild()
                        ? LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY : LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(4000)
                .setMaxWaitTime(MAX_WAIT_TIME)
                .setFastestInterval(100);
    }

    protected void unsubscribeFromLocationRequest() {
        if (mGoogleApiClient.isConnected() && mSafeLocationListener != null) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mSafeLocationListener);
        }
    }

    protected void onStart() {
        connectApi();
        super.onStart();
    }

    private void connectApi() {
        if (mGoogleApiClient == null || mGoogleApiClient.isConnected()) {
            return;
        }
        mGoogleApiClient.registerConnectionCallbacks(mApiCallback);
        mGoogleApiClient.registerConnectionFailedListener(mApiFailedListener);
        mGoogleApiClient.connect();
    }

    protected boolean isGpsEnable(Context context) {
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, e);
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    protected void checkForGPSStatus(OnSuccessListener<LocationSettingsResponse> successListener) {
        //https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnSuccessListener(this, successListener);

        result.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;

                    resolvable.startResolutionForResult(
                            LocationAwareActivity.this,
                            REQUEST_CODE_LOCATION_SERVICES);
                } catch (IntentSender.SendIntentException sendEx) {
                    showMessage(getString(R.string.unknown_error));
                }
            }
        });
    }

    protected void onStop() {
        super.onStop();
        try {
            mSafeLocationListener.disposeHandler();

            if (mGoogleApiClient == null) {
                return;
            }

            if (mGoogleApiClient.isConnected() && mSafeLocationListener != null) {
                LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mSafeLocationListener);
            }
            mGoogleApiClient.unregisterConnectionCallbacks(mApiCallback);
            mGoogleApiClient.unregisterConnectionFailedListener(mApiFailedListener);
            mGoogleApiClient.disconnect();
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Problems disconnecting Google API Client", e));
        }
    }

    // NOTE: This is a workaround to prevent memory leaks in the Android sdk.
    // It was based on https://code.luasoftware.com/tutorials/android/how-to-fix-memory-leaks-in-android/
    private static class SafeLocationListener extends LocationCallback {
        private final WeakReference<LocationAwareActivity> mRef;
        private final Handler mHandler;
        private Runnable mLocationUnavailableRunnable;

        SafeLocationListener(LocationAwareActivity instance, Handler handler) {
            mRef = new WeakReference<>(instance);
            mHandler = handler;
        }

        public void disposeHandler() {
            mHandler.removeCallbacks(mLocationUnavailableRunnable);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            mHandler.removeCallbacks(mLocationUnavailableRunnable);
            mLocationUnavailableRunnable = null;

            LocationAwareActivity instance = mRef.get();
            if (instance == null) {
                Log.w(TAG, "onLocationChanged data discarded.");
                return;
            }

            Location location = locationResult.getLastLocation();
            recordUALocation(location);

            if (!AppUtils.isProductionBuild()) {
                // We fake the current gps location for non Prod builds
                setMockedGPSCoordinates(location);
            }
            Log.d(TAG, "calling onLocationChanged");
            instance.onLocationChanged(location);
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            Log.d(TAG, "onLocationAvailability, isLocationAvailable = " + locationAvailability.isLocationAvailable());
            if (!locationAvailability.isLocationAvailable()) {
                Log.e(TAG, new LogErrorException("Problems loading user current location, current location not available"));
                if (mLocationUnavailableRunnable == null) {
                    Log.d(TAG, "Scheduled location not available");
                    // This "scheduling" shouldn't be needed and until recently it wasn't.
                    // It's required as a workaround of a bug in google play services
                    // https://issuetracker.google.com/issues/198176818
                    mLocationUnavailableRunnable = () -> {
                        Log.e(TAG, new LogErrorException("Problems loading user current location, current location not available"));
                        LocationAwareActivity instance = mRef.get();
                        if (instance != null) {
                            instance.onLocationFailed();
                        }
                    };
                    mHandler.postDelayed(mLocationUnavailableRunnable, MAX_WAIT_TIME);
                }
            }
        }
    }

    public void setAskForLocationPermission(boolean askForLocationPermission) {
        mAskForLocationPermission = askForLocationPermission;
    }

    private static void recordUALocation(Location location) {
        if (location == null) {
            return;
        }
        UAirship.shared().getAnalytics().addEvent(
                new LocationEvent(location, LocationEvent.UPDATE_TYPE_SINGLE,
                        -1,
                        -1,
                        true));
    }

    private static void setMockedGPSCoordinates(Location location) {
        // White Bear Lake Store
        // location.setLatitude(45.0853814);
        // location.setLongitude(-93.0081479);

        if (AppUtils.getBrand() == BrandEnum.NNYB_BRAND) {
            // Store Manhattan Beach Naohs 2101
            // location.setLatitude(33.8851819);
            // location.setLongitude(-118.4087701);

            // Store Caramillo Noahs Los Angeles 2153
            location.setLatitude(34.2224796);
            location.setLongitude(-119.0677147);
        } else if (AppUtils.getBrand() == BrandEnum.EBB_BRAND) {
            // Store Colfax & Indiana 3099
            location.setLatitude(39.7338693);
            location.setLongitude(-105.1631624);
        } else {
            // Caribou HQ
            location.setLatitude(45.04380161);
            location.setLongitude(-93.33053046);
        }
    }
}

