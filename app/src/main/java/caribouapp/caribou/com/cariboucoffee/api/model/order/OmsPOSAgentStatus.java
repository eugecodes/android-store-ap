package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model.EnumPOSAgentStatus;

/**
 * Created by gonzalogelos on 5/4/18.
 */

public class OmsPOSAgentStatus implements Serializable {

    @SerializedName("status")
    private EnumPOSAgentStatus mStatus;

    public EnumPOSAgentStatus getStatus() {
        return mStatus;
    }

    public void setStatus(EnumPOSAgentStatus status) {
        mStatus = status;
    }
}
