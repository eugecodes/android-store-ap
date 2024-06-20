package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static caribouapp.caribou.com.cariboucoffee.stores.StoresServiceImpl.ORDER_AHEAD;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.YextApi;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextSearchResult;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;
import caribouapp.caribou.com.cariboucoffee.stores.StoresServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class StoreServicesImplTest {


    private StoresService mStoreService;

    @Mock
    private YextApi mYextApi;

    private ArgumentCaptor<Callback> mLocationCallCaptor;
    private ArgumentCaptor<String> mFilterCaptor;
    private Call<YextSearchResult> mLocationCall;


    @Before
    public void setup() {

        mLocationCall = mock(Call.class);
        mLocationCallCaptor = ArgumentCaptor.forClass(Callback.class);

        mFilterCaptor = ArgumentCaptor.forClass(String.class);

        when(
                mYextApi.getStoreLocationsNear(
                        anyString(),
                        anyInt(),
                        anyString(),
                        anyDouble(),
                        anyInt(),
                        anyString(),
                        mFilterCaptor.capture())).thenReturn(mLocationCall);

        mStoreService = new StoresServiceImpl(mYextApi);

    }

    @Test
    public void testBrandFilter() {
        LatLng caribouHqLatLng = new LatLng(45.0441802, -93.3339986);
        mStoreService.getStoreLocationsNear(10, 10, caribouHqLatLng, null,
                30d, 400d, caribouHqLatLng,
                new ResultCallback<List<StoreLocation>>() {
                    @Override
                    public void onSuccess(List<StoreLocation> data) {
                        // Check that the customField Id and customField value
                        Assert.assertFalse(mFilterCaptor.getValue().contains(ORDER_AHEAD));
                        try {
                            Assert.assertTrue(mFilterCaptor.getValue().contains(URLEncoder.encode(AppUtils.getBrandYextValue(AppUtils.getBrand()), "UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFail(int errorCode, String errorMessage) {
                        Assert.fail();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Assert.fail();
                    }
                });


        verify(mLocationCall).enqueue(mLocationCallCaptor.capture());
        mLocationCallCaptor.getValue().onResponse(null, Response.success(GsonUtil.readObjectFromClasspath("/store_locations.json", YextSearchResult.class)));
    }

    @Test
    public void testOrderAheadFilter() {
        LatLng caribouHqLatLng = new LatLng(45.0441802, -93.3339986);
        mStoreService.getStoreLocationsNear(10, 10, caribouHqLatLng, true, 30d, 400d,
                caribouHqLatLng, new ResultCallback<List<StoreLocation>>() {
                    @Override
                    public void onSuccess(List<StoreLocation> data) {
                        // Check that the customField Id and customField value
                        Assert.assertTrue(mFilterCaptor.getValue().contains(ORDER_AHEAD));
                    }

                    @Override
                    public void onFail(int errorCode, String errorMessage) {
                        Assert.fail();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Assert.fail();
                    }
                });


        verify(mLocationCall).enqueue(mLocationCallCaptor.capture());
        mLocationCallCaptor.getValue().onResponse(null, Response.success(GsonUtil.readObjectFromClasspath("/store_locations.json", YextSearchResult.class)));
    }
}
