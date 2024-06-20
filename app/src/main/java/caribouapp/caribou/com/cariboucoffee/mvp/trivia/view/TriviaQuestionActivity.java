package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;


import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityTriviaQuestionBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaQuestionContract;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.model.TriviaQuestionModel;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaQuestionPresenter;
import icepick.Icepick;
import icepick.State;

public class TriviaQuestionActivity extends BaseActivity<ActivityTriviaQuestionBinding>
        implements TriviaQuestionContract.View, TriviaAnswersView.TriviaAnswerListener, Animator.AnimatorListener {

    private static final long POST_ANSWER_SHOW_TIME = 3000;
    @State
    TriviaQuestionModel mModel;

    private TriviaQuestionContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trivia_question;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        TriviaQuestionPresenter presenter = new TriviaQuestionPresenter(this, mModel);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        if (mModel == null) {
            mModel = new TriviaQuestionModel();
            mPresenter.setModel(mModel);
            mPresenter.loadQuestion();
        }

        getBinding().contentIncluded.setModel(mModel);
        getBinding().contentIncluded.tavAnswers.setListener(this);
        getBinding().contentIncluded.lavTimer.setVisibility(View.GONE);
        getBinding().contentIncluded.lavTimer.addAnimatorListener(this);
        getBinding().contentIncluded.lavTimer.enableMergePathsForKitKatAndAbove(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void answerSelected(String answer) {
        getBinding().contentIncluded.lavTimer.pauseAnimation();
        mPresenter.answerSelected(answer);
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.TRIVIA_QUESTION;
    }

    @Override
    public void goToCorrectAnswerScreen() {
        startActivity(new Intent(this, TriviaFinishCorrectAnswerActivity.class));
        finish();
    }

    @Override
    public void goToWrongAnswerScreen() {
        startActivity(TriviaFinishFailAnswerActivity.createIntent(this, true));
        finish();
    }
    @Override
    public void goToTimeOutScreen() {
        startActivity(TriviaFinishFailAnswerActivity.createIntent(this, false));
        finish();
    }

    @Override
    public void startTimer() {
        getBinding().contentIncluded.lavTimer.setVisibility(View.VISIBLE);
        getBinding().contentIncluded.lavTimer.playAnimation();
    }

    @Override
    public void startPostAnswerTimer(boolean answerWasCorrect) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (answerWasCorrect) {
                goToCorrectAnswerScreen();
            } else {
                goToWrongAnswerScreen();
            }
        }, POST_ANSWER_SHOW_TIME);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        //NO-OP
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mPresenter.timeIsUp();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        //NO-OP
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        //NO-OP
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
