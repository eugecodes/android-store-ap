package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import static caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreFeature.ORDER_OUT_OF_STORE;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.api.OAuthAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.common.StartOrderPresenter;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.TokenSignInRequest;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;
import caribouapp.caribou.com.cariboucoffee.util.GeoUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import retrofit2.Response;

/**
 * Created by andressegurola on 10/9/17.
 */

public class LocationsPresenter extends StartOrderPresenter<LocationsContract.View> implements LocationsContract.Presenter {

    private static final String TAG = LocationsPresenter.class.getSimpleName();

    @Inject
    StoresService mStoresService;

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    EventLogger mEventLogger;

    @Inject
    Tagger mTagger;

    @Inject
    UserServices mUserServices;

    @Inject
    OAuthAPI mOAuthAPI;

    private LocationsModel mModel = new LocationsModel();

    public LocationsPresenter(LocationsContract.View view) {
        super(view);
    }

    @Override
    public void loadStoreLocationsForCurrentLocation(LatLng latLng) {
        if (getView() == null) {
            return;
        }
        getView().clearErrorMessage();
        mModel.setSearchError(null);
        mModel.setCurrentSearchLocation(createCoordinates(latLng));
        mModel.setIncludeCurrentLocation(true);
        loadStoreLocationsForPosition(latLng);
    }
    private void loadStoreLocationsForPosition(final LatLng center) {
        mStoresService
                .getStoreLocationsNear(AppConstants.LOCATIONS_PAGE_SIZE, 0, center, null,
                        AppConstants.LOCATIONS_DEFAULT_SEARCH_RADIUS_IN_MILES,
                        AppConstants.LOCATIONS_STATE_WIDE_SEARCH_RADIUS_IN_MILES,
                        GeoUtil.toLatLng(mModel.getCurrentLocation()),
                        new BaseViewResultCallback<List<StoreLocation>>(getView()) {
                            @Override
                            protected void onSuccessViewUpdates(List<StoreLocation> stores) {
                                mModel.setLastSearchCoordinates(new double[]{center.latitude, center.longitude});
                                mModel.setFullSearchResult(stores);
                                updateStores();
                            }
                        });
    }

    private void updateStores() {
        mModel.setSelectedStore(null);
        if (mModel.getFilteredSearchResult().isEmpty()) {
            mModel.setSearchError(LocationsModel.SearchError.NO_STORES_FOR_SEARCH);
            updateNoStores();
        } else {
            mModel.setSearchError(null);
            getView().displayStoreLocations(mModel.getFilteredSearchResult(), mModel.isIncludeCurrentLocation());
        }
    }

    private void updateNoStores() {
        getView()
                .displayNoStoresForSearch(createLatLng(mModel.getLastSearchCoordinates()),
                        !mModel.getStoreFeatureFilter().isEmpty() && !mModel.getFullSearchResult().isEmpty());
    }

    @Override
    public boolean isSearchForOosStoresMode() {
        return mModel.isSearchForOosStoresMode();
    }

    @Override
    public void setSearchForOosStoresMode(String searchText) {
        mModel.setSearchForOosStoresMode(true);
        addOosFilter();
        search(searchText);
    }

    private void addOosFilter() {
        Set<StoreFeature> set = mModel.getStoreFeatureFilter();
        set.add(ORDER_OUT_OF_STORE);
        mModel.setStoreFeatureFilter(set);
        mEventLogger.logLocationFilterOrderAhead();
    }

    @Override
    public void search(String text) {
        mModel.setIncludeCurrentLocation(false);
        mModel.setSearchError(null);
        getView().clearErrorMessage();
        mModel.setCurrentSearchLocationName(text);
        getView().setCurrentLocationName(text);
        mStoresService
                .getStoreLocationsNear(AppConstants.LOCATIONS_PAGE_SIZE, 0, text, null,
                        AppConstants.LOCATIONS_DEFAULT_SEARCH_RADIUS_IN_MILES,
                        AppConstants.LOCATIONS_STATE_WIDE_SEARCH_RADIUS_IN_MILES,
                        GeoUtil.toLatLng(mModel.getCurrentLocation()),
                        new BaseViewResultCallback<List<StoreLocation>>(getView()) {
                            @Override
                            protected void onSuccessViewUpdates(List<StoreLocation> stores) {
                                mTagger.tagLocationSearched();
                                mModel.setLastSearchCoordinates(null);
                                mModel.setCurrentSearchLocationName(text);
                                if (!isSearchForOosStoresMode()) {
                                    mEventLogger.logLocationSearch();
                                }
                                mModel.setSearchForOosStoresMode(false);
                                mModel.setFullSearchResult(stores);
                                updateStores();
                            }
                        });

    }

