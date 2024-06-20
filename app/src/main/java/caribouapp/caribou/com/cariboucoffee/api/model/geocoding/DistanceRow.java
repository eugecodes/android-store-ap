package caribouapp.caribou.com.cariboucoffee.api.model.geocoding;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistanceRow {

    @SerializedName("elements")
    private List<DistanceElement> mElements;

    public List<DistanceElement> getElements() {
        return mElements;
    }

    public void setElements(List<DistanceElement> elements) {
        this.mElements = elements;
    }
}
