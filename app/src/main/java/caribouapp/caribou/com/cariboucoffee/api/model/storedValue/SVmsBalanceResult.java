package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by jmsmuy on 11/8/17.
 */

public class SVmsBalanceResult {

    @SerializedName("cardNumber")
    private String mCardNumber;

    @SerializedName("balanceAmount")
    private BigDecimal mBalanceAmount;

    @SerializedName("pointsBalance")
    private BigDecimal mPointsBalance;

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
    }

    public BigDecimal getBalanceAmount() {
        return mBalanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        mBalanceAmount = balanceAmount;
    }

    public BigDecimal getPointsBalance() {
        return mPointsBalance;
    }

    public void setPointsBalance(BigDecimal pointsBalance) {
        mPointsBalance = pointsBalance;
    }
}
