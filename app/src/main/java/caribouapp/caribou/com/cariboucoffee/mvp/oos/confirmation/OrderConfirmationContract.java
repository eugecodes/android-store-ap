package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation;

import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by gonzalogelos on 3/15/18.
 */

public interface OrderConfirmationContract {

    interface Presenter extends MvpPresenter {

        void closeClicked();

        void autoReloadClicked();

        void onPause();

        void onResume();

        void init();

        void imHereSignal();

        boolean isThisGuestFlow();
    }

    interface View extends MvpView {

        void showImHereModal(String message);

        void setPickupTime(String youOrderWillBeReadyMessage);

        void goToDashBoard();

        void goToAutoReload();

        void setDeliveryTime(String deliveryMessage);

        AppScreen getScreenName();
    }
}
