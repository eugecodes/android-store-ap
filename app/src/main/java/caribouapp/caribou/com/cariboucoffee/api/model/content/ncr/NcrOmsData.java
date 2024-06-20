package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.order.ProductCustomizationData;

public class NcrOmsData implements Serializable, ProductCustomizationData {

    @SerializedName("id")
    private String mId;

    @SerializedName("defaultSalesItemId")
    private String mDefaultSalesItemId;

    @SerializedName("salesItems")
    private List<NcrSaleItem> mSalesItems;

    @SerializedName("salesItemIds")
    private List<String> mSaleItemIds;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getDefaultSalesItemId() {
        return mDefaultSalesItemId;
    }

    public void setDefaultSalesItemId(String defaultSalesItemId) {
        mDefaultSalesItemId = defaultSalesItemId;
    }

    public List<NcrSaleItem> getSalesItems() {
        return mSalesItems;
    }

    public void setSalesItems(List<NcrSaleItem> salesItems) {
        mSalesItems = salesItems;
    }

    @Override
    public boolean isActive() {
        // TODO code actual implementation based on ncr data
        return true;
    }

    public List<String> getSaleItemIds() {
        return mSaleItemIds;
    }

    public void setSaleItemIds(List<String> saleItemIds) {
        mSaleItemIds = saleItemIds;
    }
}
