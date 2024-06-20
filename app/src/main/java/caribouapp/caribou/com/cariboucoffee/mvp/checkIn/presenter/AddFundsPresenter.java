package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPaymentCard;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResult;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.AddFundsData;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.AddFundsRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.AddFundsResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackErrorMapper;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.AddFundsContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.AddFundsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.AddFundsActivity;
import retrofit2.Response;

/**
 * Created by jmsmuy on 11/28/17.
 */

public class AddFundsPresenter extends BasePresenter<AddFundsContract.View> implements AddFundsContract.Presenter {

    private static final BigDecimal[] OTHER_AMOUNTS = {
            new BigDecimal(10),
            new BigDecimal(20),
            new BigDecimal(30),
            new BigDecimal(40)};
    private static final BigDecimal[] CHECKOUT_OTHER_AMOUNTS = {
            new BigDecimal(25),
            new BigDecimal(50),
            new BigDecimal(75)};

    @Inject
    UserServices mUserServices;

    @Inject
    SVmsAPI mSVmsAPI;

    @Inject
    UserAccountService mUserAccountService;

    @Inject
    ErrorMessageMapper mErrorMessageMapper;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    EventLogger mEventLogger;

    @Inject
    Tagger mTagger;

    @Inject
    SettingsServices mSettingsServices;

    private AddFundsModel mModel;

    private boolean mIsCheckoutAddFunds;

    private BigDecimal mAmountNeededForOrder;

    private BigDecimal checkoutMinimum;

    public AddFundsPresenter(AddFundsActivity view, boolean isCheckoutAddFunds, BigDecimal amountNeedForOrder) {
        super(view);
        mIsCheckoutAddFunds = isCheckoutAddFunds;
        mAmountNeededForOrder = amountNeedForOrder;
    }

    @Override
    public void init() {
        if (mIsCheckoutAddFunds) {
            mAppDataStorage.setOrderLastScreen(AppScreen.ADD_FUNDS);
        }
        checkoutMinimum = new BigDecimal(String.valueOf(mSettingsServices.getAddFundsCheckoutMinAmount()));
        checkoutMinimum = checkoutMinimum.setScale(2);
    }

    @Override
    public void setMoneyValue(BigDecimal value) {
        mModel.setMoneyToAdd(value);
        recalculateBalance();
    }

    private void recalculateBalance() {
        BigDecimal currentBalance = mUserServices.getMoneyBalance();
        if (currentBalance == null || mModel == null || mModel.getMoneyToAdd() == null) {
            return;
        }
        mModel.setNewBalance(currentBalance.add(mModel.getMoneyToAdd()));
        hasErrors();
    }

