package caribouapp.caribou.com.cariboucoffee.api.model.geocoding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlaceInfo implements Serializable {

    @SerializedName("address_components")
    private List<AddressComponent> mAddressComponents;

    public List<AddressComponent> getAddressComponents() {
        return mAddressComponents;
    }

    public void setAddressComponents(List<AddressComponent> addressComponents) {
        mAddressComponents = addressComponents;
    }
}
