package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by andressegurola on 10/11/17.
 */

public class LocationsModel extends BaseObservable implements Serializable {

    public enum SearchError {
        NO_LOCATION_FOR_NAME,
        NO_STORES_FOR_SEARCH
    }

    private List<StoreLocation> mFullSearchResult = new ArrayList<>();

    private List<StoreLocation> mFilteredSearchResult = new ArrayList<>();

    private boolean mIncludeCurrentLocation = true;

    private boolean mSearchForOosStoresMode;

    private String mCurrentSearchLocationName;

    private double[] mCurrentSearchLocation;

    private boolean mLocationAvailable = true;

    private double[] mCurrentLocation;

    private StoreLocation mSelectedStore;

    private SearchError mSearchError;

    private double[] mLastSearchCoordinates;

    private Set<StoreFeature> mStoreFeatureFilter = new HashSet<>();

    private boolean mOngoingOrderExists;

    public List<StoreLocation> getFullSearchResult() {
        return mFullSearchResult;
    }

    public void setFullSearchResult(List<StoreLocation> fullSearchResult) {
        mFullSearchResult = new ArrayList<>(StreamSupport.stream(fullSearchResult)
                .filter(storeLocation -> !storeLocation.isClosed()).collect(Collectors.toList()));
        applyFilter();
    }

    public Set<StoreFeature> getStoreFeatureFilter() {
        return mStoreFeatureFilter;
    }

    public void setStoreFeatureFilter(Set<StoreFeature> storeFeatureFilter) {
        mStoreFeatureFilter = storeFeatureFilter;
        applyFilter();
    }

    private void applyFilter() {

        if (mStoreFeatureFilter.isEmpty()) {
            mFilteredSearchResult.clear();
            mFilteredSearchResult.addAll(mFullSearchResult);
            return;
        }

        mFilteredSearchResult.clear();
        mFilteredSearchResult.addAll(StreamSupport.stream(mFullSearchResult)
                .filter(storeLocation -> !storeLocation.getFeatures().isEmpty()
                        && StreamSupport.stream(mStoreFeatureFilter)
                        .allMatch(storeFeature -> storeLocation.getFeatures().contains(storeFeature)))
                .collect(Collectors.toList()));

    }

    public boolean isIncludeCurrentLocation() {
        return mIncludeCurrentLocation;
    }

    public void setIncludeCurrentLocation(boolean includeCurrentLocation) {
        mIncludeCurrentLocation = includeCurrentLocation;
    }

    public double[] getCurrentSearchLocation() {
        return mCurrentSearchLocation;
    }

    public void setCurrentLocation(double[] gpsLocation) {
        mCurrentLocation = gpsLocation;
    }

    public double[] getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentSearchLocation(double[] currentSearchLocation) {
        mCurrentSearchLocation = currentSearchLocation;
    }

    public StoreLocation getSelectedStore() {
        return mSelectedStore;
    }

    public void setSelectedStore(StoreLocation selectedStore) {
        mSelectedStore = selectedStore;
    }

    @Bindable
    public String getCurrentSearchLocationName() {
        return mCurrentSearchLocationName;
    }

    public void setCurrentSearchLocationName(String currentSearchLocationName) {
        mCurrentSearchLocationName = currentSearchLocationName;
        notifyPropertyChanged(BR.currentSearchLocationName);
    }

    public void setSearchError(SearchError error) {
        mSearchError = error;
    }

    public SearchError getSearchError() {
        return mSearchError;
    }

    public double[] getLastSearchCoordinates() {
        return mLastSearchCoordinates;
    }

    public void setLastSearchCoordinates(double[] lastSearchCoordinates) {
        mLastSearchCoordinates = lastSearchCoordinates;
    }

    public List<StoreLocation> getFilteredSearchResult() {
        return mFilteredSearchResult;
    }

    public void setFilteredSearchResult(List<StoreLocation> filteredSearchResult) {
        mFilteredSearchResult = filteredSearchResult;
    }

    public boolean isOngoingOrderExists() {
        return mOngoingOrderExists;
    }

    public void setOngoingOrderExists(boolean ongoingOrderExists) {
        mOngoingOrderExists = ongoingOrderExists;
    }

    public boolean isSearchForOosStoresMode() {
        return mSearchForOosStoresMode;
    }

    public void setSearchForOosStoresMode(boolean searchForOosStoresMode) {
        mSearchForOosStoresMode = searchForOosStoresMode;
    }

    public boolean isLocationAvailable() {
        return mLocationAvailable;
    }

    public void setLocationAvailable(boolean locationAvailable) {
        mLocationAvailable = locationAvailable;
    }
}
