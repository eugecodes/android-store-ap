package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityTriviaCountdownBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaCountdownContract;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaCountdownPresenter;

public class TriviaCountdownActivity extends BaseActivity<ActivityTriviaCountdownBinding>
        implements TriviaCountdownContract.View, Animator.AnimatorListener, FragmentTriviaPause.OnPauseListener {

    private TriviaCountdownContract.Presenter mPresenter;
    private FragmentTriviaPause mFragmentTriviaPause;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trivia_countdown;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TriviaCountdownPresenter presenter = new TriviaCountdownPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;
        getBinding().contentIncluded.ibPlayPauseBtn.setOnClickListener(v -> mPresenter.pauseCountdown());
        getBinding().contentIncluded.lavTimer.setVisibility(View.GONE);
        getBinding().contentIncluded.lavTimer.addAnimatorListener(this);
        getBinding().contentIncluded.lavTimer.enableMergePathsForKitKatAndAbove(true);

        mFragmentTriviaPause = new FragmentTriviaPause();
        mPresenter.checkTriviaAvailable();

    }

    private void startCountdown() {
        getBinding().contentIncluded.lavTimer.setVisibility(View.VISIBLE);
        getBinding().contentIncluded.lavTimer.playAnimation();
    }

    private void pauseCountdown() {
        getBinding().contentIncluded.lavTimer.pauseAnimation();
    }

    @Override
    public void goToTriviaStart() {
        startActivity(new Intent(this, TriviaQuestionActivity.class));
        finish();
    }

    @Override
    public void goToPausedCountdown() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_trivia_pause_container, mFragmentTriviaPause)
                .commit();
        pauseCountdown();
    }

    @Override
    public void goToAlreadyPlayed() {
        startActivity(new Intent(this, TriviaAlreadyPlayedActivity.class));
        finish();
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.TRIVIA_WAIT;
    }

    @Override
    public void startAnimation() {
        startCountdown();
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mPresenter.countdownFinished();
    }

    @Override
    public void onAnimationStart(Animator animation) {
        //NO-OP
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
    public void unpauseClicked() {
        goToTriviaStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
