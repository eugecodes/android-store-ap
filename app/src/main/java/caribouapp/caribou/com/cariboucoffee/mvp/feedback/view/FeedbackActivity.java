package caribouapp.caribou.com.cariboucoffee.mvp.feedback.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityFeedbackBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.FeedbackContract;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.model.FeedbackModel;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.presenter.FeedbackPresenter;
import icepick.Icepick;
import icepick.State;

/**
 * Created by jmsmuy on 2/19/18.
 */

public class FeedbackActivity extends BaseActivity<ActivityFeedbackBinding> implements FeedbackContract.Feedback.View {

    private FeedbackContract.Feedback.Presenter mPresenter;

    @State
    FeedbackModel mModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        createStars();
        resetStars();

        if (mModel == null) {
            mModel = new FeedbackModel();
        }

        FeedbackPresenter presenter = new FeedbackPresenter(this, mModel);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;


        getBinding().contentIncluded.btnContinue.setOnClickListener((v) -> mPresenter.sendFeedback());
        getBinding().contentIncluded.btnCancel.setOnClickListener((v) -> mPresenter.cancelFeedback());

        getBinding().contentIncluded.setModel(mModel);

        getBinding().contentIncluded.tvFeedbackTextError.setVisibility(View.GONE);
        getBinding().contentIncluded.tvStarsError.setVisibility(View.GONE);
    }

    private ImageView[] starViews;

    private void createStars() {
        starViews = new ImageView[]{getBinding().contentIncluded.ivStar1,
                getBinding().contentIncluded.ivStar2,
                getBinding().contentIncluded.ivStar3,
                getBinding().contentIncluded.ivStar4,
                getBinding().contentIncluded.ivStar5
        };

        View.OnClickListener starClickListener = v -> {
            mPresenter.selectRating((String) v.getTag());
            getBinding().contentIncluded.tvStarsError.setVisibility(View.GONE);
        };

        for (View star : starViews) {
            star.setOnClickListener(starClickListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }


    @Override
    public void selectedRating(Integer star) {
        if (star == null) {
            resetStars();
            return;
        }
        for (int starIndex = 0; starIndex < starViews.length; starIndex++) {
            activateStar(starViews[starIndex], star > starIndex, star == starIndex + 1, starIndex + 1);
        }
    }

    private void activateStar(ImageView star, boolean activate, boolean announce, int starViewValue) {
        final String starsCD = getResources().getQuantityString(R.plurals.stars_cd, starViewValue, starViewValue);
        final String selectedStr = getString(R.string.selected_cd, starsCD);
        star.setActivated(activate);
        star.setContentDescription(activate ? selectedStr : starsCD);
        if (announce) {
            announceForAccessibility(star, selectedStr);
        }
    }

    private void announceForAccessibility(ImageView ivStar, String message) {
        final AccessibilityEvent event = AccessibilityEvent.obtain();
        event.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
        ivStar.announceForAccessibility(message);
    }


    @Override
    public void closeFeedback(boolean feedbackSuccess) {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.EXTRA_FEEDBACK_SUCCESS, feedbackSuccess);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void resetStars() {
        selectedRating(0);
    }
}
