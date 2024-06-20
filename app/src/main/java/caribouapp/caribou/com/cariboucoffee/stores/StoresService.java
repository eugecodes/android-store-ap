package caribouapp.caribou.com.cariboucoffee.stores;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;

/**
 * Created by asegurola on 2/27/18.
 */

public interface StoresService {

    void getStoreLocationsNear(int limit, int offset,
                               LatLng centerCoordinates, Boolean orderAhead,
                               @NotNull Double firstRadius,
                               Double secondRadius,
                               LatLng currentLocation,
                               ResultCallback<List<StoreLocation>> callback);

    void getStoreLocationsNear(int limit, int offset,
                               String addressName, Boolean orderAhead,
                               @NotNull Double firstRadius,
                               Double secondRadius,
                               LatLng currentLocation,
                               ResultCallback<List<StoreLocation>> callback);

    void getStoreLocationById(String locationId, ResultCallback<StoreLocation> callback);
}
