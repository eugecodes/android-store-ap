package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BrandEnum;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.common.LocationAwareActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityLocationsBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.SignUpActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.GeoUtil;
import caribouapp.caribou.com.cariboucoffee.util.IntentUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import icepick.Icepick;
import icepick.State;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.EXTRA_CHECKIN_REWARD_SELECTED;


public class LocationsActivity extends LocationAwareActivity<ActivityLocationsBinding> implements
        OnMapReadyCallback, LocationsContract.View,
        LocationListAdapter.LocationListListener, LocationsFilterListener {

    private static final String TAG = LocationsActivity.class.getSimpleName();


    private static final float MAX_ZOOM_FOR_MORE_THAN_ONE_RESULT = 19f;
    private static final float MAX_ZOOM_FOR_ONE_RESULT = 16f;
    private static final String EXTRA_SEARCH_TEXT = "search_text";
    private static final String EXTRA_SEARCH_ONLY_OOS = "search_only_oos";
    @State
    LocationsModel mModel;
    private GoogleMap mMap;
    private LocationListAdapter mLocationListAdapter;
    private Set<Marker> mMarkers = new HashSet<>();
    private Map<StoreLocation, Marker> mStoreToMarker = new HashMap<>();
    private LocationsContract.Presenter mPresenter;
    private boolean mFirstTime;
    private LinearLayoutManager mLinearLayoutManager;

    private Marker mPreviouslySelectedMarker;
    private boolean mUserDeniedLocationPermissions;

    @Inject
    OrderNavHelper mOrderNavHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_locations;
    }

    public static Intent createIntent(Context context, String textToSearch, RewardItemModel rewardItemModel) {
        Intent intent = new Intent(context, LocationsActivity.class);
        intent.putExtra(EXTRA_SEARCH_TEXT, textToSearch);
        intent.putExtra(EXTRA_SEARCH_ONLY_OOS, true);
        intent.putExtra(EXTRA_CHECKIN_REWARD_SELECTED, rewardItemModel);
        return intent;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, LocationsActivity.class);
    }

    private boolean isSearchOnlyOos() {
        return getIntent().getBooleanExtra(EXTRA_SEARCH_ONLY_OOS, false);
    }

    private RewardItemModel getRewardItemModel() {
        return (RewardItemModel) getIntent().getSerializableExtra(EXTRA_CHECKIN_REWARD_SELECTED);
    }

    private String getSearchText() {
        return getIntent().getStringExtra(EXTRA_SEARCH_TEXT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SourceApplication.get(this).getComponent().inject(this);

        Icepick.restoreInstanceState(this, savedInstanceState);

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            showLoadingLayer();
            mFirstTime = true;
            mModel = new LocationsModel();
        }

        mLocationListAdapter = new LocationListAdapter();
        mLocationListAdapter.setListener(this);


        RecyclerView rvLocationsList = getBinding().contentIncluded.rvLocationsList;
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvLocationsList.setLayoutManager(mLinearLayoutManager);
        rvLocationsList.setAdapter(mLocationListAdapter);

        rvLocationsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int toSelect = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (toSelect < 0) {
                    return;
                }
                setSelectedLocation(mLocationListAdapter.getItems().get(toSelect));
            }
        });


        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(getBinding().contentIncluded.rvLocationsList);

        LocationsPresenter locationsPresenter = new LocationsPresenter(this);
        SourceApplication.get(this).getComponent().inject(locationsPresenter);
        mPresenter = locationsPresenter;

        getBinding().contentIncluded.searchView.setPresenter(mPresenter);

        if (!isGooglePlayServicesAvailable(this)) {
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        // Show loading layer while we wait for the map fragment to be ready
        showLoadingLayer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.loadOrderData();
    }

    @Override
    public void onLocationChanged(Location location) {
        unsubscribeFromLocationRequest();
        if (mFirstTime) {
            LocationsModel model = mPresenter.getModel();
            double[] currentLocation = GeoUtil.toDoubleArray(location);
            model.setCurrentSearchLocation(currentLocation);
            model.setCurrentLocation(currentLocation);
            zoomToCurrentLocation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locations_menu, menu);
        MenuItem listItem = menu.findItem(R.id.action_list);
        if (listItem != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                listItem.setContentDescription(getString(R.string.button_cd,
                        getString(R.string.action_list_cd)));
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_list) {
            startActivityForResult(LocationsListActivity
                    .createIntent(this, mPresenter.getModel()), AppConstants.REQUEST_CODE_LOCATIONS_LIST_SCREEN);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new LocationMarkerAdapter(this));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMyLocationButtonClickListener(() -> {
            zoomToCurrentLocation();
            return true;
        });
        mMap.setOnMarkerClickListener(marker -> {
            StoreLocation storeLocation = (StoreLocation) marker.getTag();
            setSelectedLocation(storeLocation);
            return true;
        });

        mMap.setOnInfoWindowClickListener(marker -> {
            StoreLocation storeLocation = (StoreLocation) marker.getTag();
            IntentUtil.showDirectionsTo(LocationsActivity.this, new LatLng(storeLocation.getLatitude(), storeLocation.getLongitude()));
        });

        mMap.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.location_list_height));

        checkPermissionAndMap();

        // Hide loading layer for Fragment map creation
        hideLoadingLayer();

        startCurrentLocationRequest();

        mPresenter.setModel(mModel);
        if (isSearchOnlyOos()) {
            mFirstTime = false;
            mPresenter.setSearchForOosStoresMode(getSearchText());
        }

        if (mFirstTime) {
            zoomToCurrentLocation();
        }
        if (mPresenter.isSearchForOosStoresMode()) {
            hideLoadingLayer(); //This is needed in case we come from the
        }
    }

    @Override
    protected AppScreen getScreenName() {
        return isFromStartingOrder() ? AppScreen.LOCATIONS_OA : AppScreen.LOCATIONS;
    }

    private void checkPermissionAndMap() {
        if (mMap == null || ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(GeoUtil.isLocationServicesEnabled(this));
    }

    protected void onLocationPermissionGranted() {
        super.onLocationPermissionGranted();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        checkPermissionAndMap();
    }

    @Override
    public void onLocationPermissionDenied() {
        mUserDeniedLocationPermissions = true;
        if (getSearchText() == null) {
            zoomToCurrentLocation();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void displayStoreLocations(List<StoreLocation> stores, boolean includeCurrentLocation) {
        clearErrorMessage();
        mLocationListAdapter.setData(stores);

        clearMapMarkers();
        StoreLocation firstStore = null;
        Set<LatLng> toInclude = new HashSet<>();
        for (StoreLocation storeLocation : stores) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(storeLocation.getLatitude(), storeLocation.getLongitude()))
                    .title(StringUtils.format("%s\n%s,%s", storeLocation.getName(), storeLocation.getLocatity(), storeLocation.getState()))
                    .icon(BitmapDescriptorFactory
                            .fromResource(
                                    R.drawable.ic_location_pin)));

            if (firstStore == null) {
                firstStore = storeLocation;
            }

            marker.setTag(storeLocation);

            mMarkers.add(marker);
            mStoreToMarker.put(storeLocation, marker);
            toInclude.add(marker.getPosition());
        }

        if (mModel.getSelectedStore() == null) {
            setSelectedLocation(firstStore);
        } else {
            setSelectedLocation(mModel.getSelectedStore());
        }

        UIUtil.hideKeyboard(this);


        double[] currentPos = mPresenter.getModel().getCurrentSearchLocation();

        if (includeCurrentLocation && currentPos != null) {
            toInclude.add(new LatLng(currentPos[0], currentPos[1]));
        }

        mMap.setMaxZoomPreference(stores.size() > 1 ? MAX_ZOOM_FOR_MORE_THAN_ONE_RESULT : MAX_ZOOM_FOR_ONE_RESULT);

        if (!toInclude.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : toInclude) {
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDimensionPixelOffset(R.dimen.large_spacing)));
        }
    }

    @Override
    public void setCurrentLocationName(String locationName) {
        getBinding().contentIncluded.searchView.setText(locationName);
    }

    private void clearMapMarkers() {
        Log.d(TAG, "Markers in memory: " + mMarkers.size());
        mMap.clear();
        mMarkers.clear();
        mStoreToMarker.clear();
        mPreviouslySelectedMarker = null;
    }

    @Override
    public void displayNoStoresForSearch(LatLng searchLatLng, boolean filterIsHidingLocations) {
        clearMapMarkers();
        if (filterIsHidingLocations) {
            displayError(R.string.no_stores_matching_filters);
        } else {
            displayError(R.string.no_stores_on_location);
        }

        if (searchLatLng == null) {
            return;
        }
        mMap.setMaxZoomPreference(16f);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(searchLatLng));
    }

    @Override
    public void displayNoLocationForName() {
        clearMapMarkers();
        displayError(R.string.location_not_recognized);
    }

    @Override
    public void displaySelectedStore(StoreLocation store) {

        // Set map marker
        if (mPreviouslySelectedMarker != null) {
            mPreviouslySelectedMarker.setIcon(BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_location_pin));
        }
        Marker marker = mStoreToMarker.get(store);
        marker.setIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_location_pin_selected));
        marker.showInfoWindow();
        mPreviouslySelectedMarker = marker;


        // Set horizontal scroll for selected store
        int position = mLocationListAdapter.getPosition(store);
        int visiblePosition = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (visiblePosition == position) {
            return;
        }

        getBinding().contentIncluded.rvLocationsList.scrollToPosition(position);
    }

    private void displayError(@StringRes int errorMessage) {
        mLocationListAdapter.clearData();
        getBinding().contentIncluded.cvErrorCard.setVisibility(View.VISIBLE);
        getBinding().contentIncluded.tvErrorMessage.setText(errorMessage);
    }

    @Override
    public void clearErrorMessage() {
        getBinding().contentIncluded.cvErrorCard.setVisibility(View.GONE);
    }

    private void zoomToCurrentLocation() {
        clearErrorMessage();
        boolean isDefaultBrandLocation = false;

        if (mMap == null) {
            return;
        }

        if (!mUserDeniedLocationPermissions
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        boolean locationServicesEnabled = GeoUtil.isLocationServicesEnabled(this);
        LocationsModel locationsModel = mPresenter.getModel();
        if (!mUserDeniedLocationPermissions
                && locationServicesEnabled && locationsModel.getCurrentLocation() == null && locationsModel.isLocationAvailable()) {
            // Don't zoom the map yet, we need to wait for the gps coordinates.
            return;
        }

        if (locationsModel.getCurrentSearchLocation() == null) {
            // If there is no location services enabled and we don't have a current location yet, we default to Caribou HQ
            Log.d(TAG, "Using caribou default location");
            if (AppUtils.getBrand() == BrandEnum.CBOU_BRAND) {
                mPresenter.getModel().setCurrentSearchLocation(AppConstants.DEFAULT_CARIBOU_LOCATION);
            } else {
                mPresenter.getModel().setCurrentSearchLocation(AppConstants.DEFAULT_UNITE_STATES_LOCATION);
                isDefaultBrandLocation = true;
            }
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        hideLoadingLayer();
        mFirstTime = false;

        double[] coordinates = mPresenter.getModel().getCurrentSearchLocation();
        LatLng pos = new LatLng(coordinates[0], coordinates[1]);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,
                isDefaultBrandLocation ? AppConstants.DEFAULT_NO_COORDINATES_MAP_ZOOM : AppConstants.DEFAULT_MAP_ZOOM));

        mPresenter.loadStoreLocationsForCurrentLocation(pos);
    }

    @Override
    public void openLocationDetails(StoreLocation storeLocation) {
        setSelectedLocation(storeLocation);
        startActivity(LocationDetailsActivity.createIntent(this, storeLocation));
    }

    @Override
    public void setSelectedLocation(StoreLocation storeLocation) {
        if (storeLocation == null) {
            return;
        }
        mPresenter.setSelectedStore(storeLocation);
    }

    @Override
    public void startNewOrder(StoreLocation storeLocation) {
        mPresenter.startNewOrder(storeLocation.getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_LOCATIONS_LIST_SCREEN) {
            LocationsModel model = (LocationsModel) data.getSerializableExtra(AppConstants.EXTRA_LOCATIONS_SEARCH_MODEL);
            if (model == null) {
                return;
            }
            mModel = model;
            if (mMap != null) {
                // NOTE: The map was already initialized before. This means that the activity instance is not new.
                // Since the onMapReady is not gonna get called a call to setModel is necessary.
                mPresenter.setModel(mModel);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void applyFilters(final Set<StoreFeature> storeFeatures) {
        mPresenter.applyFilter(storeFeatures);
    }

    @Override
    public void showFilterDialog(Set<StoreFeature> storeFeatureFilter) {
        LocationsFilterFragmentDialog dialog = LocationsFilterFragmentDialogBuilder
                .newLocationsFilterFragmentDialog((HashSet<StoreFeature>) storeFeatureFilter);
        dialog.show(this.getSupportFragmentManager(), AppConstants.FILTER_DIALOG_FRAGMENT);
    }

    @Override
    public void askForLoginOrSignup() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sign_up_log_in)
                .setMessage(R.string.sign_in_sign_up_message)
                .setNeutralButton(R.string.no_thanks, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.log_me_in_im_a_member, (dialog, which) -> goToSignIn())
                .setPositiveButton(R.string.yes_sign_me_up, (dialog, which) -> goToSignUp())
                .show();
    }

    private boolean isFromStartingOrder() {
        return isSearchOnlyOos();
    }

    public void goToSignIn() {
        startActivity(new Intent(this, SignInActivity.class));
    }

    private void goToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    @Override
    public void updateOrderAheadEnabled(boolean orderAheadEnabled) {
        mLocationListAdapter.setOrderAheadEnabled(orderAheadEnabled);
    }

    @Override
    public void goToProductMenu(StoreLocation storeLocation) {
        finish();
        startActivity(mOrderNavHelper
                .createIntentStartOrder(this, storeLocation, MenuActivity.MenuOrigin.OTHER,
                        getRewardItemModel()));
    }

    @Override
    public void createOrder(StoreLocation storeLocation) {
        mPresenter.createOrder(storeLocation);
    }

    @Override
    public void showStoreNotAvailableDialog() {
        DialogUtil.storeNotAvailableDialog(this);
    }

    @Override
    public void showStoreTempOff() {
        DialogUtil.storeTemporaryOff(this);
    }

    @Override
    public void showStoreClosedDialog() {
        DialogUtil.showStoreClosedAlert(this, () -> {
        });
    }

    @Override
    public void showStoreAlmostClosedDialog() {
        DialogUtil.showStoreAlmostClosedAlert(this, () -> {
        });
    }

    @Override
    public void showStoreNotOrderOutOfStore() {
        DialogUtil.showStoreNotOrderOutOfStoreAlert(this, () -> {
        });
    }

    @Override
    public void onLocationFailed() {
        unsubscribeFromLocationRequest();
        mPresenter.getModel().setLocationAvailable(false);
        zoomToCurrentLocation();
    }
}
