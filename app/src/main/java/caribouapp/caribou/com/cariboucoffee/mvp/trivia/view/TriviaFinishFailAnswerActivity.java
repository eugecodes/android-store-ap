package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsBanner;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityTriviaResultBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view.RecentOrderActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaFinishContract;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaFinishPresenter;

public class TriviaFinishFailAnswerActivity extends BaseActivity<ActivityTriviaResultBinding> implements TriviaFinishContract.View {

    private TriviaFinishContract.Presenter mPresenter;
    private static final String WRONG_ANSWER = "wrong_answer";


    public static Intent createIntent(Context context, boolean wrongAnswer) {
        Intent intent = new Intent(context, TriviaFinishFailAnswerActivity.class);
        intent.putExtra(WRONG_ANSWER, wrongAnswer);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trivia_result;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TriviaFinishPresenter presenter = new TriviaFinishPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;
        mPresenter.checkOrderAhead();
        mPresenter.loadBanner();

        if (isWrongAnswer()) {
            mPresenter.logTriviaWrongAnswer();
        } else {
            mPresenter.logTriviaTimeout();
        }
    }

    @Override
    public void goToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void goToPickALocation() {
        startActivity(new Intent(this, RecentOrderActivity.class));
        finish();
    }

    @Override
    public void goToUseInStore() {
        //NO-OP
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.TRIVIA_RESULT;
    }

    private boolean isWrongAnswer() {
        return getIntent().getBooleanExtra(WRONG_ANSWER, false);
    }

    @Override
    public void updateUI(boolean orderAhead) {
        if (isWrongAnswer()) {
            getBinding().tvSecondaryTitle.setText(getString(R.string.try_again));
            getBinding().tvSecondarySubtitle.setText(getString(R.string.tomorrow_with_exclamation));
        } else {
            getBinding().tvSecondaryTitle.setText(getString(R.string.sorry));
            getBinding().tvSecondarySubtitle.setText(getString(R.string.times_up));
        }
        if (orderAhead) {
            getBinding().triviaFinishPrimaryBtn.setOnClickListener(v -> mPresenter.goToStartAnOrder());
            getBinding().triviaFinishSecondaryBtn.setOnClickListener(v -> mPresenter.takeMeToApp());
            getBinding().triviaFinishSecondaryBtn.setText(R.string.take_me_to_the_app);
        } else {
            getBinding().triviaFinishPrimaryBtn.setText(getString(R.string.take_me_to_the_app));
            getBinding().triviaFinishPrimaryBtn.setOnClickListener(v -> mPresenter.takeMeToApp());
            getBinding().triviaFinishSecondaryBtn.setVisibility(View.GONE);
        }
        getBinding().tvContentInformation.setText(getString(R.string.lose_trivia));


    }

    @Override
    public void setBanner(CmsBanner banner) {
        getBinding().setBannerModel(banner);
        getBinding().ivBannerTriviaBlur.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
