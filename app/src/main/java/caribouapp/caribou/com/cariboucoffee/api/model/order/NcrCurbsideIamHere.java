package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NcrCurbsideIamHere implements Serializable {

    @SerializedName("notification_timestamp")
    private String mIamHereTime;

    public String getIamHereTime() {
        return mIamHereTime;
    }

    public void setIamHereTime(String iamHereTime) {
        mIamHereTime = iamHereTime;
    }
}
