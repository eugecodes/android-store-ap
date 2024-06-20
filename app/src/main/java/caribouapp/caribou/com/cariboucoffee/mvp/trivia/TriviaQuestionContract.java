package caribouapp.caribou.com.cariboucoffee.mvp.trivia;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.model.TriviaQuestionModel;

public interface TriviaQuestionContract {

    interface View extends MvpView {
        void goToTimeOutScreen();

        void goToCorrectAnswerScreen();

        void goToWrongAnswerScreen();

        void startTimer();

        void startPostAnswerTimer(boolean answerWasCorrect);
    }

    interface Presenter extends MvpPresenter {
        void setModel(TriviaQuestionModel model);

        void loadQuestion();

        void answerSelected(String answer);

        void timeIsUp();
    }
}
