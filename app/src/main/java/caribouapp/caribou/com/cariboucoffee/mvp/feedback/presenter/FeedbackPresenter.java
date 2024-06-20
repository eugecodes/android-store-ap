package caribouapp.caribou.com.cariboucoffee.mvp.feedback.presenter;

import android.text.TextUtils;

import java.io.IOException;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAppInfo;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestFeedback;
import caribouapp.caribou.com.cariboucoffee.common.ReviewStatusEnum;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.FeedbackContract;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.model.FeedbackModel;
import caribouapp.caribou.com.cariboucoffee.util.FeedbackUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jmsmuy on 2/19/18.
 */

public class FeedbackPresenter extends BasePresenter<FeedbackContract.Feedback.View> implements FeedbackContract.Feedback.Presenter {

    private static final String BRAND = "Caribou";
    private static final String ANDROID = "Android";
    private static final String TAG = FeedbackPresenter.class.getSimpleName();
    @Inject
    SettingsServices mSettingsServices;

    @Inject
    UserServices mUserServices;

    @Inject
    UserAccountService mUserAccountService;

    @Inject
    AmsApi mAmsApi;

    private FeedbackModel mModel;

    public FeedbackPresenter(FeedbackContract.Feedback.View view, FeedbackModel model) {
        super(view);
        mModel = model;
        getView().selectedRating(mModel.getStars());
    }

    @Override
    public void selectRating(String star) {
        Integer stars = Integer.valueOf(star);
        mModel.setStars(stars);
        getView().selectedRating(stars);
    }

    @Override
    public void sendFeedback() {

        mModel.setConfirmedPressed(true);

        if (mModel.getStars() == null || TextUtils.isEmpty(mModel.getFeedback())) {
            return;
        }
        mUserAccountService.getProfileDataWithCache(new BaseViewResultCallback<Void>(getView()) {

            @Override
            protected void onSuccessViewUpdates(Void data) {
                sendFeedBackApi();
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                onSuccessViewUpdates(null);
            }
        });


    }

    private void sendFeedBackApi() {
        FeedbackUtils.updateStatus(ReviewStatusEnum.REVIEWED, mSettingsServices);
        AmsRequestFeedback amsRequestFeedback = new AmsRequestFeedback();
        AmsAppInfo amsAppInfo = new AmsAppInfo();
        amsAppInfo.setOs(ANDROID);
        amsAppInfo.setOsVersion(String.valueOf(android.os.Build.VERSION.SDK_INT));
        amsAppInfo.setBrand(BRAND);
        amsAppInfo.setVersion(BuildConfig.VERSION_NAME);
        amsRequestFeedback.setAmsAppInfo(amsAppInfo);
        amsRequestFeedback.setFeedbackRating(String.valueOf(mModel.getStars()));
        amsRequestFeedback.setFeedbackText(mModel.getFeedback());
        amsRequestFeedback.setEmail(mUserServices.getEmail());
        amsRequestFeedback.setFirstName(mUserServices.getFirstName());
        amsRequestFeedback.setLastName(mUserServices.getLastName());
        amsRequestFeedback.setMobile(mUserServices.getPhoneNumber());
        mAmsApi.sendFeedback(mUserServices.getUid(), amsRequestFeedback).enqueue(new BaseViewRetrofitCallback<ResponseBody>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Response<ResponseBody> response) {
                try {
                    String stringBody = response.body().string();
                    if (!TextUtils.isEmpty(stringBody)) {
                        Log.e(TAG, new LogErrorException(stringBody));
                    }
                } catch (IOException e) {
                    Log.e(TAG, e);
                } finally {
                    getView().closeFeedback(true);
                }

            }
        });
    }

    @Override
    public void cancelFeedback() {
        FeedbackUtils.updateStatus(ReviewStatusEnum.ASK_ME_LATER, mSettingsServices);
        getView().closeFeedback(false);
    }
}