    @Override
    public void setModel(AddFundsModel addFundsModel) {
        mModel = addFundsModel;
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(AmsResponse data) {
                loadCardData(data.getResult());
                updateShowAddPayment();
            }
        });
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
    public void setUseCardOnFile(boolean cardOnFile) {
        mModel.setUseCardOnProfile(cardOnFile);
        updateShowAddPayment();
    }

    private void updateShowAddPayment() {
        if (getView() == null) {
            return;
        }
        getView().showAddPayment(mModel.getCardNumber() == null || !mModel.isUseCardOnProfile());
    }

    @Override
    public void addFundsOneTime(String token) {
        AddFundsRequest addFundsRequest = new AddFundsRequest();
        addFundsRequest.setUid(mUserServices.getUid());
        addFundsRequest.setAddFundsData(new AddFundsData(mModel.getMoneyToAdd(), token));
        addFundsInternal(addFundsRequest);
    }


    @Override
    public void addFundsFromCardOnFile(String token) {
        resetErrors();
        if (hasErrors()) {
            return;
        }
        if (hasEnoughFounds()) {
            getView().showInsufficientAlertDialog();
            return;
        }

        AddFundsRequest addFundsRequest = new AddFundsRequest();
        addFundsRequest.setUid(mUserServices.getUid());
        addFundsRequest.setAddFundsData(new AddFundsData(mModel.getMoneyToAdd(), token));
        addFundsInternal(addFundsRequest);
    }

    private void addFundsInternal(AddFundsRequest addFundsRequest) {
        mSVmsAPI.addFunds(addFundsRequest).enqueue(
                new BaseViewRetrofitErrorMapperCallback<AddFundsResponse>(getView(), true,
                        true) {

                    @Override
                    protected void onSuccessViewUpdates(Response<AddFundsResponse> response) {
                        // This extra call to showLoading should no be necessary is here as a workaround to
                        // prevent any accidental call to add funds while the app is transitioning back to the previous AppScreen.
                        getView().showLoadingLayer();

                        // Successful call
                        BigDecimal balance = response.body().getAddFundsResult().getBalance();
                        mUserServices.setMoneyBalance(balance);
                        mEventLogger.logAddFundsCompleted();
                        mTagger.tagAddFunds();
                        getView().finishedSuccessfulAddFunds(balance, mModel.getMoneyToAdd());
                    }
                });
    }


    @Override
    public void navigateToAddPayment() {
        resetErrors();
        if (hasErrors()) {
            return;
        }
        if (hasEnoughFounds()) {
            getView().showInsufficientAlertDialog();
            return;
        }
        getView().goToAddPayment(mModel.getToken());
    }

    /**
     * This method returns the money options for when the user came from checkout
     * Logic:
     * if the order is less than 5 bucks, 5 bucks is initial value
     * else it's the minimum amount to fulfill the order
     * then the next 2 values are the next multiples of 5
     * the 2 after those are the generated adding 10 to the previous
     *
     * @return
     */
    @Override
    public List<BigDecimal> getCheckoutAddFundsValues(BigDecimal amountNeededForOrder) {
        List<BigDecimal> checkoutMoneyAmounts = new ArrayList<>();
        BigDecimal five = new BigDecimal("5.00");
        BigDecimal ten = new BigDecimal("10.00");
        // First we decide which should be the first value
        BigDecimal value = amountNeededForOrder.compareTo(checkoutMinimum) < 0 ? checkoutMinimum : amountNeededForOrder;
        checkoutMoneyAmounts.add(value);
        // Now we go to the next multiple of 5
        value = value.subtract(value.remainder(five)).add(five);
        checkoutMoneyAmounts.add(value);
        // And the next multiple of 5
        value = value.add(five);
        checkoutMoneyAmounts.add(value);
        // Add 10
        value = value.add(ten);
        checkoutMoneyAmounts.add(value);
        // Lastly add another 10
        checkoutMoneyAmounts.add(value.add(ten));

        return checkoutMoneyAmounts;
    }

    @Override
    public void loadMoneyItems() {
        if (mIsCheckoutAddFunds) {
            getView().setupMoneyItems(getCheckoutAddFundsValues(mAmountNeededForOrder), null);
            // Now we also check if the amount needed is less than the minimum, if it is we alert the user
            mModel.setMinimumAmountToAddAlert(mAmountNeededForOrder.compareTo(checkoutMinimum) < 0);
        } else {
            getView().setupMoneyItems(Arrays.asList(CHECKOUT_OTHER_AMOUNTS), Arrays.asList(OTHER_AMOUNTS));
        }
    }

    private boolean resetErrors() {
        return getView().amountToAddErrorEnabled(false);
    }

    private boolean hasErrors() {
        return getView().amountToAddErrorEnabled(mModel.getMoneyToAdd() == null || mModel.getMoneyToAdd().compareTo(BigDecimal.ZERO) <= 0);
    }

    private boolean hasEnoughFounds() {
        return mIsCheckoutAddFunds && mModel.getMoneyToAdd().compareTo(mAmountNeededForOrder) < 0;
    }

    // Remove when Dagger Test configuration available
    public void setSettingsServices(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    // Remove when Dagger Test configuration available
    public void setAppDataStorage(AppDataStorage appDataStorage) {
        mAppDataStorage = appDataStorage;
    }
}
