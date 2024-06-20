package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asegurola on 2/26/18.
 */

public class YextLocationDistance {
    @SerializedName("id")
    private String mLocationId;

    @SerializedName("distanceMiles")
    private double mDistanceInMiles;

    public String getLocationId() {
        return mLocationId;
    }

    public void setLocationId(String locationId) {
        mLocationId = locationId;
    }

    public double getDistanceInMiles() {
        return mDistanceInMiles;
    }

    public void setDistanceInMiles(double distanceInMiles) {
        mDistanceInMiles = distanceInMiles;
    }
}
