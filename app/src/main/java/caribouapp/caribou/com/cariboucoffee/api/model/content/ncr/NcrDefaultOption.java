package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class NcrDefaultOption implements Serializable {

    @SerializedName("linkGroupId")
    private String mLinkGroupId;

    @SerializedName("salesItemId")
    private String mSalesItemId;

    @SerializedName("defaultQuantity")
    private BigDecimal mDefaultQuantity;

    public String getLinkGroupId() {
        return mLinkGroupId;
    }

    public void setLinkGroupId(String linkGroupId) {
        mLinkGroupId = linkGroupId;
    }

    public String getSalesItemId() {
        return mSalesItemId;
    }

    public void setSalesItemId(String salesItemId) {
        mSalesItemId = salesItemId;
    }

    public BigDecimal getDefaultQuantity() {
        return mDefaultQuantity;
    }

    public void setDefaultQuantity(BigDecimal defaultQuantity) {
        mDefaultQuantity = defaultQuantity;
    }
}
