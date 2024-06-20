package caribouapp.caribou.com.cariboucoffee.mvp.feedback.view;

import android.content.Intent;
import android.os.Bundle;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityFeedbackPopUpBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.FeedbackContract;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.presenter.FeedbackPopupPresenter;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;

/**
 * Created by jmsmuy on 2/19/18.
 */

public class FeedbackPopupActivity extends BaseActivity<ActivityFeedbackPopUpBinding> implements FeedbackContract.Popup.View {

    private FeedbackContract.Popup.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback_pop_up;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FeedbackPopupPresenter presenter = new FeedbackPopupPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        getBinding().contentIncluded.btnImYourBiggestFan.setOnClickListener((v) -> {
            mPresenter.goToPlayStore();
            AppUtils.goToPlayStore(this);
        });
        getBinding().contentIncluded.btnIveGotSomeIdeasForYou.setOnClickListener((v) -> goToFeedbackActivity());
        getBinding().contentIncluded.btnImNotReady.setOnClickListener((v) -> mPresenter.askMeLater());
        getBinding().contentIncluded.btnDontAskAgain.setOnClickListener((v) -> mPresenter.dontAskAgain());
    }

    private void goToFeedbackActivity() {
        startActivityForResult(new Intent(this, FeedbackActivity.class), AppConstants.REQUEST_CODE_FEEDBACK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == AppConstants.REQUEST_CODE_FEEDBACK
                && data.getBooleanExtra(AppConstants.EXTRA_FEEDBACK_SUCCESS, false)) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.askMeLater();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void close() {
        finish();
    }
}
