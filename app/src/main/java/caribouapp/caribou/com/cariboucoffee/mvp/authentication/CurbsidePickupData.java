package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CurbsidePickupData implements Serializable {

    @SerializedName("carMake")
    private String mCarMake;

    @SerializedName("carColor")
    private String mCarColor;

    @SerializedName("carType")
    private String mCarType;

    public CurbsidePickupData(String carMake, String carColor, String carType) {
        mCarMake = carMake;
        mCarColor = carColor;
        mCarType = carType;
    }

    public String getCarMake() {
        return mCarMake;
    }

    public void setCarMake(String carMake) {
        mCarMake = carMake;
    }

    public String getCarColor() {
        return mCarColor;
    }

    public void setCarColor(String carColor) {
        mCarColor = carColor;
    }

    public String getCarType() {
        return mCarType;
    }

    public void setCarType(String carType) {
        mCarType = carType;
    }
}
