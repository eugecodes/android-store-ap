package caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.presenter;


import static caribouapp.caribou.com.cariboucoffee.AppConstants.AMOUNT_DAYS_RECENT_ORDERS;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.common.StartOrderPresenter;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.PickLocationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model.PickLocationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by asegurola on 3/7/18.
 */

public class PickLocationPresenter extends StartOrderPresenter<PickLocationContract.View> implements PickLocationContract.Presenter {

    private static final int AMOUNT_RECENT_LOCATIONS = 3;
    private static final String TAG = PickLocationPresenter.class.getSimpleName();
    private final Map<String, StoreLocation> mRecentLocationsById = new HashMap<>();
    @Inject
    StoresService mStoresService;
    @Inject
    SettingsServices mSettingsServices;
    @Inject
    OrderService mOrderService;
    @Inject
    UserServices mUserServices;
    @Inject
    AppDataStorage mAppDataStorage;
    private PickLocationModel mModel;
    private boolean mRecentLocationsLoaded;
    private boolean mNearbyLocationsLoaded;
    private AtomicInteger mRemainingYextCallsCounter;

    public PickLocationPresenter(PickLocationContract.View view) {
        super(view);
    }

    @Override
    public void setModel(PickLocationModel model) {
        mModel = model;
        getView().setLocationHeader(mSettingsServices.getChooseLocationHeaderText());
    }

    @Override
    public void loadNearbyLocations(LatLng currentLocation) {
        // Loads nearby stores with Order Out of Store feature.
        mStoresService.getStoreLocationsNear(AppConstants.LOCATIONS_PAGE_SIZE, 0, currentLocation,
                true,
                AppConstants.LOCATIONS_DEFAULT_SEARCH_RADIUS_IN_MILES,
                null,
                currentLocation,
                new BaseViewResultCallback<List<StoreLocation>>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(List<StoreLocation> data) {
                        if (data != null) {
                            mModel.setNearbyStores(data);
                        }
                        mNearbyLocationsLoaded = true;
                        generateLocationsList();
                        Log.i(TAG, "Finished loading nearby locations");
                    }
                });
    }

    @Override
    public void loadRecentLocations() {
        mOrderService.getRecentOrder(AMOUNT_DAYS_RECENT_ORDERS,
                new BaseViewResultCallback<List<RecentOrderModel>>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(List<RecentOrderModel> recentOrderModels) {
                        for (RecentOrderModel recentOrderModel : recentOrderModels) {
                            mRecentLocationsById.put(recentOrderModel.getRecentOrderStore().getId(), null);
                            if (mModel.getRecentStores().size() >= AMOUNT_RECENT_LOCATIONS) {
                                break;
                            }
                        }
                        mRecentLocationsLoaded = true;

                        generateLocationsList();
                        Log.i(TAG, "Finished loading recent locations");
                    }
                });
    }

    @Override
    public boolean isThisGuestFlow() {
        return (!mUserServices.isUserLoggedIn() && mUserServices.isGuestUserFlowStarted());
    }

    /**
     * This method removes nearby locations that have been listed under recent locations
     * once both lists are populated
     */
    private void generateLocationsList() {

        boolean locationsNotYetLoaded = mModel.isLocationServicesAvailable() && mModel.isLocationAvailable() && !mNearbyLocationsLoaded;
        if (!mRecentLocationsLoaded || locationsNotYetLoaded) {
            return;
        }
        if (mNearbyLocationsLoaded && mUserServices.isUserLoggedIn()) {
            populateRecentStoreWithNearbyStoresData();
        }
        mRemainingYextCallsCounter = getNumberOfYextCallToGetRecentStorePendingData();
        if (mRemainingYextCallsCounter.intValue() == 0) {
            checkFinishLoadingLocations();
        }
        for (Map.Entry<String, StoreLocation> entry : mRecentLocationsById.entrySet()) {
            if (entry.getValue() == null) {
                loadYextLocationById(entry.getKey());
            }
        }

    }

    private void populateRecentStoreWithNearbyStoresData() {
        for (StoreLocation recentLocation : mModel.getNearbyStores()) {
            if (recentLocation.getId() != null) {
                if (!mRecentLocationsById.containsKey(recentLocation.getId())) {
                    continue;
                }
            }

            mRecentLocationsById.put(recentLocation.getId(), recentLocation);
        }
    }

    private AtomicInteger getNumberOfYextCallToGetRecentStorePendingData() {
        return new AtomicInteger(StreamSupport.stream(mRecentLocationsById.values()).filter(storeLocation ->
                storeLocation == null).collect(Collectors.toList()).size());
    }

    private void loadYextLocationById(String storeId) {
        mStoresService.getStoreLocationById(storeId, new BaseViewResultCallback<StoreLocation>(getView()) {
            @Override
            protected void onSuccessViewUpdates(StoreLocation data) {
                mRecentLocationsById.put(data.getId(), data);
                checkFinishLoadingLocations();
            }

            @Override
            protected void onErrorView(Throwable throwable) {
                mRecentLocationsById.remove(storeId);
                checkFinishLoadingLocations();
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                mRecentLocationsById.remove(storeId);
                checkFinishLoadingLocations();
            }

            @Override
            protected void onPostResponse() {
                mRemainingYextCallsCounter.decrementAndGet();
                super.onPostResponse();
            }
        });
    }

    private void checkFinishLoadingLocations() {

        if (mRemainingYextCallsCounter.intValue() > 0) {
            return;
        }
        mModel.setRecentStores(StreamSupport.stream(mRecentLocationsById.values())
                .filter(storeLocation -> storeLocation != null).collect(Collectors.toList()));
        /*
         * If user type is guest then we are not removing
         *  the recent location from near by location
         *  */
        if (!isThisGuestFlow()) {
            removeRecentLocationFromNearbyLocation();
        }
        /*
         * If user type is guest then don't need to show
         * recent stores, so setting to empty store list
         *  */
        if (isThisGuestFlow()) {
            mModel.setRecentStores(new ArrayList<>());
        }
        getView().displayLocations();
        mModel.setFinishedLoading(true);
    }

    private void removeRecentLocationFromNearbyLocation() {
        for (StoreLocation store : mModel.getRecentStores()) {
            mModel.getNearbyStores().remove(store);
        }
    }

    @Override
    public void search(String text) {
        getView().goToLocationSearch(text);
    }

    @Override
    public void showFilterDialog() {
        //NO-OP
    }

    @Override
    public void searchCleared() {
        //NO-OP
    }

    @Override
    public void searchTextEmpty(boolean isEmpty) {
        //NO-OP
    }

    public void setStoresService(StoresService storesService) {
        mStoresService = storesService;
    }

    public void setPickLocationModel(PickLocationModel model) {
        mModel = model;
    }

    @Override
    public void setOrderService(OrderService orderService) {
        mOrderService = orderService;
    }

    public void setUserServices(UserServices userServices) {
        mUserServices = userServices;
    }
}
