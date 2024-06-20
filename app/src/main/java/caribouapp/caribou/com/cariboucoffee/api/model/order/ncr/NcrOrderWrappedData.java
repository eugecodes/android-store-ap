package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerAppliedReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerDeliveryData;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerPickupData;
import caribouapp.caribou.com.cariboucoffee.order.OrderServerData;

public class NcrOrderWrappedData implements OrderServerData {

    @SerializedName("user_id")
    private String mUserId;

    @SerializedName("location")
    private NcrLocation mNcrLocation;

    @SerializedName("data")
    private NcrOrderApiData mNcrOrderApiData;

    @SerializedName("status")
    private NcrOrderStatus mNcrOrderStatus;

    @SerializedName("precalculatedSubtotal")
    private BigDecimal mPrecalculatedSubtotal;

    @SerializedName("schedule_in")
    private Long mScheduleIn;

    @SerializedName("subtotal")
    private BigDecimal mSubtotal;

    @SerializedName("total")
    private BigDecimal mTotal;

    @SerializedName("tax")
    private BigDecimal mTax;

    @SerializedName("pickup")
    private ServerPickupData mPickup;

    @SerializedName("delivery")
    private ServerDeliveryData mDeliveryData;

    @SerializedName("reorder_errors")
    private List<NcrReorderErrors> mReorderErrorsList;

    @SerializedName("reward_errors")
    private List<NcrRewardError> mRewardErrors;

    @SerializedName("check_number")
    private String mCheckNumber;

    @SerializedName("reward_ids")
    private List<Integer> mRewardsToApply = new ArrayList<>();

    @SerializedName("applied_rewards")
    private List<ServerAppliedReward> mAppliedRewards;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public NcrLocation getNcrLocation() {
        return mNcrLocation;
    }

    public void setNcrLocation(NcrLocation ncrLocation) {
        mNcrLocation = ncrLocation;
    }

    public NcrOrderApiData getNcrOrderApiData() {
        return mNcrOrderApiData;
    }

    public void setNcrOrderApiData(NcrOrderApiData ncrOrderApiData) {
        mNcrOrderApiData = ncrOrderApiData;
    }

    public NcrOrderStatus getNcrOrderStatus() {
        return mNcrOrderStatus;
    }

    public void setNcrOrderStatus(NcrOrderStatus ncrOrderStatus) {
        mNcrOrderStatus = ncrOrderStatus;
    }

    public BigDecimal getTotal() {
        return mTotal;
    }

    public void setTotal(BigDecimal total) {
        mTotal = total;
    }

    public BigDecimal getTax() {
        return mTax;
    }

    public void setTax(BigDecimal tax) {
        mTax = tax;
    }

    public BigDecimal getSubtotal() {
        return mSubtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        mSubtotal = subtotal;
    }

    public BigDecimal getPrecalculatedSubtotal() {
        return mPrecalculatedSubtotal;
    }

    public void setPrecalculatedSubtotal(BigDecimal precalculatedSubtotal) {
        mPrecalculatedSubtotal = precalculatedSubtotal;
    }

    public Long getScheduleIn() {
        return mScheduleIn;
    }

    public void setScheduleIn(Long scheduleIn) {
        mScheduleIn = scheduleIn;
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

    public void setDeliveryData(ServerDeliveryData pickupData) {
        mDeliveryData = pickupData;
    }

    public List<NcrReorderErrors> getReorderErrorsList() {
        return mReorderErrorsList;
    }

    public void setReorderErrorsList(List<NcrReorderErrors> reorderErrorsList) {
        mReorderErrorsList = reorderErrorsList;
    }

    public String getCheckNumber() {
        return mCheckNumber;
    }

    public void setCheckNumber(String checkNumber) {
        mCheckNumber = checkNumber;
    }

    public List<ServerAppliedReward> getAppliedRewards() {
        return mAppliedRewards;
    }

    public void setAppliedRewards(List<ServerAppliedReward> appliedRewards) {
        mAppliedRewards = appliedRewards;
    }

    public List<Integer> getRewardsToApply() {
        return mRewardsToApply;
    }

    public void setRewardsToApply(List<Integer> rewardsToApply) {
        mRewardsToApply = rewardsToApply;
    }

    public List<NcrRewardError> getRewardErrors() {
        return mRewardErrors;
    }

    public void setRewardErrors(List<NcrRewardError> rewardErrors) {
        mRewardErrors = rewardErrors;
    }
}

