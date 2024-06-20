package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.StartOrderPresenter;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;

/**
 * Created by jmsmuy on 10/10/17.
 */

public class LocationDetailsPresenter extends StartOrderPresenter<LocationDetailsContract.View> implements LocationDetailsContract.Presenter {


    @Inject
    StoresService mStoresService;

    @Inject
    AppDataStorage mAppDataStorage;

    private StoreLocation mModel;

    public LocationDetailsPresenter(LocationDetailsContract.View view) {
        super(view);
    }

    @Override
    public void init(boolean newOrder) {
        if (newOrder) {
            mAppDataStorage.setOrderLastScreen(AppScreen.LOCATION_DETAILS);
        } else {
            addSchedule();
        }
    }

    @Override
    public StoreLocation getStoreLocation() {
        return mModel;
    }

    private void addSchedule() {
        getView().showSchedule(mModel.getOpenHourSchedule());
    }

    @Override
    public void setStoreLocation(StoreLocation storeLocation) {
        mModel = storeLocation;
        getView().setStoreFeatures(storeLocation.getFeatures());
    }

    public String getStoreName() {
        return mModel != null ? mModel.getName() : null;
    }


    @Override
    public LatLng getStorePosition() {
        return mModel != null ? new LatLng(mModel.getLatitude(), mModel.getLongitude()) : null;
    }
}
