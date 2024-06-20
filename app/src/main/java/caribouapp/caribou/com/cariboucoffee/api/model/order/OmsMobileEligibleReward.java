package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OmsMobileEligibleReward implements Serializable {

    @SerializedName("wallet_id")
    private String mWalletId;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("auto_apply")
    private boolean mAutoApply;

    public String getWalletId() {
        return mWalletId;
    }

    public void setWalletId(String walletId) {
        mWalletId = walletId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isAutoApply() {
        return mAutoApply;
    }

    public void setAutoApply(boolean autoApply) {
        mAutoApply = autoApply;
    }
}
