package caribouapp.caribou.com.cariboucoffee.mvp.feedback;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

/**
 * Created by jmsmuy on 2/19/18.
 */

public interface FeedbackContract {

    interface Popup {

        interface View extends MvpView {

            void close();
        }

        interface Presenter extends MvpPresenter {

            void askMeLater();

            void dontAskAgain();

            void goToPlayStore();
        }

    }

    interface Feedback {

        interface View extends MvpView {

            void selectedRating(Integer star);

            void closeFeedback(boolean feedbackSuccess);
        }

        interface Presenter extends MvpPresenter {

            void selectRating(String star);

            void sendFeedback();

            void cancelFeedback();
        }

    }

}