    @Override
    public LocationsModel getModel() {
        return mModel;
    }

    public void setModel(LocationsModel model) {
        mModel = model;
        if (isSearchForOosStoresMode()) {
            addOosFilter();
        }

        if (mModel.getCurrentSearchLocationName() != null) {
            getView().setCurrentLocationName(mModel.getCurrentSearchLocationName());
        }

        if (mModel.getSearchError() != null) {
            switch (mModel.getSearchError()) {
                case NO_LOCATION_FOR_NAME:
                    getView().displayNoLocationForName();
                    break;
                case NO_STORES_FOR_SEARCH:
                    updateNoStores();
                    break;
            }
        } else {
            getView().displayStoreLocations(mModel.getFilteredSearchResult(), mModel.isIncludeCurrentLocation());
            if (mModel.getSelectedStore() != null) {
                getView().displaySelectedStore(mModel.getSelectedStore());
            }
        }
    }

    @Override
    public void startNewOrder(String storeLocationId) {
        if (mUserServices.isUserLoggedIn()) {
            super.startNewOrder(storeLocationId);
        } else if (mSettingsServices.isGuestCheckoutEnabledFromDashboard()) {
            callAnonTokenForGuestUser(() -> {
                startGuestUserFlow();
                super.startNewOrder(storeLocationId);
            });
        } else {
            getView().askForLoginOrSignup();
        }
    }

    private LatLng createLatLng(double[] coordinates) {
        if (coordinates == null) {
            return null;
        }
        return new LatLng(coordinates[0], coordinates[1]);
    }

    private double[] createCoordinates(LatLng latLng) {
        return new double[]{latLng.latitude, latLng.longitude};
    }

    @Override
    public void setSelectedStore(StoreLocation store) {
        StoreLocation previouslySelected = mModel.getSelectedStore();
        if (previouslySelected != null && previouslySelected.equals(store)) {
            return;
        }
        mModel.setSelectedStore(store);
        getView().displaySelectedStore(store);
    }

    @Override
    public void applyFilter(Set<StoreFeature> filterOptions) {
        for (StoreFeature storeFeature : filterOptions) {
            if (!storeFeature.equals(ORDER_OUT_OF_STORE)) {
                mEventLogger.logLocationFilterOrderAhead();
            }
            Log.d(TAG, "StoreFilter: " + storeFeature);
        }
        mModel.setStoreFeatureFilter(filterOptions);
        updateStores();
    }


    @Override
    public void showFilterDialog() {
        getView().showFilterDialog(mModel.getStoreFeatureFilter());
    }

    @Override
    public void searchCleared() {
        // NO-OP
    }

    @Override
    public void searchTextEmpty(boolean isEmpty) {
        // NO-OP
    }

    private void callAnonTokenForGuestUser(Runnable callback) {
        /**
         * Generate device id
         * and isAnonymous flag is true for guest checkout
         * */
        mOAuthAPI.authenticate(new TokenSignInRequest(mSettingsServices.getDeviceId(), true))
                .enqueue(new BaseViewRetrofitCallback<OauthSignInResponse>(getView()) {

                    @Override
                    protected void onSuccessViewUpdates(Response<OauthSignInResponse> response) {
                        OauthSignInResponse oAuthSignInResponse = response.body();
                        saveGuestSignInData(oAuthSignInResponse.getToken(), oAuthSignInResponse.getAud());
                        callback.run();
                    }

                    @Override
                    protected void onFail(Response<OauthSignInResponse> response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().showWarning(R.string.unknown_error);
                    }
                });
    }

    private void saveGuestSignInData(String token, String uid) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {
            return;
        }
        mUserServices.setAnonAuthToken(token);
        mUserServices.setGuestUid(uid);
    }

    private void startGuestUserFlow() {
        mUserServices.setGuestUserFlowStarted(true);
    }
}
