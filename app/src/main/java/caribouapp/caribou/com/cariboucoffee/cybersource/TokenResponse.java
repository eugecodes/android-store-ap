package caribouapp.caribou.com.cariboucoffee.cybersource;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;

/**
 * Created by andressegurola on 12/4/17.
 */

public class TokenResponse implements Serializable {
    private String mPaymentToken;

    private CardEnum mCardType;

    private String mMaskedCardNumber;

    public String getPaymentToken() {
        return mPaymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.mPaymentToken = paymentToken;
    }

    public CardEnum getCardType() {
        return mCardType;
    }

    public void setCardType(CardEnum cardEnum) {
        mCardType = cardEnum;
    }

    public String getMaskedCardNumber() {
        return mMaskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        mMaskedCardNumber = maskedCardNumber;
    }
}
