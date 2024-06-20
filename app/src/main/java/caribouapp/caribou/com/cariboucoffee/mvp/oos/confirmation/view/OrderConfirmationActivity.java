package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.databinding.ContentOrderConfirmationBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.AutoReloadActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.OrderConfirmationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.ConfirmationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.OrderConfirmationPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import icepick.Icepick;
import icepick.State;

/**
 * Created by gonzalogelos on 3/15/18.
 */

public class OrderConfirmationActivity extends BaseActivity<ContentOrderConfirmationBinding>
        implements OnMapReadyCallback, OrderConfirmationContract.View {

    private static final String EXTRA_ORDER_DATA = "orderData";
    @State
    ConfirmationModel mConfirmationModel;
    private OrderConfirmationItemsAdapter mCartItemsAdapter;
    private GoogleMap mMap;
    private OrderConfirmationContract.Presenter mPresenter;

    public static Intent createIntent(Context context, Order orderData) {
        Intent orderConfirmationIntent = new Intent(context, OrderConfirmationActivity.class);
        orderConfirmationIntent.putExtra(EXTRA_ORDER_DATA, orderData);
        return orderConfirmationIntent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.content_order_confirmation;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (mConfirmationModel == null) {
            mConfirmationModel = new ConfirmationModel();
            mConfirmationModel.setOrder(getOrderData());
        }
        OrderConfirmationPresenter orderConfirmationPresenter = new OrderConfirmationPresenter(this, mConfirmationModel);
        SourceApplication.get(this).getComponent().inject(orderConfirmationPresenter);
        mPresenter = orderConfirmationPresenter;

        getBinding().setConfirmationModel(mConfirmationModel);
        mCartItemsAdapter = new OrderConfirmationItemsAdapter(this);
        mCartItemsAdapter.setList(mConfirmationModel.getOrder().getItems());

        RecyclerView rvOrderItemsList = getBinding().rvCartItems;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvOrderItemsList.setLayoutManager(linearLayoutManager);
        rvOrderItemsList.setAdapter(mCartItemsAdapter);
        rvOrderItemsList.setNestedScrollingEnabled(false);

        mPresenter.init();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        getBinding().tvClose.setOnClickListener(view -> mPresenter.closeClicked());
        getBinding().ivClose.setOnClickListener(view -> mPresenter.closeClicked());
        getBinding().btnReloadCard.setOnClickListener(view -> mPresenter.autoReloadClicked());

        getBinding().setPresenter(mPresenter);
        getBinding().setLifecycleOwner(this);

        if (mPresenter.isThisGuestFlow()) {
            getBinding().rlMobileWalletLayout.setVisibility(View.GONE);
        } else {
            getBinding().rlMobileWalletLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng coordinate = new LatLng(
                mConfirmationModel.getOrder().getStoreLocation().getLatitude(),
                mConfirmationModel.getOrder().getStoreLocation().getLongitude());
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, AppConstants.DEFAULT_MAP_ZOOM);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(
                        mConfirmationModel.getOrder().getStoreLocation().getLatitude(),
                        mConfirmationModel.getOrder().getStoreLocation().getLongitude()))
                .icon(BitmapDescriptorFactory
                        .fromResource(
                                R.drawable.ic_location_pin_selected)));
        mMap.animateCamera(location);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void showImHereModal(String message) {
        DialogUtil.showImHereModal(this, message);
    }

    @Override
    public void setPickupTime(String yourOrderWillBeReadyMessage) {
        getBinding().includeOrderConfirmationHeader.setOrderReadyIn(yourOrderWillBeReadyMessage);
    }

    @Override
    public void goToDashBoard() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void goToAutoReload() {
        startActivity(AutoReloadActivity.createIntent(this));
    }

    @Override
    public void setDeliveryTime(String deliveryMessage) {
        getBinding().includeOrderConfirmationHeader.setOrderReadyIn(deliveryMessage);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private Order getOrderData() {
        return (Order) getIntent().getSerializableExtra(EXTRA_ORDER_DATA);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onBackPressed() {
        mPresenter.closeClicked();
    }

    @Override
    public AppScreen getScreenName() {
        return AppScreen.ORDER_CONFIRMATION;
    }

}
