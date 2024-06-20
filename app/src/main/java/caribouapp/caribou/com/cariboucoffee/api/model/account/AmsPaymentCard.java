package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;

/**
 * Created by jmsmuy on 11/23/17.
 */

public class AmsPaymentCard {

    @SerializedName("id")
    private String mId;

    @SerializedName("firstName")
    private String mFirstName;

    @SerializedName("lastName")
    private String mLastName;

    @SerializedName("cardNumberPartial")
    private String mCardNumberPartial;

    @SerializedName("type")
    private String mCardType;

    @SerializedName("isPrimary")
    private boolean mPrimary = true;

    @SerializedName("expireMonth")
    private String mExpiringMonth;

    @SerializedName("expireYear")
    private String mExpiringYear;

    @SerializedName("billingAddress")
    private AmsAddress mBillingAddress;

    @SerializedName("token")
    private String mToken;

    @SerializedName("status")
    private String mStatus;

    public AmsPaymentCard(String firstName, String lastName, String maskedCardNumber, String month,
                          String year, AmsAddress address, CardEnum cardType, String paymentToken) {
        setFirstName(firstName);
        setLastName(lastName);
        setCardNumberPartial(maskedCardNumber);
        setExpiringMonth(month);
        setExpiringYear(year);
        setBillingAddress(address);
        setCardType(cardType.getApiName());
        setPrimary(true);
        setToken(paymentToken);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getCardNumberPartial() {
        return mCardNumberPartial;
    }

    public void setCardNumberPartial(String cardNumberPartial) {
        mCardNumberPartial = cardNumberPartial;
    }

    public String getCardType() {
        return mCardType;
    }

    public void setCardType(String cardType) {
        mCardType = cardType;
    }

    public String getExpiringMonth() {
        return mExpiringMonth;
    }

    public void setExpiringMonth(String expiringMonth) {
        mExpiringMonth = expiringMonth;
    }

    public String getExpiringYear() {
        return mExpiringYear;
    }

    public void setExpiringYear(String expiringYear) {
        mExpiringYear = expiringYear;
    }

    public AmsAddress getBillingAddress() {
        return mBillingAddress;
    }

    public void setBillingAddress(AmsAddress billingAddress) {
        mBillingAddress = billingAddress;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public boolean isPrimary() {
        return mPrimary;
    }

    public void setPrimary(boolean primary) {
        mPrimary = primary;
    }
}
