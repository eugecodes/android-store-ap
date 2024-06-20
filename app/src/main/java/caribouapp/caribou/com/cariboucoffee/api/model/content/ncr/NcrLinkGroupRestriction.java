package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NcrLinkGroupRestriction implements Serializable {

    @SerializedName("maxQuantity")
    private int mMaxQuantity;

    @SerializedName("minQuantity")
    private int mMinQuantity;

    @SerializedName("freeQuantity")
    private int mFreeQuantity;

    @SerializedName("defaultQuantity")
    private int mDefaultQuantity;

    public int getMaxQuantity() {
        return mMaxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        mMaxQuantity = maxQuantity;
    }

    public int getMinQuantity() {
        return mMinQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        mMinQuantity = minQuantity;
    }

    public int getFreeQuantity() {
        return mFreeQuantity;
    }

    public void setFreeQuantity(int freeQuantity) {
        mFreeQuantity = freeQuantity;
    }

    public int getDefaultQuantity() {
        return mDefaultQuantity;
    }

    public void setDefaultQuantity(int defaultQuantity) {
        mDefaultQuantity = defaultQuantity;
    }
}
