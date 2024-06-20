package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAutoReloadSettings;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPaymentCard;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResult;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingData;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackErrorMapper;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.AutoReloadContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.AutoReloadModel;
import retrofit2.Response;

/**
 * Created by jmsmuy on 11/30/17.
 */

public class AutoReloadPresenter extends BasePresenter<AutoReloadContract.View> implements AutoReloadContract.Presenter {

    private static final String TAG = AutoReloadPresenter.class.getSimpleName();

    private static final BigDecimal[] MONEY_ADD_VALUES = {
            new BigDecimal(25),
            new BigDecimal(50),
            new BigDecimal(75)};
    private static final BigDecimal[] MONEY_THRESHOLD_VALUES = {
            new BigDecimal(15),
            new BigDecimal(20),
            new BigDecimal(25)};
    private static final BigDecimal[] MONEY_OTHER_ADD_VALUES = {
            new BigDecimal(10),
            new BigDecimal(20),
            new BigDecimal(30),
            new BigDecimal(40),
            new BigDecimal(100)};
    private static final BigDecimal[] MONEY_OTHER_THRESHOLD_VALUES = {
            new BigDecimal(10),
            new BigDecimal(50),
            new BigDecimal(75),
            new BigDecimal(100)};

    @Inject
    UserServices mUserServices;

    @Inject
    AmsApi mAmsApi;

    @Inject
    UserAccountService mUserAccountService;

    @Inject
    ErrorMessageMapper mErrorMessageMapper;

    @Inject
    EventLogger mEventLogger;

    private AutoReloadModel mModel;

    public AutoReloadPresenter(AutoReloadContract.View view) {
        super(view);
    }

    @Override
    public void setModel(AutoReloadModel autoReloadModel) {
        mModel = autoReloadModel;
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(AmsResponse data) {
                loadCardData(data.getResult());
                loadAutoReloadData();
                updateShowAddPayment();
            }
        });

    }

    @Override
    public void updateModel() {
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(AmsResponse data) {
                loadCardData(data.getResult());
                updateShowAddPayment();
                configureAutoreload();
            }
        });
    }

    @Override
    public void termsAndConditionsClicked() {
        mEventLogger.logAutoReloadTermsAndConditionsClicked();
        getView().goToTermsAndConditions();
    }

    @Override
    public void loadMoneyAmounts() {
        getView().setupMoneyAmounts(Arrays.asList(MONEY_ADD_VALUES), Arrays.asList(MONEY_OTHER_ADD_VALUES),
                Arrays.asList(MONEY_THRESHOLD_VALUES), Arrays.asList(MONEY_OTHER_THRESHOLD_VALUES));
    }

    private void loadAutoReloadData() {
        BigDecimal incrementAmount = null;
        BigDecimal thresholdAmount = null;
        mModel.setMoneyToAdd(incrementAmount = mUserServices.getAutoReloadIncrementAmount());
        mModel.setLowerThreshold(thresholdAmount = mUserServices.getAutoReloadThresholdAmount());
        getView().setupMoneyAmountsSelected(incrementAmount, thresholdAmount);
    }

    private void loadCardData(AmsResult amsResult) {
        AmsPaymentCard card;
        if (amsResult.getAmsBillingInformation() == null
                || amsResult.getAmsBillingInformation().getPaymentCardList() == null
                || amsResult.getAmsBillingInformation().getPaymentCardList().isEmpty()) {
            return;
        }

        card = amsResult.getAmsBillingInformation().getPaymentCardList().get(0);

        if (card != null) {
            mModel.loadCardData(card);
        }
    }

    @Override
    public void setMoneyAddValue(BigDecimal amount) {
        mModel.setMoneyToAdd(amount);
    }

    @Override
    public void setMoneyThresholdValue(BigDecimal amount) {
        mModel.setLowerThreshold(amount);
    }

    private boolean hasError() {
        return getView().addThisAmountErrorEnabled(mModel.getMoneyToAdd() == null)
                || getView().thresholdAmountErrorEnabled(mModel.getLowerThreshold() == null)
                || getView().tnCErrorEnabled(!mModel.isTermsAndConditions());
    }

    @Override
    public void configureAutoreload() {
        if (hasError()) {
            return;
        }

        UpdateBillingRequest updateBillingRequest = new UpdateBillingRequest();
        updateBillingRequest.setUid(mUserServices.getUid());

        UpdateBillingData updateBillingData = new UpdateBillingData();
        updateBillingData.setAffirmativeConsent(true);

        AmsAutoReloadSettings amsAutoReloadSettings = new AmsAutoReloadSettings();
        amsAutoReloadSettings.setCurrency(AppConstants.CURRENCY_USD);
        amsAutoReloadSettings.setIncrementAmount(mModel.getMoneyToAdd());
        amsAutoReloadSettings.setThresholdAmount(mModel.getLowerThreshold());
        amsAutoReloadSettings.setEnabled(true);

        amsAutoReloadSettings.setPaymentCardId("");

        updateBillingData.setAmsAutoReloadSettings(amsAutoReloadSettings);

        updateBillingRequest.setUpdateBillingData(updateBillingData);

        mAmsApi.updateBilling(updateBillingRequest).enqueue(
                new BaseViewRetrofitErrorMapperCallback<UpdateBillingResponse>(getView(),
                        true, true) {

                    @Override
                    protected void onSuccessViewUpdates(Response<UpdateBillingResponse> response) {
                        mEventLogger.logAutoReloadCompleted();
                        getView().settingsApplied(mModel.getLowerThreshold(), mModel.getMoneyToAdd());
                    }
                });
    }

    @Override
    public void setTermsAndConditions(boolean value) {
        mModel.setTermsAndConditions(value);
    }

    @Override
    public void setUseCardOnFile(boolean cardOnFile) {
        mModel.setUseCardOnProfile(cardOnFile);
        updateShowAddPayment();
    }

    @Override
    public void navigateToAddPayment() {
        if (hasError()) {
            return;
        }
        getView().goToAddPayment(mModel.getToken());
    }

    private void updateShowAddPayment() {
        getView().showAddPayment(!mModel.isUseCardOnProfile() || mModel.getCardNumber() == null);
    }

}
