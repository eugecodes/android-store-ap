package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutCustomLocationMarkerInfoBinding;

/**
 * Created by andressegurola on 10/17/17.
 */

public class LocationMarkerAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutCustomLocationMarkerInfoBinding mBinding;

    private Context mContext;

    public LocationMarkerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (mBinding == null) {
            mBinding = LayoutCustomLocationMarkerInfoBinding.inflate(LayoutInflater.from(mContext), null, false);
        }
        final StoreLocation storeLocation = (StoreLocation) marker.getTag();
        mBinding.setModel(storeLocation);
        mBinding.executePendingBindings();

        return mBinding.getRoot();
    }
}
