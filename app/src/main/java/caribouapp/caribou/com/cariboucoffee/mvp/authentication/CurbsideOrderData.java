package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;

public class CurbsideOrderData implements Serializable {
    @SerializedName("orderId")
    private String mOrderId;
    @SerializedName("pickupTime")
    private DateTime mPickupTime;
    @SerializedName("locationId")
    private String mLocationId;
    @SerializedName("curbsidePickupData")
    private CurbsidePickupData mCurbsidePickupData;
    @SerializedName("asap")
    private boolean mAsap;
    @SerializedName("status")
    private NcrOrderStatus mStatus;

    public CurbsideOrderData(String orderId, DateTime pickupTime,
                             String locationId, CurbsidePickupData curbsidePickupData,
                             boolean asap,
                             NcrOrderStatus status) {
        mOrderId = orderId;
        mPickupTime = pickupTime;
        mLocationId = locationId;
        mCurbsidePickupData = curbsidePickupData;
        mAsap = asap;
        mStatus = status;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public DateTime getPickupTime() {
        return mPickupTime;
    }

    public String getLocationId() {
        return mLocationId;
    }

    public CurbsidePickupData getCurbsidePickupData() {
        return mCurbsidePickupData;
    }

    public boolean isAsap() {
        return mAsap;
    }

    public NcrOrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(NcrOrderStatus status) {
        this.mStatus = status;
    }
}
