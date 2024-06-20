package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.order.OrderServerData;

/**
 * Created by asegurola on 4/5/18.
 */

public class OmsOrder implements Serializable, OrderServerData {

    public static final String REQUEST_TYPE_BOUNCE = "bounce";

    public static final String REQUEST_TYPE_PLACE = "place";

    @SerializedName("location_id")
    private String mLocationId;

    @SerializedName("user_id")
    private Long mUserId;

    @SerializedName("id")
    private Long mId;

    @SerializedName("request_type")
    private String mRequestType;

    @SerializedName("status")
    private OmsOrderStatus mStatus;

    @SerializedName("total")
    private BigDecimal mTotal;

    @SerializedName("tax")
    private BigDecimal mTax;

    @SerializedName("pif")
    private BigDecimal mPif;

    @SerializedName("created_at")
    private DateTime mCreatedAt;

    @SerializedName("metadata")
    private OmsOrderMetadata mOmsMetadata;

    @SerializedName("tip")
    private OmsTipOption mOmsTipOptionSelected;

    @SerializedName("line_items")
    private List<OmsOrderItem> mOrderItems = new ArrayList<>();

    @SerializedName("enqueue_in")
    private Long mEnqueuedInMinutes;

    @Deprecated
    @SerializedName("pickup_time")
    private DateTime mPickupTime;

    @SerializedName("pickup")
    private ServerPickupData mPickup;

    @SerializedName("delivery")
    private ServerDeliveryData mDeliveryData;

    @SerializedName("store_name")
    private String mStoreName;

    @SerializedName("store_address")
    private String mStoreAddress;

    @SerializedName("rewards_applied")
    private List<ServerAppliedReward> mRewards;

    @SerializedName("subtotal")
    private BigDecimal mSubtotal;

    @SerializedName("discount")
    private BigDecimal mDiscount;

    @SerializedName("meets_minimum_amount_for_delivery")
    private boolean mMinimumForDeliveryMet;

    public String getLocationId() {
        return mLocationId;
    }

    public void setLocationId(String locationId) {
        mLocationId = locationId;
    }

    public String getRequestType() {
        return mRequestType;
    }

    public void setRequestType(String requestType) {
        mRequestType = requestType;
    }

    public List<OmsOrderItem> getOrderItems() {
        return mOrderItems;
    }

    public void setOrderItems(List<OmsOrderItem> orderItems) {
        mOrderItems = orderItems;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long userId) {
        mUserId = userId;
    }

    public BigDecimal getTotal() {
        return mTotal;
    }

    public void setTotal(BigDecimal total) {
        mTotal = total;
    }

    public DateTime getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        mCreatedAt = createdAt;
    }

    public BigDecimal getTax() {
        return mTax;
    }

    public void setTax(BigDecimal tax) {
        mTax = tax;
    }

    public OmsOrderItem getItemById(int id) {
        for (OmsOrderItem orderItem : mOrderItems) {
            if (orderItem.getId() == id) {
                return orderItem;
            }
        }
        return null;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public OmsOrderMetadata getOmsMetadata() {
        return mOmsMetadata;
    }

    public void setOmsMetadata(OmsOrderMetadata omsMetadata) {
        mOmsMetadata = omsMetadata;
    }

    public OmsOrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OmsOrderStatus status) {
        mStatus = status;
    }

    public Long getEnqueuedInMinutes() {
        return mEnqueuedInMinutes;
    }

    public void setEnqueuedInMinutes(Long enqueuedInMinutes) {
        mEnqueuedInMinutes = enqueuedInMinutes;
    }

    public DateTime getPickupTime() {
        return mPickupTime;
    }

    public void setPickupTime(DateTime pickupTime) {
        mPickupTime = pickupTime;
    }

    public String getStoreName() {
        return mStoreName;
    }

    public void setStoreName(String storeName) {
        mStoreName = storeName;
    }

    public String getStoreAddress() {
        return mStoreAddress;
    }

    public void setStoreAddress(String storeAddress) {
        mStoreAddress = storeAddress;
    }

    public BigDecimal getPif() {
        return mPif;
    }

    public void setPif(BigDecimal pif) {
        mPif = pif;
    }

    public List<ServerAppliedReward> getRewards() {
        return mRewards;
    }

    public void setRewards(List<ServerAppliedReward> rewards) {
        mRewards = rewards;
    }

    public BigDecimal getSubtotal() {
        return mSubtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        mSubtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return mDiscount;
    }

    public void setDiscount(BigDecimal discount) {
        mDiscount = discount;
    }

    public ServerPickupData getPickup() {
        return mPickup;
    }

    public void setPickup(ServerPickupData pickup) {
        mPickup = pickup;
    }

    public ServerDeliveryData getDeliveryData() {
        return mDeliveryData;
    }

    public void setDeliveryData(ServerDeliveryData deliveryData) {
        mDeliveryData = deliveryData;
    }

    public boolean isMinimumForDeliveryMet() {
        return mMinimumForDeliveryMet;
    }

    public void setMinimumForDeliveryMet(boolean minimumForDeliveryMet) {
        mMinimumForDeliveryMet = minimumForDeliveryMet;
    }

    public OmsTipOption getOmsTipOptionSelected() {
        return mOmsTipOptionSelected;
    }

    public void setOmsTipOptionSelected(OmsTipOption omsTipOptionSelected) {
        mOmsTipOptionSelected = omsTipOptionSelected;
    }
}
