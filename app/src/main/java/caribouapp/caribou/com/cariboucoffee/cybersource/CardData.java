package caribouapp.caribou.com.cariboucoffee.cybersource;

import java.util.HashMap;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;

/**
 * Created by andressegurola on 11/24/17.
 */

public class CardData {
    private Map<String, String> mFields = new HashMap<>();

    public void setCardType(CardEnum cardEnum) {
        mFields.put("card_type", cardEnum == null ? null : cardEnum.getCybersourceCode());
    }

    public void setNumber(String number) {
        mFields.put("card_number", number);
    }

    /**
     * @param expireDate in MM-YYYY format
     */
    public void setExpireDate(String expireDate) {
        mFields.put("card_expiry_date", expireDate);
    }

    public void setCvn(String cvv) {
        mFields.put("card_cvn", cvv);
    }

    public Map<String, String> asMap() {
        return mFields;
    }
}
