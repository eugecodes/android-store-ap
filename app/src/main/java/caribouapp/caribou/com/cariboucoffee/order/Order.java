package caribouapp.caribou.com.cariboucoffee.order;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerDeliveryData;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerPickupData;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsidePickupData;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;

/**
 * Created by asegurola on 4/4/18.
 */

public abstract class Order<ItemType extends OrderItem> extends BaseObservable implements Serializable {

    @SerializedName("storeLocation")
    private StoreLocation mStoreLocation;

    @SerializedName("total")
    @Bindable
    private BigDecimal mTotalWithoutTip;

    @SerializedName("items")
    private List<ItemType> mItems = new ArrayList<>();

    @SerializedName("taxes")
    private BigDecimal mTaxes;

    @SerializedName("pif")
    private BigDecimal mPif;

    @SerializedName("deliveryFee")
    private BigDecimal mDeliveryFee;

    @SerializedName("lastActivity")
    private DateTime mLastActivity;

    @SerializedName("useServerSubtotal")
    private boolean mUseServerSubtotal;

    @SerializedName("errorReplicatingItems")
    private boolean mErrorReplicatingItems;

    @SerializedName("fromReorder")
    private boolean mFromReorder;

    @SerializedName("edited")
    private boolean mEdited;

    @SerializedName("discountLines")
    private List<DiscountLine> mDiscountLines = new ArrayList<>();

    private transient PreSelectedReward mPreSelectedReward;

    private boolean mBulkReOrderPrepTimeDialogEnable = true;

    private boolean mMaxQuantityHasChangedDialogEnable = true;

    @SerializedName("subtotal")
    private BigDecimal mSubtotal;

    @SerializedName("chosenPickUpTime")
    private PickUpTimeModel mChosenPickUpTime = new PickUpTimeModel(true);

    @SerializedName("pickupData")
    private PickupData mPickupData;

    @SerializedName("deliveryData")
    private DeliveryData mDeliveryData;

    @SerializedName("meetsMinimumAmountForDelivery")
    private boolean mMinimumForDeliveryMet;

    @SerializedName("rewardErrorMessage")
    private String mRewardErrorMessage;

    @SerializedName("chosenTip")
    private TippingOption mChosenTipOption;

    @Bindable
    private BigDecimal mTotalWithTip;

    public Order(StoreLocation storeLocation) {
        mStoreLocation = storeLocation;
    }

    public StoreLocation getStoreLocation() {
        return mStoreLocation;
    }

    public void setStoreLocation(StoreLocation storeLocation) {
        mStoreLocation = storeLocation;
    }

    @SerializedName("errorMaxQuantityHasChanged")
    private boolean mErrorMaxQuantityHasChanged;

    @Bindable
    public List<ItemType> getItems() {
        return mItems;
    }

    public void setItems(List<ItemType> items) {
        mItems = items;
        notifyPropertyChanged(BR.totalItemsInCart);
    }

    public void addItem(ItemType orderItem) {
        mItems.add(orderItem);
        notifyPropertyChanged(BR.totalItemsInCart);
    }

    @Bindable
    public BigDecimal getSubtotal() {
        return mUseServerSubtotal ? mSubtotal : getPrecalculatedSubtotal();
    }

