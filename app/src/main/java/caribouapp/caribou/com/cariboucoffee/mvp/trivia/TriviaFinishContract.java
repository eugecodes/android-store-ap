package caribouapp.caribou.com.cariboucoffee.mvp.trivia;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsBanner;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

public interface TriviaFinishContract {

    interface View extends MvpView {
        void goToDashboard();

        void goToPickALocation();

        void goToUseInStore();

        void updateUI(boolean orderAhead);

        void setBanner(CmsBanner banner);
    }

    interface Presenter extends MvpPresenter {

        void takeMeToApp();

        void goToStartAnOrder();

        void logStartNewMobileOrder();

        void useInStore();

        void checkOrderAhead();

        void loadBanner();

        void logTriviaAlreadyPlayed();

        void logTriviaWrongAnswer();

        void logTriviaCorrectAnswer();

        void logTriviaTimeout();

        void logTriviaUserInStore();
    }
}
