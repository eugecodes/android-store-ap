package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

public class OmsApplyRewardRequest {

    @SerializedName("wallet_ids")
    private String mWalletId;

    public String getWalletId() {
        return mWalletId;
    }

    public void setWalletId(String walletId) {
        mWalletId = walletId;
    }
}
