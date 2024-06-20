package caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.TriviaEventResult;
import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsBanner;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaFinishContract;
import retrofit2.Response;

public class TriviaFinishPresenter extends BasePresenter<TriviaFinishContract.View> implements TriviaFinishContract.Presenter {

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    EventLogger mEventLogger;

    @Inject
    CmsApi mCmsApi;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    UserServices mUserServices;

    public TriviaFinishPresenter(TriviaFinishContract.View view) {
        super(view);
    }

    @Override
    public void takeMeToApp() {
        getView().goToDashboard();
    }

    @Override
    public void goToStartAnOrder() {
        getView().goToPickALocation();
    }

    @Override
    public void logStartNewMobileOrder() {
        mEventLogger.logTriviaStartMobileOrder();
    }

    @Override
    public void useInStore() {
        getView().goToUseInStore();
    }

    @Override
    public void checkOrderAhead() {
        getView().updateUI(mSettingsServices.isOrderAhead());
    }

    @Override
    public void loadBanner() {
        mCmsApi.getRandomBanner().enqueue(new BaseViewRetrofitCallback<CmsBanner>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Response<CmsBanner> response) {
                getView().setBanner(response.body());
            }
        });
    }

    @Override
    public void logTriviaAlreadyPlayed() {
        mEventLogger.logTriviaAlreadyPlayed();
        mUserServices.setLastPlayedTrivia(new LocalDate());
    }

    @Override
    public void logTriviaWrongAnswer() {
        mEventLogger.logTriviaCompleted(mAppDataStorage.getTriviaEventSource(), TriviaEventResult.WRONG_ANSWER);
    }

    @Override
    public void logTriviaCorrectAnswer() {
        mEventLogger.logTriviaCompleted(mAppDataStorage.getTriviaEventSource(), TriviaEventResult.CORRECT_ANSWER);
    }

    @Override
    public void logTriviaTimeout() {
        mEventLogger.logTriviaCompleted(mAppDataStorage.getTriviaEventSource(), TriviaEventResult.TIMEOUT);
    }

    @Override
    public void logTriviaUserInStore() {
        mEventLogger.logTriviaUseInStore();
    }
}
