package caribouapp.caribou.com.cariboucoffee.mvp.enrollment;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

public interface SaveSignInDataContract {

    interface Presenter extends MvpPresenter {

        void saveSignInData(String token, String uid);

        void loadUserProfile();

        boolean isGuestFlowActiveAfterLogin();

        void stopGuestFlowAndStartLoyaltyUserFlow();


    }

    interface View extends MvpView {

        void goToDashboard();
    }

}
