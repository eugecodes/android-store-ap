package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CmsStoreReward extends CmsReward {

    @SerializedName("omsIds")
    List<String> omsIds;

    public List<String> getOmsIds() {
        return omsIds;
    }

    public void setOmsIds(List<String> omsIds) {
        this.omsIds = omsIds;
    }
}
