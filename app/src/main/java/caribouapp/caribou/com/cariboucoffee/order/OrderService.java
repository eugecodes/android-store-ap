package caribouapp.caribou.com.cariboucoffee.order;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.order.NcrCurbsideIamHere;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsMobileEligibleReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsPOSAgentStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.MoveLoyaltyRequest;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.RewardBannerModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import okhttp3.ResponseBody;

/**
 * Created by asegurola on 4/2/18.
 */
public interface OrderService<OrderItemType extends OrderItem> {

    int ERROR_CODE_PROBLEMS_RECREATING_ORDER = 10;

    int ERROR_CODE_DELIVERY_MINIMUM_NOT_MET = 11;

    void pruneOrderIfEmpty(ResultCallback<Order> callback);

    void pruneOrderIfReOrder(ResultCallback<Order> callback);

    void updateLastActivity();

    void setOrderTimeoutEnabled(boolean enabled);

    void loadCurrentOrder(ResultCallback<Order> callback);

    void createOrderWithLocation(StoreLocation storeLocation, ResultCallback<Order> callback);

    void addItem(OrderItemType orderItem, ResultCallback<OrderItemType> callback);

    void updateItem(OrderItemType orderItem, ResultCallback<OrderItemType> callback);

    void removeItem(OrderItemType orderItem, ResultCallback<OrderItemType> callback);

    void updateQuantity(OrderItemType orderItemType, int newQuantity, ResultCallback<OrderItemType> callback);

    void setOrderListener(OrderListener orderListener);

    void setOrderDropOffListener(OrderDropOffListener dropOffListener);

    void discard(ResultCallback<Order> callback);

    boolean isContinueOrderSupported();

    void checkout(ResultCallback<Order> callback);

    void placeOrder(ResultCallback<Order> callback);

    void checkOmsStatus(ResultCallback<ResponseBody> callback);

    void reorder(RecentOrderModel recentOrderModel, StoreLocation storeLocation, ResultCallback<Order> callback);

    void setPickupTime(PickUpTimeModel pickUpTime);

    void setPreSelectedReward(PreSelectedReward preSelectedReward);

    void clearPreSelectedReward();

    void getProductCustomizations(String omsProdId, ResultCallback<ProductCustomizationData> callback);

    interface OrderListener<OrderItemType> {
        void itemAdded(OrderItemType orderItem);

        void itemChanged(OrderItemType orderItem);

        void itemRemoved(OrderItemType orderItem);

        void subtotalChanged(BigDecimal newSubtotal);
    }

    void getPOSAgentStatus(String storeId, ResultCallback<OmsPOSAgentStatus> callback);

    void applyReward(int rewardId, ResultCallback<Order> callback);

    void clearReward(ResultCallback<Order> callback);

    void loadOrderBanner(ResultCallback<RewardBannerModel> callback);

    void getRecentOrder(int maxDaysAgo, ResultCallback<List<RecentOrderModel>> callback);

    boolean isPickupTimeSupported();

    boolean isRewardsSupported();

    void loadEligibleRewards(RewardsData rewardsData, ResultCallback<List<OmsMobileEligibleReward>> callback);

    boolean shouldDisplayCurbsideIamHere();

    boolean shouldDisplayCurbsideSuccessOrError();

    void sendCurbsideIamHereSignal(ResultCallback<NcrCurbsideIamHere> resultCallback);

    DateTime getCurbsidePickupTime();

    void eraseCurbsideData();

    void waitForCurbsideFinished(Order order, ResultCallback<Order> callback);

    void moveOrderToLoyaltyAccount(String ncrOrderId,
                                   MoveLoyaltyRequest moveLoyaltyRequest,
                                   ResultCallback<NcrOrderWrappedData> callback);

}
