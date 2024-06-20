package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTimeConstants;

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
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutScheduleLocationDetailsBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutStoreFeatureItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;

/**
 * Created by jmsmuy on 10/9/17.
 */

public class LocationDetailsActivity extends BaseActivity<ViewDataBinding> implements LocationDetailsContract.View, OnMapReadyCallback {
    private static final String EXTRA_STORE_LOCATION = "extra_store_location";
    private static final String EXTRA_NEW_ORDER_LOCATION_PREVIEW = "extra_new_order_preview";
    @Inject
    OrderNavHelper mOrderNavHelper;
    @Inject
    Clock mClock;
    private LocationDetailsContract.Presenter mPresenter;
    private GoogleMap mMap;
    private final Set<Marker> mMarkers = new HashSet<>();
    private Map<StoreFeature, LayoutStoreFeatureItemBinding> mStoreFeatureBindings;

    public static Intent createIntent(Context context, StoreLocation storeLocation) {
        Intent intent = new Intent(context, LocationDetailsActivity.class);
        intent.putExtra(EXTRA_STORE_LOCATION, storeLocation);
        return intent;
    }

    public static Intent createIntentForNewOrder(Context context, StoreLocation storeLocation) {
        Intent intent = new Intent(context, LocationDetailsActivity.class);
        intent.putExtra(EXTRA_STORE_LOCATION, storeLocation);
        intent.putExtra(EXTRA_NEW_ORDER_LOCATION_PREVIEW, true);
        return intent;
    }

    public StoreLocation getStoreLocationFromIntent() {
        return (StoreLocation) getIntent().getSerializableExtra(EXTRA_STORE_LOCATION);
    }

    public boolean isNewOrderMode() {
        return getIntent().getBooleanExtra(EXTRA_NEW_ORDER_LOCATION_PREVIEW, false);
    }

    @Override
    protected int getLayoutId() {
        return isNewOrderMode() ? R.layout.activity_location_new_order_details : R.layout.activity_location_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SourceApplication.get(this).getComponent().inject(this);

        LocationDetailsPresenter presenter = new LocationDetailsPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        setSupportActionBar(getBinding().getRoot().findViewById(R.id.tb));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        mapFragment.getMapAsync(this);

        initStoreFeatures();
        StoreLocation storeLocation = getStoreLocationFromIntent();
        mPresenter.setStoreLocation(storeLocation);
        getBinding().setVariable(BR.model, mPresenter.getStoreLocation());
        getBinding().setVariable(BR.newOrderMode, isNewOrderMode());

        mPresenter.init(isNewOrderMode());

        Button btnStartOrder = findViewById(R.id.btn_start_order);
        if (btnStartOrder != null) {
            btnStartOrder.setOnClickListener(v -> mPresenter.startNewOrder(storeLocation.getId()));
        }
        TextView storeOpenHours = findViewById(R.id.include_location_info).findViewById(R.id.tv_store_hours);
        if (storeLocation.getOpenHourSchedule().isEmpty()) {
            storeOpenHours.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected AppScreen getScreenName() {
        return isNewOrderMode() ? AppScreen.STORE_DETAIL_OA : AppScreen.STORE_DETAIL;
    }

    private void initStoreFeatures() {
        FlexboxLayout container = findViewById(R.id.fl_store_features_container);

        mStoreFeatureBindings = new HashMap<>();
        for (StoreFeature storeFeature : StoreFeature.values()) {
            LayoutStoreFeatureItemBinding binding = LayoutStoreFeatureItemBinding.inflate(getLayoutInflater(), container, true);
            binding.tvStoreFeature.setText(storeFeature.getNameRes());
            binding.tvStoreFeature.setEnabled(false);
            binding.getRoot().setVisibility(View.GONE);

            mStoreFeatureBindings.put(storeFeature, binding);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isNewOrderMode()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.locations_details_menu, menu);
            MenuItem listItem = menu.findItem(R.id.action_settings);
            if (listItem != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    listItem.setContentDescription(getString(R.string.menu_favorite_cd));
                }
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMarkers.clear();

        LatLng storePosition = mPresenter.getStorePosition();

        mMarkers.add(mMap.addMarker(new MarkerOptions()
                .position(storePosition)
                .title(mPresenter.getStoreName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_pin_selected))));

        onLocationPermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                onLocationPermissionGranted();
            }
        }
    }

    private void onLocationPermissionGranted() {
        if (ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            AppConstants.REQUEST_CODE_LOCATION_PERMISSION);
            return;
        }
        mMap.setMyLocationEnabled(true);
        Handler handler = new Handler();
        handler.post(this::zoomToCurrentLocation);
    }

    private void zoomToCurrentLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPresenter.getStorePosition(), 15f));
    }

    @Override
    public void setStoreFeatures(List<StoreFeature> availableStoreFeatures) {
        for (StoreFeature storeFeature : StoreFeature.values()) {
            LayoutStoreFeatureItemBinding binding = mStoreFeatureBindings.get(storeFeature);
            binding.tvStoreFeature.setEnabled(true);
            binding.getRoot().setVisibility(availableStoreFeatures.contains(storeFeature) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showSchedule(Map<Integer, LocationScheduleModel> schedule) {
        if (schedule == null || schedule.isEmpty()) {
            return;
        }

        LinearLayout llScheduleList = findViewById(R.id.ll_schedule_list);
        llScheduleList.removeAllViews();
        for (int i = DateTimeConstants.MONDAY; i <= DateTimeConstants.SUNDAY; i++) {
            LayoutScheduleLocationDetailsBinding binding =
                    LayoutScheduleLocationDetailsBinding.inflate(getLayoutInflater(), llScheduleList, true);
            binding.setModel(schedule.get(i));
        }

        LocationScheduleModel holidaysHours = schedule.get(LocationScheduleModel.WEEK_DAY_HOLIDAYS);
        if (holidaysHours != null) {
            LayoutScheduleLocationDetailsBinding binding =
                    LayoutScheduleLocationDetailsBinding.inflate(getLayoutInflater(), llScheduleList, true);
            binding.setModel(holidaysHours);
        }

    }

    @Override
    public void updateOrderAheadEnabled(boolean orderAhead) {
        findViewById(R.id.btn_start_order).setVisibility(orderAhead ? View.VISIBLE : View.GONE);
    }

    @Override
    public void goToProductMenu(StoreLocation storeLocation) {
        finish();
        startActivity(mOrderNavHelper.createIntentStartOrder(this, storeLocation, MenuActivity.MenuOrigin.OTHER, null));
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
