package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.common.StartOrderContract;

/**
 * Created by jmsmuy on 10/10/17.
 */

public interface LocationDetailsContract {

    interface View extends StartOrderContract.View {

        void setStoreFeatures(List<StoreFeature> storeFeatures);

        void showSchedule(Map<Integer, LocationScheduleModel> model);
    }

    interface Presenter extends StartOrderContract.Presenter {

        void init(boolean newOrder);

        void setStoreLocation(StoreLocation storeLocation);

        LatLng getStorePosition();

        StoreLocation getStoreLocation();

        String getStoreName();
    }
}
