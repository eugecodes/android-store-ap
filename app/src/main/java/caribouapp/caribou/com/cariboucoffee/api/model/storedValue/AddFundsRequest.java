package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequest;

/**
 * Created by andressegurola on 12/6/17.
 */

public class AddFundsRequest extends AmsRequest {

    @SerializedName("uid")
    private String mUid;
    @SerializedName("addFunds")
    private AddFundsData mAddFundsData;

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public AddFundsData getAddFundsData() {
        return mAddFundsData;
    }

    public void setAddFundsData(AddFundsData addFundsData) {
        mAddFundsData = addFundsData;
    }
}
