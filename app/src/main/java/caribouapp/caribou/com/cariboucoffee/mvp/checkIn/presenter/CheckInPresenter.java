package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseClaimedRewards;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsRequestClaimReward;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsResponse;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.common.RewardsService;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.BarcodeService;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.CheckInContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.CheckInModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.CheckInActivity;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jmsmuy on 11/15/17.
 */

public class CheckInPresenter extends BasePresenter<CheckInContract.View> implements CheckInContract.Presenter {

    private static final String TAG = CheckInPresenter.class.getSimpleName();
    private static final int INSUFFICIENT_FUNDS = 400;
    public static final int HEADER = 1;
    public static final int ITEM = 2;

    @Inject
    SVmsAPI mSVmsAPI;

    @Inject
    CmsApi mCmsApi;

    @Inject
    UserServices mUserServices;

    @Inject
    ErrorMessageMapper mErrorMessageMapper;

    @Inject
    PushManager mPushManager;

    @Inject
    Clock mClock;

    @Inject
    BarcodeService mBarcodeService;

    @Inject
    EventLogger mEventLogger;

    @Inject
    Tagger mTagger;

    @Inject
    RewardsService mRewardsService;

    @Inject
    OrderService mOrderService;

    private CheckInModel mModel;
    private boolean mFinishedLoadingRewards;

    public CheckInPresenter(CheckInActivity view, CheckInModel model) {
        super(view);
        mModel = model;
    }

    @Override
    public void loadData(boolean useCache) {
        loadPointsToDisplay(useCache);
        mFinishedLoadingRewards = false;
        mModel.setShowMessageError(false);
        loadRewards();
    }

