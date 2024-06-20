package caribouapp.caribou.com.cariboucoffee.api;

import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.DistanceResult;
import caribouapp.caribou.com.cariboucoffee.api.model.geocoding.PlacesFromAddressSearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingApi {

    String GOOGLE_GEOCODING_URL = "https://maps.googleapis.com/";

    @GET("maps/api/geocode/json")
    Call<PlacesFromAddressSearchResult> getLocationsFromZipCode(@Query("components") String components, @Query("key") String apiKey);

    @GET("maps/api/distancematrix/json")
    Call<DistanceResult> getDistance(@Query("origins") String origins, @Query("destinations") String destinations, @Query("key") String apiKey);

}
