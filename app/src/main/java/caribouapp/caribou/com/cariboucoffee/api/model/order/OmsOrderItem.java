package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asegurola on 4/5/18.
 */

public class OmsOrderItem implements Serializable {

    @SerializedName("id")
    private Long mId;

    @SerializedName("product")
    private OmsEntityRef mProduct;

    @SerializedName("qty")
    private int mQuantity;

    @SerializedName("product_price")
    private BigDecimal mPrice;

    @SerializedName("subtotal")
    private BigDecimal mSubtotal;

    @SerializedName("groups")
    private List<OmsOrderGroupModifier> mGroups = new ArrayList<>();

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public OmsEntityRef getProduct() {
        return mProduct;
    }

    public void setProduct(OmsEntityRef product) {
        mProduct = product;
    }

    public List<OmsOrderGroupModifier> getGroups() {
        return mGroups;
    }

    public void setGroups(List<OmsOrderGroupModifier> groups) {
        mGroups = groups;
    }

    public BigDecimal getPrice() {
        return mPrice;
    }

    public void setPrice(BigDecimal price) {
        mPrice = price;
    }

    public BigDecimal getSubtotal() {
        return mSubtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        mSubtotal = subtotal;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }
}
