package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ServerAppliedReward implements Serializable {

    //TODO delete when server use the same for Positouch and NCR
    @SerializedName(value = "wallet_code", alternate = {"wallet_id"})
    private String mWalletId;

    @SerializedName("discount_line")
    private String mDiscountLine;

    @SerializedName("auto_apply")
    private boolean mAutoPlay;

    public String getWalletId() {
        return mWalletId;
    }

    public void setWalletId(String walletId) {
        mWalletId = walletId;
    }

    public String getDiscountLine() {
        return mDiscountLine;
    }

    public void setDiscountLine(String discountLine) {
        mDiscountLine = discountLine;
    }

    public boolean isAutoPlay() {
        return mAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }
}
