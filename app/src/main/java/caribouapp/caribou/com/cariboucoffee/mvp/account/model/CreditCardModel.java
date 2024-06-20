package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by jmsmuy on 11/22/17.
 */

public class CreditCardModel extends BaseObservable {

    private CardEnum mCard;
    private String mCardEnding;
    private String mToken;

    public CreditCardModel(CardEnum card, String cardEnding, String token) {
        mCard = card;
        mCardEnding = cardEnding;
        mToken = token;
    }

    @Bindable
    public CardEnum getCard() {
        return mCard;
    }

    public void setCard(CardEnum card) {
        mCard = card;
    }

    @Bindable
    public String getCardEnding() {
        return mCardEnding;
    }

    public void setCardEnding(String cardEnding) {
        this.mCardEnding = cardEnding;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
