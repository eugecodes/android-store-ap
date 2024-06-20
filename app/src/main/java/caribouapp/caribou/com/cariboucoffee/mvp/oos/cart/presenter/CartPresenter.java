package caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.presenter;

import java.math.BigDecimal;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.CartContract;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by asegurola on 4/6/18.
 */

public class CartPresenter extends OOSFlowPresenter<CartContract.View> implements CartContract.Presenter {

    private static final String TAG = CartPresenter.class.getSimpleName();

    @Inject
    OrderService mOrderService;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    EventLogger mEventLogger;

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    Clock mClock;

    public CartPresenter(CartContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        mAppDataStorage.setOrderLastScreen(AppScreen.CART);
    }

    @Override
    public void loadData() {
        mOrderService.setOrderListener(new OrderService.OrderListener<OrderItem>() {
            @Override
            public void itemAdded(OrderItem orderItem) {
                // NO-OP
            }

            @Override
            public void itemChanged(OrderItem orderItem) {
                UIUtil.runWithBaseView(getView(), baseView -> getView().updateItem(orderItem));
            }

            @Override
            public void itemRemoved(OrderItem orderItem) {
                UIUtil.runWithBaseView(getView(), baseView -> getView().updateItemRemoved(orderItem));
            }

            @Override
            public void subtotalChanged(BigDecimal newSubtotal) {
                UIUtil.runWithBaseView(getView(), baseView -> getView().updateSubtotal(newSubtotal));
            }
        });
        loadOrder();
    }


    @Override
    protected void setOrder(Order data) {
        if (data.isErrorReplicatingItems()) {
            getView().showErrorOrderNotComplete();
            data.setErrorReplicatingItems(false);
        }
        if (data.shouldShowBulkReOrderPrepTimeDialog()) {
            getView().showBulkOrderDialog();
        }
        if (data.shouldShowMaxQuantityHasChangedDialog()) {
            getView().showMaxQuantityHasChangedDialog();
        }

        //This is to avoid showing dialog after passing through cart the first time after reorder
        data.setMaxQuantityHasChangedDialogEnable(false);
        data.setBulkReOrderPrepTimeDialogEnable(false);

        data.setUseServerSubtotal(false);
        getView().displayOrder(data);
    }

    @Override
    public void updateItemQuantity(OrderItem orderItem, int newQuantity) {
        mOrderService.updateQuantity(orderItem, newQuantity, new BaseViewResultCallback<OrderItem>(getView(), false) {
            @Override
            protected void onSuccessViewUpdates(OrderItem data) {
                // NO-OP
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                super.onFailView(errorCode, errorMessage);
                getView().updateItem(orderItem);
            }

            @Override
            protected void onErrorView(Throwable throwable) {
                super.onErrorView(throwable);
                getView().updateItem(orderItem);
            }
        });
    }

    @Override
    public void removeItem(OrderItem orderItem) {
        mOrderService.removeItem(orderItem, new BaseViewResultCallback<OrderItem>(getView()) {
            @Override
            protected void onSuccessViewUpdates(OrderItem data) {
                // NO-OP
            }
        });
    }

    @Override
    public void checkout() {
        mOrderService.loadCurrentOrder(new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order orderData) {
                if (orderData.getItems().isEmpty()) {
                    getView().showNoItemsMessage();
                    return;
                }
                if (orderData.isBulkOrder()
                        && !orderData.getStoreLocation().enoughTimeForBulkOrder(mClock,
                        mSettingsServices.getBulkPrepTimeInMins(), mSettingsServices.getOrderMinutesBeforeClosing())) {
                    getView().showStoreNearClosingForBulk();
                    return;
                }

                if (!orderData.validateOrderItemQuantity()) {
                    getView().showQuantityLimitDialog();
                    return;
                }

                if (!orderData.validateOrderNotOnlyFreeItems()) {
                    getView().showFreeItemsOnlyNotAllowedDialog(mSettingsServices.getZeroTotalOrderErrorMessage());
                    return;
                }

                mOrderService.checkout(new BaseViewResultCallback<Order>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Order data) {
                        getView().goToCheckout();
                    }
                });
            }
        });
    }

    @Override
    public void discardOrder() {
        mOrderService.discard(new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                try {
                    mEventLogger.logOrderCancelled(mAppDataStorage.getOrderLastScreen(), data.isFromReorder());
                } catch (RuntimeException e) {
                    Log.e(TAG, new LogErrorException("Problems sending orderCancelled analytics", e));
                }
            }
        });
    }

    @Override
    public void cancelOrder() {
        discardOrder();
        getView().goToDashboard();
    }

    @Override
    public void removeReward() {
        mOrderService.clearPreSelectedReward();
    }

    @Override
    protected boolean shouldCheckForOrderDeletion() {
        return true;
    }

    public void setOrderService(OrderService orderService) {
        mOrderService = orderService;
    }

    public void setAppDataStorage(AppDataStorage appDataStorage) {
        mAppDataStorage = appDataStorage;
    }

    public void setEventLogger(EventLogger eventLogger) {
        mEventLogger = eventLogger;
    }

    public void setSettingsServices(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    public void setClock(Clock clock) {
        mClock = clock;
    }
}
