package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter;


import android.text.TextUtils;

import androidx.databinding.library.baseAdapters.BR;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.model.order.NcrCurbsideIamHere;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.common.CurbsideStatusEnum;
import caribouapp.caribou.com.cariboucoffee.common.MainLooperResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.PeriodicUITask;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsideOrderData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.OrderConfirmationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.ConfirmationModel;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;
import caribouapp.caribou.com.cariboucoffee.util.DateUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by gonzalogelos on 3/15/18.
 */

public class OrderConfirmationPresenter extends BasePresenter<OrderConfirmationContract.View> implements OrderConfirmationContract.Presenter {

    private static final String TAG = OrderConfirmationPresenter.class.getSimpleName();
    private static final long CURBSIDE_IAM_HERE_INTERVAL_MINUTES = 1;
    private final ConfirmationModel mModel;
    @Inject
    EventLogger mEventLogger;
    @Inject
    OrderService mOrderService;
    @Inject
    SettingsServices mSettingsServices;
    @Inject
    UserServices mUserServices;
    private PeriodicUITask mCurbsidePeriodicUITask;
    private boolean mEnableEraseData;

    public OrderConfirmationPresenter(OrderConfirmationContract.View view, ConfirmationModel model) {
        super(view);
        mModel = model;
    }

    @Override
    public void closeClicked() {
        getView().goToDashBoard();
    }

    @Override
    public void imHereSignal() {
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

    private void loadCurbsideSuccess() {
        mModel.setCurbsideIamHereState(CurbsideStatusEnum.SUCCESS);
        mModel.setCurbsideLocationPhone(null);
    }

    private void loadCurbsideError() {
        mModel.setCurbsideIamHereState(CurbsideStatusEnum.ERROR);
        mModel.setCurbsideLocationPhone(mUserServices.getCurbsideLocationPhone());
    }

    @Override
    public void onPause() {
        mEnableEraseData = false;
        enableCurbsidePeriodicUITask(false);
    }

    @Override
    public void onResume() {
        mEnableEraseData = true;
        enableCurbsidePeriodicUITask(mSettingsServices.isOrderAhead() && mSettingsServices.isImHereCurbSideFeatureEnabled());
    }

    @Override
    public void init() {
        if (mModel.getOrder().isDelivery()) {
            getView().setDeliveryTime(mSettingsServices.getPickupDeliveryPrepMessage());
        } else {
            getView().setPickupTime(mModel.getOrder().getChosenPickUpTime().isAsap()
                    ? mSettingsServices.getPickupASAPMessage()
                    : populateScheduleMessageParams(mSettingsServices.getPickupScheduleMessage()));
        }
        showCurbSideImHereTeachingCounter();
        setupCurbsidePickup();
        discardOrder();
    }

    private void showCurbSideImHereTeachingCounter() {
        if (mModel.getOrder().isCurbside()
                && mSettingsServices.isOrderAhead()
                && mSettingsServices.isImHereCurbSideFeatureEnabled()
                && mUserServices.getCurbSideImHereTeachingCounter() < mSettingsServices.getCurbSideImHereTeachingMessageMaxAttempts()) {
            getView().showImHereModal(mSettingsServices.getCurbSideImHereTeachingMessage());
            mUserServices.incrementCurbSideImHereTeachingCounter();
        }
    }

    private void setupCurbsidePickup() {
        try {
            if (mModel.getOrder().isCurbside()
                    && mSettingsServices.isOrderAhead()
                    && mSettingsServices.isImHereCurbSideFeatureEnabled()) {
                if (mModel.getOrder().getChosenPickUpTime().isAsap() && ((NcrOrder) mModel.getOrder()).getStatus() != NcrOrderStatus.Finished) {
                    waitForCurbsideFinished();
                } else {
                    setupCurbsidePeriodicUITask();
                    enableCurbsidePeriodicUITask(true);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    private void waitForCurbsideFinished() {
        mCurbsidePeriodicUITask = null;
        mOrderService.waitForCurbsideFinished(mModel.getOrder(), new MainLooperResultCallback<Order>() {
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
        mCurbsidePeriodicUITask =
                new PeriodicUITask(getView(), CURBSIDE_IAM_HERE_INTERVAL_MINUTES, TimeUnit.MINUTES, () -> {
                    loadCurbsideIamHere();
                });
    }

    private void enableCurbsidePeriodicUITask(boolean enabled) {
        if (mCurbsidePeriodicUITask != null) {
            mCurbsidePeriodicUITask.setEnabled(enabled);
        }
    }

    private void loadCurbsideIamHere() {
        if (mModel.getCurbsideIamHereState() == CurbsideStatusEnum.NONE
                && mSettingsServices.isOrderAhead()
                && mSettingsServices.isImHereCurbSideFeatureEnabled()
                && mOrderService.shouldDisplayCurbsideIamHere()) {
            mModel.setCurbsideIamHereState(CurbsideStatusEnum.IM_HERE);
            mModel.setCurbsideIamHereMessage(getCurbsideMessage());
        } else if (
                mModel.getCurbsideIamHereState() == CurbsideStatusEnum.IM_HERE
                        && !mOrderService.shouldDisplayCurbsideIamHere()) {
            mModel.setCurbsideIamHereState(CurbsideStatusEnum.NONE);
        } else {
            mModel.notifyPropertyChanged(BR.curbsideIamHereState);
        }
    }

    private String getCurbsideMessage() {
        return !TextUtils.isEmpty(mUserServices.getCurbsideLocationMessage())
                ? mUserServices.getCurbsideLocationMessage()
                : mSettingsServices.getCurbSideImHereMessage();
    }

    public void autoReloadClicked() {
        mEventLogger.logAutoReloadStarted();
        getView().goToAutoReload();
    }

    private void discardOrder() {
        ResultCallback<Order> discardCallback = new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                // NO-OP
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Log.e(TAG, new LogErrorException(StringUtils.format("Error %d discarding the order: %s", errorCode, errorMessage)));
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, new LogErrorException("Error discarding order", error));
            }
        };
        mOrderService.discard(discardCallback);
    }

    private String populateScheduleMessageParams(String scheduleMessage) {
        return scheduleMessage.replace(":pickupTime:", mModel.getOrder().getChosenPickUpTime().toString());
    }

    @Override
    public boolean isThisGuestFlow() {
        return (!mUserServices.isUserLoggedIn() && mUserServices.isGuestUserFlowStarted());
    }


    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public void setOrderService(OrderService orderService) {
        mOrderService = orderService;
    }

    public void setSettingsServices(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    public void setUserServices(UserServices userServices) {
        mUserServices = userServices;
    }
}
