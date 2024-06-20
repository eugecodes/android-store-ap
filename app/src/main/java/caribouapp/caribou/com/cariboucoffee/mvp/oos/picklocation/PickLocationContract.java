package caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation;

import com.google.android.gms.maps.model.LatLng;

import caribouapp.caribou.com.cariboucoffee.common.SearchFieldViewPresenter;
import caribouapp.caribou.com.cariboucoffee.common.StartOrderContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model.PickLocationModel;

/**
 * Created by asegurola on 3/6/18.
 */

public interface PickLocationContract {
    interface View extends StartOrderContract.View {
        void displayLocations();

        void checkLocationServices();

        void goToLocationSearch(String searchText);

        void setLocationHeader(String headerText);
    }

    interface Presenter extends StartOrderContract.Presenter, SearchFieldViewPresenter {

        void setModel(PickLocationModel model);

        void loadNearbyLocations(LatLng currentLocation);

        void loadRecentLocations();

        boolean isThisGuestFlow();
    }
}
