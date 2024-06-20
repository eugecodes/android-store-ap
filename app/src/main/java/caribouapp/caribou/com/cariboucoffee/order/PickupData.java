package caribouapp.caribou.com.cariboucoffee.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerPickupData;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsidePickupData;

public class PickupData implements Serializable {

    @SerializedName("pickupType")
    private YextPickupType mYextPickupType = YextPickupType.WalkIn;

    // Only for mYextPickupType == Curbside
    @SerializedName("CurbSidePickupData")
    private CurbsidePickupData mCurbsidePickupData;

    public PickupData() {
    }

    public PickupData(ServerPickupData serverPickupData) {
        mYextPickupType = YextPickupType.fromServerName(serverPickupData.getType());
        mCurbsidePickupData = new CurbsidePickupData(serverPickupData.getCarMake(), serverPickupData.getCarColor(), serverPickupData.getType());
    }

    public YextPickupType getYextPickupType() {
        return mYextPickupType;
    }

    public void setYextPickupType(YextPickupType yextPickupType) {
        mYextPickupType = yextPickupType;
    }

    public CurbsidePickupData getCurbsidePickupData() {
        return mCurbsidePickupData;
    }

    public void setCurbsidePickupData(CurbsidePickupData curbsidePickupData) {
        mCurbsidePickupData = curbsidePickupData;
    }
}
