package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.order.Order;

/**
 * Created by jmsmuy on 9/28/17.
 */

public interface DashboardContract {

    interface View extends MvpView {

        void goToTriviaCountDown();

        void goToTriviaAlreadyPlayed();

        void setupMenuNavigation();

        void setBackgroundStyle(TimeOfDay backgroundStyle);

        void setClosestStore(StoreLocation storeLocation);

        void goToSignIn();

        void navigateToCheckIn();

        void showPerksPoints(int perksAmount);

        void showPerksPointsLoading(boolean showLoading);

        void setOrderButtonAsContinue(boolean continueOrder);

        void goToStartNewOrder(List<RecentOrderModel> recentOrderList);

        void setOrderButtonVisible(boolean visible);

        void goToContinueOrder(StoreLocation storeLocation);

        void goToFeedbackPopUp();

        void setCartItems(Integer size);

        void goToMenu();

        void goToPerksProgramEnrollment();

        void updateUserLoggedInStatus(boolean loggedIn);

        void updateTriviaOnDashboard();

        void hideTrivia();

        void goToConfirmationScreen(Order orderData);

        void goToPickLocation();

        AppScreen getScreenName();

        void showNationalOutageDialog(String title, String message);

        void goToPickupLocationScreen();
    }

    interface Presenter extends MvpPresenter {

        void pushRegister();

        boolean isEnrolledBrandRewardsSystem();

        DashboardModel getModel();

        void loadTimeOfDayData();

        void sendCurbsideIamHereSignal();

        void curbsideSuccessGotIt();

        void curbsideSuccessOrErrorClose();

        void checkForTriviaStatus();

        void updateTimeOfDayData();

        void checkForUserLogIn();

        void setDefaultTitle(String title);

        void loadClosestStore(LatLng currentLocation, double radiusInMiles);

        void checkOrderAheadActive();

        void updateUserName();

        void updatePerkPoints();

        void signOut();

        void loadFunds();

        void updateData();

        void startOrContinueOrder();

        void onResume();

        void onPause();

        void enableUpdateOrderStatus(boolean enabled);

        void popupFeedbackScreen();

        void menuOptionsClick();

        SettingsServices getSettings();

        boolean shouldAskForLocationPermission();

        void checkTriviaAvailable();

        void startGuestUserFlow();

        void stopGuestUserFlow();

        boolean isGuestCheckoutEnabledFromDashboard();

        void callAnonTokenForGuestUser();
    }
}
