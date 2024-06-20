package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.common.EntityWithId;

/**
 * Created by asegurola on 3/21/18.
 */

public class ItemOption extends EntityWithId<String> {

    @SerializedName("label")
    private String mLabel;

    @SerializedName("price")
    private BigDecimal mPrice;

    @SerializedName("additionalCharges")
    private boolean mAdditionalCharges;

    @SerializedName("hideLabel")
    private boolean mHideLabel;

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public BigDecimal getPrice() {
        return mPrice;
    }

    public void setPrice(BigDecimal price) {
        mPrice = price;
    }

    public boolean isAdditionalCharges() {
        return mAdditionalCharges;
    }

    public void setAdditionalCharges(boolean additionalCharges) {
        mAdditionalCharges = additionalCharges;
    }

    public boolean isHideLabel() {
        return mHideLabel;
    }

    public void setHideLabel(boolean hideLabel) {
        mHideLabel = hideLabel;
    }
}
