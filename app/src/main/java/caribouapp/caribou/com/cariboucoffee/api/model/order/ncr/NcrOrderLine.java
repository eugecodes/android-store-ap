package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class NcrOrderLine {

    @SerializedName(value = "Description", alternate = "description")
    private String mDescription;

    @SerializedName(value = "LineId", alternate = "lineId")
    private String mLineId;

    @SerializedName(value = "ParentLineId", alternate = "parentLineId")
    private String mParentLineId;

    @SerializedName(value = "ProductId", alternate = "productId")
    private NcrValueType<String> mProductId;

    @SerializedName(value = "Quantity", alternate = "quantity")
    private NcrValueType<Integer> mQuantity;

    @SerializedName(value = "UnitPrice", alternate = "unitPrice")
    private BigDecimal mUnitePrice;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLineId() {
        return mLineId;
    }

    public void setLineId(String lineId) {
        mLineId = lineId;
    }

    public String getParentLineId() {
        return mParentLineId;
    }

    public void setParentLineId(String parentLineId) {
        mParentLineId = parentLineId;
    }

    public NcrValueType<String> getProductId() {
        return mProductId;
    }

    public void setProductId(NcrValueType<String> productId) {
        mProductId = productId;
    }

    public NcrValueType<Integer> getQuantity() {
        return mQuantity;
    }

    public void setQuantity(NcrValueType<Integer> quantity) {
        mQuantity = quantity;
    }

    public BigDecimal getUnitePrice() {
        return mUnitePrice;
    }

    public void setUnitePrice(BigDecimal unitePrice) {
        mUnitePrice = unitePrice;
    }
}
