package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityRecentOrderBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.view.PickLocationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.RecentOrderContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrdersModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.presenter.RecentOrderPresenter;
import icepick.Icepick;
import icepick.State;

/**
 * Created by jmsmuy on 4/9/18.
 */

public class RecentOrderActivity extends BaseActivity<ActivityRecentOrderBinding> implements RecentOrderContract.View {

    @State
    RecentOrdersModel mModel;
    private RecentOrderContract.Presenter mPresenter;
    private RecentOrdersAdapter mOrdersAdapter;
    private static final String EXTRA_RECENT_ORDER_LIST = "extra_recent_order_list";

    @Inject
    OrderNavHelper mOrderNavHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recent_order;
    }

    public static Intent createIntent(Context context, ArrayList<RecentOrderModel> recentOrderModels) {
        Intent intent = new Intent(context, RecentOrderActivity.class);
        intent.putExtra(EXTRA_RECENT_ORDER_LIST, recentOrderModels);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Icepick.restoreInstanceState(this, savedInstanceState);

        SourceApplication.get(this).getComponent().inject(this);

        if (mModel == null) {
            mModel = new RecentOrdersModel();
        }

        RecentOrderPresenter presenter = new RecentOrderPresenter(this, mModel);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        mOrdersAdapter = new RecentOrdersAdapter(mPresenter);
        getBinding().contentIncluded.rvOrders.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getBinding().contentIncluded.rvOrders.setAdapter(mOrdersAdapter);
        getBinding().contentIncluded.btnStartOrder.setOnClickListener(view -> mPresenter.startNewOrder());

        mPresenter.loadData(getRecentOmsOrder());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void setOrderList(RecentOrdersModel model) {
        getBinding().contentIncluded.setModel(model);
        mOrdersAdapter.setOrderList(model.getRecentOrderList());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void goToStartNewOrder() {
        startActivity(new Intent(this, PickLocationActivity.class));
    }

    @Override
    public void goToReOrder(StoreLocation storeLocation) {
        startActivity(mOrderNavHelper.createIntentReOrder(this, storeLocation));
    }

    @Override
    public void showCantReorderErrorMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sorry)
                .setMessage(R.string.sorry_we_cant_place_this_order)
                .setPositiveButton(R.string.okay, (dialog, which) -> {
                })
                .show();
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
    public void showStoreTempOff() {
        DialogUtil.storeTemporaryOff(this);
    }

    @Override
    public void showStoreNotAvailableDialog() {
        DialogUtil.storeNotAvailableDialog(this);
    }

    @Override
    public void goToProductMenu(StoreLocation storeLocation) {
        //No implementation
    }

    @Override
    public void createOrder(StoreLocation storeLocation) {
        mPresenter.reorder(storeLocation);
    }

    @Override
    public void updateOrderAheadEnabled(boolean orderAhead) {
        // NO-OP
    }

    private List<RecentOrderModel> getRecentOmsOrder() {
        return (List<RecentOrderModel>) getIntent().getSerializableExtra(EXTRA_RECENT_ORDER_LIST);
    }
}
