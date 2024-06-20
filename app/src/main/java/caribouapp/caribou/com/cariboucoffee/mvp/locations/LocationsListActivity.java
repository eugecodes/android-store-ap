package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityLocationsListBinding;
import icepick.Icepick;
import icepick.State;

public class LocationsListActivity extends BaseActivity<ActivityLocationsListBinding>
        implements LocationsContract.View, LocationVerticalListAdapter.LocationListListener, LocationsFilterListener {


    public static Intent createIntent(Context context, LocationsModel model) {
        Intent intent = new Intent(context, LocationsListActivity.class);
        intent.putExtra(AppConstants.EXTRA_LOCATIONS_SEARCH_MODEL, model);
        return intent;
    }

    private LocationVerticalListAdapter mLocationsAdapter;

    private LocationsPresenter mPresenter;

    @State
    LocationsModel mModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_locations_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        // Setup toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (mModel == null) {
            mModel = (LocationsModel) getIntent().getSerializableExtra(AppConstants.EXTRA_LOCATIONS_SEARCH_MODEL);
        }

        mLocationsAdapter = new LocationVerticalListAdapter(LocationVerticalListAdapter.LocationListType.TOUCH_TO_OPEN) {
            @Override
            protected int getRowContentDescriptionStringRes() {
                return R.string.location_row_content_description;
            }
        };
        getBinding().contentIncluded.rvLocationsList.setAdapter(mLocationsAdapter);
        getBinding().contentIncluded.rvLocationsList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mLocationsAdapter.setListener(this);

        // Setup presenter
        LocationsPresenter locationsPresenter = new LocationsPresenter(this);
        SourceApplication.get(this).getComponent().inject(locationsPresenter);
        mPresenter = locationsPresenter;
        getBinding().contentIncluded.searchView.setPresenter(mPresenter);
        mPresenter.setModel(mModel);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locations_list_menu, menu);
        MenuItem mapItem = menu.findItem(R.id.action_map);
        if (mapItem != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mapItem.setContentDescription(getResources().getString(R.string.button_cd,
                        getResources().getString(R.string.action_map_cd)));

            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            goBack();
            return true;
        } else if (itemId == R.id.action_map) {
            goBack();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Intent data = new Intent();
        data.putExtra(AppConstants.EXTRA_LOCATIONS_SEARCH_MODEL, mPresenter.getModel());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void displayStoreLocations(List<StoreLocation> stores, boolean includeCurrentLocation) {
        clearErrorMessage();
        mLocationsAdapter.setData(stores);
    }

    @Override
    public void setCurrentLocationName(String locationName) {
        getBinding().contentIncluded.searchView.setText(locationName);
    }

    @Override
    public void displayNoStoresForSearch(LatLng searchLatLng, boolean filterIsHidingLocations) {
        if (filterIsHidingLocations) {
            displayError(R.string.no_stores_matching_filters);
        } else {
            displayError(R.string.no_stores_on_location);
        }
    }

    @Override
    public void displayNoLocationForName() {
        displayError(R.string.location_not_recognized);
    }

    private void displayError(@StringRes int messageRes) {
        mLocationsAdapter.clearData();
        getBinding().contentIncluded.cvErrorCard.setVisibility(View.VISIBLE);
        getBinding().contentIncluded.tvErrorMessage.setText(messageRes);
    }

    @Override
    public void displaySelectedStore(StoreLocation store) {
        // NO-OP
    }

    @Override
    public void clearErrorMessage() {
        getBinding().contentIncluded.cvErrorCard.setVisibility(View.GONE);
    }

    @Override
    public void showFilterDialog(Set<StoreFeature> storeFeatureFilter) {
        LocationsFilterFragmentDialog dialog = LocationsFilterFragmentDialogBuilder
                .newLocationsFilterFragmentDialog((HashSet<StoreFeature>) storeFeatureFilter);
        dialog.show(this.getSupportFragmentManager(), AppConstants.FILTER_DIALOG_FRAGMENT);
    }

    @Override
    public void askForLoginOrSignup() {
        //NO-OP
    }

    @Override
    public void openLocationDetails(StoreLocation storeLocation) {
        startActivity(LocationDetailsActivity.createIntent(this, storeLocation));
    }

    @Override
    public void chooseLocation(StoreLocation storeLocation) {
        mPresenter.startNewOrder(storeLocation.getId());
    }

    @Override
    public void viewLocationOnMap(StoreLocation storeLocation) {
        // NO-OP
    }

    @Override
    public void applyFilters(Set<StoreFeature> storeFeatures) {
        mPresenter.applyFilter(storeFeatures);
    }

    @Override
    public void goToProductMenu(StoreLocation storeLocation) {
        // NO-OP
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
    public void showStoreTempOff() {
        DialogUtil.storeTemporaryOff(this);
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
}
