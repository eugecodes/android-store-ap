package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.api.YextApi;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextGetLocationResult;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;
import caribouapp.caribou.com.cariboucoffee.stores.StoresServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import retrofit2.Call;

@RunWith(MockitoJUnitRunner.class)
public class StoreYextOpenStatusTest {

    @Mock
    private YextApi mYextApi;

    private StoresService mStoreService;

    @Mock
    private Call<YextGetLocationResult> mGetLocationCall;

    @Before
    public void setup() {
        mStoreService = new StoresServiceImpl(mYextApi);
        when(mYextApi.getLocation(anyString(), anyString()))
                .thenReturn(mGetLocationCall);
    }

    @Test
    public void testYextOpenHoursMonday() {
        MockitoUtil
                .mockEnqueue(mGetLocationCall,
                        "/test_yext_store_open_hours_get_location.json", YextGetLocationResult.class);

        mStoreService.getStoreLocationById("01", new ResultCallback<StoreLocation>() {
            @Override
            public void onSuccess(StoreLocation data) {

                // Check 3:10 am should be closed
                StoreHoursCheckUtil.OpenStatus openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 6, 3, 10)), data);
                Assert.assertFalse(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(4, 0), openStatus.getNextTime());


                // Check 4:10 am should be open
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 6, 4, 10)), data);
                Assert.assertTrue(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(23, 0), openStatus.getNextTime());


                // Check 13:00 am should be open
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 6, 13, 0)), data);
                Assert.assertTrue(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(23, 0), openStatus.getNextTime());


                // Check 23:30 am should be closed
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 6, 23, 30)), data);
                Assert.assertFalse(openStatus.isOpen());
                Assert.assertEquals(null, openStatus.getNextTime());

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
    }


    @Test
    public void testYextOpenHoursWeekendSaturday() {
        MockitoUtil
                .mockEnqueue(mGetLocationCall,
                        "/test_yext_store_open_hours_get_location.json", YextGetLocationResult.class);

        mStoreService.getStoreLocationById("01", new ResultCallback<StoreLocation>() {
            @Override
            public void onSuccess(StoreLocation data) {

                // Check 6:10 am should be closed
                StoreHoursCheckUtil.OpenStatus openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 4, 6, 10)), data);
                Assert.assertFalse(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(8, 0), openStatus.getNextTime());


                // Check 8:10 am should be open
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 4, 8, 10)), data);
                Assert.assertTrue(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(16, 0), openStatus.getNextTime());


                // Check 13:00 am should be open
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 4, 13, 0)), data);
                Assert.assertTrue(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(16, 0), openStatus.getNextTime());


                // Check 17:00 am should be closed
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 4, 17, 0)), data);
                Assert.assertFalse(openStatus.isOpen());
                Assert.assertEquals(null, openStatus.getNextTime());

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
    }


    @Test
    public void testYextOpenHoursWeekendSunday() {
        MockitoUtil
                .mockEnqueue(mGetLocationCall,
                        "/test_yext_store_open_hours_get_location.json", YextGetLocationResult.class);

        mStoreService.getStoreLocationById("01", new ResultCallback<StoreLocation>() {
            @Override
            public void onSuccess(StoreLocation data) {

                // Check 8:10 am should be closed
                StoreHoursCheckUtil.OpenStatus openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 5, 8, 10)), data);
                Assert.assertFalse(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(10, 0), openStatus.getNextTime());


                // Check 10:10 am should be open
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 5, 10, 10)), data);
                Assert.assertTrue(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(14, 0), openStatus.getNextTime());


                // Check 13:00 am should be open
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 5, 13, 0)), data);
                Assert.assertTrue(openStatus.isOpen());
                Assert.assertEquals(new LocalTime(14, 0), openStatus.getNextTime());


                // Check 15:00 am should be closed
                openStatus =
                        StoreHoursCheckUtil.getStoreOpenStatus(new CustomClock(new DateTime(2019, 5, 5, 15, 0)), data);
                Assert.assertFalse(openStatus.isOpen());
                Assert.assertEquals(null, openStatus.getNextTime());

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
    }
}
