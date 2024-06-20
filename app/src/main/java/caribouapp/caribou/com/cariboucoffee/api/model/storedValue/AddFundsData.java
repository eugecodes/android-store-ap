package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by andressegurola on 12/6/17.
 */

public class AddFundsData {

    @SerializedName("amount")
    private BigDecimal mAmount;
    @SerializedName("paymentToken")
    private String mPaymentToken;

    public AddFundsData(BigDecimal amount, String paymentToken) {
        mAmount = amount;
        mPaymentToken = paymentToken;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal amount) {
        mAmount = amount;
    }

    public String getPaymentToken() {
        return mPaymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        mPaymentToken = paymentToken;
    }
}
