package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;

/**
 * Created by andressegurola on 12/6/17.
 */

public class AddFundsResponse extends ResponseWithHeader {
    @SerializedName("addFundsResult")
    private AddFundsResult mAddFundsResult;

    public AddFundsResult getAddFundsResult() {
        return mAddFundsResult;
    }

    public void setAddFundsResult(AddFundsResult addFundsResult) {
        mAddFundsResult = addFundsResult;
    }
}
