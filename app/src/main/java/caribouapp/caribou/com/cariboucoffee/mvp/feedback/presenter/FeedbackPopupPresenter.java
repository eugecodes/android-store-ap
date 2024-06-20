package caribouapp.caribou.com.cariboucoffee.mvp.feedback.presenter;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.common.ReviewStatusEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.FeedbackContract;
import caribouapp.caribou.com.cariboucoffee.util.FeedbackUtils;

/**
 * Created by jmsmuy on 2/19/18.
 */

public class FeedbackPopupPresenter extends BasePresenter<FeedbackContract.Popup.View> implements FeedbackContract.Popup.Presenter {

    @Inject
    SettingsServices mSettingsServices;

    public FeedbackPopupPresenter(FeedbackContract.Popup.View view) {
        super(view);
    }

    @Override
    public void askMeLater() {
        FeedbackUtils.updateStatus(ReviewStatusEnum.ASK_ME_LATER, mSettingsServices);
        getView().close();
    }

    @Override
    public void dontAskAgain() {
        FeedbackUtils.updateStatus(ReviewStatusEnum.DONT_ASK_ME_AGAIN, mSettingsServices);
        getView().close();
    }

    @Override
    public void goToPlayStore() {
        FeedbackUtils.updateStatus(ReviewStatusEnum.REVIEWED, mSettingsServices);
        getView().close();
    }
}
