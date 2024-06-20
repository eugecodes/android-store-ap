package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by jmsmuy on 1/23/18.
 */

public class ResponseAvailableRewardsCriteria {

    @SerializedName("points")
    private BigDecimal mPoints;

    public BigDecimal getPoints() {
        return mPoints;
    }

    public void setPoints(BigDecimal points) {
        mPoints = points;
    }
}
