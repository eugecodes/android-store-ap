package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.GeocodingApi;
import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.AddressComponent;
import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.DistanceElement;
import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.DistanceResult;
import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.DistanceRow;
import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.PlaceInfo;
import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.PlacesFromAddressSearchResult;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.RetrofitCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import retrofit2.Response;

/**
 * Created by andressegurola on 10/10/17.
 */

public class GeolocationImpl implements GeolocationServices {

    private static final String TAG = GeolocationImpl.class.getSimpleName();

    private static final String GEOCODE_TYPE_ADMINISTRATIVE_AREA_LEVEL1 = "administrative_area_level_1";
    private static final String GEOCODE_TYPE_ADMINISTRATIVE_AREA_LEVEL3 = "administrative_area_level_3";
    private static final String GEOCODE_TYPE_LOCALITY = "locality";
    private static final String GEOCODE_TYPE_SUBLOCALITY_LEVEL_1 = "sublocality_level_1";
    private static final String GEOCODE_TYPE_NEIGHBORHOOD = "neighborhood";

    private final GeocodingApi mGeocodingApi;
    private final String mApiKey;

    @Inject
    public GeolocationImpl(Application context, GeocodingApi geocodingApi) {
        mGeocodingApi = geocodingApi;
        mApiKey = context.getResources().getString(R.string.google_rest_api_key);
    }

    @Override
    public void getCityAndStateFromZipcode(String zipcode, final ResultCallback<LocationInfo> resultCallback) {

        String searchQuery = prepareSearchQuery(zipcode);
        mGeocodingApi.getLocationsFromZipCode(searchQuery, mApiKey)
                .enqueue(new RetrofitCallbackWrapper<PlacesFromAddressSearchResult, LocationInfo>(resultCallback) {
                    @Override
                    protected void onSuccess(Response<PlacesFromAddressSearchResult> response) {

                        PlacesFromAddressSearchResult results = response.body();

                        List<PlaceInfo> placeInfos = results.getPlaceInfos();
                        if (placeInfos == null || placeInfos.isEmpty()) {
                            resultCallback.onFail(0, "Error getting city and state from zipcode: " + zipcode);
                            return;
                        }

                        List<AddressComponent> addressComponents = results.getPlaceInfos().get(0).getAddressComponents();
                        if (addressComponents == null || addressComponents.isEmpty()) {
                            resultCallback.onFail(0, "Error getting city and state from zipcode: " + zipcode);
                            return;
                        }

                        AddressComponent localityComponent = null;
                        AddressComponent subLocalityComponent = null;
                        AddressComponent neighborhoodComponent = null;
                        AddressComponent administrativeLevel3Component = null;
                        AddressComponent stateComponent = null;

                        for (AddressComponent addressComponent : addressComponents) {
                            List<String> types = addressComponent.getTypes();

                            if (types.contains(GEOCODE_TYPE_LOCALITY)) {
                                localityComponent = addressComponent;
                            } else if (types.contains(GEOCODE_TYPE_SUBLOCALITY_LEVEL_1)) {
                                subLocalityComponent = addressComponent;
                            } else if (types.contains(GEOCODE_TYPE_NEIGHBORHOOD)) {
                                neighborhoodComponent = addressComponent;
                            } else if (types.contains(GEOCODE_TYPE_ADMINISTRATIVE_AREA_LEVEL3)) {
                                administrativeLevel3Component = addressComponent;
                            } else if (types.contains(GEOCODE_TYPE_ADMINISTRATIVE_AREA_LEVEL1)) {
                                stateComponent = addressComponent;
                            }
                        }

                        AddressComponent cityComponent = null;
                        if (localityComponent != null) {
                            cityComponent = localityComponent;
                        } else if (subLocalityComponent != null) {
                            cityComponent = subLocalityComponent;
                        } else if (neighborhoodComponent != null) {
                            cityComponent = neighborhoodComponent;
                        } else if (administrativeLevel3Component != null) {
                            cityComponent = administrativeLevel3Component;
                        }

                        if (cityComponent == null || stateComponent == null) {
                            resultCallback.onFail(0, "Error getting city and state from zipcode: " + zipcode);
                            return;
                        }

                        LocationInfo locationInfo = new LocationInfo();
                        locationInfo.setState(stateComponent.getShortName());
                        locationInfo.setCity(cityComponent.getShortName());
                        resultCallback.onSuccess(locationInfo);
                    }
                });
    }

    private String prepareSearchQuery(String zipcode) {
        return StringUtils.format("postal_code:%s|country:%s", zipcode, AppConstants.US_COUNTRY_ABREV);
    }

    @Override
    public void distanceBetween(LatLng latLng, String locationName, ResultCallback<BigDecimal> resultCallback) {

        String origin = StringUtils.format("%s,%s", latLng.latitude, latLng.longitude);

        mGeocodingApi.getDistance(origin, locationName, mApiKey)
                .enqueue(new RetrofitCallbackWrapper<DistanceResult, BigDecimal>(resultCallback) {
                    @Override
                    protected void onSuccess(Response<DistanceResult> response) {

                        DistanceResult results = response.body();

                        if (results.getStatus() != DistanceResult.StatusCode.OK) {
                            resultCallback.onFail(0, "Error getting distance to location name: " + locationName);
                            return;
                        }

                        List<DistanceRow> rows = results.getRows();
                        if (rows == null || rows.isEmpty()) {
                            resultCallback.onFail(0, "Error getting distance to location name: " + locationName);
                            return;
                        }

                        List<DistanceElement> elements = rows.get(0).getElements();
                        if (elements == null || elements.isEmpty()) {
                            resultCallback.onFail(0, "Error getting distance to location name: " + locationName);
                            return;
                        }

                        DistanceElement element = elements.get(0);
                        if (element.getStatus() != DistanceElement.StatusCode.OK) {
                            resultCallback.onFail(0, "Error getting distance to location name: " + locationName);
                            return;
                        }

                        resultCallback.onSuccess(getDistanceInMiles(element.getDistance().getValue()));
                    }
                });
    }

    private BigDecimal getDistanceInMiles(Double distance) {
        return new BigDecimal(distance / AppConstants.METER_TO_MILES_DIVIDER);
    }
}
