package caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;

/**
 * Created by asegurola on 3/7/18.
 */

public class PickLocationModel extends BaseObservable implements Serializable {

    private List<StoreLocation> mNearbyStores = new ArrayList<>();
    private List<StoreLocation> mRecentStores = new ArrayList<>();
    private boolean mLocationServicesAvailable;
    private boolean mLocationAvailable = true;
    private boolean mFinishedLoading;

    @Bindable
    public List<StoreLocation> getRecentStores() {
        return mRecentStores;
    }

    public void setRecentStores(List<StoreLocation> recentStores) {
        mRecentStores = recentStores;
        notifyPropertyChanged(BR.recentStores);
    }

    @Bindable
    public List<StoreLocation> getNearbyStores() {
        return mNearbyStores;
    }

    public void setNearbyStores(List<StoreLocation> nearbyStores) {
        mNearbyStores = nearbyStores;
        notifyPropertyChanged(BR.nearbyStores);
    }

    @Bindable
    public boolean isLocationServicesAvailable() {
        return mLocationServicesAvailable;
    }

    public void setLocationServicesAvailable(boolean locationServicesAvailable) {
        mLocationServicesAvailable = locationServicesAvailable;
        notifyPropertyChanged(BR.locationServicesAvailable);
    }

    public void addRecentStore(StoreLocation storeLocation) {
        mRecentStores.add(storeLocation);
        notifyPropertyChanged(BR.recentStores);
    }

    @Bindable
    public boolean isFinishedLoading() {
        return mFinishedLoading;
    }

    public void setFinishedLoading(boolean finishedLoading) {
        mFinishedLoading = finishedLoading;
        notifyPropertyChanged(BR.finishedLoading);
    }

    public boolean isLocationAvailable() {
        return mLocationAvailable;
    }

    public void setLocationAvailable(boolean locationAvailable) {
        mLocationAvailable = locationAvailable;
    }
}
