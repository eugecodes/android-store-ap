package caribouapp.caribou.com.cariboucoffee.common;

import com.google.android.gms.location.LocationListener;

public interface LocationAwareListener extends LocationListener {
    void onLocationFailed();
}
