package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

public class AmsAssociateUA extends AmsRequest {

    @SerializedName("deviceType")
    private String mDeviceType;

    @SerializedName("channelId")
    private String mChannelId;

    public String getDeviceType() {
        return mDeviceType;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public void setDeviceType(String deviceType) {
        mDeviceType = deviceType;
    }

    public void setChannelId(String channelId) {
        mChannelId = channelId;
    }
}
