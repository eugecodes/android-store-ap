package caribouapp.caribou.com.cariboucoffee.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by andressegurola on 10/24/17.
 */

public final class GeoUtil {

    private GeoUtil() {
    }

    private static final String TAG = GeoUtil.class.getSimpleName();

    public static boolean isLocationServicesEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm != null && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public static LatLng toLatLng(double[] coordinates) {
        if (coordinates == null || coordinates.length == 0) {
            return null;
        }
        return new LatLng(coordinates[0], coordinates[1]);
    }

    public static LatLng toLatLng(Location location) {
        if (location == null) {
            return null;
        }
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static double[] toDoubleArray(Location location) {
        if (location == null) {
            return null;
        }
        return new double[]{location.getLatitude(), location.getLongitude()};
    }
}
