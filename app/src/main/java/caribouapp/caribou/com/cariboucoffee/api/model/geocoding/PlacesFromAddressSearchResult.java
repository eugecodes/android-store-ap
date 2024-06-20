package caribouapp.caribou.com.cariboucoffee.api.model.geocoding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlacesFromAddressSearchResult implements Serializable {

    @SerializedName("results")
    private List<PlaceInfo> mPlaceInfos;

    public List<PlaceInfo> getPlaceInfos() {
        return mPlaceInfos;
    }

    public void setPlaceInfos(List<PlaceInfo> placeInfos) {
        mPlaceInfos = placeInfos;
    }
}
