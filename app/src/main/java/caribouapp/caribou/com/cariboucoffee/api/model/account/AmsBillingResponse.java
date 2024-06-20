package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

public class AmsBillingResponse {

    @SerializedName("Type")
    private AmsResponseType mType;

    public AmsResponseType getType() {
        return mType;
    }

    public void setType(AmsResponseType type) {
        mType = type;
    }
}
