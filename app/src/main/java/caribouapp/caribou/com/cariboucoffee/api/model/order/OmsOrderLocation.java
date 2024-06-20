package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jmsmuy on 4/18/18.
 */

public class OmsOrderLocation implements Serializable {

    @SerializedName("name")
    private String mName;

    @SerializedName("number")
    private int mNumber;

    @SerializedName("price_tier")
    private int mPriceTier;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public int getPriceTier() {
        return mPriceTier;
    }

    public void setPriceTier(int priceTier) {
        mPriceTier = priceTier;
    }
}
