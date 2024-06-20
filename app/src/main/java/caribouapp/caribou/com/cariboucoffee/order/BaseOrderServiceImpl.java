package caribouapp.caribou.com.cariboucoffee.order;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerAppliedReward;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuDataService;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

public abstract class BaseOrderServiceImpl<TOrderItem extends OrderItem, TOrder extends Order<TOrderItem>> implements OrderService<TOrderItem> {

    private static final String TAG = BaseOrderServiceImpl.class.getSimpleName();

    private final SettingsServices mSettingsServices;

    private OrderDropOffListener mDropOffListener;

    private OrderListener mListener;

    private Clock mClock;

    private MenuDataService mMenuDataService;

    private TOrder mOrder;

    private static final int ORDER_VALIDITY_MINUTES = 30;

    private boolean mOrderTimeoutEnabled;

    public BaseOrderServiceImpl(Clock clock, MenuDataService menuDataService, SettingsServices settingsServices) {
        mClock = clock;
        mMenuDataService = menuDataService;
        mSettingsServices = settingsServices;
    }

    public int getCheckStatusMaxAttempts() {
        return mSettingsServices.getOrderAheadCheckStatusMaxAttempts();
    }

    public TOrder getOrder() {
        return mOrder;
    }

    public void setOrder(TOrder order) {
        mOrder = order;
    }

    @Override
    public void setOrderTimeoutEnabled(boolean enabled) {
        mOrderTimeoutEnabled = enabled;
    }

    @Override
    public void loadCurrentOrder(ResultCallback<Order> callback) {
        try {
            checkIfOrderValid();
            callback.onSuccess(mOrder);
        } catch (RuntimeException e) {
            callback.onError(e);
        }
    }

    /**
     * Checks if the order is still valid, in case it isn't, it deletes it along with the possible
     * persisted version of it.
     * Also updates SelectedRewardId, if the order has been bounced (has an id) updates the value of the
     * SelectedRewardId
     */
    protected void checkIfOrderValid() {
        if (mOrder == null) {
            return;
        }

        DateTime lastValidDateTime = mClock.getCurrentDateTime().minusMinutes(ORDER_VALIDITY_MINUTES);
        if (mOrder.getLastActivity() == null || mOrder.getLastActivity().isBefore(lastValidDateTime) && !mOrderTimeoutEnabled) {
            fireOrderDropOff();
            discard(new ResultCallback<Order>() {
                @Override
                public void onSuccess(Order data) {
                    //  NO-OP
                }

                @Override
                public void onFail(int errorCode, String errorMessage) {
                    Log.e(TAG, new LogErrorException(StringUtils.format("Error %d discarding the order: %s", errorCode, errorMessage)));
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, new LogErrorException("Error discarding order", error));
                }
            });
        }
    }

    /**
     * This method should be called anytime an activity/fragment from the flow where there is a next
     * action to be made on the order
     */
    @Override
    public void updateLastActivity() {
        if (mOrder == null) {
            return;
        }
        mOrder.setLastActivity(mClock.getCurrentDateTime());
    }

    @Override
    public void setOrderDropOffListener(OrderDropOffListener dropOffListener) {
        mDropOffListener = dropOffListener;
    }

    private void fireOrderDropOff() {
        if (mDropOffListener != null) {
            mDropOffListener.orderDropOff(mOrder);
        }
    }

    @Override
    public void discard(ResultCallback<Order> callback) {
        Order order = mOrder;
        mOrder = null;
        callback.onSuccess(order);
    }

    @Override
    public void pruneOrderIfEmpty(ResultCallback<Order> callback) {
        if (mOrder == null) {
            return;
        }
        if (mOrder.getItems() == null || mOrder.getItems().size() == 0) {
            discard(callback);
        }
    }

    public void pruneOrderIfReOrder(ResultCallback<Order> callback) {
        if (mOrder == null) {
            return;
        }
        if (mOrder.isFromReorder()) {
            discard(callback);
        }
    }

    protected void updateDiscountLines(List<ServerAppliedReward> appliedRewards) {
        List<DiscountLine> discountLines = new ArrayList<>();
        if (appliedRewards != null) {
            for (ServerAppliedReward serverAppliedReward : appliedRewards) {
                DiscountLine discountLine = new DiscountLine();
                discountLine.setRewardId(Integer.parseInt(serverAppliedReward.getWalletId()));
                discountLine.setDiscountLine(serverAppliedReward.getDiscountLine());
                discountLine.setAutoApply(serverAppliedReward.isAutoPlay());
                discountLines.add(discountLine);
            }
        }
        mOrder.setDiscountLines(discountLines);
    }

    @Override
    public void setOrderListener(OrderListener orderListener) {
        mListener = orderListener;
    }

    protected void fireItemAdded(TOrderItem orderItem) {
        if (mListener != null) {
            mListener.itemAdded(orderItem);
        }
    }

    protected void fireItemChanged(TOrderItem orderItem) {
        if (mListener != null) {
            mListener.itemChanged(orderItem);
        }
    }

    protected void fireSubtotalChanged() {
        if (mListener != null) {
            mListener.subtotalChanged(mOrder.getSubtotal());
        }
    }

    protected void fireItemRemoved(TOrderItem orderItem) {
        if (mListener != null) {
            mListener.itemRemoved(orderItem);
        }
    }

    public MenuDataService getMenuDataService() {
        return mMenuDataService;
    }

    public Clock getClock() {
        return mClock;
    }

    public void setClock(Clock clock) {
        mClock = clock;
    }

    public void setMenuDataService(MenuDataService menuDataService) {
        mMenuDataService = menuDataService;
    }

    @Override
    public void setPickupTime(PickUpTimeModel pickUpTime) {
        getOrder().setChosenPickUpTime(pickUpTime);
    }

    @Override
    public void setPreSelectedReward(PreSelectedReward preSelectedReward) {
        mOrder.setPreSelectedReward(preSelectedReward);
    }

    @Override
    public void clearPreSelectedReward() {
        mOrder.setPreSelectedReward(null);
        mOrder.setRewardErrorMessage(null);
    }

    public SettingsServices getSettingsServices() {
        return mSettingsServices;
    }

    @Override
    public boolean isRewardsSupported() {
        return mSettingsServices.isOrderAheadRewardsEnabled();
    }

}
