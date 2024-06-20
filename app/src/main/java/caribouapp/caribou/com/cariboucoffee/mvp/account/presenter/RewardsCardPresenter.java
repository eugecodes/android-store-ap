package caribouapp.caribou.com.cariboucoffee.mvp.account.presenter;

import java.math.BigDecimal;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAutoReloadSettings;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsBillingInformation;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsStoredValueCard;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingData;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackErrorMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.RewardsCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.AutoReloadPresenter;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;
import retrofit2.Response;

/**
 * Created by gonzalogelos on 9/6/18.
 */

public class RewardsCardPresenter extends BasePresenter<AccountContract.RewardsCardView> implements AccountContract.RewardsCardPresenter {

    private static final String TAG = AutoReloadPresenter.class.getSimpleName();
    @Inject
    UserServices mUserServices;
    @Inject
    AmsApi mAmsApi;
    @Inject
    UserAccountService mUserAccountService;
    @Inject
    SVmsAPI mSVmsAPI;
    @Inject
    PushManager mPushManager;
    @Inject
    EventLogger mEventLogger;

    private RewardsCardModel mRewardsCardModel;

    public RewardsCardPresenter(AccountContract.RewardsCardView view, RewardsCardModel model) {
        super(view);
        mRewardsCardModel = model;
    }

    @Override
    public void loadData() {
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {
            @Override
            protected void onSuccessViewUpdates(AmsResponse response) {
                AmsBillingInformation amsBillingInformation = response.getResult().getAmsBillingInformation();
                if (amsBillingInformation.getAmsStoredValueCardList() == null) {
                    return;
                }
                AmsStoredValueCard amsStoredValueCard = amsBillingInformation.getAmsStoredValueCardList().get(0);
                mRewardsCardModel.setCardNumber(amsStoredValueCard.getCardNumber());
                AmsAutoReloadSettings amsAutoReloadSettings = amsStoredValueCard.getAmsAutoReloadSettings();
                mRewardsCardModel.setIncrementAmount(amsAutoReloadSettings.getIncrementAmount());
                mRewardsCardModel.setThresholdAmount(amsAutoReloadSettings.getThresholdAmount());
                mRewardsCardModel.setAutoReloadEnabled(amsAutoReloadSettings.getEnabled());
                getBalance();
            }

        });
    }

    private void getBalance() {
        mSVmsAPI.getBalance(new SVmsRequest(mUserServices.getUid(), null)).enqueue(
                new BaseViewRetrofitErrorMapperCallback<SVmsResponse>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<SVmsResponse> response) {
                        SVmsResponse sVmsResponse = response.body();
                        mUserServices.setWallet(sVmsResponse);
                        mRewardsCardModel.setBalance(sVmsResponse.getBalanceResult().getBalanceAmount());
                        getView().setAutoReload(mRewardsCardModel.getAutoReloadEnabled(),
                                mRewardsCardModel.getThresholdAmount(), mRewardsCardModel.getIncrementAmount());
                        getView().setCardNumber(mRewardsCardModel.getLastFourCardDigit());
                    }
                });
    }

    @Override
    public void autoReloadClicked(boolean enabled) {
        if (enabled) {
            mEventLogger.logAutoReloadStarted();
            getView().goToAutoReloadSetting();
        } else {
            setAutoReloadOff();
        }
    }

    private void setAutoReloadOff() {
        mAmsApi.updateBilling(buildAutoreloadOffUpdateBillingRequest()).enqueue(
                new BaseViewRetrofitErrorMapperCallback<UpdateBillingResponse>(getView(), true, true) {

                    @Override
                    protected void onSuccessViewUpdates(Response<UpdateBillingResponse> response) {
                        mRewardsCardModel.setAutoReloadEnabled(false);
                        getView().setAutoReload(false, null, null);
                    }
                });
    }

    @Override
    public void setBalance(BigDecimal newBalance) {
        mUserServices.setMoneyBalance(newBalance);
        mRewardsCardModel.setBalance(newBalance);
    }

    @Override
    public void setAutoReload(boolean enabled) {
        mRewardsCardModel.setAutoReloadEnabled(enabled);
    }

    @Override
    public void updateCardNumber() {
        //NOTE : Call getprofile so we can ge the correct cardNumber, webview response with *************0123
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {
            @Override
            protected void onSuccessViewUpdates(AmsResponse data) {
                AmsStoredValueCard amsStoredValueCard = data.getResult().getAmsBillingInformation().getAmsStoredValueCardList().get(0);
                mUserServices.setCaribouCardNumber(amsStoredValueCard.getCardNumber());
                mPushManager.registerForPaytronixPush();
                mRewardsCardModel.setCardNumber(amsStoredValueCard.getCardNumber());
                getView().setCardNumber(mRewardsCardModel.getLastFourCardDigit());
            }
        });
    }

    private UpdateBillingRequest buildAutoreloadOffUpdateBillingRequest() {
        UpdateBillingRequest updateBillingRequest = new UpdateBillingRequest();
        updateBillingRequest.setUid(mUserServices.getUid());
        UpdateBillingData updateBillingData = new UpdateBillingData();
        updateBillingData.setAffirmativeConsent(true);
        AmsAutoReloadSettings amsAutoReloadSettings = new AmsAutoReloadSettings();
        amsAutoReloadSettings.setCurrency(AppConstants.CURRENCY_USD);
        amsAutoReloadSettings.setEnabled(false);
        amsAutoReloadSettings.setPaymentCardId("");
        updateBillingData.setAmsAutoReloadSettings(amsAutoReloadSettings);
        updateBillingRequest.setUpdateBillingData(updateBillingData);
        return updateBillingRequest;
    }
}
