package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asegurola on 2/26/18.
 */

public class YextSearchResult {

    @SerializedName("response")
    private YextResponseData mYextResponseData;

    public YextResponseData getYextResponseData() {
        return mYextResponseData;
    }

    public void setYextResponseData(YextResponseData yextResponseData) {
        mYextResponseData = yextResponseData;
    }
}
