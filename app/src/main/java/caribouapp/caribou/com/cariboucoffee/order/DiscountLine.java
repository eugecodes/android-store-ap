package caribouapp.caribou.com.cariboucoffee.order;

import java.io.Serializable;

public class DiscountLine implements Serializable {
    private int mRewardId;
    private String mDiscountLine;
    private boolean mAutoApply;

    public int getRewardId() {
        return mRewardId;
    }

    public void setRewardId(int rewardId) {
        mRewardId = rewardId;
    }

    public String getDiscountLine() {
        return mDiscountLine;
    }

    public void setDiscountLine(String discountLine) {
        mDiscountLine = discountLine;
    }

    public boolean isAutoApply() {
        return mAutoApply;
    }

    public void setAutoApply(boolean autoApply) {
        mAutoApply = autoApply;
    }
}
