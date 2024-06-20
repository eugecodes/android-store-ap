package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;

public class ServerPickupData implements Serializable {

    @SerializedName("type")
    private String mType;

    @SerializedName("pickup_time")
    private DateTime mPickupTime;

    @SerializedName("car_type")
    private String mCarType;

    @SerializedName("car_color")
    private String mCarColor;

    @SerializedName("car_make")
    private String mCarMake;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public DateTime getPickupTime() {
        return mPickupTime;
    }

    public void setPickupTime(DateTime pickupTime) {
        mPickupTime = pickupTime;
    }

    public String getCarType() {
        return mCarType;
    }

    public void setCarType(String carType) {
        mCarType = carType;
    }

    public String getCarColor() {
        return mCarColor;
    }

    public void setCarColor(String carColor) {
        mCarColor = carColor;
    }

    public String getCarMake() {
        return mCarMake;
    }

    public void setCarMake(String carMake) {
        mCarMake = carMake;
    }
}
