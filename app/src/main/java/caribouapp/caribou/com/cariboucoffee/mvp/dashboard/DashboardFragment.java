package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;


import static caribouapp.caribou.com.cariboucoffee.AppConstants.EXTRA_MESSAGE;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_LOCATION_SERVICES;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_SIGN_IN;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.urbanairship.messagecenter.Inbox;
import com.urbanairship.messagecenter.MessageCenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.LocationAwareActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentDashboardBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.account.view.AccountActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.CheckInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.SignUpActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.WelcomeBackActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.faq.view.FaqActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.view.FeedbackPopupActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.OrderConfirmationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.view.PickLocationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view.RecentOrderActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.view.TermsAndPrivacyActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.view.TriviaAlreadyPlayedActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.view.TriviaCountdownActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.view.CashStarWebView;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import icepick.Icepick;
import icepick.State;

public class DashboardFragment extends BaseUpdateFragment<FragmentDashboardBinding> implements
        DashboardContract.View,
        UAirshipInboxUnreadCounterObserver.OnNewInboxUnreadCountAvailableListener {

    private static final String TAG = DashboardFragment.class.getSimpleName();

    @Inject
    Inbox richPushInbox;

    @Inject
    OrderNavHelper mOrderNavHelper;

    @State
    DashboardModel mDashboardModel;

    private DrawerLayout mDrawer;
    private DashboardContract.Presenter mPresenter;
    private UAirshipInboxUnreadCounterObserver mUAirshipInboxCounterObserver;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dashboard;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SourceApplication.get(getActivity()).getComponent().inject(this);

        Icepick.restoreInstanceState(this, savedInstanceState);

        if (mDashboardModel == null) {
            mDashboardModel = new DashboardModel();
        }
        DashboardPresenter dashboardPresenter = new DashboardPresenter(this, mDashboardModel);
        SourceApplication.get(getContext()).getComponent().inject(dashboardPresenter);
        mPresenter = dashboardPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mPresenter.checkForUserLogIn();

        getBinding().menuDashboard.setView(this);
        getBinding().contentDashboard.setModel(mDashboardModel);
        getBinding().contentDashboard.setPresenter(mPresenter);
        getBinding().menuDashboard.setModel(mDashboardModel);
        getBinding().menuDashboard.setSettings(mPresenter.getSettings());
        getBinding().contentDashboard.triviaAnimationView.enableMergePathsForKitKatAndAbove(true);
        getBinding().setLifecycleOwner(getViewLifecycleOwner());
        mDrawer = getBinding().drawerLayout;
        mPresenter.loadTimeOfDayData();
        updateUserLoggedInStatus(mDashboardModel.isUserLoggedIn());

        getBinding().contentDashboard.btnOpenDrawer.setOnClickListener(v -> mDrawer.openDrawer(GravityCompat.START));
        getBinding().contentDashboard.btnLocation.setOnClickListener(v -> goToLocations());

        initUAirshipInboxCounter();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        NavigationView navigationMenu = getBinding().navView;
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationMenu.getLayoutParams();
        params.width = metrics.widthPixels;
        navigationMenu.setLayoutParams(params);

        return getBinding().getRoot();
    }

    private LocationAwareActivity getLocationAwareActivity() {
        return (LocationAwareActivity) getActivity();
    }

    public boolean onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    public void closeDrawer() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void hideTrivia() {
        getBinding().contentDashboard.triviaAnimationView.setVisibility(View.GONE);
        getBinding().contentDashboard.mainDashboardCtaPanel.setOnClickListener(null);
    }

    @Override
    public void goToConfirmationScreen(Order orderData) {
        startActivity(OrderConfirmationActivity.createIntent(getContext(), orderData));
    }

    @Override
    public void goToPickLocation() {
        startActivity(new Intent(getContext(), PickLocationActivity.class));
    }

    @Override
    public AppScreen getScreenName() {
        return AppScreen.HOME_SCREEN;
    }

    @Override
    public void showNationalOutageDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void goToPickupLocationScreen() {
        mPresenter.startGuestUserFlow();
        goToPickLocation();
    }

    @Override
    public void updateTriviaOnDashboard() {
        getBinding().contentDashboard.triviaAnimationView.setVisibility(mDashboardModel.isDailyTriviaActive() ? View.VISIBLE : View.GONE);
        getBinding().contentDashboard.tvTitleMsg.setVisibility(View.VISIBLE);
        mPresenter.setDefaultTitle(getString(R.string.dashboard_default_welcome_message));
        if (!mDashboardModel.isDailyTriviaActive()) {
            getBinding().contentDashboard.mainDashboardCtaPanel.setOnClickListener(null);
            return;
        }

        getBinding().contentDashboard.triviaAnimationView.playAnimation();
        getBinding().contentDashboard.mainDashboardCtaPanel.setOnClickListener(v -> mPresenter.checkForTriviaStatus());
    }

    @Override
    public void goToTriviaCountDown() {
        startActivity(new Intent(getContext(), TriviaCountdownActivity.class));
    }

    @Override
    public void goToTriviaAlreadyPlayed() {
        startActivity(new Intent(getContext(), TriviaAlreadyPlayedActivity.class));
    }

    private void initUAirshipInboxCounter() {
        mUAirshipInboxCounterObserver =
                new UAirshipInboxUnreadCounterObserver(richPushInbox, this);
        getLifecycle().addObserver(mUAirshipInboxCounterObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBinding().contentDashboard.curbsideImHereContainer.curbsideImHereContainer.setVisibility(View.GONE);
        getBinding().contentDashboard.tvTitleMsg.setVisibility(View.GONE);
        getBinding().contentDashboard.triviaAnimationView.setVisibility(View.GONE);
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDashboardModel.isUserLoggedIn()) {
            mPresenter.enableUpdateOrderStatus(false);
        }
        mPresenter.onPause();
    }

    @Override
    public void setupMenuNavigation() {
        if (mDashboardModel.isUserLoggedIn()) {
            getBinding().menuDashboard.llRewards.setOnClickListener(v -> goToRewards());
            getBinding().menuDashboard.llAccountAction.setOnClickListener(v -> goToAccount());
            getBinding().menuDashboard.tvSignout.setText(R.string.menu_signout);
            getBinding().menuDashboard.tvSignout.setOnClickListener(v -> mPresenter.signOut());
            getBinding().menuDashboard.llInbox.setOnClickListener(view -> MessageCenter.shared().showMessageCenter());
        } else {
            getBinding().menuDashboard.llRewards.setOnClickListener(v -> askForLoginOrSignup());
            getBinding().menuDashboard.llAccountAction.setOnClickListener(v -> askForLoginOrSignup());
            getBinding().menuDashboard.llInbox.setOnClickListener(view -> askForLoginOrSignup());
            getBinding().menuDashboard.tvSignout.setText(R.string.menu_signin);
            getBinding().menuDashboard.tvSignout.setOnClickListener(v -> goToSignIn());
        }
        getBinding().menuDashboard.llLocations.setOnClickListener(v -> goToLocations());
        getBinding().menuDashboard.llMenuAction.setOnClickListener(view -> mPresenter.menuOptionsClick());
        getBinding().menuDashboard.llEGift.setOnClickListener(view -> openEGift());
        getBinding().menuDashboard.ibClose.setOnClickListener(view -> getBinding().drawerLayout.closeDrawers());

        getBinding().menuDashboard.tvTerms
                .setOnClickListener(view -> startActivity(new Intent(getContext(), TermsAndPrivacyActivity.class)));

        getBinding().menuDashboard.tvFaq
                .setOnClickListener(view -> startActivity(new Intent(getContext(), FaqActivity.class)));

    }

    private void goToAccount() {
        startActivity(new Intent(getContext(), AccountActivity.class));
    }

    private void askForLoginOrSignup() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.sign_up_log_in)
                .setMessage(R.string.sign_in_sign_up_message)
                .setNeutralButton(R.string.no_thanks, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.log_me_in_im_a_member, (dialog, which) -> goToSignIn())
                .setPositiveButton(R.string.yes_sign_me_up, (dialog, which) -> goToSignUp())
                .show();
    }

    @Override
    public void updateUserLoggedInStatus(boolean loggedIn) {
        if (loggedIn) {
            getBinding().contentDashboard.btnCheckIn.getRoot()
                    .setOnClickListener(view -> navigateToCheckIn());
            getBinding().contentDashboard.btnStartOrder
                    .setOnClickListener(view -> mPresenter.startOrContinueOrder());
        } else {
            getBinding().contentDashboard.btnCheckIn.getRoot().setOnClickListener(view -> goToSignUp());
            getBinding().contentDashboard.btnCheckIn.pointsLabel.setText(getString(R.string.sign_up));
            getBinding().contentDashboard.btnCheckIn.checkInPayLabel.setText(getString(R.string.earn_rewards));
            getBinding().contentDashboard.btnStartOrder.setOnClickListener(view -> goToGuestFlow());
            getBinding().contentDashboard.flOrderNow.setVisibility(View.VISIBLE);
            getBinding().contentDashboard.btnCheckIn.clCheckInButtonContainer
                    .setContentDescription(getString(R.string.checkin_pay_button_log_out_cd));
        }
    }

    @Override
    public void setBackgroundStyle(TimeOfDay timeOfDay) {
        Log.d(TAG, "setBackgroundStyle: " + timeOfDay);
        @DrawableRes
        int dashboardBackground, menuBackground;
        @ColorRes
        int menuTextColor, menuBottomTextColor, dashboardControlsTextColor,
                dashboardTextColor, dashboardControlsSecondaryTextColor, dashboardMenuNavColor,
                dashboardCheckInPayBgColor, dashboardCheckInPayForegroundColor;

        // TODO I think it could better if we have a colorConfiguration that has all the colors we need,
        // and move all the switch to some colorConfigurationUtility that loads the colorConfiguration
        // object and we use that object on the activity.
        switch (timeOfDay) {
            case Evening:
                dashboardBackground = R.drawable.background_dashboard_dusk;
                menuBackground = R.drawable.background_menu_dusk;
                menuTextColor = R.color.menuDuskTextColor;
                menuBottomTextColor = R.color.menuDuskBottomTextColor;
                dashboardControlsTextColor = R.color.dashboardDuskBannerTextColor;
                dashboardControlsSecondaryTextColor = R.color.dashboardDuskBannerSecondaryTextColor;
                dashboardTextColor = R.color.dashboardDuskTextColor;
                dashboardMenuNavColor = R.color.dashboardDuskMenuButtonColor;
                dashboardCheckInPayBgColor = R.color.dashboardDuskCheckInBackgroundColor;
                dashboardCheckInPayForegroundColor = R.color.dashboardDuskCheckInPayTextColor;
                break;
            case Morning:
                dashboardBackground = R.drawable.background_dashboard_dawn;
                menuBackground = R.drawable.background_menu_dawn;
                menuTextColor = R.color.menuDawnTextColor;
                menuBottomTextColor = R.color.menuDawnBottomTextColor;
                dashboardControlsTextColor = R.color.dashboardDawnBannerTextColor;
                dashboardControlsSecondaryTextColor = R.color.dashboardDawnBannerSecondaryTextColor;
                dashboardTextColor = R.color.dashboardDawnTextColor;
                dashboardMenuNavColor = R.color.dashboardDawnMenuButtonColor;
                dashboardCheckInPayBgColor = R.color.dashboardDawnCheckInBackgroundColor;
                dashboardCheckInPayForegroundColor = R.color.dashboardDawnCheckInPayTextColor;
                break;
            case Day:
                dashboardBackground = R.drawable.background_dashboard_midday;
                menuBackground = R.drawable.background_menu_midday;
                menuTextColor = R.color.menuMiddayTextColor;
                menuBottomTextColor = R.color.menuMiddayBottomTextColor;
                dashboardControlsTextColor = R.color.dashboardMiddayBannerTextColor;
                dashboardControlsSecondaryTextColor = R.color.dashboardMiddayBannerSecondaryTextColor;
                dashboardTextColor = R.color.dashboardMiddayTextColor;
                dashboardMenuNavColor = R.color.dashboardMiddayMenuButtonColor;
                dashboardCheckInPayBgColor = R.color.dashboardMiddayCheckInBackgroundColor;
                dashboardCheckInPayForegroundColor = R.color.dashboardMiddayCheckInPayTextColor;
                break;
            case Night:
                dashboardBackground = R.drawable.background_dashboard_nightsky;
                menuBackground = R.drawable.background_menu_nightsky;
                menuTextColor = R.color.menuNightskyTextColor;
                menuBottomTextColor = R.color.menuNightskyBottomTextColor;
                dashboardControlsTextColor = R.color.dashboardNightskyBannerTextColor;
                dashboardControlsSecondaryTextColor = R.color.dashboardNightskyBannerSecondaryTextColor;
                dashboardTextColor = R.color.dashboardNightskyTextColor;
                dashboardMenuNavColor = R.color.dashboardNightskyMenuButtonColor;
                dashboardCheckInPayBgColor = R.color.dashboardNightskyCheckInBackgroundColor;
                dashboardCheckInPayForegroundColor = R.color.dashboardNightskyCheckInPayTextColor;
                break;
            default:
                dashboardBackground = R.drawable.background_dashboard_late_nightsky;
                menuBackground = R.drawable.background_menu_late_nightsky;
                menuTextColor = R.color.menuNightskyTextColor;
                menuBottomTextColor = R.color.menuNightskyBottomTextColor;
                dashboardControlsTextColor = R.color.dashboardNightskyBannerTextColor;
                dashboardControlsSecondaryTextColor = R.color.dashboardNightskyBannerSecondaryTextColor;
                dashboardTextColor = R.color.dashboardNightskyTextColor;
                dashboardMenuNavColor = R.color.dashboardNightskyMenuButtonColor;
                dashboardCheckInPayBgColor = R.color.dashboardNightskyCheckInBackgroundColor;
                dashboardCheckInPayForegroundColor = R.color.dashboardNightskyCheckInPayTextColor;
                break;
        }
        // TODO DBB-270
        // try to fix the crash if getContext is null
        if (getContext() != null) {
            try {
                getBinding().contentDashboard.dashboardBackgroundPanel.setImageResource(dashboardBackground);
                getBinding().menuDashboard.menuBackgroundPanel.setImageResource(menuBackground);
                getBinding().menuDashboard.setTextColor(ContextCompat.getColor(getContext(), menuTextColor));
                getBinding().menuDashboard.setBottomTextColor(ContextCompat.getColor(getContext(), menuBottomTextColor));
                getBinding().contentDashboard.setTextColor(ContextCompat.getColor(getContext(), dashboardTextColor));
                getBinding().contentDashboard.setControlsTextColor(ContextCompat.getColor(getContext(), dashboardControlsTextColor));
                getBinding().contentDashboard.setControlsSecondaryTextColor(ContextCompat.getColor(getContext(),
                        dashboardControlsSecondaryTextColor));
                getBinding().contentDashboard.setMenuNavColor(ContextCompat.getColor(getContext(), dashboardMenuNavColor));
                getBinding().contentDashboard.setCheckInPayBgColor(ContextCompat.getColor(getContext(), dashboardCheckInPayBgColor));
                getBinding().contentDashboard.setCheckInPayForegroundColor(ContextCompat.getColor(getContext(), dashboardCheckInPayForegroundColor));
            } catch (Exception e) {
                Log.e(TAG, ("Problems while setting color and drawables" + e.getMessage()));
            }
        }

    }


    @Override
    public void setClosestStore(StoreLocation storeLocation) {
        getBinding().contentDashboard.btnLocation.setVisibility(View.VISIBLE);
        if (storeLocation == null) {
            getBinding().contentDashboard.btnLocation.setText(R.string.no_store_close_by);
            getBinding().contentDashboard.btnLocation.setContentDescription(getString(R.string.no_store_close_by));
            return;
        }
        getBinding().contentDashboard.btnLocation
                .setText(getString(R.string.closest_store, storeLocation.getAddressShort(), storeLocation.getDistanceInMiles()));
        String tvNearestLocationText = getBinding().contentDashboard.btnLocation.getText().toString();
        getBinding().contentDashboard.btnLocation
                .setContentDescription(String.format(getString(R.string.nearest_location_content_description), tvNearestLocationText));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    public void onLocationChanged(Location location) {
        mPresenter.loadClosestStore(
                new LatLng(location.getLatitude(), location.getLongitude()),
                location.getAccuracy() / AppConstants.METERS_IN_A_MILE /* meters to miles */);
    }

    public void goToGuestFlow() {
        if (!mDashboardModel.isUserLoggedIn() && mPresenter.isGuestCheckoutEnabledFromDashboard()) {
            mPresenter.callAnonTokenForGuestUser();
        } else {
            goToSignIn();
        }
    }

    @Override
    public void goToSignIn() {
        startActivityForResult(new Intent(getContext(), SignInActivity.class), REQUEST_CODE_SIGN_IN);
    }

    private void goToSignUp() {
        startActivity(new Intent(getContext(), SignUpActivity.class));
    }

    private void goToRewards() {
        startActivity(new Intent(getContext(), CheckInActivity.class));
    }

    private void goToLocations() {
        startActivity(LocationsActivity.createIntent(getContext()));
    }

    @Override
    public void navigateToCheckIn() {
        startActivity(new Intent(getContext(), CheckInActivity.class));
    }

    @Override
    public void showPerksPoints(int perksAmount) {
        getBinding().contentDashboard.btnCheckIn.pointsLabel
                .setText(getResources().getQuantityString(R.plurals.labeled_points, perksAmount, perksAmount));
        if (perksAmount > 0) {
            getBinding().contentDashboard.btnCheckIn.clCheckInButtonContainer
                    .setContentDescription(getResources()
                            .getQuantityString(R.plurals.checkin_pay_button_cd, perksAmount, perksAmount));
        } else {
            getBinding().contentDashboard.btnCheckIn.clCheckInButtonContainer
                    .setContentDescription(getString(R.string.checkin_pay_button_no_points_cd));
        }
    }

    @Override
    public void showPerksPointsLoading(boolean showLoading) {
        getBinding().contentDashboard.btnCheckIn.setLoadingPoints(showLoading);
    }

    @Override
    public void setOrderButtonAsContinue(boolean continueOrder) {
        runOnUiThread(() -> getBinding().contentDashboard.btnStartOrder
                .setText(continueOrder ? R.string.continue_order : R.string.start_new_order));
    }

    @Override
    public void goToStartNewOrder(List<RecentOrderModel> recentOrderList) {
        startActivity(RecentOrderActivity.createIntent(getContext(), (ArrayList<RecentOrderModel>) recentOrderList));

    }

    @Override
    public void setOrderButtonVisible(boolean visible) {
        getBinding().contentDashboard.flOrderNow.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void goToContinueOrder(StoreLocation storeLocation) {
        startActivity(mOrderNavHelper.createIntentContinueOrder(getContext(), storeLocation));
    }

    @Override
    public void goToFeedbackPopUp() {
        startActivityForResult(new Intent(getContext(), FeedbackPopupActivity.class), AppConstants.REQUEST_CODE_FEEDBACK);
    }

    @Override
    public void setCartItems(Integer size) {
        getBinding().contentDashboard.cvCart.setItemCount(size);
        if (size != null && size > 0) {
            getBinding().contentDashboard.btnStartOrder
                    .setContentDescription(getResources()
                            .getQuantityString(R.plurals.continue_order_now_cd, size, size));
        } else {
            getBinding().contentDashboard.btnStartOrder
                    .setContentDescription(getString(R.string.order_now_cd));
        }
    }

    @Override
    public void goToMenu() {
        startActivity(MenuActivity.createIntent(getContext(), false, MenuActivity.MenuOrigin.OTHER));
    }

    @Override
    public void goToPerksProgramEnrollment() {
        Intent welcomeBackIntent = new Intent(getContext(), WelcomeBackActivity.class);
        welcomeBackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(welcomeBackIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == AppConstants.REQUEST_CODE_FEEDBACK
                && data.getBooleanExtra(AppConstants.EXTRA_FEEDBACK_SUCCESS, false)) {
            showMessage(R.string.thanks_got_your_feedback);
        } else if (requestCode == REQUEST_CODE_LOCATION_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                mPresenter.startOrContinueOrder();
            } else {
                goToLocations();
            }
        } else if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == Activity.RESULT_OK) {
            String printableMsg = data.getStringExtra(EXTRA_MESSAGE);
            if (printableMsg != null) {
                showWarning(printableMsg);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            openEGift();
        }
    }


    private void openEGift() {
        startActivity(new Intent(getContext(), CashStarWebView.class));
    }

    @Override
    public void onNewInboxUnreadCountAvailable(int count) {
        getBinding().setInboxCount(count);
        if (count > 0) {
            getBinding().contentDashboard.btnOpenDrawer
                    .setContentDescription(getResources()
                            .getQuantityString(R.plurals.hamburger_with_messages_cd, count, count));
        } else {
            getBinding().contentDashboard.btnOpenDrawer
                    .setContentDescription(getString(R.string.hamburger_cd));
        }
    }

    @Override
    public void updatesFinished() {
        mPresenter.checkTriviaAvailable();
        mPresenter.updateData();
        mPresenter.pushRegister();
        if (mPresenter.getSettings().isLocations() && getLocationAwareActivity() != null) {
            getLocationAwareActivity().setAskForLocationPermission(mPresenter.shouldAskForLocationPermission());
            getLocationAwareActivity().startCurrentLocationRequest();
        }
        // Force menu binding update
        getBinding().menuDashboard.setSettings(mPresenter.getSettings());
    }

}
