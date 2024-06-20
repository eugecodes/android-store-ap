package caribouapp.caribou.com.cariboucoffee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTimeZone;
import org.joda.time.tz.UTCProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.YextApi;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextGetLocationResult;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextSearchResult;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.PickLocationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model.PickLocationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.presenter.PickLocationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderStore;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.ncr.NcrRecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.stores.StoresServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import retrofit2.Call;

@RunWith(MockitoJUnitRunner.class)
public class PickLocationTest {

    private PickLocationPresenter mPickLocationPresenter;
    private PickLocationModel mPickLocationModel;
    @Mock
    private StoresServiceImpl mStoresService;
    @Mock
    private OrderService mOrderService;
    @Mock
    private YextApi mYextApi;
    @Mock
    private Call<YextGetLocationResult> mGetLocationCall;
    @Mock
    private Call<YextSearchResult> mNearbyStoresCall;
    @Mock
    private PickLocationContract.View mView;
    @Mock
    private UserServices mUserServices;

    @Before
    public void setup() {
        DateTimeZone.setProvider(new UTCProvider());
        mPickLocationPresenter = new PickLocationPresenter(mView);

        mPickLocationModel = new PickLocationModel();
        mPickLocationPresenter.setPickLocationModel(mPickLocationModel);
        when(mYextApi.getStoreLocationsNear(anyString(), anyInt(), anyString(), anyDouble(), anyInt(), anyString(), anyString())).thenReturn(mNearbyStoresCall);
        MockitoUtil.mockEnqueue(mNearbyStoresCall, "/store_locations.json", YextSearchResult.class);

        mockOrderService();

        mStoresService = new StoresServiceImpl(mYextApi);
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(mView).runOnUiThread(any());
        when(mView.isActive()).thenReturn(true);
        mPickLocationPresenter.setStoresService(mStoresService);
        mPickLocationPresenter.setOrderService(mOrderService);
        mPickLocationPresenter.setUserServices(mUserServices);
    }

    private void mockOrderService() {
        doAnswer(invocation -> {
            final List<RecentOrderModel> data = new ArrayList<>();
            data.add(buildRecentOrder());
            final ResultCallback<List<RecentOrderModel>> callback = (ResultCallback<List<RecentOrderModel>>) invocation.getArguments()[1];
            callback.onSuccess(data);
            return null;
        }).when(mOrderService).getRecentOrder(anyInt(), any());
    }

    private NcrRecentOrderModel buildRecentOrder() {
        NcrRecentOrderModel result = new NcrRecentOrderModel();
        result.setRecentOrderStore(new RecentOrderStore("Any Name", "9920"));
        return result;
    }


    @Test
    public void testRecentLocationInNearbyLocation() {
        Mockito.when(mYextApi.getLocation(Mockito.anyString(), Mockito.any())).thenReturn(mGetLocationCall);
        MockitoUtil.mockEnqueue(mGetLocationCall, "/test_yext_store_open_hours_get_location.json", YextGetLocationResult.class);
        MockitoUtil.mockEnqueue(mNearbyStoresCall, "/store_locations.json", YextSearchResult.class);
        mPickLocationPresenter.loadNearbyLocations(new LatLng(0,0));
        mPickLocationPresenter.loadRecentLocations();
        assertEquals(1, mPickLocationModel.getRecentStores().size());
        assertEquals(49, mPickLocationModel.getNearbyStores().size());
        assertFalse(mPickLocationModel.getNearbyStores().contains(mPickLocationModel.getRecentStores().get(0)));
    }

    @Test
    public void testRecentLocationNotInNearbyLocation() {
        Mockito.when(mYextApi.getLocation(Mockito.anyString(), Mockito.any())).thenReturn(mGetLocationCall);
        MockitoUtil.mockEnqueue(mGetLocationCall, "/test_yext_store_open_hours_get_location.json", YextGetLocationResult.class);
        MockitoUtil.mockEnqueue(mNearbyStoresCall, "/store_locations_without_9920_store.json", YextSearchResult.class);
        mPickLocationPresenter.loadNearbyLocations(new LatLng(0,0));
        mPickLocationPresenter.loadRecentLocations();
        assertEquals(1, mPickLocationModel.getNearbyStores().size());
        assertEquals(1, mPickLocationModel.getRecentStores().size());
        assertFalse(mPickLocationModel.getNearbyStores().contains(mPickLocationModel.getRecentStores().get(0)));
    }
}
