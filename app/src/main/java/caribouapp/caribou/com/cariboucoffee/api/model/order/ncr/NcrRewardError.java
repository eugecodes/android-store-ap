package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NcrRewardError implements Serializable {

    @SerializedName("reward")
    private int mRewardId;

    @SerializedName("message")
    private String mMessage;

    public int getRewardId() {
        return mRewardId;
    }

    public void setRewardId(int rewardId) {
        mRewardId = rewardId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
