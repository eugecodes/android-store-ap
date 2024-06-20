package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by jmsmuy on 11/23/17.
 */

public class AmsAutoReloadSettings {

    @SerializedName("enabled")
    private Boolean mEnabled;

    @SerializedName("curency")
    private String mCurrency;

    @SerializedName("incrementAmount")
    private BigDecimal mIncrementAmount;

    @SerializedName("thresholdAmount")
    private BigDecimal mThresholdAmount;

    @SerializedName("errorCount")
    private Integer mErrorCount;

    @SerializedName("paymentCardId")
    private String mPaymentCardId;

    public Boolean getEnabled() {
        return mEnabled;
    }

    public void setEnabled(Boolean enabled) {
        mEnabled = enabled;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public BigDecimal getIncrementAmount() {
        return mIncrementAmount;
    }

    public void setIncrementAmount(BigDecimal incrementAmount) {
        mIncrementAmount = incrementAmount;
    }

    public BigDecimal getThresholdAmount() {
        return mThresholdAmount;
    }

    public void setThresholdAmount(BigDecimal thresholdAmount) {
        mThresholdAmount = thresholdAmount;
    }

    public Integer getErrorCount() {
        return mErrorCount;
    }

    public void setErrorCount(Integer errorCount) {
        mErrorCount = errorCount;
    }

    public String getPaymentCardId() {
        return mPaymentCardId;
    }

    public void setPaymentCardId(String paymentCardId) {
        mPaymentCardId = paymentCardId;
    }
}
