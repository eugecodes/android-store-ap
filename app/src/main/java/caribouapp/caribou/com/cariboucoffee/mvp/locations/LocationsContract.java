package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.common.SearchFieldViewPresenter;
import caribouapp.caribou.com.cariboucoffee.common.StartOrderContract;

/**
 * Created by andressegurola on 10/3/17.
 */

public interface LocationsContract {

    interface View extends StartOrderContract.View {
        void displayStoreLocations(List<StoreLocation> stores, boolean includeCurrentLocation);

        void setCurrentLocationName(String locationName);

        void displayNoStoresForSearch(LatLng searchLatLng, boolean filterIsHidingLocations);

        void displayNoLocationForName();

        void displaySelectedStore(StoreLocation store);

        void clearErrorMessage();

        void showFilterDialog(Set<StoreFeature> storeFeatureFilter);

        void askForLoginOrSignup();
    }

    interface Presenter extends StartOrderContract.Presenter, SearchFieldViewPresenter {
        void loadStoreLocationsForCurrentLocation(LatLng latLng);

        LocationsModel getModel();

        void setModel(LocationsModel model);

        void setSelectedStore(StoreLocation tag);

        void applyFilter(Set<StoreFeature> filterOptions);

        boolean isSearchForOosStoresMode();

        void setSearchForOosStoresMode(String searchText);
    }
}
