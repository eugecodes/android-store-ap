package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;

/**
 * Created by andressegurola on 10/10/17.
 */

public interface GeolocationServices {

    void getCityAndStateFromZipcode(String zipcode, ResultCallback<LocationInfo> resultCallback);

    void distanceBetween(LatLng latLng, String locationName, ResultCallback<BigDecimal> resultCallback);
}
