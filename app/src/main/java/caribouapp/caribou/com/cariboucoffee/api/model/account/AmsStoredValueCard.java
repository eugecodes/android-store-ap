package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;


/**
 * Created by jmsmuy on 11/23/17.
 */

public class AmsStoredValueCard {

    @SerializedName("cardNumber")
    private String mCardNumber;

    @SerializedName("autoReloadSettings")
    private AmsAutoReloadSettings mAmsAutoReloadSettings;


    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
    }

    public AmsAutoReloadSettings getAmsAutoReloadSettings() {
        return mAmsAutoReloadSettings;
    }

}
