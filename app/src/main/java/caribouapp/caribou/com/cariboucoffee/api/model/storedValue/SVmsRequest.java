package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;


/**
 * Created by jmsmuy on 11/8/17.
 */

public class SVmsRequest {

    // TODO: Remove mSourceApp param when service stops using it
    @SerializedName("sourceApp")
    private String mSourceApp = BuildConfig.SOURCE_APP;

    @SerializedName("uid")
    private String mUid;

    @SerializedName("cardNumber")
    private String mCardNumber;

    @SerializedName("getBalance")
    private SVmsRequestBalance mBalance;

    /**
     * Constructor for getBalance call
     *
     * @param uid
     */
    public SVmsRequest(String uid, String cardNumber) {
        mUid = uid;
        mCardNumber = cardNumber == null ? "" : cardNumber;
        mBalance = new SVmsRequestBalance(false);
    }

    public String getSourceApp() {
        return mSourceApp;
    }

    public void setSourceApp(String sourceApp) {
        mSourceApp = sourceApp;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
    }

    public SVmsRequestBalance getBalance() {
        return mBalance;
    }

    public void setBalance(SVmsRequestBalance balance) {
        mBalance = balance;
    }
}
