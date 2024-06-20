package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPaymentCard;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
/**
 * Created by andressegurola on 12/7/17.
 */

public class BaseFundsModel extends BaseObservable {

    private BigDecimal mMoneyToAdd;

    private boolean mUseCardOnProfile;

    private String mCardName;

    private String mCardNumber;

    private String mToken;

    @Bindable
    public BigDecimal getMoneyToAdd() {
        return mMoneyToAdd;
    }

    public void setMoneyToAdd(BigDecimal moneyToAdd) {
        mMoneyToAdd = moneyToAdd;
        notifyPropertyChanged(BR.moneyToAdd);
    }

    public void loadCardData(AmsPaymentCard card) {
        setCardName(CardEnum.getCardTypeFromApiName(card.getCardType()).toString());
        setCardNumber(StringUtils.getSuffix(card.getCardNumberPartial(), AppConstants.AMOUNT_DIGITS_TO_SHOW));
        setToken(card.getToken());
        mUseCardOnProfile = true;
    }

    @Bindable
    public String getCardName() {
        return mCardName;
    }

    public void setCardName(String cardName) {
        mCardName = cardName;
        notifyPropertyChanged(BR.cardName);
    }

    @Bindable
    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
        notifyPropertyChanged(BR.cardNumber);
    }

    public boolean isUseCardOnProfile() {
        return mUseCardOnProfile;
    }

    public void setUseCardOnProfile(boolean useCardOnProfile) {
        mUseCardOnProfile = useCardOnProfile;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
