package caribouapp.caribou.com.cariboucoffee.stores;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.api.YextApi;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextGetLocationResult;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextLocation;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextResponseData;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextSearchResult;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import retrofit2.Response;

/**
 * Created by asegurola on 2/27/18.
 */

public class StoresServiceImpl implements StoresService {

    private static final String TAG = StoresServiceImpl.class.getSimpleName();
    private static final String LOCATIONS = "location";
    public static final String ORDER_AHEAD = "c_orderAhead";

    private final YextApi mYextApi;

    public StoresServiceImpl(YextApi yextApi) {
        mYextApi = yextApi;
    }

    @Override
    public void getStoreLocationsNear(int limit, int offset, LatLng centerCoordinates,
                                      Boolean orderAhead,
                                      Double firstRadius,
                                      Double secondRadius,
                                      LatLng currentLocation,
                                      ResultCallback<List<StoreLocation>> callback) {

        getStoreLocationsNearTwoPhasedSearchInternal(limit, offset, centerCoordinates, null, orderAhead,
                firstRadius, secondRadius, currentLocation, callback);
    }

    @Override
    public void getStoreLocationsNear(int limit, int offset, String addressName, Boolean orderAhead,
                                      Double firstRadius,
                                      Double secondRadius,
                                      LatLng currentLocation,
                                      ResultCallback<List<StoreLocation>> callback) {
        getStoreLocationsNearTwoPhasedSearchInternal(limit, offset, null, addressName, orderAhead,
                firstRadius, secondRadius, currentLocation, callback);
    }


    private void getStoreLocationsNearTwoPhasedSearchInternal(int limit, int offset, LatLng center, String addressName,
                                                              Boolean orderAhead,
                                                              @NotNull Double firstRadius,
                                                              Double secondRadius,
                                                              LatLng currentLocation,
                                                              ResultCallback<List<StoreLocation>> callback) {

        getStoreLocationsNearInternal(limit, offset, center, addressName, orderAhead, firstRadius, currentLocation,
                new ResultCallbackWrapper<List<StoreLocation>>(callback) {
                    @Override
                    public void onSuccess(List<StoreLocation> data) {
                        if (data.isEmpty() && secondRadius != null) {
                            // If we didn't get any results and a second radius as specified, then we do a
                            // second search with the alternative radius
                            getStoreLocationsNearInternal(
                                    limit, offset, center, addressName, orderAhead, secondRadius, currentLocation, callback
                            );
                        } else {
                            callback.onSuccess(data);
                        }
                    }
                });
    }

    @Override
    public void getStoreLocationById(String locationId, ResultCallback<StoreLocation> callback) {
        mYextApi.getLocation(locationId, BuildConfig.YEXT_DATA_VERSION).enqueue(new BaseRetrofitCallback<YextGetLocationResult>() {
            @Override
            protected void onSuccess(Response<YextGetLocationResult> response) {
                YextLocation yextLocation = response.body().getResponse();
                if (yextLocation != null) {
                    yextLocation.populateStoreHours();
                    callback.onSuccess(new StoreLocation(yextLocation, null));
                    return;
                }

                callback.onSuccess(null);
            }

            @Override
            protected void onError(Throwable throwable) {
                callback.onError(throwable);
            }

            @Override
            protected void onFail(Response<YextGetLocationResult> response) {
                callback.onFail(response.code(), response.message());
            }

            @Override
            protected void onNetworkFail(IOException throwable) {
                callback.onError(throwable);
            }
        });
    }

    private void getStoreLocationsNearInternal(int limit, int offset, LatLng center, String addressName,
                                               Boolean orderAhead, double radiusInMiles, LatLng currentLocation,
                                               ResultCallback<List<StoreLocation>> callback) {
        String locationsFilter;
        try {
            StringBuilder filters = new StringBuilder();
            filters.append("{\"$and\":[");

            filters.append("{\"c_brand\":{\"$eq\":\"");
            filters.append(AppUtils.getBrandYextValue(AppUtils.getBrand()));
            filters.append("\"}}");

            if (orderAhead != null) {
                filters.append(",{" + ORDER_AHEAD + ":{\"$eq\":");
                filters.append(orderAhead);
                filters.append("}}");
            }

            filters.append("]}");

            locationsFilter = URLEncoder.encode(filters.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, new LogErrorException("Error encoding locationFilter field", e));
            return;
        }
        mYextApi.getStoreLocationsNear(LOCATIONS, limit,
                center != null ? center.latitude + "," + center.longitude : addressName, radiusInMiles,
                offset, BuildConfig.YEXT_DATA_VERSION, locationsFilter)
                .enqueue(new BaseRetrofitCallback<YextSearchResult>() {

                    @Override
                    protected void onSuccess(Response<YextSearchResult> response) {
                        YextResponseData yextResponseData = response.body().getYextResponseData();
                        if (!yextResponseData.getYextLocations().isEmpty()) {
                            for (YextLocation yextLocation : yextResponseData.getYextLocations()) {
                                yextLocation.populateStoreHours();
                            }

                        }

                        callback.onSuccess(
                                StreamSupport.stream(yextResponseData.getYextLocations())
                                        .map(yextLocation ->
                                                new StoreLocation(
                                                        yextLocation, currentLocation))
                                        .filter(storeLocation -> !storeLocation.isComingSoon() && !storeLocation.isClosed())
                                        .collect(Collectors.toList()));
                    }

                    @Override
                    protected void onError(Throwable throwable) {
                        callback.onError(throwable);
                    }

                    @Override
                    protected void onFail(Response<YextSearchResult> response) {
                        callback.onFail(response.code(), response.message());
                    }

                    @Override
                    protected void onNetworkFail(IOException throwable) {
                        callback.onError(throwable);
                    }
                });
    }
}
