package caribouapp.caribou.com.cariboucoffee.common;

import com.newrelic.agent.android.NewRelic;

import java.net.HttpURLConnection;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsPOSAgentStatus;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model.EnumPOSAgentStatus;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;

/**
 * Created by asegurola on 4/6/18.
 */

public abstract class StartOrderPresenter<T extends StartOrderContract.View> extends BasePresenter<T> implements StartOrderContract.Presenter {

    @Inject
    OrderService mOrderService;

    @Inject
    StoresService mStoresService;

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    EventLogger mEventLogger;

    @Inject
    Clock mClock;

    public StartOrderPresenter(T view) {
        super(view);
    }

    public void createOrder(StoreLocation storeLocation) {
        mOrderService.createOrderWithLocation(storeLocation, new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                mEventLogger.logOrderStarted(false);
                getView().goToProductMenu(storeLocation);
            }
        });
    }

    @Override
    public void loadOrderData() {
        getView().updateOrderAheadEnabled(mSettingsServices.isOrderAhead());
    }

    @Override
    public void startNewOrder(String storeLocationId) {

        mOrderService.getPOSAgentStatus(storeLocationId, new BaseViewResultCallback<OmsPOSAgentStatus>(getView()) {
            @Override
            protected void onSuccessViewUpdates(OmsPOSAgentStatus data) {
                if (data.getStatus() == (EnumPOSAgentStatus.ONLINE)) {
                    checkPreparationTimeAndStartOrder(storeLocationId);
                } else {
                    notAvailable();
                }
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                if (errorCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    notAvailable();
                } else {
                    super.onFailView(errorCode, errorMessage);
                }
            }

            private void notAvailable() {
                IllegalStateException storeStateException = new IllegalStateException("Store status : Offline, Store id :" + storeLocationId);
                NewRelic.recordHandledException(storeStateException);
                getView().showStoreNotAvailableDialog();
            }
        });
    }

    private void checkPreparationTimeAndStartOrder(String storeLocationId) {
        mStoresService.getStoreLocationById(storeLocationId, new BaseViewResultCallback<StoreLocation>(getView()) {
            @Override
            protected void onSuccessViewUpdates(StoreLocation data) {
                if (!data.isOrderOutOfStore()) {
                    getView().showStoreNotOrderOutOfStore();
                } else if (data.isOrderAheadTempOff()) {
                    getView().showStoreTempOff();
                } else if (StoreHoursCheckUtil.isStoreAbleToReceiveOrder(mClock, data, mSettingsServices.getOrderMinutesBeforeClosing())) {
                    getView().createOrder(data);
                } else if (StoreHoursCheckUtil.isStoreOpen(mClock, data)) {
                    getView().showStoreAlmostClosedDialog();
                } else {
                    getView().showStoreClosedDialog();
                }
            }
        });
    }

    public void setOrderService(OrderService orderService) {
        mOrderService = orderService;
    }

    public void setStoresService(StoresService storesService) {
        mStoresService = storesService;
    }

    public void setClock(Clock clock) {
        mClock = clock;
    }

    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public OrderService getOrderService() {
        return mOrderService;
    }

    public void setSettingsServices(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    public EventLogger getEventLogger() {
        return mEventLogger;
    }

    public SettingsServices getSettingsServices() {
        return mSettingsServices;
    }
}
