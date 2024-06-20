package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jmsmuy on 11/23/17.
 */

public class AmsBillingInformation {

    @SerializedName("paymentCards")
    List<AmsPaymentCard> mPaymentCardList;

    @SerializedName("storedValueCards")
    List<AmsStoredValueCard> mAmsStoredValueCardList;

    public List<AmsPaymentCard> getPaymentCardList() {
        return mPaymentCardList;
    }

    public void setPaymentCardList(List<AmsPaymentCard> paymentCardList) {
        mPaymentCardList = paymentCardList;
    }

    public List<AmsStoredValueCard> getAmsStoredValueCardList() {
        return mAmsStoredValueCardList;
    }

    public void setAmsStoredValueCardList(List<AmsStoredValueCard> amsStoredValueCardList) {
        mAmsStoredValueCardList = amsStoredValueCardList;
    }
}
