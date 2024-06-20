package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.AMOUNT_DAYS_RECENT_ORDERS;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.ASAP;

import android.text.TextUtils;

import androidx.databinding.library.baseAdapters.BR;

import com.google.android.gms.maps.model.LatLng;
import com.urbanairship.UAirship;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.analytics.TriviaEventSource;
import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.OAuthAPI;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.TriviaApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsHomeMessage;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.order.NcrCurbsideIamHere;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsBalanceResult;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaCheckResquest;
import caribouapp.caribou.com.cariboucoffee.api.model.trivia.TriviaEligibleResponse;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.CurbsideStatusEnum;
import caribouapp.caribou.com.cariboucoffee.common.MainLooperResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.PeriodicUITask;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.TokenSignInRequest;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsideOrderData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuDataService;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.DateUtil;
import caribouapp.caribou.com.cariboucoffee.util.FeedbackUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import retrofit2.Response;

/**
 * Created by jmsmuy on 9/27/17.
 */

public class DashboardPresenter extends BasePresenter<DashboardContract.View> implements DashboardContract.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();

    private static final long ORDER_STATUS_INTERVAL_MINUTES = 1;
    private static final long CURBSIDE_IAM_HERE_INTERVAL_MINUTES = 1;

    @Inject
    CmsApi mCmsApi;

    @Inject
    Clock mClock;

    @Inject
    UserServices mUserServices;

    @Inject
    UserAccountService mUserAccountService;

    @Inject
    DashboardDataStorage mStorage;

    @Inject
    StoresService mStoresService;

    @Inject
    SVmsAPI mSVmsAPI;

    @Inject
    SettingsServices mSettings;

    @Inject
    OrderService mOrderService;

    @Inject
    PushManager mPushManager;

    @Inject
    MenuDataService mMenuDataService;

    @Inject
    EventLogger mEventLogger;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    Random mRandom;

    @Inject
    Tagger mTagger;

    @Inject
    TriviaApi mTriviaApi;

    @Inject
    OAuthAPI mOAuthAPI;

    private DashboardModel mModel;

    private String mDefaultTitle;

    private boolean mCheckInAlreadyShown = false;
    private ScheduledExecutorService mOrderStatusScheduleService = null;
    private boolean mTriviaFinishLoading;
    private boolean mTimeOfDayFinishLoading;
    private boolean mAlreadyRegisterForPush;

    private PeriodicUITask mCurbsidePeriodicUITask;
    private boolean mEnableEraseData;

    public DashboardPresenter(DashboardContract.View dashboard, DashboardModel model) {
        super(dashboard);
        mModel = model;
    }

    private void setupCurbsidePickup() {
        try {
            CurbsideOrderData curbsideOrderData = mUserServices.getCurbsideOrderData();
            if (curbsideOrderData != null) {
                if (curbsideOrderData.isAsap() && curbsideOrderData.getStatus() != NcrOrderStatus.Finished) {
                    waitForCurbsideFinished(curbsideOrderData);
                } else {
                    setupCurbsidePeriodicUITask();
                    enableCurbsidePeriodicUITask(true);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }


    private void waitForCurbsideFinished(CurbsideOrderData curbsideOrderData) {
        if (mCurbsidePeriodicUITask != null) {
            mCurbsidePeriodicUITask.setEnabled(false);
            mCurbsidePeriodicUITask = null;
        }
        NcrOrder ncrOrder = new NcrOrder(null);
        ncrOrder.setNcrOrderId(curbsideOrderData.getOrderId());
        mOrderService.waitForCurbsideFinished(ncrOrder, new MainLooperResultCallback<Order>() {
                    @Override
                    public void onMainSuccess(Order data) {
                        loadCurbsideIamHere();
                        setupCurbsidePeriodicUITask();
                        enableCurbsidePeriodicUITask(true);
                    }

                    @Override
                    public void onMainError(Throwable error) {
                        eraseCurbsideData();
                        loadCurbsideError();
                        setupCurbsidePeriodicUITask();
                        enableCurbsidePeriodicUITask(true);
                    }

                    @Override
                    public void onMainFail(int errorCode, String errorMessage) {
                        eraseCurbsideData();
                        loadCurbsideError();
                        setupCurbsidePeriodicUITask();
                        enableCurbsidePeriodicUITask(true);
                    }
                }
        );
    }

    private void eraseCurbsideData() {
        if (mEnableEraseData) {
            mOrderService.eraseCurbsideData();
        }
    }

    private void setupCurbsidePeriodicUITask() {
        if (mCurbsidePeriodicUITask != null) {
            enableCurbsidePeriodicUITask(false);
        }
        mCurbsidePeriodicUITask =
                new PeriodicUITask(getView(), CURBSIDE_IAM_HERE_INTERVAL_MINUTES, TimeUnit.MINUTES, () -> {
                    loadCurbsideIamHere();
                });
    }

    public void setRandom(Random random) {
        mRandom = random;
    }

    public void setTriviaApi(TriviaApi triviaApi) {
        mTriviaApi = triviaApi;
    }

    public void setTimeOfDayFinishLoading(boolean timeOfDayFinishLoading) {
        mTimeOfDayFinishLoading = timeOfDayFinishLoading;
    }

    private void showNationalOutageDialog() {
        if (mUserServices.isUserLoggedIn()
                && mSettings.isNationalOutageDialogEnabled()
                && mUserServices.getNationalShortageDialogCounter() < mSettings.getNationalOutageDialogMaxAttempts()) {
            getView().showNationalOutageDialog(mSettings.getNationalOutageDialogTitle(),
                    mSettings.getNationalOutageDialogMessage());
            mUserServices.incrementNationalShortageDialogCounter();
        }
    }

    @Override
    public void pushRegister() {
        if (!mModel.isUserLoggedIn() || mAlreadyRegisterForPush) {
            return;
        }
        mPushManager.registerForPaytronixPush();
        mTagger.tagUserLoggedIn();
        mAlreadyRegisterForPush = true;
    }

    @Override
    public boolean isEnrolledBrandRewardsSystem() {
        return mUserServices.isSubscribedToRewardsProgram(AppUtils.getBrand());
    }

    public void setDefaultTitle(String defaultTitle) {
        mDefaultTitle = defaultTitle;
    }

    @Override
    public void updateUserName() {
        mUserAccountService.getProfileDataWithCache(new BaseViewResultCallback<Void>(getView(), false) {

            @Override
            protected void onSuccessViewUpdates(Void data) {
                mModel.setFirstName(mUserServices.getFirstName());
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                onSuccessViewUpdates(null);
            }

            @Override
            protected void onErrorView(Throwable throwable) {
                onSuccessViewUpdates(null);
            }

        });
        mModel.setFirstName(mUserServices.getFirstName());
    }

    @Override
    public void signOut() {
        mPushManager.unregisterForPush();
        mUserServices.signOut();
        mAlreadyRegisterForPush = false;
        FeedbackUtils.signOut(mSettings);
        mModel.reset();
        disableContinueOrder();
        updateData();
    }

    @Override
    public void updateData() {
        if (getView() == null) {
            return;
        }
        checkForUserLogIn();
        checkLoadingMessagesFinished();
        getView().setupMenuNavigation();
        getView().updateUserLoggedInStatus(mUserServices.isUserLoggedIn());
        if (!mModel.isUserLoggedIn()) {
            enableUpdateOrderStatus(false);
            enableCurbsidePeriodicUITask(false);
            return;
        }
        if (!isEnrolledBrandRewardsSystem()) {
            getView().goToPerksProgramEnrollment();
            return;
        }
        updateUserName();
        updatePerkPoints();
        loadFunds();
        popupFeedbackScreen();
        checkOrderAheadActive();
        if (mOrderService.isContinueOrderSupported()) {
            enableUpdateOrderStatus(true);
        } else {
            disableContinueOrder();
        }
    }

    @Override
    public void startOrContinueOrder() {
        if (mModel.isContinueOrderMode()) {
            getContinueOrderStatus();
        } else {
            if (!mSettings.isReorder()) {
                getView().goToStartNewOrder(new ArrayList<>());
                return;
            }
            if (!mUserServices.isGuestUserFlowStarted() && mUserServices.isUserLoggedIn()) {
                loadRecentOrders();
            }

        }

    }

    private void loadRecentOrders() {
        mOrderService.getRecentOrder(AMOUNT_DAYS_RECENT_ORDERS,
                new BaseViewResultCallback<List<RecentOrderModel>>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(List<RecentOrderModel> recentOrderModels) {
                        if (recentOrderModels != null && !recentOrderModels.isEmpty()) {
                            getView().goToStartNewOrder(recentOrderModels);
                        } else {
                            getView().goToPickLocation();
                        }
                    }

                    @Override
                    protected void onErrorView(Throwable throwable) {
                        getView().goToStartNewOrder(new ArrayList<>());
                    }

                    @Override
                    protected void onFailView(int errorCode, String errorMessage) {
                        getView().goToStartNewOrder(new ArrayList<>());
                    }
                });
    }

    private void getContinueOrderStatus() {
        mOrderService.loadCurrentOrder(new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order order) {
                if (order.isFinished()) {
                    getView().goToConfirmationScreen(order);
                } else {
                    getView().goToContinueOrder(order.getStoreLocation());
                }
            }

            @Override
            public void onFailView(int errorCode, String errorMessage) {
                Log.e(TAG, new LogErrorException("onFail: " + errorCode + "-" + errorMessage));
            }

            @Override
            public void onErrorView(Throwable error) {
                Log.e(TAG, new LogErrorException("onError", error));
            }
        });
    }

    /**
     * This starts a scheduler that every one minute checks the status of the order
     *
     * @param enabled
     */
    @Override
    public void enableUpdateOrderStatus(boolean enabled) {
        if (!mSettings.isOrderAhead()) {
            if (mOrderStatusScheduleService != null) {
                mOrderStatusScheduleService.shutdownNow();
            }
            mOrderStatusScheduleService = null;
            return;
        }

        if (enabled && mOrderStatusScheduleService == null) {
            mModel.setOrderNowLoading(true);
            mOrderStatusScheduleService = Executors.newSingleThreadScheduledExecutor();
            mOrderStatusScheduleService.scheduleAtFixedRate(() -> {
                if (getView() == null) {
                    return;
                }
                getView().runOnUiThread(() ->
                        mOrderService.loadCurrentOrder(new BaseViewResultCallback<Order>(getView(), false) {
                            @Override
                            protected void onSuccessViewUpdates(Order data) {
                                mModel.setOrderNowLoading(false);
                                updateContinueOrderButton(data);
                            }

                            @Override
                            protected void onErrorView(Throwable throwable) {
                                Log.e(TAG, new LogErrorException("onError", throwable));
                                disableContinueOrder();
                            }

                            @Override
                            protected void onFailView(int errorCode, String errorMessage) {
                                Log.e(TAG, new LogErrorException("onFail: " + errorCode + "-" + errorMessage));
                                disableContinueOrder();
                            }
                        }));
            }, 0, ORDER_STATUS_INTERVAL_MINUTES, TimeUnit.MINUTES);
        } else if (!enabled && mOrderStatusScheduleService != null) {
            mOrderStatusScheduleService.shutdownNow();
            mOrderStatusScheduleService = null;
        }
    }

    @Override
    public void onResume() {
        mEnableEraseData = true;
        setupCurbsidePickup();
        enableCurbsidePeriodicUITask(true);
        showNationalOutageDialog();
    }

    @Override
    public void onPause() {
        mEnableEraseData = false;
        enableCurbsidePeriodicUITask(false);
    }

    private void updateContinueOrderButton(Order order) {
        boolean continueOrder = order != null && !order.getItems().isEmpty();
        mModel.setContinueOrderMode(continueOrder);
        getView().setOrderButtonAsContinue(continueOrder);

        if (continueOrder) {
            mModel.setCartItemCount(order.getTotalItemsInCart());
            getView().setCartItems(mModel.getCartItemCount());
        }
    }

    private void disableContinueOrder() {
        mModel.setOrderNowLoading(false);
        mModel.setContinueOrderMode(false);
        getView().setCartItems(null);
        getView().setOrderButtonAsContinue(false);
    }

    @Override
    public void popupFeedbackScreen() {
        try {
            if (FeedbackUtils.shouldShowPopup(mSettings)) {
                getView().goToFeedbackPopUp();
            }
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Error while checking if feedback screen should pop up"));
        }
    }

    @Override
    public void menuOptionsClick() {
        mMenuDataService.clearCache();
        getView().goToMenu();
    }

    @Override
    public SettingsServices getSettings() {
        return mSettings;
    }

    public void setSettings(SettingsServices settings) {
        mSettings = settings;
    }

    @Override
    public boolean shouldAskForLocationPermission() {
        boolean usersFirstTimeOnDashboard = mUserServices.isUsersFirstTimeOnDashboard();
        if (usersFirstTimeOnDashboard) {
            mUserServices.setUsersFirstTimeOnDashboard(false);
        }
        return usersFirstTimeOnDashboard;
    }

    private void loadCurbsideIamHere() {
        if (mSettings.isOrderAhead()
                && mSettings.isImHereCurbSideFeatureEnabled()
                && mOrderService.shouldDisplayCurbsideIamHere()) {
            mModel.setCurbsideIamHereState(CurbsideStatusEnum.IM_HERE);
            mModel.setCurbsidePickupTime(getCurbsidePickupTime());
            mModel.setCurbsideIamHereMessage(getCurbsideMessage());
        } else if (
                mModel.getCurbsideIamHereState() == CurbsideStatusEnum.IM_HERE
                        && !mOrderService.shouldDisplayCurbsideIamHere()) {
            mModel.setCurbsideIamHereState(CurbsideStatusEnum.NONE);
        } else if (
                (mModel.getCurbsideIamHereState() == CurbsideStatusEnum.ERROR
                        || mModel.getCurbsideIamHereState() == CurbsideStatusEnum.SUCCESS)
                        && !mOrderService.shouldDisplayCurbsideSuccessOrError()) {
            mModel.setCurbsideIamHereState(CurbsideStatusEnum.NONE);
        } else {
            mModel.notifyPropertyChanged(BR.curbsideIamHereState);
        }
    }

    private String getCurbsidePickupTime() {
        CurbsideOrderData curbsideOrderData = mUserServices.getCurbsideOrderData();
        if (curbsideOrderData.isAsap()) {
            return ASAP;
        } else {
            return DateUtil.formatHourAMPM(mOrderService.getCurbsidePickupTime());
        }
    }

    private String getCurbsideMessage() {
        return !TextUtils.isEmpty(mUserServices.getCurbsideLocationMessage())
                ? mUserServices.getCurbsideLocationMessage()
                : mSettings.getCurbSideImHereMessage();
    }

    private void loadCurbsideSuccess() {
        mModel.setCurbsideIamHereState(CurbsideStatusEnum.SUCCESS);
        mModel.setCurbsideLocationPhone(null);
        mModel.setCurbsideIamHereMessage(null);
    }

    private void loadCurbsideError() {
        mModel.setCurbsideIamHereState(CurbsideStatusEnum.ERROR);
        mModel.setCurbsideLocationPhone(mUserServices.getCurbsideLocationPhone());
        mModel.setCurbsideIamHereMessage(null);
    }

    private void hideAllCurbsideIamHere() {
        mModel.setCurbsideIamHereState(CurbsideStatusEnum.NONE);
    }

    @Override
    public void checkForUserLogIn() {
        mModel.setUserLoggedIn(mUserServices.isUserLoggedIn());
        if (mUserServices.isUserLoggedIn()) {
            mUserServices.setGuestUserFlowStarted(false);
        }
    }

    @Override
    public void loadFunds() {
        mModel.setLoadPoints(mSettings.isPointsDisplay());

        if (!mModel.isLoadPoints()) {
            return;
        }
        getView().showPerksPointsLoading(true);
        mSVmsAPI.getBalance(new SVmsRequest(mUserServices.getUid(), null))
                .enqueue(new BaseRetrofitCallback<SVmsResponse>() {
                    @Override
                    protected void onSuccess(Response<SVmsResponse> response) {
                        if (getView() == null) {
                            return;
                        }
                        try {
                            SVmsBalanceResult balanceResult = response.body().getBalanceResult();
                            if (balanceResult.getPointsBalance() == null) {
                                Log.e(TAG, new LogErrorException("Error fetching perk points. Empty points data."));
                                return;
                            }
                            mUserServices.setWallet(response.body());
                        } catch (RuntimeException e) {
                            Log.e(TAG, e);
                        }
                    }

                    @Override
                    protected void onCallFinished() {
                        if (getView() == null) {
                            return;
                        }
                        getView().showPerksPointsLoading(false);
                        updatePerkPoints();
                    }
                });
    }

    @Override
    public void checkTriviaAvailable() {
        if (getView() == null) {
            return;
        }
        mModel.setDailyTriviaActive(false);
        mTriviaFinishLoading = false;
        getView().hideTrivia();

        if (!mSettings.isTrivia() || !mUserServices.isUserLoggedIn()) {
            mTriviaFinishLoading = true;
            checkLoadingMessagesFinished();
            return;
        }
        mModel.setDailyTriviaActive(!isUserPlayedTriviaToday());

        if (mModel.isDailyTriviaActive()) {
            mAppDataStorage.setTriviaEventSource(TriviaEventSource.Design1);
            mEventLogger.logTriviaCtaShown(TriviaEventSource.Design1);
        }

        mTriviaFinishLoading = true;
        checkLoadingMessagesFinished();
    }

    @Override
    public void startGuestUserFlow() {
        mUserServices.setGuestUserFlowStarted(true);
    }

    @Override
    public void stopGuestUserFlow() {
        mUserServices.setGuestUserFlowStarted(false);
    }

    @Override
    public boolean isGuestCheckoutEnabledFromDashboard() {
        return mSettings.isGuestCheckoutEnabledFromDashboard();
    }

    @Override
    public void callAnonTokenForGuestUser() {
        /**
         * Generate device id
         * and isAnonymous flag is true for guest checkout
         * */
        mOAuthAPI.authenticate(new TokenSignInRequest(UAirship.shared().getChannel().getId(), true))
                .enqueue(new BaseViewRetrofitCallback<OauthSignInResponse>(getView()) {

                    @Override
                    protected void onSuccessViewUpdates(Response<OauthSignInResponse> response) {
                        OauthSignInResponse oAuthSignInResponse = response.body();
                        saveGuestSignInData(oAuthSignInResponse.getToken(), oAuthSignInResponse.getAud());
                    }

                    @Override
                    protected void onFail(Response<OauthSignInResponse> response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().showWarning(R.string.unknown_error);
                    }
                });
    }

    private boolean isUserPlayedTriviaToday() {
        LocalDate lastPlayedTrivia = mUserServices.getLastPlayedTrivia();
        return lastPlayedTrivia != null && mClock.getCurrentDateTime().toLocalDate().compareTo(lastPlayedTrivia) <= 0;
    }

    @Override
    public void updatePerkPoints() {
        BigDecimal pointsBigDecimal = mUserServices.getPointsBalance();
        if (pointsBigDecimal == null) {
            return;
        }

        int points = pointsBigDecimal.intValue();
        mModel.setPerksPoints(points);
        getView().showPerksPoints(points);
    }

    @Override
    public void loadClosestStore(LatLng currentLocation, double radiusInMilesForCheckin) {
        /*
          This call is intended to get the closet location in a 30miles radius
          Note that the last parameter "pageSize" could have been set to 1 since we only are
          interested in getting the closest one, so a result size of 1 is enough. At the time of
          implementation the momentFeed service wasn't working properly when sending a pageSize
          of 1, it was returning an empty result list, but when using a larger pageSize it did
          return results. So, for now we are leaving the default pageSize value for this call.
         */
        mStoresService.getStoreLocationsNear(1, 0, currentLocation, null,
                AppConstants.LOCATIONS_DEFAULT_SEARCH_RADIUS_IN_MILES,
                null,
                currentLocation,
                new BaseViewResultCallback<List<StoreLocation>>(getView(), false) {
                    @Override
                    protected void onSuccessViewUpdates(List<StoreLocation> stores) {
                        if (stores.isEmpty()) {
                            getView().setClosestStore(null);
                            return;
                        }

                        StoreLocation closestStore = stores.get(0);
                        getView().setClosestStore(closestStore);
                        if (mModel.isUserLoggedIn() && mSettings.isOpenToCheckIn() && !mCheckInAlreadyShown
                                && closestStore.getDistanceInMiles() <= radiusInMilesForCheckin + AppConstants.LOCATIONS_NEAR_BY) {
                            getView().navigateToCheckIn();
                            mCheckInAlreadyShown = true;
                        }
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
    public void checkOrderAheadActive() {
        getView().setOrderButtonVisible(mSettings.isOrderAhead());
    }


    public void setClock(Clock clock) {
        mClock = clock;
    }

    @Override
    public void loadTimeOfDayData() {
        mStorage.loadTimeOfDayRanges(new BaseViewResultCallback<TimeOfDayTimeRanges>(getView(), false) {
            @Override
            protected void onSuccessViewUpdates(TimeOfDayTimeRanges data) {
                mModel.setRanges(data);
                mCmsApi.getHome().enqueue(new BaseRetrofitCallback<CmsHomeMessage>() {
                    @Override
                    protected void onSuccess(Response<CmsHomeMessage> response) {
                        if (getView() == null) {
                            return;
                        }
                        setTimeOfDayMessages(response.body());
                    }

                    @Override
                    protected void onCallFinished() {
                        if (getView() == null) {
                            return;
                        }
                        super.onCallFinished();
                        mTimeOfDayFinishLoading = true;
                        checkLoadingMessagesFinished();
                    }
                });
            }
        });
    }

    @Override
    public void sendCurbsideIamHereSignal() {
        if (mModel.getCurbsideIamHereState() == CurbsideStatusEnum.IM_HERE) {
            CurbsideOrderData curbsideOrderData = mUserServices.getCurbsideOrderData();

            mOrderService.sendCurbsideIamHereSignal(new BaseViewResultCallback<NcrCurbsideIamHere>(getView()) {
                @Override
                protected void onSuccessViewUpdates(NcrCurbsideIamHere ncrCurbsideIamHere) {
                    mEventLogger.logImHereSignal(
                            getView().getScreenName(), curbsideOrderData.getOrderId(),
                            DateUtil.formatLocalTimezone(curbsideOrderData.getPickupTime()),
                            ncrCurbsideIamHere.getIamHereTime());
                    loadCurbsideSuccess();
                }

                @Override
                protected void onFailView(int errorCode, String errorMessage) {
                    loadCurbsideError();
                }

                @Override
                protected void onErrorView(Throwable throwable) {
                    loadCurbsideError();
                }
            });
        }
    }

    @Override
    public void curbsideSuccessGotIt() {
        if (mModel.getCurbsideIamHereState() == CurbsideStatusEnum.SUCCESS) {
            hideAllCurbsideIamHere();
        }
    }

    @Override
    public void curbsideSuccessOrErrorClose() {
        if (mModel.getCurbsideIamHereState() == CurbsideStatusEnum.SUCCESS || mModel.getCurbsideIamHereState() == CurbsideStatusEnum.ERROR) {
            hideAllCurbsideIamHere();
        }
    }

    @Override
    public void checkForTriviaStatus() {
        mTriviaApi.checkEligibility(new TriviaCheckResquest(mUserServices.getUid()))
                .enqueue(new BaseViewRetrofitCallback<TriviaEligibleResponse>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<TriviaEligibleResponse> response) {
                        if (response.body().isEligible()) {
                            getView().goToTriviaCountDown();
                        } else {
                            getView().goToTriviaAlreadyPlayed();
                        }
                    }
                });
    }

    private void setTimeOfDayMessages(CmsHomeMessage homeContent) {
        for (TimeOfDayData timeOfDayData : mModel.getRanges().getRanges()) {
            switch (timeOfDayData.getTimeOfDay()) {
                case LateNight:
                    timeOfDayData.setMessages(homeContent.getLateNight());
                    break;
                case Morning:
                    timeOfDayData.setMessages(homeContent.getMorning());
                    break;
                case Day:
                    timeOfDayData.setMessages(homeContent.getDay());
                    break;
                case Evening:
                    timeOfDayData.setMessages(homeContent.getEvening());
                    break;
                case Night:
                    timeOfDayData.setMessages(homeContent.getNight());
                    break;
                case LoggedOut:
                    timeOfDayData.setMessages(homeContent.getLoggedOut());
            }
        }
        mModel.setTriviaMessages(homeContent.getTriviaMessages());
    }

    @Override
    public void updateTimeOfDayData() {
        if (mModel.getRanges() == null) {
            return;
        }
        TimeOfDayData currentTimeOfDayBackground;
        TimeOfDayData currentTimeOfDayMessage;
        currentTimeOfDayBackground = mModel.calculateCurrentTimeOfDay(mClock);
        getView().setBackgroundStyle(currentTimeOfDayBackground.getTimeOfDay());
        if (mModel.isDailyTriviaActive()) {
            setupHomeMessages(mModel.getTriviaMessages());
        } else {
            currentTimeOfDayMessage = mUserServices.isUserLoggedIn() ? currentTimeOfDayBackground : mModel.getLoggedOutMessage();
            setupHomeMessages(currentTimeOfDayMessage.getMessages());
        }
    }

    private void setupHomeMessages(List<String> messages) {
        String title = mDefaultTitle;

        if (messages != null && !messages.isEmpty()) {
            // Pick a random message for the day.
            String randomMessage = messages.get(mRandom.nextInt(messages.size()));
            if (randomMessage != null && !randomMessage.isEmpty()) {
                title = randomMessage;
            }
        }
        mModel.setTitle(title);
    }

    private void checkLoadingMessagesFinished() {
        if (!mTimeOfDayFinishLoading || !mTriviaFinishLoading) {
            return;
        }
        getView().updateTriviaOnDashboard();
        updateTimeOfDayData();
        if (mSettings.isOrderAhead() && mSettings.isImHereCurbSideFeatureEnabled()) {
            enableCurbsidePeriodicUITask(true);
        }
    }

    private void enableCurbsidePeriodicUITask(boolean enabled) {
        if (mCurbsidePeriodicUITask != null) {
            mCurbsidePeriodicUITask.setEnabled(true);
            if (enabled) {
                loadCurbsideIamHere();
            }
        }
    }

    public void saveGuestSignInData(String token, String uid) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {
            return;
        }
        mUserServices.setAnonAuthToken(token);
        mUserServices.setGuestUid(uid);
        getView().goToPickupLocationScreen();
    }

    @Override
    public DashboardModel getModel() {
        return mModel;
    }

    public void setModel(DashboardModel model) {
        mModel = model;
    }

    public void setUserServices(UserServices userServices) {
        mUserServices = userServices;
    }

    public void setAppDataStorage(AppDataStorage appDataStorage) {
        mAppDataStorage = appDataStorage;
    }

    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public void setOrderService(OrderService orderService) {
        mOrderService = orderService;
    }

    public void setPushManager(PushManager pushManager) {
        mPushManager = pushManager;
    }

    public void setTagger(Tagger tagger) {
        mTagger = tagger;
    }
}
