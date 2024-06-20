package caribouapp.caribou.com.cariboucoffee.api.model.geocoding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AddressComponent implements Serializable {

    @SerializedName("long_name")
    private String mLongName;

    @SerializedName("short_name")
    private String mShortName;

    @SerializedName("types")
    private List<String> mTypes;

    public String getLongName() {
        return mLongName;
    }

    public void setLongName(String longName) {
        mLongName = longName;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public List<String> getTypes() {
        return mTypes;
    }

    public void setTypes(List<String> types) {
        mTypes = types;
    }
}
