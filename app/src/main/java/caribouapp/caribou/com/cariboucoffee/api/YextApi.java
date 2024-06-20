package caribouapp.caribou.com.cariboucoffee.api;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextGetLocationResult;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextSearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by asegurola on 2/26/18.
 */

public interface YextApi {

    @GET("entities/{locationId}")
    Call<YextGetLocationResult> getLocation(@Path("locationId") String locationId,
                                            @Query("v") String version);

    @GET("entities/geosearch")
    Call<YextSearchResult> getStoreLocationsNear(@Query("entityTypes") String entityType,
                                                 @Query("limit") int limit,
                                                 @Query("location") String centerCoordinates,
                                                 @Query("radius") double radiusInMiles,
                                                 @Query("offset") int offset,
                                                 @Query("v") String version,
                                                 @Query(value = "filter", encoded = true) String filterJsonData);
}

