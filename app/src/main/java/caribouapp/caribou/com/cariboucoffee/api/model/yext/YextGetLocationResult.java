package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asegurola on 4/24/18.
 */

public class YextGetLocationResult {
    @SerializedName("response")
    private YextLocation mResponse;

    public YextLocation getResponse() {
        return mResponse;
    }

    public void setResponse(YextLocation response) {
        mResponse = response;
    }
}
