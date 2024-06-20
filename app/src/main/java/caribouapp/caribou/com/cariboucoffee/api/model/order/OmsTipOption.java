package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class OmsTipOption implements Serializable {

    @SerializedName("amount")
    private BigDecimal mAmount;

    @SerializedName("description")
    private String mDescription;

    public OmsTipOption(BigDecimal amount, String description) {
        mAmount = amount;
        mDescription = description;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal amount) {
        mAmount = amount;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
