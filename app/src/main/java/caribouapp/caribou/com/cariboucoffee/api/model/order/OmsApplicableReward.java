package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OmsApplicableReward implements Serializable {

    @SerializedName("wallet_id")
    private String mWalletId;

    @SerializedName("applicable")
    private boolean mApplicable;

    @SerializedName("discount_line")
    private String mDiscountLine;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("ranking")
    private int mRanking;

    public boolean isApplicable() {
        return mApplicable;
    }

    public void setApplicable(boolean applicable) {
        mApplicable = applicable;
    }

    public String getDiscountLine() {
        return mDiscountLine;
    }

    public void setDiscountLine(String discountLine) {
        mDiscountLine = discountLine;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getRanking() {
        return mRanking;
    }

    public void setRanking(int ranking) {
        mRanking = ranking;
    }

    public String getWalletId() {
        return mWalletId;
    }

    public void setWalletId(String walletId) {
        mWalletId = walletId;
    }
}
