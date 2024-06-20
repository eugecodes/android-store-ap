package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by andressegurola on 12/7/17.
 */

public class AddFundsResult {
    @SerializedName("balance")
    private BigDecimal mBalance;

    @SerializedName("transactionId")
    private String mTransactionId;

    public BigDecimal getBalance() {
        return mBalance;
    }

    public void setBalance(BigDecimal balance) {
        mBalance = balance;
    }

    public String getTransactionId() {
        return mTransactionId;
    }

    public void setTransactionId(String transactionId) {
        mTransactionId = transactionId;
    }
}