    private void loadPointsAndRun(boolean useCache, Runnable runnable) {
        getBalanceCall(useCache).enqueue(new BaseViewRetrofitErrorMapperCallback<SVmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(Response<SVmsResponse> response) {
                mUserServices.setWallet(response.body());
                setBalanceAndCardNumber();
                if (runnable != null) {
                    runnable.run();
                }
            }
        });

    }

    private Call<SVmsResponse> getBalanceCall(boolean useCache) {
        return useCache ? mSVmsAPI.getBalance(new SVmsRequest(mUserServices.getUid(), null))
                : mSVmsAPI.getBalanceNoCache(new SVmsRequest(mUserServices.getUid(), null));
    }

    private void setBalanceAndCardNumber() {
        BigDecimal moneyBalance = mUserServices.getMoneyBalance();
        if (moneyBalance != null) {
            setBalance(moneyBalance);
        }
        String caribouCardNumber = mUserServices.getCaribouCardNumber();
        if (!caribouCardNumber.isEmpty()) {
            setCardNumber(mUserServices.getCaribouCardNumber(), false);
        }
        BigDecimal pointsBalance = mUserServices.getPointsBalance();
        if (pointsBalance != null) {
            mModel.setPoints(pointsBalance);
        }
    }

    private void loadPointsToDisplay(boolean useCache) {
        getBalanceCall(useCache).enqueue(new BaseRetrofitCallback<SVmsResponse>() {

            @Override
            protected void onSuccess(Response<SVmsResponse> response) {
                if (getView() == null) {
                    return;
                }
                try {
                    mUserServices.setWallet(response.body());
                    setBalanceAndCardNumber();
                } catch (RuntimeException e) {
                    Log.e(TAG, e);
                    mModel.setShowMessageError(true);
                }
            }

            @Override
            protected void onFail(Response<SVmsResponse> response) {
                super.onFail(response);
                if (getView() == null) {
                    return;
                }
                mModel.setShowMessageError(true);
            }

            @Override
            protected void onError(Throwable throwable) {
                super.onError(throwable);
                if (getView() == null) {
                    return;
                }
                mModel.setShowMessageError(true);
            }

            @Override
            protected void onNetworkFail(IOException throwable) {
                super.onNetworkFail(throwable);
                if (getView() == null) {
                    return;
                }
                mModel.setShowMessageError(true);
            }

            @Override
            protected void onCallFinished() {
                if (getView() == null) {
                    return;
                }
            }
        });
    }

    public void setCardNumber(String cardNumber, boolean newCardNumber) {
        if (cardNumber == null || "".equals(cardNumber)) {
            Log.e(TAG, new LogErrorException("Trying to set an empty cardnumber: " + cardNumber));
            return;
        }

        if (newCardNumber) {
            mUserServices.setCaribouCardNumber(cardNumber);
            mPushManager.registerForPaytronixPush();
        }

        mModel.setCardNumber(cardNumber);

        getView().showLoadingLayer();
        new GenerateBarcodeAsyncTask(CheckInPresenter.this, mBarcodeService).execute(mModel.getCardNumber());
    }

    public void setBalance(BigDecimal balance) {
        mModel.setBalance(balance);
        mUserServices.setMoneyBalance(balance);

    }

    @Override
    public void addFundsClicked() {
        mEventLogger.logAddFundsStarted();
        getView().goToAddFunds();
    }

    @Override
    public void loadRewards() {
        mFinishedLoadingRewards = false;
        mRewardsService.loadRewards(true, true, false,
                new BaseViewResultCallback<RewardsData>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(RewardsData data) {
                        mFinishedLoadingRewards = true;
                        mModel.calculateRewardList(true, data.getAvailableRewards(), data.getRedeemedRewards());
                        checkRewardsLoaded();
                    }

                    @Override
                    protected void onFailView(int errorCode, String errorMessage) {
                        Log.e(TAG, new LogErrorException("onFail: " + errorCode + "-" + errorMessage));
                        mModel.setShowMessageError(true);
                    }

                    @Override
                    protected void onErrorView(Throwable throwable) {
                        Log.e(TAG, new LogErrorException("onError", throwable));
                        mModel.setShowMessageError(true);
                    }
                });
    }

    private void checkRewardsLoaded() {
        BigDecimal userPoints = mUserServices.getPointsBalance();
        if (!mFinishedLoadingRewards || userPoints == null) {
            return;
        }

        mModel.updateRedeemable(userPoints);
        getView().setCards(mModel.getRewardsOrderedList());
    }

    @Override
    public void redeemReward(RewardItemModel rewardItemModel) {
        mSVmsAPI.claimReward(mUserServices.getUid(),
                new SVmsRequestClaimReward(rewardItemModel.getRewardId(), rewardItemModel.getCode(), String.valueOf(rewardItemModel.getPoints())))
                .enqueue(new BaseViewRetrofitCallback<ResponseBody>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<ResponseBody> response) {
                        try {
                            String stringBody = response.body().string();
                            if (TextUtils.isEmpty(stringBody)) {
                                mEventLogger.logRewardRedeem();
                                mTagger.tagRewardRedeemed();
                                loadPointsAndRun(false, () -> {
                                    loadRewards();
                                    if (rewardItemModel.isOmsMobileEligible()) {
                                        getView().showStartOrderAfterRedeeming(rewardItemModel);
                                    } else {
                                        getView().showSuccessOnRedeem();
                                    }
                                });
                                return;
                            }

                            ResponseClaimedRewards responseClaimedRewards = GsonUtil.defaultGson().fromJson(stringBody, ResponseClaimedRewards.class);
                            if (responseClaimedRewards.getStatusCode() == INSUFFICIENT_FUNDS) {
                                getView().showWarning(R.string.insufficient_funds);
                                return;
                            }

                            getView().showWarning(R.string.error_redeeming_perk);
                        } catch (IOException e) {
                            Log.e(TAG, e);
                            getView().showWarning(R.string.error_redeeming_perk);
                        }
                    }
                });
    }

    @Override
    public void rewardClicked(RewardItemModel rewardItemModel) {
        if (rewardItemModel.isSelectionEnabled()
                && rewardItemModel.isOmsMobileEligible()
                && rewardItemModel.isRedeemed()) {
            getView().goToStartNewOrder(rewardItemModel);
        } else if (rewardItemModel.isBuyable()) {
            getView().showRedeemConfirmation(rewardItemModel);
        }
    }

    private static class GenerateBarcodeAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<CheckInPresenter> mPresenterRef;

        private final WeakReference<BarcodeService> mBarcodeServiceRef;

        GenerateBarcodeAsyncTask(CheckInPresenter presenter, BarcodeService barcodeService) {
            mPresenterRef = new WeakReference<>(presenter);
            mBarcodeServiceRef = new WeakReference<>(barcodeService);
        }

        @Override
        protected Bitmap doInBackground(String... barcodeData) {

            BarcodeService barcodeService = mBarcodeServiceRef.get();
            if (barcodeData == null || barcodeData[0] == null) {
                return null;
            }

            return barcodeService.generateBarcode(barcodeData[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            CheckInPresenter presenter = mPresenterRef.get();
            if (presenter == null || presenter.getView() == null) {
                return;
            }
            presenter.getView().hideLoadingLayer();
            if (bitmap != null) {
                presenter.getView().setBarcode(bitmap);
            }
        }
    }
}
