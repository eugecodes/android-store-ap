package caribouapp.caribou.com.cariboucoffee.mvp.trivia;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

public interface TriviaCountdownContract {

    interface View extends MvpView {
        void goToTriviaStart();

        void goToPausedCountdown();

        void goToAlreadyPlayed();

        void startAnimation();
    }

    interface Presenter extends MvpPresenter {
        void pauseCountdown();

        void countdownFinished();

        void checkTriviaAvailable();
    }

}
