package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.cybersource.CybersourceConstants;
import caribouapp.caribou.com.cariboucoffee.util.CardParserUtil;

/**
 * Created by jmsmuy on 11/22/17.
 */

public enum CardEnum {
    VISA(R.drawable.card_visa, AppConstants.VISA_API_NAMING, CybersourceConstants.CARD_TYPE_VISA, CardParserUtil.CardType.VISA),
    MASTER(R.drawable.card_mastercard, AppConstants.MASTER_API_NAMING, CybersourceConstants.CARD_TYPE_MASTER, CardParserUtil.CardType.MASTER_CARD),
    AMEX(R.drawable.card_amex, AppConstants.AMEX_API_NAMING, CybersourceConstants.CARD_TYPE_AMEX, CardParserUtil.CardType.AMEX),
    DISCOVER(R.drawable.card_discover, AppConstants.DISCOVER_API_NAMING, CybersourceConstants.CARD_TYPE_DISC, CardParserUtil.CardType.DISCOVER),
    UNKNOWN(R.drawable.card_unknown, null, null, null);

    private int mCardLogo;
    private String mApiName;
    private String mCybersourceCode;
    private CardParserUtil.CardType mCardType;

    CardEnum(int cardLogo, String apiName, String cybersourceCode, CardParserUtil.CardType cardType) {
        mCardLogo = cardLogo;
        mApiName = apiName;
        mCybersourceCode = cybersourceCode;
        mCardType = cardType;
    }

    public static CardEnum fromCybersourceCode(String cybersourceCode) {
        switch (cybersourceCode) {
            case CybersourceConstants.CARD_TYPE_VISA:
                return CardEnum.VISA;
            case CybersourceConstants.CARD_TYPE_MASTER:
                return CardEnum.MASTER;
            case CybersourceConstants.CARD_TYPE_AMEX:
                return CardEnum.AMEX;
            case CybersourceConstants.CARD_TYPE_DISC:
                return CardEnum.DISCOVER;
            default:
                return null;
        }
    }

    public static CardEnum getCardTypeFromApiName(String cardType) {

        for (CardEnum cardEnum : CardEnum.values()) {
            if (cardEnum.getApiName() != null
                    && cardEnum.getApiName().equalsIgnoreCase(cardType)) {
                return cardEnum;
            }
        }

        return CardEnum.UNKNOWN;

    }

    public static CardEnum getCardTypeFromCardNumber(String number, String ccv) {
        if (number == null) {
            return CardEnum.UNKNOWN;
        }
        CardParserUtil.CardType cardType = CardParserUtil.getCardType(number, ccv);
        for (CardEnum card : CardEnum.values()) {
            if (!card.equals(UNKNOWN) && card.mCardType.equals(cardType)) {
                return card;
            }
        }
        return CardEnum.UNKNOWN;
    }

    public int getCardLogo() {
        return mCardLogo;
    }

    public String getApiName() {
        return mApiName;
    }

    public String getCybersourceCode() {
        return mCybersourceCode;
    }

}
