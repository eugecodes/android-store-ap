package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class NcrLinkItem implements Serializable {

    @SerializedName("name")
    private String mName;

    @SerializedName("id")
    private String mId;

    @SerializedName("productId")
    private String mProductId;

    @SerializedName("price")
    private BigDecimal mPrice;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public BigDecimal getPrice() {
        return mPrice;
    }

    public void setPrice(BigDecimal price) {
        mPrice = price;
    }
}
