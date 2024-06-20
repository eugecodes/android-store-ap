package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.DialogUtil;
import caribouapp.caribou.com.cariboucoffee.common.OptionChooserView;
import caribouapp.caribou.com.cariboucoffee.common.OptionFactory;
import caribouapp.caribou.com.cariboucoffee.common.OptionItem;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityOrderCheckoutBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.AddFundsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationDetailsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.OrderCheckoutContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.PickUpTimeListener;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.TippingListener;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter.OrderCheckoutPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.GuestUserDetailActivityDialog;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.OrderConfirmationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.view.PickupTypeActivity;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import icepick.Icepick;
import icepick.State;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by gonzalogelos on 4/5/18.
 */

public class OrderCheckoutActivity extends OOSFlowActivity<ActivityOrderCheckoutBinding> implements OrderCheckoutContract.View,
        PickUpTimeListener, OptionChooserView.OptionChooserListener, TippingListener {

    private static boolean callGuestToLoyaltyAPI = false;
    private static boolean displayThankYouPopUp = false;
    private static String ncrOrderId = "";
    @State
    CheckoutModel mModel;
    private CheckoutItemsAdapter mCheckoutItemAdapter;
    private OrderCheckoutContract.Presenter mPresenter;

    public static Intent createIntent(Context context) {
        return new Intent(context, OrderCheckoutActivity.class);
    }

    public static boolean isCallGuestToLoyaltyAPI() {
        return callGuestToLoyaltyAPI;
    }

    public static void setCallGuestToLoyaltyAPI(boolean callGuestToLoyaltyAPI) {
        OrderCheckoutActivity.callGuestToLoyaltyAPI = callGuestToLoyaltyAPI;
    }

    public static boolean isDisplayThankYouPopUp() {
        return displayThankYouPopUp;
    }

    public static void setDisplayThankYouPopUp(boolean displayThankYouPopUp) {
        OrderCheckoutActivity.displayThankYouPopUp = displayThankYouPopUp;
    }


    public static String getNcrOrderId() {
        return ncrOrderId;
    }

    public static void setNcrOrderId(String mNcrOrderId) {
        ncrOrderId = mNcrOrderId;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_checkout;
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

        if (mModel == null) {
            mModel = new CheckoutModel();
        }

        OrderCheckoutPresenter presenter = new OrderCheckoutPresenter(this, mModel);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;
        setOOSFlowPresenter(presenter);

        mCheckoutItemAdapter = new CheckoutItemsAdapter();
        mCheckoutItemAdapter.setListener(this::askRemoveRewardConfirmation);
        RecyclerView rvCheckoutOrderSummary = getBinding().checkoutContentIncluded.rvCheckoutOrderSummary;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvCheckoutOrderSummary.setLayoutManager(linearLayoutManager);
        rvCheckoutOrderSummary.setAdapter(mCheckoutItemAdapter);

        mPresenter.init();

        getBinding().checkoutContentIncluded.setModel(mModel);
//        mPresenter.loadPerkPoints();

        getBinding().checkoutContentIncluded.mcAddTip.setListener(this);

        getBinding().checkoutContentIncluded.btnUpdatePickUpTime.setOnClickListener(v -> mPresenter.editPickupTime());

        getBinding().checkoutContentIncluded.btnUpdatePickupType.setOnClickListener(v -> mPresenter.editPickupType());

        getBinding().checkoutContentIncluded.tvViewOnMap.setOnClickListener(v -> viewOnMap());

        getBinding().checkoutContentIncluded.btnAddReward.setOnClickListener(v -> mPresenter.addAvailableReward());

        getBinding().checkoutContentIncluded.tvCustomTip.setOnClickListener(v -> mPresenter.selectCustomTip());

        getBinding().checkoutContentIncluded.mcAddTip.setListener(this);

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
    public void showCloseDialog() {
        DialogUtil.showCancelOrder(this, (dialog, which) -> mPresenter.cancelOrder());
    }

    @Override
    public void goToAddFunds(BigDecimal amountNeededToAdd) {
        // We force a loading screen to avoid flickering when comming back from a successful addFunds.
        showLoadingLayer(true);
        startActivityForResult(AddFundsActivity.createIntentForCheckoutAddFunds(this, true, amountNeededToAdd), AppConstants.REQUEST_CODE_ADD_FUNDS);
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.CHECKOUT;
    }

    public void displayOrderData(Order orderData) {
        mCheckoutItemAdapter.setData(mModel.getOrder());
        getBinding().setModel(mModel);
    }

    @Override
    public void anchorViewToBannerOrTop() {
        NestedScrollView sv = getBinding().checkoutContentIncluded.sv;
        sv.post(() -> sv.scrollTo(0, 0));
    }

    @Override
    public void anchorViewToReward() {
        NestedScrollView sv = getBinding().checkoutContentIncluded.sv;
        sv.post(() -> sv.scrollTo(0, sv.getHeight()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_cancel) {
            showCloseDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateMoneyBalance(boolean enoughBalanceMoney, BigDecimal balanceMoney) {
        Button btnAddsFundsAndPlace = getBinding().checkoutContentIncluded.btnAddFundsAndPlace;
        Button btnPlaceOrder = getBinding().checkoutContentIncluded.btnPlaceOrder;
        Button btnContinueAsGuestUser = getBinding().checkoutContentIncluded.llGuestCheckoutOption.btnContinueAsGuestUser;
        Button btnSignInSignUp = getBinding().checkoutContentIncluded.llGuestCheckoutOption.btnSignInSignUp;

        btnAddsFundsAndPlace.setOnClickListener(v -> mPresenter.addFundsClicked());
        btnPlaceOrder.setOnClickListener(v -> mPresenter.placeOrder());
        btnContinueAsGuestUser.setOnClickListener(v -> openGuestDetailDialog());
        btnSignInSignUp.setOnClickListener(v -> goToSignIn());

        if (mPresenter.isThisGuestFlow()) {
            getBinding().checkoutContentIncluded.llRewardContainer.setVisibility(View.INVISIBLE);
            getBinding().checkoutContentIncluded.btnAddFundsAndPlace.setVisibility(View.INVISIBLE);
            getBinding().checkoutContentIncluded.btnPlaceOrder.setVisibility(View.INVISIBLE);
            getBinding().checkoutContentIncluded.llGuestCheckoutOption.llGuestCheckoutOptionDetails.setVisibility(View.VISIBLE);
            if (mPresenter.isContinueAsGuestCheckEnabled()) {
                btnContinueAsGuestUser.setVisibility(View.VISIBLE);
            } else {
                btnContinueAsGuestUser.setVisibility(View.GONE);
            }
        } else {
            getBinding().checkoutContentIncluded.llRewardContainer.setVisibility(View.VISIBLE);
            btnAddsFundsAndPlace.setVisibility(enoughBalanceMoney ? View.GONE : View.VISIBLE);
            btnPlaceOrder.setVisibility(enoughBalanceMoney ? View.VISIBLE : View.GONE);
            getBinding().checkoutContentIncluded.llGuestCheckoutOption.llGuestCheckoutOptionDetails.setVisibility(View.GONE);
        }

        getBinding().checkoutContentIncluded.rlPerksBalanceContainer.setVisibility(View.VISIBLE);
        getBinding().checkoutContentIncluded.rvCheckoutOrderSummary.setVisibility(View.VISIBLE);

        if (enoughBalanceMoney && !isLoading()) {
            animatePlaceOrderButton();
        }
    }

    public void goToSignIn() {
        setCallGuestToLoyaltyAPI(true);
        NcrOrder ncrOrder = (NcrOrder) mModel.getOrder();
        setNcrOrderId(ncrOrder.getNcrOrderId());
        startActivity(SignInActivity.createIntent(this));
    }

    public void openGuestDetailDialog() {
        CheckoutModel checkoutModel = mPresenter.getCheckoutModel();
        Intent intent = new Intent(this, GuestUserDetailActivityDialog.class);
        intent.putExtra(GuestUserDetailActivityDialog.EXTRA_CHECKOUT_MODEL, checkoutModel);
        startActivity(intent);
    }

    @Override
    public void goToConfirmation(Order orderData) {
        //TODO we should improve OrderConfirmationActivity onCreate and just leave only the necessary in there,
        //TODO so it doesn't take to long to start activity
        showLoadingLayer(true);
        startActivity(OrderConfirmationActivity.createIntent(this, orderData));
        finish();
    }

    @Override
    public void showFailToPlaceOrderDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sorry)
                .setMessage(getString(R.string.problem_with_order))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    finish();
                    startActivity(new Intent(OrderCheckoutActivity.this, MainActivity.class));
                })
                .show();
    }

    @Override
    public void showNationalOutageDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void onResume() {
        mPresenter.loadOrder();
        mPresenter.loadPerkPoints();
        anchorViewToBannerOrTop();
        super.onResume();
        if (isDisplayThankYouPopUp()) {
            setDisplayThankYouPopUp(false);
            mPresenter.displayNationalOutageDialog();
        }
    }

    @Override
    public void hideRewardErrorBanner() {
        getBinding().checkoutContentIncluded.llRewardErrorMessage.setVisibility(View.GONE);
    }

    @Override
    public void setShowPickupTimeField(boolean show) {
        getBinding().checkoutContentIncluded.rlPickupTime.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setShowPickupLocationField(boolean show) {
        getBinding().checkoutContentIncluded.rlPickupType.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAsapNotAvailable() {
        ActivityOrderCheckoutBinding binding = getBinding();
        binding.checkoutContentIncluded.tvAsapNotAvailable.setVisibility(View.VISIBLE);
        binding.checkoutContentIncluded.tvAsapNotAvailable
                .setText(getResources().getQuantityString(R.plurals.asap_not_available, mModel.getOrder().getTotalItemsInCart()));
    }

    @Override
    public void showStoreNearClosingForBulk() {
        DialogUtil.showStoreNearClosingForBulk(this);
    }

    @Override
    public void showPickupTypeScreen() {
        startActivityForResult(
                PickupTypeActivity.createIntentNextScreenMenu(this, null, getString(R.string.confirm), true),
                AppConstants.REQUEST_CODE_UPDATE_PICKUP_TYPE);
    }

    @Override
    public void showCustomTipDialog(TippingOption selectedTipOption, BigDecimal orderTotal) {
        TippingFragmentDialog tippingFragmentDialog = TippingFragmentDialogBuilder.newTippingFragmentDialog(mModel.getOrder(),
                selectedTipOption);
        tippingFragmentDialog.show(getSupportFragmentManager(), AppConstants.SELECT_CUSTOM_TIP_DIALOG_FRAGMENT);
    }

    @Override
    public void setPickupCurbsideTipMessage(String pickupCurbsideMessage) {
        getBinding().checkoutContentIncluded.tvCurbsideMessage.setText(pickupCurbsideMessage);
    }

    @Override
    public void showDeliveryMinimumNotMetDialog(BigDecimal deliveryMinimum) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.oops))
                .setMessage(getString(R.string.required_delivery_minimum_not_met,
                        StringUtils.formatMoneyAmount(this, deliveryMinimum, null)))
                .setNegativeButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void showDeliveryClosedDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.oops))
                .setMessage(getString(R.string.delivery_closed))
                .setNegativeButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void showNotValidSelectedPickupTime() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.oops))
                .setMessage(getString(R.string.pickup_time_outdated))
                .setNegativeButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void showTipping() {
        getBinding().checkoutContentIncluded.addTipContainer.setVisibility(View.VISIBLE);
    }

    public void setDeliveryMessage(String pickupDeliveryPrepMessage) {
        getBinding().checkoutContentIncluded.tvDeliveryMessage.setText(pickupDeliveryPrepMessage);
    }

    private void askRemoveRewardConfirmation() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.order_remove_reward_confirmation_message))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.remove, (dialog, which) -> mPresenter.removeReward())
                .show();
    }

    private void animatePlaceOrderButton() {
        Button button = getBinding().checkoutContentIncluded.btnPlaceOrder;

        if (button.getTag(R.id.bounceAnimator) != null) {
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                button,
                "translationY",
                0, getResources().getInteger(R.integer.bounce_animation_translation), 0
        );
        animator.setInterpolator(new BouncyButtonInterpolator());
        animator.setStartDelay(getResources().getInteger(R.integer.bounce_animation_delay_time));
        animator.setDuration(getResources().getInteger(R.integer.bounce_animation_time));

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                button.setTag(R.id.bounceAnimator, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                button.setTag(R.id.bounceAnimator, null);
            }
        });

        button.setTag(R.id.bounceAnimator, animator);

        animator.start();
    }

    @Override
    public void showStoreClosedDialog() {
        DialogUtil.showStoreClosedAlert(this, () -> {
        });
    }

    @Override
    public void showPickUpTimesDialog(List<PickUpTimeModel> pickUpTimes) {
        PickUpTimeFragmentDialog dialog = PickUpTimeFragmentDialogBuilder.newPickUpTimeFragmentDialog(new ArrayList<>(pickUpTimes));
        dialog.show(getSupportFragmentManager(), AppConstants.PICK_UP_TIME_DIALOG_FRAGMENT);
    }

    @Override
    public void viewOnMap() {
        startActivityForResult(
                LocationDetailsActivity
                        .createIntent(this, mModel.getOrder().getStoreLocation()), AppConstants.REQUEST_CODE_NEW_ORDER_LOCATION_DETAILS);
    }

    @Override
    public void showChooseANewPickUpTimeDialog() {
        showMessage(R.string.choose_new_pick_up_time);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_ADD_FUNDS) {
            // We first hide the loading screen that was forced when navigating to addFunds
            hideLoadingLayer();
            if (resultCode == RESULT_OK) {
                mPresenter.addedFunds();
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_UPDATE_PICKUP_TYPE
                && resultCode == AppConstants.RESULT_CODE_BOUNCE_REQUIRED) {
            mModel.setBounceRequired(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isLoading()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void applyPickUpTime(PickUpTimeModel selectedPickUp) {
        mModel.getOrder().setChosenPickUpTime(selectedPickUp);
    }

    @Override
    public void optionChosen(OptionItem option) {
        mPresenter.setTippingOption(option == null ? null : (TippingOption) option.getValue());
    }

    @Override
    public void setTipping(TippingOption tippingOption) {
        mPresenter.setTippingOption(tippingOption);
    }

    public void setupTippingOptions(List<TippingOption> tippingOptions) {
        getBinding().checkoutContentIncluded.mcAddTip.setOptionsWithNone(createTipOptions(tippingOptions));
    }

    private List<OptionItem> createTipOptions(List<TippingOption> tippingOptions) {
        return StreamSupport.stream(tippingOptions).map(tippingOption -> OptionFactory.getInstance(this)
                .createOptionItemFromTippingOption(tippingOption)).collect(Collectors.toList());
    }

    @Override
    public void showChosenTip(TippingOption tippingOption, BigDecimal tip) {
        getBinding().checkoutContentIncluded.tvCustomTip.setSelected(false);
        getBinding().checkoutContentIncluded.tvCustomTip.setText(R.string.custom);

        OptionChooserView optionChooserView = getBinding().checkoutContentIncluded.mcAddTip;
        getBinding().checkoutContentIncluded.mcAddTip.deselectAllValues();
        OptionItem optionForTip = optionChooserView.getMainOptionForValue(tippingOption);
        //If tipping option is null we select the none option
        if (optionForTip != null || tippingOption == null) {
            getBinding().checkoutContentIncluded.mcAddTip.setSelectedValue(optionForTip);
        } else {
            getBinding().checkoutContentIncluded.mcAddTip.deselectAllValues();
            getBinding().checkoutContentIncluded.tvCustomTip.setSelected(true);
            getBinding().checkoutContentIncluded.tvCustomTip.setText(StringUtils.formatMoneyAmount(this, tip));
        }
    }
}

