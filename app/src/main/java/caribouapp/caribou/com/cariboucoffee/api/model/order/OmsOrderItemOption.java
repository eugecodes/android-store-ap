package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class OmsOrderItemOption implements Serializable {

    @SerializedName("id")
    private long mId;

    @SerializedName("label")
    private String mLabel;

    @SerializedName("price")
    private BigDecimal mPrice;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

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
}
