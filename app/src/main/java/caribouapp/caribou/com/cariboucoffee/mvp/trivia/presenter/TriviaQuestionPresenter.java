package caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.TriviaApi;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.StartTriviaRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.StartTriviaResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaAnswerRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaAnswerResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaQuestionContract;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.model.TriviaQuestionModel;
import retrofit2.Response;

public class TriviaQuestionPresenter extends BasePresenter<TriviaQuestionContract.View> implements TriviaQuestionContract.Presenter {

    private TriviaQuestionModel mModel;

    @Inject
    TriviaApi mTriviaApi;

    @Inject
    UserServices mUserServices;

    @Inject
    EventLogger mEventLogger;

    @Inject
    AppDataStorage mAppDataStorage;

    public TriviaQuestionPresenter(TriviaQuestionContract.View view, TriviaQuestionModel model) {
        super(view);
        mModel = model;
    }

    @Override
    public void setModel(TriviaQuestionModel model) {
        mModel = model;
    }

    @Override
    public void loadQuestion() {
        mTriviaApi.startTrivia(new StartTriviaRequest(mUserServices.getUid()))
                .enqueue(new BaseViewRetrofitCallback<StartTriviaResponse>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<StartTriviaResponse> response) {
                        StartTriviaResponse startTriviaResponse = response.body();
                        mModel.setQuestion(startTriviaResponse.getQuestion());
                        mModel.setAnswers(startTriviaResponse.getAnswers());
                        getView().startTimer();
                        mEventLogger.logTriviaQuestionShown(mAppDataStorage.getTriviaEventSource());
                        mUserServices.setLastPlayedTrivia(new LocalDate());
                    }
                });
    }

    /**
     * This method is called when we have an actual answer or the countdown has finished in which case
     * the param answer is fed null
     *
     * @param answer
     */
    @Override
    public void answerSelected(String answer) {
        if (answer == null) {
            return;
        }
        mTriviaApi.answerTrivia(new TriviaAnswerRequest(mUserServices.getUid(), answer))
                .enqueue(new BaseViewRetrofitCallback<TriviaAnswerResponse>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<TriviaAnswerResponse> response) {
                        if (!response.body().isAnsweredInTime()) {
                            timeIsUp();
                            return;
                        }
                        mModel.setCorrectAnswer(response.body().getCorrectAnswer());
                        getView().startPostAnswerTimer(response.body().isCorrectlyAnswered());
                    }
                });

    }

    @Override
    public void timeIsUp() {
        getView().goToTimeOutScreen();
    }
}
