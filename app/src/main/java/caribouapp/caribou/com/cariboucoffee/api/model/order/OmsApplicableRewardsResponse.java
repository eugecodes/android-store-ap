package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OmsApplicableRewardsResponse implements Serializable {

    @SerializedName("data")
    private List<OmsApplicableReward> mData;

    public List<OmsApplicableReward> getData() {
        return mData;
    }

    public void setData(List<OmsApplicableReward> data) {
        mData = data;
    }
}
