package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andressegurola on 12/7/17.
 */

public class UpdateBillingRequest extends AmsRequest {
    @SerializedName("uid")
    private String mUid;

    @SerializedName("updateBilling")
    private UpdateBillingData mUpdateBillingData;


    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public UpdateBillingData getUpdateBillingData() {
        return mUpdateBillingData;
    }

    public void setUpdateBillingData(UpdateBillingData updateBillingData) {
        mUpdateBillingData = updateBillingData;
    }
}