    public BigDecimal getPrecalculatedSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : mItems) {
            subtotal = subtotal.add(item.getSubtotal());
        }
        return subtotal;
    }

    public BigDecimal getTotalWithTip() {
        TippingOption selectedTipOption = getChosenTipOption();
        if (selectedTipOption == null || getChosenTipOption().calculateTip(this) == null) {
            return mTotalWithoutTip;
        } else {
            return mTotalWithoutTip.add(getChosenTipOption().calculateTip(this));
        }
    }

    public BigDecimal getTotalWithoutTip() {
        return mTotalWithoutTip;
    }

    public void setTotal(BigDecimal total) {
        mTotalWithoutTip = total;
        notifyPropertyChanged(BR.totalWithoutTip);
        notifyPropertyChanged(BR.totalWithTip);
    }

    public BigDecimal getTaxes() {
        return mTaxes;
    }

    public void setTaxes(BigDecimal taxes) {
        mTaxes = taxes;
    }

    public DateTime getLastActivity() {
        return mLastActivity;
    }

    public void setLastActivity(DateTime lastActivity) {
        mLastActivity = lastActivity;
    }

    @Bindable
    public int getTotalItemsInCart() {
        int itemCount = 0;
        for (OrderItem orderItem : mItems) {
            itemCount += orderItem.getQuantity();
        }
        return itemCount;
    }

    public boolean isErrorReplicatingItems() {
        return mErrorReplicatingItems;
    }

    public void setErrorReplicatingItems(boolean errorReplicatingItems) {
        mErrorReplicatingItems = errorReplicatingItems;
    }

    public void setUseServerSubtotal(boolean useServerSubtotal) {
        mUseServerSubtotal = useServerSubtotal;
    }

    @Bindable
    public PickUpTimeModel getChosenPickUpTime() {
        return mChosenPickUpTime;
    }

    public void setChosenPickUpTime(PickUpTimeModel chosenPickUpTime) {
        mChosenPickUpTime = chosenPickUpTime;
        notifyPropertyChanged(BR.chosenPickUpTime);
    }

    public TippingOption getChosenTipOption() {
        return mChosenTipOption;
    }

    public void setChosenTipOption(TippingOption chosenTipOption) {
        mChosenTipOption = chosenTipOption;
        notifyPropertyChanged(BR.totalWithTip);
    }

    public boolean isFromReorder() {
        return mFromReorder;
    }

    public void setFromReorder(boolean fromReorder) {
        mFromReorder = fromReorder;
    }

    public boolean isEdited() {
        return mEdited;
    }

    public void setEdited(boolean edited) {
        mEdited = edited;
    }

    @Bindable
    public BigDecimal getPif() {
        return mPif;
    }

    public void setPif(BigDecimal pif) {
        mPif = pif;
        notifyPropertyChanged(BR.pif);
    }


    public void setSubtotal(BigDecimal subtotal) {
        mSubtotal = subtotal;
    }

    @Bindable
    public PreSelectedReward getPreSelectedReward() {
        return mPreSelectedReward;
    }

    public void setPreSelectedReward(PreSelectedReward preSelectedReward) {
        mPreSelectedReward = preSelectedReward;
        notifyPropertyChanged(BR.preSelectedReward);
    }

    @Bindable
    public List<DiscountLine> getDiscountLines() {
        return mDiscountLines;
    }

    public void setDiscountLines(List<DiscountLine> discountLines) {
        mDiscountLines = discountLines;
        notifyPropertyChanged(BR.discountLines);
    }

    public DiscountLine findDiscountLineForRewardId(int rewardId) {
        for (DiscountLine discountLine : mDiscountLines) {
            if (discountLine.getRewardId() == rewardId) {
                return discountLine;
            }
        }
        return null;
    }

    public boolean isAllAutoAppliedDiscountLines() {
        for (DiscountLine discountLine : mDiscountLines) {
            if (!discountLine.isAutoApply()) {
                return false;
            }
        }
        return true;
    }

    public boolean isBulkOrder() {
        for (ItemType item : mItems) {
            if (item.getMenuData().isBulk()) {
                return true;
            }
        }
        return false;
    }

    public abstract boolean isFinished();

    public abstract boolean isNew();

    public abstract boolean orderItemPassesMaxQuantityCheck(OrderItem selectedOrderItem, int newQuantity);

    public abstract boolean validateOrderItemQuantity();

    public abstract boolean validateOrderNotOnlyFreeItems();

    public boolean shouldShowBulkReOrderPrepTimeDialog() {
        return this.isBulkOrder() && this.isFromReorder() && mBulkReOrderPrepTimeDialogEnable;
    }

    public boolean shouldShowMaxQuantityHasChangedDialog() {
        return this.isErrorMaxQuantityHasChanged() && mMaxQuantityHasChangedDialogEnable;
    }

    public boolean isMaxQuantityHasChangedDialogEnable() {
        return mMaxQuantityHasChangedDialogEnable;
    }

    public boolean isBulkReOrderPrepTimeDialogEnable() {
        return mBulkReOrderPrepTimeDialogEnable;
    }

    public void setBulkReOrderPrepTimeDialogEnable(boolean bulkReOrderPrepTimeDialogEnable) {
        mBulkReOrderPrepTimeDialogEnable = bulkReOrderPrepTimeDialogEnable;
    }

    public void setMaxQuantityHasChangedDialogEnable(boolean maxQuantityHasChangedDialogEnable) {
        mMaxQuantityHasChangedDialogEnable = maxQuantityHasChangedDialogEnable;
    }

    public PickupData getPickupData() {
        return mPickupData;
    }

    public void setPickupData(PickupData pickupData) {
        mPickupData = pickupData;
    }

    public DeliveryData getDeliveryData() {
        return mDeliveryData;
    }

    public void setDeliveryData(DeliveryData deliveryData) {
        mDeliveryData = deliveryData;
    }

    protected ServerDeliveryData toServerDeliveryData() {
        if (getDeliveryData() == null || !isDelivery()) {
            return null;
        }
        return getDeliveryData().toServerDeliveryData();

    }

    public boolean isMinimumForDeliveryMet() {
        return mMinimumForDeliveryMet;
    }

    public void setMinimumForDeliveryMet(boolean minimumForDeliveryMet) {
        mMinimumForDeliveryMet = minimumForDeliveryMet;
    }

    public boolean isDelivery() {
        return getPickupData() != null && getPickupData().getYextPickupType() == YextPickupType.Delivery;
    }

    public boolean isCurbside() {
        return getPickupData() != null && getPickupData().getYextPickupType() == YextPickupType.Curbside;
    }

    public DateTime buildPickupTime(Clock clock) {
        LocalTime pickupTime = getChosenPickUpTime().getPickUpTime();
        return clock.getCurrentDateTime()
                .withHourOfDay(pickupTime.getHourOfDay())
                .withMinuteOfHour(pickupTime.getMinuteOfHour())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }

    public ServerPickupData buildPickupData(Clock clock) {
        ServerPickupData serverPickupData = new ServerPickupData();
        serverPickupData.setType(YextPickupType.WalkIn.getServerName());

        PickupData pickupData = getPickupData();

        if (pickupData != null) {
            serverPickupData.setType(pickupData.getYextPickupType().getServerName());
            if (pickupData.getYextPickupType() == YextPickupType.Curbside) {
                CurbsidePickupData curbsidePickupData = pickupData.getCurbsidePickupData();
                serverPickupData.setCarColor(curbsidePickupData.getCarColor());
                serverPickupData.setCarType(curbsidePickupData.getCarType());
                serverPickupData.setCarMake(curbsidePickupData.getCarMake());
            }
        }

        if (!getChosenPickUpTime().isAsap()) {
            serverPickupData.setPickupTime(buildPickupTime(clock));
        }
        return serverPickupData;
    }

    public BigDecimal getDeliveryFee() {
        return mDeliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        mDeliveryFee = deliveryFee;
    }

    public String getRewardErrorMessage() {
        return mRewardErrorMessage;
    }

    public void setRewardErrorMessage(String rewardErrorMessage) {
        mRewardErrorMessage = rewardErrorMessage;
    }

    public boolean isErrorMaxQuantityHasChanged() {
        return mErrorMaxQuantityHasChanged;
    }

    public void setErrorMaxQuantityHasChanged(boolean errorMaxQuantityHasChanged) {
        mErrorMaxQuantityHasChanged = errorMaxQuantityHasChanged;
    }

    public boolean hasAppliedRewards() {
        for (DiscountLine discountLine : getDiscountLines()) {
            if (!discountLine.isAutoApply()) {
                return true;
            }
        }
        return false;
    }

}
