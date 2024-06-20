package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.OrderCheckoutContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.AmountTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.RewardBannerModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.PickupData;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by gonzalogelos on 4/5/18.
 */

public class OrderCheckoutPresenter extends OOSFlowPresenter<OrderCheckoutContract.View>
        implements OrderCheckoutContract.Presenter {

    private static final String TAG = OrderCheckoutPresenter.class.getSimpleName();
    private static final int MINUTE_INTERVAL_BETWEEN_PICK_UP_OPTIONS = 10;
    private static final int MINUTE_TO_ROUND = 10;
    private final CheckoutModel mModel;
    @Inject
    SVmsAPI mSVmsAPI;
    @Inject
    Tagger mTagger;
    @Inject
    UserServices mUserServices;
    @Inject
    OrderService mOrderService;
    @Inject
    AppDataStorage mAppDataStorage;
    @Inject
    Clock mClock;
    @Inject
    EventLogger mEventLogger;
    @Inject
    ErrorMessageMapper mErrorMessageMapper;
    @Inject
    SettingsServices mSettingsServices;
    @Inject
    OrderNavHelper mOrderNavHelper;

    public OrderCheckoutPresenter(OrderCheckoutContract.View view, CheckoutModel model) {
        super(view);
        mModel = model;
    }

    @Override
    public void init() {
        mAppDataStorage.setOrderLastScreen(AppScreen.CHECKOUT);
        getView().setShowPickupTimeField(mOrderService.isPickupTimeSupported());
        getView().setPickupCurbsideTipMessage(mSettingsServices.getPickupCurbsideTipMessage());
        getView().setDeliveryMessage(mSettingsServices.getPickupDeliveryPrepMessage());
        showNationalOutageDialog();
    }

    private void showNationalOutageDialog() {
        if (mUserServices.isUserLoggedIn() && mSettingsServices.isNationalOutageDialogEnabled()) {
            getView().showNationalOutageDialog(mSettingsServices.getNationalOutageDialogTitle(),
                    mSettingsServices.getNationalOutageDialogMessage());
        }
    }

    @Override
    public void loadPerkPoints() {
        if (mUserServices.isUserLoggedIn() && !mUserServices.isGuestUserFlowStarted()) {
            mSVmsAPI.getBalance(new SVmsRequest(mUserServices.getUid(), null))
                    .enqueue(new BaseViewRetrofitErrorMapperCallback<SVmsResponse>(getView(),
                            true) {
                        @Override
                        protected void onSuccessViewUpdates(Response<SVmsResponse> response) {
                            mUserServices.setWallet(response.body());
                            mModel.setRewardsCardBalance(mUserServices.getMoneyBalance());
                            checkEnoughFunds();
                        }
                    });
        } else {
            mModel.setRewardsCardBalance(new BigDecimal("0"));
            checkEnoughFunds();
        }

    }

    public void editPickupTime() {
        List<PickUpTimeModel> options = new ArrayList<>();
        Order order = mModel.getOrder();
        options.addAll(calculateTimesForPickup(order.getStoreLocation().getClosingTime(mClock)));
        getView().showPickUpTimesDialog(options);
    }

    private void setDefaultPickupTime() {
        mModel.getOrder().setChosenPickUpTime(getFirstPickUpTimeModel());
    }

    private void loadInitialBulkPickupTime() {
        getView().showAsapNotAvailable();
        if (mModel.getOrder().getChosenPickUpTime().isAsap()) {
            setDefaultPickupTime();
        }
    }

    private boolean isValidSelectedPickupTime(PickUpTimeModel pickupTime) {
        return pickupTime.isAsap() && !mModel.getOrder().isBulkOrder() || !pickupTime.getPickUpTime().isBefore(getFirstTimeForPickUp());
    }

    private LocalTime getFirstTimeForPickUp() {
        return roundUp(mClock.getCurrentTime())
                .plusMinutes(isBulkOrder()
                        ? mSettingsServices.getBulkPrepTimeInMins() : mSettingsServices.getPickupTimeFirstOptionOffset());
    }

    private LocalTime roundUp(LocalTime timeToRoundUp) {
        timeToRoundUp = timeToRoundUp.withMillisOfSecond(0).withSecondOfMinute(0);
        int minutesToRoundUp = MINUTE_TO_ROUND - timeToRoundUp.get(DateTimeFieldType.minuteOfHour()) % MINUTE_TO_ROUND;
        if (minutesToRoundUp != MINUTE_TO_ROUND) {
            timeToRoundUp = timeToRoundUp.plusMinutes(minutesToRoundUp);
        }
        return timeToRoundUp;
    }

    private PickUpTimeModel getFirstPickUpTimeModel() {
        return isBulkOrder() ? new PickUpTimeModel(getFirstTimeForPickUp(), false) : new PickUpTimeModel(true);

    }

    @Override
    public void addedFunds() {
        mEventLogger.logOrderFundsAdded();
        checkEnoughFunds();
        placeOrder();
    }

    @Override
    public void addAvailableReward() {
        mOrderService
                .applyReward(
                        mModel.getRewardBannerModel().getRewardId(),
                        new BaseViewResultCallback<Order>(getView()) {
                            @Override
                            protected void onSuccessViewUpdates(Order data) {
                                mEventLogger.logOrderWithRewardApplied();
                                mTagger.tagOrderAheadWithReward();
                                mModel.setOrder(data);
                                updateSelectedTipOption();
                                checkEnoughFunds();
                                getView().displayOrderData(data);
                                getView().hideRewardErrorBanner();
                                getView().anchorViewToReward();
                            }
                        });
    }

    @Override
    public void removeReward() {
        mOrderService.clearReward(new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                updateSelectedTipOption();
                checkEnoughFunds();
                getView().displayOrderData(data);
                getView().anchorViewToBannerOrTop();
                loadBanner();
                mOrderService.clearPreSelectedReward();
            }
        });
    }

    @Override
    public void loadBanner() {
        if (!mOrderService.isRewardsSupported()) {
            return;
        }
        mOrderService.loadOrderBanner(new BaseViewResultCallback<RewardBannerModel>(getView()) {
            @Override
            protected void onSuccessViewUpdates(RewardBannerModel banner) {
                mModel.setRewardBannerModel(banner);
            }

            @Override
            protected void onErrorView(Throwable throwable) {
                Log.e(TAG, new LogErrorException("onError", throwable));
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                Log.e(TAG, new LogErrorException("onFail: " + errorCode + "-" + errorMessage));
            }
        });
    }

    @Override
    public void editPickupType() {
        getView().showPickupTypeScreen();
    }

    @Override
    public void selectCustomTip() {
        getView().showCustomTipDialog(mModel.getOrder().getChosenTipOption(), mModel.getOrder().getTotalWithoutTip());
    }

    /**
     * This algorithm completes the current time until the next % 10 == 0 minutes arrive, then adds 10 extra minutes
     * then lists all the 10 minute interval times, until the store closes
     *
     * @param closingHour
     * @return
     */
    private List<PickUpTimeModel> calculateTimesForPickup(LocalTime closingHour) {
        List<PickUpTimeModel> pickUpTimes = new ArrayList<>();
        if (closingHour == null) { // If closing hour is null, the store is closed all day
            return pickUpTimes;
        }
        if (!isBulkOrder()) {
            pickUpTimes.add(new PickUpTimeModel(true));
        }
        LocalTime firstPickUpTime = getFirstTimeForPickUp();
        LocalDateTime nextPickUpTime = new LocalDate().toLocalDateTime(firstPickUpTime);
        int currentDay = nextPickUpTime.getDayOfWeek();

        while (isBeforeClosingTime(nextPickUpTime.toLocalTime(), closingHour) && currentDay == nextPickUpTime.getDayOfWeek()) {
            pickUpTimes.add(new PickUpTimeModel(nextPickUpTime.toLocalTime()));
            nextPickUpTime = nextPickUpTime.plusMinutes(MINUTE_INTERVAL_BETWEEN_PICK_UP_OPTIONS);
        }
        return pickUpTimes;
    }

    private boolean isBeforeClosingTime(LocalTime currentTime, LocalTime closingHour) {
        return !currentTime.isAfter(closingHour.minusMinutes(mSettingsServices.getOrderMinutesBeforeClosing()
                + (mModel.getOrder().isBulkOrder() ? mSettingsServices.getBulkPrepTimeInMins() : 0)));
    }

    private void loadPerksPointNoCacheAndGoToConfirmation() {
        mSVmsAPI.getBalanceNoCache(new SVmsRequest(mUserServices.getUid(), null))
                .enqueue(new BaseViewRetrofitErrorMapperCallback<SVmsResponse>(getView(),
                        true, true) {
                    @Override
                    protected ErrorMessageMapper buildErrorMessageMapper() {
                        return mErrorMessageMapper;
                    }

                    @Override
                    protected void onSuccessViewUpdates(Response<SVmsResponse> response) {
                        mUserServices.setWallet(response.body());
                        mModel.setRewardsCardBalance(mUserServices.getMoneyBalance());
                        getView().goToConfirmation(mModel.getOrder());
                    }

                    @Override
                    protected void onError(Throwable throwable) {
                        Log.e(TAG, new LogErrorException("Failed to load getBalance after placeOrder", throwable));
                        if (getView() == null) {
                            return;
                        }
                        getView().goToConfirmation(mModel.getOrder());
                    }

                    @Override
                    protected void onNetworkFail(IOException throwable) {
                        onError(throwable);
                    }

                    @Override
                    protected void onFail(Response<SVmsResponse> response) {
                        Log.e(TAG, new LogErrorException("Failed to load getBalance after placeOrder: " + response.code()));
                        if (getView() == null) {
                            return;
                        }
                        getView().goToConfirmation(mModel.getOrder());
                    }
                });
    }

    @Override
    public void checkEnoughFunds() {
        if (mUserServices.isUserLoggedIn() && !mUserServices.isGuestUserFlowStarted()) {
            mModel.setRewardsCardBalance(mUserServices.getMoneyBalance());
            if (mModel.getOrder() == null || mModel.getRewardsCardBalance() == null) {
                return;
            }
            boolean enoughFunds = mModel.hasEnoughFounds();

            if (!enoughFunds) {
                mEventLogger.logOrderNotEnoughFunds();
            }

            getView().updateMoneyBalance(enoughFunds, mModel.getRewardsCardBalance());
        } else {
            getView().updateMoneyBalance(false, new BigDecimal("0"));
        }

    }

    @Override
    public void addFundsClicked() {
        Order order = mModel.getOrder();
        if (order.isDelivery() && !order.isMinimumForDeliveryMet()) {
            getView().showDeliveryMinimumNotMetDialog(mModel.getOrder().getStoreLocation().getDeliveryMinimum());
            return;
        }

        mEventLogger.logAddFundsStarted();
        getView().goToAddFunds(mModel.getOrder().getTotalWithTip().subtract(mUserServices.getMoneyBalance()));
    }

    public void placeOrder() {
        Order order = mModel.getOrder();

        if (!isValidSelectedPickupTime(order.getChosenPickUpTime())) {
            getView().showNotValidSelectedPickupTime();
            setDefaultPickupTime();
            return;
        }
        if (!StoreHoursCheckUtil.isStoreAbleToReceiveOrder(mClock,
                order.getStoreLocation(), mSettingsServices.getOrderMinutesBeforeClosing())) {
            getView().showStoreClosedDialog();
            return;
        }
        if (order.isBulkOrder() && !order.getStoreLocation().enoughTimeForBulkOrder(mClock,
                mSettingsServices.getBulkPrepTimeInMins(), mSettingsServices.getOrderMinutesBeforeClosing())) {
            getView().showStoreNearClosingForBulk();
            return;
        }

        if (order.isDelivery()) {
            if (!StoreHoursCheckUtil.isDeliveryOpen(mClock, order.getStoreLocation())) {
                getView().showDeliveryClosedDialog();
                return;
            }

            if (!order.isMinimumForDeliveryMet()) {
                getView().showDeliveryMinimumNotMetDialog(mModel.getOrder().getStoreLocation().getDeliveryMinimum());
                return;
            }
        }

        if (!order.getChosenPickUpTime().isAsap()) {
            if (order.getChosenPickUpTime().getPickUpTime().isBefore(mClock.getCurrentTime())) {
                getView().showChooseANewPickUpTimeDialog();
                return;
            }
            mOrderService.setPickupTime(order.getChosenPickUpTime());
        }

        mOrderService.checkOmsStatus(new BaseViewResultCallback<ResponseBody>(getView()) {

            @Override
            protected void onSuccessViewUpdates(ResponseBody data) {
                placeOrderService();
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                getView().showFailToPlaceOrderDialog();
                Log.e(TAG, new LogErrorException("On Fail : The oms connection check failed"));
            }

            @Override
            protected void onErrorView(Throwable throwable) {
                getView().showFailToPlaceOrderDialog();
                Log.e(TAG, new LogErrorException("On Error : The oms connection check failed"));
            }
        });
    }

    private void placeOrderService() {
        mOrderService.placeOrder(new BaseViewResultCallback<Order>(getView(), true, true) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                mModel.setOrder(data);
                sendToConfirmationScreen();
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                if (errorCode == OrderService.ERROR_CODE_DELIVERY_MINIMUM_NOT_MET) {
                    getView().showDeliveryMinimumNotMetDialog(mModel.getOrder().getStoreLocation().getDeliveryMinimum());
                } else {
                    getView().showFailToPlaceOrderDialog();
                }
            }

            @Override
            protected void onErrorView(Throwable throwable) {
                if (throwable instanceof SocketTimeoutException) {
                    sendToConfirmationScreen();
                    return;
                }
                getView().showFailToPlaceOrderDialog();
            }
        });
    }

    private void sendToConfirmationScreen() {
        // NOTE This null check is to support backwards compatibility with already saved data.
        try {
            Order order = mModel.getOrder();
            boolean asapOrder = order.getChosenPickUpTime() == null || order.getChosenPickUpTime().isAsap();
            mEventLogger.logOrderCompleted(order.isFromReorder(), order.isEdited(), asapOrder, order.getTotalWithTip());
            trackOrderPickUpType(order.getPickupData());
            trackBulkOrder(order);
            tagUserWithOrder(order);
            if (!asapOrder) {
                mEventLogger.logOrderNotAsap();
            }
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Problems sending orderCompleted analytics", e));
        }
        loadPerksPointNoCacheAndGoToConfirmation();
    }

    private void tagUserWithOrder(Order order) {
        mTagger.tagOrderAheadUser();
        if (order.getChosenPickUpTime() == null || order.getChosenPickUpTime().isAsap()) {
            return;
        }
        mTagger.tagOrderAheadPickup();
    }

    private void trackOrderPickUpType(PickupData pickupData) {
        if (pickupData == null || pickupData.getYextPickupType() == null) {
            return;
        }
        YextPickupType yextPickupType = pickupData.getYextPickupType();
        mEventLogger.logOrderPickUpType(yextPickupType);
        mTagger.tagPickUpOrder(yextPickupType);
    }

    private void trackBulkOrder(Order order) {
        if (!order.isBulkOrder()) {
            return;
        }
        mTagger.tagBulkOrder();
        mEventLogger.logBulkOrder();
    }

    @Override
    public void cancelOrder() {
        mOrderService.discard(new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                try {
                    mEventLogger.logOrderCancelled(mAppDataStorage.getOrderLastScreen(), data.isFromReorder());
                } catch (RuntimeException e) {
                    Log.e(TAG, new LogErrorException("Problems sending orderCancelled analytics", e));
                }
                getView().goToDashboard();
            }
        });
    }

    @Override
    protected void setOrder(Order data) {
        mModel.setOrder(data);
        mModel.getOrder().setUseServerSubtotal(true);

        if (!mModel.isBounceRequired()
                && (data.getPreSelectedReward() == null
                || (mModel.getOrder().getRewardErrorMessage() != null
                && !mModel.getOrder().getRewardErrorMessage().isEmpty()))
                && !mUserServices.isGuestUserFlowStarted()) {
            loadBanner();
        }

        getView().displayOrderData(mModel.getOrder());
        checkEnoughFunds();

        if (isBulkOrder()) {
            loadInitialBulkPickupTime();
        }

        getView().setShowPickupLocationField(mOrderNavHelper.requiresPickupData(data.getStoreLocation()));
        loadTippingAmountsValues();
        updateSelectedTipOption();
        checkBounceRequired();
    }

    private void updateSelectedTipOption() {
        TippingOption tippingOption = mModel.getOrder().getChosenTipOption();
        if (tippingOption != null) {
            getView().showChosenTip(tippingOption, tippingOption.calculateTip(mModel.getOrder()));
        }
    }

    private void checkBounceRequired() {
        if (!mModel.isBounceRequired()) {
            return;
        }

        mModel.setBounceRequired(false);
        mOrderService.checkout(new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                Log.d(TAG, "Required bounce finished.");
                setOrder(data);
            }
        });
    }

    private void loadTippingAmountsValues() {
        if (!mSettingsServices.isTippingEnabled() || !mModel.getOrder().getStoreLocation().isAcceptsTips()) {
            return;
        }

        List<TippingOption> tippingOptions = new ArrayList<>();
        for (BigDecimal amountTip : mSettingsServices.getTippingMainOptions()) {
            tippingOptions.add(new AmountTipOption(amountTip));
        }
        getView().setupTippingOptions(tippingOptions);
        getView().showTipping();
    }

    private boolean isBulkOrder() {
        return mSettingsServices.isBulkOrderingEnabled() && mModel.getOrder().isBulkOrder();
    }

    //Use only for test purpose
    public void setClock(Clock clock) {
        mClock = clock;
    }

    public void setOrderService(OrderService orderService) {
        mOrderService = orderService;
    }

    public void setSettingService(SettingsServices settingService) {
        mSettingsServices = settingService;
    }

    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public void setSVmsAPI(SVmsAPI sVmsAPI) {
        mSVmsAPI = sVmsAPI;
    }

    public void setUserServices(UserServices userServices) {
        mUserServices = userServices;
    }

    public void setTagger(Tagger tagger) {
        mTagger = tagger;
    }

    public void setSettingsServices(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    public void setErrorMessageMapper(ErrorMessageMapper errorMessageMapper) {
        mErrorMessageMapper = errorMessageMapper;
    }

    public void setAppDataStorage(AppDataStorage appDataStorage) {
        mAppDataStorage = appDataStorage;
    }

    public void setOrderNavHelper(OrderNavHelper orderNavHelper) {
        mOrderNavHelper = orderNavHelper;
    }

    @Override
    public void setTippingOption(TippingOption tippingOption) {
        mModel.getOrder().setChosenTipOption(tippingOption);
        checkEnoughFunds();
        getView().showChosenTip(tippingOption, tippingOption == null ? null : tippingOption.calculateTip(mModel.getOrder()));
    }

    @Override
    public boolean isThisGuestFlow() {
        return (!mUserServices.isUserLoggedIn() && mUserServices.isGuestUserFlowStarted());
    }

    @Override
    public boolean isContinueAsGuestCheckEnabled() {
        StoreLocation store = mModel.getOrder().getStoreLocation();
        boolean storeGCEnabled = store != null && store.isGuestCheckoutEnabled();
        return mSettingsServices.isContinueAsGuestCheckEnabled() && storeGCEnabled;
    }

    @Override
    public CheckoutModel getCheckoutModel() {
        return mModel;
    }

    @Override
    public void displayNationalOutageDialog() {
        showNationalOutageDialog();
    }
}
