package caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.view;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.EXTRA_CHECKIN_REWARD_SELECTED;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.common.LocationAwareActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityPickLocationBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationDetailsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationVerticalListAdapter;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.PickLocationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model.PickLocationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.presenter.PickLocationPresenter;
import caribouapp.caribou.com.cariboucoffee.util.GeoUtil;
import icepick.Icepick;
import icepick.State;

public class PickLocationActivity extends LocationAwareActivity<ActivityPickLocationBinding>
        implements LocationVerticalListAdapter.LocationListListener, PickLocationContract.View {

    private static final String TAG = PickLocationActivity.class.getName();
    @State
    PickLocationModel mModel;
    private LocationVerticalListAdapter mRecentLocationsListAdapter;
    private LocationVerticalListAdapter mNearbyLocationsListAdapter;
    private PickLocationContract.Presenter mPresenter;

    @Inject
    OrderNavHelper mOrderNavHelper;

    public static Intent createIntent(Context context, RewardItemModel rewardItemModel) {
        Intent intent = new Intent(context, PickLocationActivity.class);
        intent.putExtra(EXTRA_CHECKIN_REWARD_SELECTED, rewardItemModel);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pick_location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        SourceApplication.get(this).getComponent().inject(this);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            mModel = new PickLocationModel();
        }

        mRecentLocationsListAdapter = new LocationVerticalListAdapter(LocationVerticalListAdapter.LocationListType.CHOOSE_LOCATION) {
            @Override
            protected int getRowContentDescriptionStringRes() {
                return R.string.recent_order_location_row_content_description;
            }
        };
        getBinding().contentIncluded.rvRecentLocationsList.setAdapter(mRecentLocationsListAdapter);
        getBinding().contentIncluded.rvRecentLocationsList.setNestedScrollingEnabled(false);
        getBinding().contentIncluded.rvRecentLocationsList
                .setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecentLocationsListAdapter.setListener(this);

        getBinding().contentIncluded.btnSearchWalkLocations.setOnClickListener(v -> goToLocationSearch());

        mNearbyLocationsListAdapter = new LocationVerticalListAdapter(LocationVerticalListAdapter.LocationListType.CHOOSE_LOCATION) {
            @Override
            protected int getRowContentDescriptionStringRes() {
                return R.string.nearby_location_content_description;
            }
        };
        getBinding().contentIncluded.rvNearbyLocationsList.setAdapter(mNearbyLocationsListAdapter);
        getBinding().contentIncluded.rvNearbyLocationsList.setNestedScrollingEnabled(false);
        getBinding().contentIncluded.rvNearbyLocationsList
                .setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mNearbyLocationsListAdapter.setListener(this);

        // Setup presenter
        PickLocationPresenter pickLocationPresenter = new PickLocationPresenter(this);
        SourceApplication.get(this).getComponent().inject(pickLocationPresenter);
        mPresenter = pickLocationPresenter;
        mPresenter.setModel(mModel);
        mModel.setFinishedLoading(false);

        getBinding().contentIncluded.setModel(mModel);

        getBinding().contentIncluded.btnEnableLocationServices.setOnClickListener(v -> {
            showLoadingLayer();
            checkLocationServices();
        });
        getBinding().contentIncluded.sv.setPresenter(mPresenter);
        getBinding().contentIncluded.sv.setHint(R.string.location_search_hint);
        getBinding().contentIncluded.sv.setContentDescription(R.string.search_location_field_content_description);

        //We don't want to ask for permission, until user try to enable location services
        setAskForLocationPermission(false);

        // This checkForGPSStatus is needed to check if gps is down, so that the screen loads the recent locations
        if (isGpsEnable(this)
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            // hidden when displayLocations is called
            showLoadingLayer();
            startCurrentLocationRequest();
            mModel.setLocationServicesAvailable(true);
        } else {
            mModel.setLocationServicesAvailable(false);
            mModel.setFinishedLoading(true);
        }

        mPresenter.loadRecentLocations();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onLocationPermissionDenied() {
        super.onLocationPermissionDenied();
        mModel.setLocationServicesAvailable(false);
        hideLoadingLayer();
    }

    @Override
    public void openLocationDetails(StoreLocation storeLocation) {
        // NO-OP
    }

    @Override
    public void chooseLocation(StoreLocation storeLocation) {
        mPresenter.startNewOrder(storeLocation.getId());
    }

    @Override
    public void viewLocationOnMap(StoreLocation storeLocation) {
        startActivityForResult(LocationDetailsActivity
                .createIntentForNewOrder(this, storeLocation), AppConstants.REQUEST_CODE_NEW_ORDER_LOCATION_DETAILS);
    }

    @Override
    public void displayLocations() {
        mNearbyLocationsListAdapter.setData(mModel.getNearbyStores());
        if (mModel.getRecentStores().size() > 0) {
            mRecentLocationsListAdapter.setData(mModel.getRecentStores());
        }
        hideLoadingLayer();
    }

    @Override
    public void checkLocationServices() {
        if (ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            AppConstants.REQUEST_CODE_LOCATION_PERMISSION);
            return;
        }
        checkForGPSStatus(locationSettingsResponse -> tryToGetCurrentLocation());

    }

    @Override
    public void goToLocationSearch(String searchText) {
        startActivity(LocationsActivity.createIntent(this, searchText, getRewardPreselected()));
    }

    @Override
    public void setLocationHeader(String headerText) {
        if (headerText == null || headerText.isEmpty()) {
            return;
        }
        getBinding().contentIncluded.tvPickLocationHeader.setVisibility(View.VISIBLE);
        getBinding().contentIncluded.tvPickLocationHeader.setText(headerText);
        getBinding().contentIncluded.tvPickLocationHeader.setContentDescription(getString(R.string.heading_cd, headerText));
    }

    private void goToLocationSearch() {
        startActivity(LocationsActivity.createIntent(this));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.CHOOSE_LOCATION;
    }

    @Override
    public void onLocationChanged(Location location) {
        unsubscribeFromLocationRequest();
        mModel.setLocationServicesAvailable(true);
        mModel.setLocationAvailable(true);
        if (mModel.getNearbyStores().isEmpty()) {
            mModel.setFinishedLoading(false);
            mPresenter.loadNearbyLocations(GeoUtil.toLatLng(location));
            mPresenter.loadRecentLocations();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_NEW_ORDER_LOCATION_DETAILS
                && resultCode == AppConstants.RESULT_CODE_BACK_TO_DASHBOARD) {
            finish();
        } else if (requestCode == AppConstants.REQUEST_CODE_LOCATION_SERVICES) {
            if (resultCode == RESULT_CANCELED) {
                hideLoadingLayer(); //This means the user decided not to turn on location services
            } else {
                tryToGetCurrentLocation();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void goToProductMenu(StoreLocation storeLocation) {
        finish();
        startActivity(mOrderNavHelper.createIntentStartOrder(this, storeLocation, MenuActivity.MenuOrigin.OTHER, getRewardPreselected()));
    }

    @Override
    public void createOrder(StoreLocation storeLocation) {
        mPresenter.createOrder(storeLocation);
    }

    @Override
    public void updateOrderAheadEnabled(boolean orderAhead) {
        // NO-OP
    }

    @Override
    public void showStoreNotAvailableDialog() {
        DialogUtil.storeNotAvailableDialog(this);
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
    public void showStoreTempOff() {
        DialogUtil.storeTemporaryOff(this);
    }

    @Override
    public void showStoreNotOrderOutOfStore() {
        DialogUtil.showStoreNotOrderOutOfStoreAlert(this, this::finish);
    }

    public RewardItemModel getRewardPreselected() {
        return (RewardItemModel) getIntent().getSerializableExtra(EXTRA_CHECKIN_REWARD_SELECTED);
    }

    @Override
    public void onLocationFailed() {
        unsubscribeFromLocationRequest();
        mModel.setLocationAvailable(false);
        mPresenter.loadRecentLocations();
    }

    @Override
    protected void setLoadingState(boolean showing) {
        super.setLoadingState(showing);
        if (!showing) {
            getBinding().contentIncluded.sv.getBinding().etSearchText.requestFocus();
        }
    }
}
