package caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.TriviaEventSource;
import caribouapp.caribou.com.cariboucoffee.api.TriviaApi;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaCheckResquest;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaEligibleResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaCountdownContract;
import retrofit2.Response;

public class TriviaCountdownPresenter extends BasePresenter<TriviaCountdownContract.View> implements TriviaCountdownContract.Presenter {

    @Inject
    UserServices mUserServices;

    @Inject
    TriviaApi mTriviaApi;

    @Inject
    EventLogger mEventLogger;

    @Inject
    AppDataStorage mAppDataStorage;

    public TriviaCountdownPresenter(TriviaCountdownContract.View view) {
        super(view);
    }

    @Override
    public void pauseCountdown() {
        getView().goToPausedCountdown();
    }

    @Override
    public void countdownFinished() {
        getView().goToTriviaStart();
    }

    @Override
    public void checkTriviaAvailable() {
        if (!mAppDataStorage.getTriviaEventSource().equals(TriviaEventSource.Deeplink)) {
            mEventLogger.logTriviaStarted(mAppDataStorage.getTriviaEventSource());
            getView().startAnimation();
            return;
        }
        mTriviaApi.checkEligibility(new TriviaCheckResquest(mUserServices.getUid()))
                .enqueue(new BaseViewRetrofitCallback<TriviaEligibleResponse>(getView()) {

                    @Override
                    protected void onSuccessViewUpdates(Response<TriviaEligibleResponse> response) {
                        if (response.body().isEligible()) {
                            mEventLogger.logTriviaStarted(mAppDataStorage.getTriviaEventSource());
                            getView().startAnimation();
                        } else {
                            getView().goToAlreadyPlayed();
                        }
                    }
                });

    }
}
