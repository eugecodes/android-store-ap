package caribouapp.caribou.com.cariboucoffee;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by jmsmuy on 11/7/17.
 */

public interface SplashContract {
    interface View extends MvpView {
        void startupFinished();

    }

    interface Presenter extends MvpPresenter {
        void doStartupChecks();
    }
}
