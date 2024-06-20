package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by asegurola on 5/2/18.
 */

public interface OOSFlowContract {
    interface View extends MvpView {
        void goToDashboard();
    }

    interface Presenter extends MvpPresenter {

        void loadOrder();

        void orderTimeoutEnabled(boolean enabled);

        void updateLastActivity();

        void checkForOrderDeletion();
    }
}
