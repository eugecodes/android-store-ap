package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;

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
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.CheckInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view.RecentOrderActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaFinishContract;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaFinishPresenter;

public class TriviaFinishCorrectAnswerActivity extends BaseActivity<ActivityTriviaResultBinding> implements TriviaFinishContract.View {

    private TriviaFinishContract.Presenter mPresenter;

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
        mPresenter.loadBanner();

        getBinding().tvSecondaryTitle.setText(getString(R.string.you_are));
        getBinding().tvSecondarySubtitle.setText(getString(R.string.brilliant));
        getBinding().tvContentInformation.setText(getString(R.string.win_trivia));
        mPresenter.checkOrderAhead();
        mPresenter.logTriviaCorrectAnswer();
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
        startActivity(new Intent(this, CheckInActivity.class));
        finish();
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.TRIVIA_RESULT;
    }

    @Override
    public void updateUI(boolean orderAhead) {
        if (orderAhead) {
            getBinding().triviaFinishPrimaryBtn.setOnClickListener(v -> startNewMobileOrder());
            getBinding().triviaFinishSecondaryBtn.setOnClickListener(v -> userItInStore());
        } else {
            getBinding().triviaFinishPrimaryBtn.setText(getString(R.string.use_it_in_store));
            getBinding().triviaFinishPrimaryBtn.setOnClickListener(v -> userItInStore());
            getBinding().triviaFinishSecondaryBtn.setVisibility(View.GONE);
        }
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

    private void userItInStore() {
        mPresenter.logTriviaUserInStore();
        mPresenter.useInStore();
    }

    private void startNewMobileOrder() {
        mPresenter.logStartNewMobileOrder();
        mPresenter.goToStartAnOrder();

    }
}
