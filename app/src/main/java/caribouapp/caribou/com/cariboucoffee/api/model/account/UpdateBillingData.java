package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andressegurola on 12/7/17.
 */

public class UpdateBillingData {

    @SerializedName("card")
    private AmsPaymentCard mAmsPaymentCard;

    @SerializedName("autoReloadSettings")
    private AmsAutoReloadSettings mAmsAutoReloadSettings;

    @SerializedName("affirmativeConsent")
    private Boolean mAffirmativeConsent;

    public AmsAutoReloadSettings getAmsAutoReloadSettings() {
        return mAmsAutoReloadSettings;
    }

    public void setAmsAutoReloadSettings(AmsAutoReloadSettings amsAutoReloadSettings) {
        mAmsAutoReloadSettings = amsAutoReloadSettings;
    }

    public boolean isAffirmativeConsent() {
        return mAffirmativeConsent;
    }

    public void setAffirmativeConsent(boolean affirmativeConsent) {
        mAffirmativeConsent = affirmativeConsent;
    }

    public AmsPaymentCard getAmsPaymentCard() {
        return mAmsPaymentCard;
    }

    public void setAmsPaymentCard(AmsPaymentCard amsPaymentCard) {
        mAmsPaymentCard = amsPaymentCard;
    }
}
