package caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityMyCartBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.BindingAdapters;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view.ItemActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.RemoveRewardListener;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.CartContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.presenter.CartPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view.OrderCheckoutActivity;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import icepick.Icepick;
import icepick.State;

/**
 * Created by gonzalogelos on 3/22/18.
 */

public class CartActivity extends OOSFlowActivity<ActivityMyCartBinding> implements
        CartContract.View, CartItemAdapter.CartItemAdapterListener, RemoveRewardListener {

    private CartContract.Presenter mPresenter;

    private CartItemAdapter mCartItemAdapter;


    @State
    boolean mWentToCheckout = false;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, CartActivity.class);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_cart;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        CartPresenter presenter = new CartPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;
        mPresenter.init();
        setOOSFlowPresenter(presenter);

        getBinding().cartContentIncluded.btnAddMoreItems.setOnClickListener((v) -> addMoreItemsClicked());
        getBinding().cartContentIncluded.btnContinueToCheckout.setOnClickListener(view -> mPresenter.checkout());

        mCartItemAdapter = new CartItemAdapter(this);
        RecyclerView rvCartItemsList = getBinding().cartContentIncluded.rvCartItems;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvCartItemsList.setLayoutManager(linearLayoutManager);
        rvCartItemsList.setAdapter(mCartItemAdapter);
        mCartItemAdapter.setListener(this);

        getBinding().cartContentIncluded.rewardAddedBanner.setSecondaryText(R.string.discount_will_be_applied);
        getBinding().cartContentIncluded.rewardAddedBanner.setRemoveRewardListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_button_menu, menu);
        MenuItem closeMenu = menu.findItem(R.id.btn_cancel);
        if (closeMenu != null) {
            MenuItemCompat.setContentDescription(closeMenu, getString(R.string.cancel_cd));
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (mWentToCheckout) {
            hideLoadingLayer();
            mWentToCheckout = false;
        }
        mPresenter.loadData();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goToPreviousScreen();
            return true;
        }
        if (item.getItemId() == R.id.btn_cancel) {
            showCloseDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.CART;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void goToPreviousScreen() {
        finish();
    }

    public void showCloseDialog() {
        DialogUtil.showCancelOrder(this, (dialog, which) -> mPresenter.cancelOrder());
    }

    private void showEmptyCartDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.uh_oh)
                .setMessage(getString(R.string.continue_with_empty_cart_message))
                .setNegativeButton(R.string.dont_want_to_order, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.add_items_to_cart, (dialog, which) -> addMoreItemsClicked())
                .show();
    }

    @Override
    public void goToCheckout() {
        //TODO we should improve OrderCheckOutActivity onCreate and just leave only the necessary in there,
        //TODO so it doesn't take to long to start activity
        showLoadingLayer();
        mWentToCheckout = true;

        startActivity(OrderCheckoutActivity.createIntent(this));
    }

    @Override
    public void showNoItemsMessage() {
        showEmptyCartDialog();
    }

    @Override
    public void showBulkOrderDialog() {
        DialogUtil.showStartingBulkOrderDialog(this, (dialog, which) -> goToPreviousScreen(), (dialog, which) -> dialog.dismiss());
    }

    @Override
    public void showStoreNearClosingForBulk() {
        DialogUtil.showStoreNearClosingForBulk(this);
    }

    @Override
    public void showQuantityLimitDialog() {
        DialogUtil.showDismissableDialog(this,
                R.string.error_product_quantity_limit_reached_title,
                R.string.error_product_quantity_limit_reached_desc_2);
    }

    @Override
    public void showMaxQuantityHasChangedDialog() {
        DialogUtil.showDismissableDialog(this, R.string.uh_oh,
                R.string.error_order_content_changed);
    }

    @Override
    public void showFreeItemsOnlyNotAllowedDialog(String message) {
        DialogUtil.showDismissableDialog(this, R.string.uh_oh, message);
    }

    public void addMoreItemsClicked() {
        startActivity(MenuActivity.createIntent(this, true, MenuActivity.MenuOrigin.CART));
    }

    @Override
    public void removeItem(OrderItem orderItem) {
        mPresenter.removeItem(orderItem);
    }

    @Override
    public void updateItemRemoved(OrderItem orderItem) {
        mCartItemAdapter.removeItem(orderItem);
    }

    @Override
    public void updateSubtotal(BigDecimal newSubtotal) {
        runOnUiThread(() -> {
            BindingAdapters.setMoneyAmount(getBinding().cartContentIncluded.tvSubTotalValue, newSubtotal, null,
                    R.string.subtotal_cd);
            BindingAdapters.setMoneyContentDescription(getBinding().cartContentIncluded.tvSubTotalContainer, newSubtotal, null,
                    getString(R.string.subtotal_cd), null, null);
        });
    }

    @Override
    public void changeQuantity(OrderItem orderItem, int newQuantity) {
        mPresenter.updateItemQuantity(orderItem, newQuantity);
    }

    @Override
    public void editItem(OrderItem orderItem) {
        startActivity(ItemActivity.createIntent(this, orderItem.getMenuData(), orderItem));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }


    @Override
    public void displayOrder(Order order) {
        mCartItemAdapter.setItems(order.getItems());
        getBinding().setModel(order);
    }

    @Override
    public void showErrorOrderNotComplete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.uh_oh)
                .setMessage(R.string.reorder_not_all_items_available)
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void updateItem(OrderItem orderItem) {
        mCartItemAdapter.updateItem(orderItem);
    }

    @Override
    public void onBackPressed() {
        if (isLoading()) {
            return;
        }

        super.onBackPressed();
        goToPreviousScreen();
    }


    @Override
    public void removeReward() {
        mPresenter.removeReward();
    }
}
