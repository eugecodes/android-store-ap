package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.common.BaseResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 17/04/18.
 */

public abstract class OOSFlowPresenter<T extends OOSFlowContract.View> extends BasePresenter<T> implements OOSFlowContract.Presenter {

    private static final String TAG = OOSFlowPresenter.class.getSimpleName();

    @Inject
    OrderService mOrderService;

    @Inject
    EventLogger mEventLogger;

    @Inject
    AppDataStorage mAppDataStorage;

    public OOSFlowPresenter(T view) {
        super(view);
    }

    @Override
    public void loadOrder() {
        mOrderService.loadCurrentOrder(new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                if (data == null) {
                    getView().goToDashboard();
                    return;
                }
                setOrder(data);
            }
        });
    }

    protected abstract void setOrder(Order data);

    @Override
    public void orderTimeoutEnabled(boolean enabled) {
        mOrderService.setOrderTimeoutEnabled(enabled);
    }

    @Override
    public void updateLastActivity() {
        mOrderService.updateLastActivity();
    }

    /**
     * Called only when escaping the order flow
     * Checks if order should be deleted
     */
    @Override
    public void checkForOrderDeletion() {
        if (shouldCheckForOrderDeletion()) {
            mOrderService.pruneOrderIfEmpty(new BaseResultCallback<>());
            mOrderService.pruneOrderIfReOrder(new BaseViewResultCallback<Order>(getView()) {
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
    }

    /**
     * We can override this function if we want to enable this behaviour
     *
     * @return
     */
    protected boolean shouldCheckForOrderDeletion() {
        return false;
    }

    protected OrderService getOrderService() {
        return mOrderService;
    }

    public void setOrderService(OrderService orderService) {
        mOrderService = orderService;
    }
}
