package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asegurola on 2/26/18.
 */

public class YextResponseData {
    @SerializedName("count")
    private int mCount;

    @SerializedName("entities")
    private List<YextLocation> mYextLocations;

    @SerializedName("distances")
    private List<YextLocationDistance> mYextLocationDistances;

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public List<YextLocation> getYextLocations() {
        return mYextLocations;
    }

    public void setYextLocations(List<YextLocation> yextLocations) {
        mYextLocations = yextLocations;
    }

    public List<YextLocationDistance> getYextLocationDistances() {
        return mYextLocationDistances;
    }

    public void setYextLocationDistances(List<YextLocationDistance> yextLocationDistances) {
        mYextLocationDistances = yextLocationDistances;
    }
}
