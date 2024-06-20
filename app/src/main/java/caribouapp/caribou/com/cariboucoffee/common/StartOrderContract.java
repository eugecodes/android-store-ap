package caribouapp.caribou.com.cariboucoffee.common;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;

/**
 * Created by asegurola on 4/6/18.
 */

public interface StartOrderContract {

    interface View extends ErrorDialogsView {
        void goToProductMenu(StoreLocation storeLocation);

        void createOrder(StoreLocation storeLocation);

        void updateOrderAheadEnabled(boolean orderAhead);
    }

    interface Presenter extends MvpPresenter {

        void loadOrderData();

        void createOrder(StoreLocation storeLocation);

        void startNewOrder(String storeLocationId);
    }

    interface ErrorDialogsView extends MvpView {

        void showStoreClosedDialog();

        void showStoreAlmostClosedDialog();

        void showStoreNotOrderOutOfStore();

        void showStoreNotAvailableDialog();

        void showStoreTempOff();
    }
}
