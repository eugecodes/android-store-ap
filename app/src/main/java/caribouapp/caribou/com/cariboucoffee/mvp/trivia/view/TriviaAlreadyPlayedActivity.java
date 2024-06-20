package caribouapp.caribou.com.cariboucoffee.mvp.trivia.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsBanner;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityTriviaResultBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.TriviaFinishContract;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaFinishPresenter;

public class TriviaAlreadyPlayedActivity extends BaseActivity<ActivityTriviaResultBinding> implements TriviaFinishContract.View {

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

        getBinding().triviaFinishPrimaryBtn.setText(getString(R.string.take_me_to_the_app));
        getBinding().triviaFinishPrimaryBtn.setOnClickListener(v -> mPresenter.takeMeToApp());
        getBinding().triviaFinishSecondaryBtn.setVisibility(View.GONE);

        getBinding().tvSecondaryTitle.setText(getString(R.string.you_already));
        getBinding().tvSecondarySubtitle.setText(getString(R.string.played_today));
        getBinding().tvContentInformation.setText(getString(R.string.already_played_trivia_today));

        mPresenter.logTriviaAlreadyPlayed();
    }

    @Override
    public void goToDashboard() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void goToPickALocation() {
        //NO-OP
    }

    @Override
    public void goToUseInStore() {
        //NO-OP
    }

    @Override
    public void updateUI(boolean orderAhead) {
        //NO-OP
    }

    @Override
    public void setBanner(CmsBanner banner) {
        getBinding().setBannerModel(banner);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
