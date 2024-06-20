package caribouapp.caribou.com.cariboucoffee;

import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static caribouapp.caribou.com.cariboucoffee.util.DefaultStoreHelper.defaultStore;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tracer;
import caribouapp.caribou.com.cariboucoffee.analytics.TracerFactory;
import caribouapp.caribou.com.cariboucoffee.api.NcrWrapperApi;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsidePickupData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.OrderCheckoutContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter.OrderCheckoutPresenter;
import caribouapp.caribou.com.cariboucoffee.order.PickupData;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrderServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class PlaceOrderFlowNcrTest {

    OrderCheckoutPresenter mOrderCheckoutPresenter;
    CheckoutModel mCheckoutModel;
    NcrOrderWrappedData mNcrOrderWrappedData;
    StoreLocation mStoreLocation;
    Semaphore mWaitForBounce;
    Semaphore mWaitForArgumentCaptor;

    @Mock
    private NcrOrder mOrder;

    @Mock
    private NcrOrderServiceImpl mOrderService;

    @Mock
    private OrderCheckoutContract.View mOrderCheckoutView;

    @Mock
    private NcrWrapperApi mNcrWrapperApi;

    @Mock
    private UserServices mUserServices;

    @Mock
    private NcrMenuDataServiceImpl mMenuDataService;

    @Mock
    private ErrorMessageMapper mErrorMessageMapper;

    @Mock
    private Call<NcrOrderWrappedData> mPostOrderCall;

    @Mock
    private Call<NcrOrderWrappedData> mPostOrderCheckStatusCall;

    @Mock
    private Call<ResponseBody> mGetNcrCheckStatusCall;

    @Mock
    private Call<SVmsResponse> mGetBalanceCall;

    @Mock
    private PickUpTimeModel mPickUpTimeModel;

    @Mock
    private EventLogger mEventLogger;

    @Mock
    private SVmsAPI mSVmsAPI;

    @Mock
    private Tagger mTagger;

    @Mock
    private SettingsServices mSettingsServices;

    @Mock
    private TracerFactory mTracerFactory;

    @Mock
    private Tracer mTracer;

    @Mock
    private UserServices userServices;

    private CustomClock mCustomClock;

    @Before
    public void setup() throws IOException {
        mCheckoutModel = new CheckoutModel();
        mStoreLocation = defaultStore();
        initOrder();
        mCustomClock = new CustomClock(new DateTime(2020, 6,
                9, 10, 20, 0, 0));
        mOrderCheckoutPresenter = new OrderCheckoutPresenter(mOrderCheckoutView, mCheckoutModel);

        when(mSettingsServices.getOrderAheadCheckStatusMaxAttempts()).thenReturn(8);

        mOrderService = new NcrOrderServiceImpl(mCustomClock, userServices, mMenuDataService,
                mNcrWrapperApi, mSettingsServices, mTracerFactory);

        setMockToCheckoutPresenter();
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(mOrderCheckoutView).runOnUiThread(any());
        when(mNcrWrapperApi.putOrder(any(), any(), any())).then(invocation -> {
            mNcrOrderWrappedData = (NcrOrderWrappedData) invocation.getArguments()[2];
            return mPostOrderCall;
        });
        when(mErrorMessageMapper.isSuccessful(any(Response.class))).thenReturn(true);
        when(mPickUpTimeModel.isAsap()).thenReturn(true);
        when(mOrderCheckoutView.isActive()).thenReturn(true);
        when(mNcrWrapperApi.checkServerStatus()).thenReturn(mGetNcrCheckStatusCall);
        MockitoUtil.mockEnqueueWithSuccessEmptyResponse(mGetNcrCheckStatusCall);
        when(mSVmsAPI.getBalanceNoCache(any())).thenReturn(mGetBalanceCall);
        MockitoUtil.mockEnqueue(mGetBalanceCall, "/test_get_balance.json", SVmsResponse.class);
        when(mNcrWrapperApi.getOrder(anyString())).thenReturn(mPostOrderCheckStatusCall);
        MockitoUtil.mockAsynchronousRetrofitRequest(mPostOrderCheckStatusCall, "/test_order_check_status_inprogress_ncr.json", NcrOrderWrappedData.class);
        when(mSVmsAPI.getBalanceNoCache(any())).thenReturn(mGetBalanceCall);
        when(mSettingsServices.getOrderMinutesBeforeClosing()).thenReturn(15);
        setThreadLogicForNcr();
        mOrder.getStoreLocation().setDeliveryMinimum(new BigDecimal(0));
        mOrder.setTotal(new BigDecimal(10));
        mCheckoutModel.setOrder(mOrder);
        mOrderService.setOrder(mOrder);


        when(mTracerFactory.createTracer(anyString())).thenReturn(mTracer);
        when(mTracer.start()).thenReturn(mTracer);
        mOrder.setPickupData(new PickupData());
    }

    @Test
    public void testOmsStatusFail() {
        MockitoUtil.mockEnqueueWithError(mGetNcrCheckStatusCall, 404);
        mOrderCheckoutPresenter.placeOrder();
        verify(mOrderCheckoutView, times(1)).showFailToPlaceOrderDialog();
    }

    @Test
    public void testOmsStatusFailException() {
        MockitoUtil.mockEnqueueToFail(mGetNcrCheckStatusCall, new Throwable());
        mOrderCheckoutPresenter.placeOrder();
        verify(mOrderCheckoutView, times(1)).showFailToPlaceOrderDialog();
    }

    @Test
    public void testOrderFail() {
        MockitoUtil.mockEnqueueToFail(mPostOrderCall, new Throwable());
        mOrderCheckoutPresenter.placeOrder();
        verify(mOrderCheckoutView, times(1)).showFailToPlaceOrderDialog();
    }

    @Test
    public void testOrderDefaultWalkIn() throws InterruptedException {
        when(mNcrWrapperApi.putOrder(any(), any(), any())).then(invocation -> {
            NcrOrderWrappedData ncrOrderWrappedData = (NcrOrderWrappedData) invocation.getArguments()[2];
            Assert.assertEquals(ncrOrderWrappedData.getPickup().getType(), YextPickupType.WalkIn.getServerName());
            Assert.assertNull(ncrOrderWrappedData.getPickup().getCarColor());
            Assert.assertNull(ncrOrderWrappedData.getPickup().getCarType());
            mWaitForArgumentCaptor.release();
            return mPostOrderCall;
        });
        assurePlaceOrder();
    }

    @Test
    public void testOrderScheduleInParseCorrectly() throws InterruptedException {
        PickUpTimeModel pickUpTimeModel = new PickUpTimeModel(new LocalTime("14:40"), false);
        mOrder.setChosenPickUpTime(pickUpTimeModel);
        when(mNcrWrapperApi.putOrder(any(), any(), any())).then(invocation -> {
            NcrOrderWrappedData ncrOrderWrappedData = (NcrOrderWrappedData) invocation.getArguments()[2];
            Assert.assertEquals(260, ncrOrderWrappedData.getScheduleIn().intValue());
            Assert.assertNotNull(ncrOrderWrappedData.getPickup().getPickupTime());
            LocalTime pickupTime = pickUpTimeModel.getPickUpTime();
            DateTime expectedPickUpDateTime = mCustomClock.getCurrentDateTime()
                    .withHourOfDay(pickupTime.getHourOfDay())
                    .withMinuteOfHour(pickupTime.getMinuteOfHour())
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);
            Assert.assertEquals(expectedPickUpDateTime, ncrOrderWrappedData.getPickup().getPickupTime());
            mWaitForArgumentCaptor.release();
            return mPostOrderCall;
        });
        assurePlaceOrder();
    }

    @Test
    public void testOrderWithOutScheduleIn() throws InterruptedException {
        PickUpTimeModel pickUpTimeModel = new PickUpTimeModel(true);
        mOrder.setChosenPickUpTime(pickUpTimeModel);
        when(mNcrWrapperApi.putOrder(any(), any(), any())).then(invocation -> {
            NcrOrderWrappedData ncrOrderWrappedData = (NcrOrderWrappedData) invocation.getArguments()[2];
            Assert.assertNull(ncrOrderWrappedData.getScheduleIn());
            Assert.assertNull(ncrOrderWrappedData.getPickup().getPickupTime());
            mWaitForArgumentCaptor.release();
            return mPostOrderCall;
        });
        assurePlaceOrder();
    }

    @Test
    public void testOrderDriveThruNotSendingCarInformation() throws InterruptedException {
        PickupData pickupData = new PickupData();
        pickupData.setYextPickupType(YextPickupType.DriveThru);
        mOrder.setPickupData(pickupData);
        when(mNcrWrapperApi.putOrder(any(), any(), any())).then(invocation -> {
            NcrOrderWrappedData ncrOrderWrappedData = (NcrOrderWrappedData) invocation.getArguments()[2];
            Assert.assertEquals(ncrOrderWrappedData.getPickup().getType(), YextPickupType.DriveThru.getServerName());
            Assert.assertNull(ncrOrderWrappedData.getPickup().getCarColor());
            Assert.assertNull(ncrOrderWrappedData.getPickup().getCarType());
            mWaitForArgumentCaptor.release();
            return mPostOrderCall;
        });
        assurePlaceOrder();
    }

    @Test
    public void testOrderCurbSide() throws InterruptedException {
        PickupData pickupData = new PickupData();
        pickupData.setYextPickupType(YextPickupType.Curbside);
        pickupData.setCurbsidePickupData(new CurbsidePickupData("Chevy", "Orange", "Sedan"));
        mOrder.setPickupData(pickupData);
        when(mNcrWrapperApi.putOrder(any(), any(), any())).then(invocation -> {
            NcrOrderWrappedData ncrOrderWrappedData = (NcrOrderWrappedData) invocation.getArguments()[2];
            Assert.assertEquals(ncrOrderWrappedData.getPickup().getType(), YextPickupType.Curbside.getServerName());
            Assert.assertEquals("Orange", ncrOrderWrappedData.getPickup().getCarColor());
            Assert.assertEquals("Sedan", ncrOrderWrappedData.getPickup().getCarType());
            Assert.assertEquals("Chevy", ncrOrderWrappedData.getPickup().getCarMake());
            mWaitForArgumentCaptor.release();
            return mPostOrderCall;
        });
        assurePlaceOrder();
    }

    @Test
    public void testPlaceOrderWithTimeOut() throws IOException {
        Response<NcrOrderWrappedData> response = Response.success(GsonUtil.readObjectFromClasspath("/test_order_check_status_inprogress_ncr.json", NcrOrderWrappedData.class));
        response.body().setNcrOrderStatus(NcrOrderStatus.InProgress);
        MockitoUtil.mockAsynchronousRetrofitRequest(mPostOrderCheckStatusCall, response);
        assertPlaceOrder(8, false);
    }

    @Test
    public void testPlaceOrderWithReceivedForFullfimentStatus() throws IOException {
        Response<NcrOrderWrappedData> response = Response.success(GsonUtil.readObjectFromClasspath("/test_order_check_status_inprogress_ncr.json", NcrOrderWrappedData.class));
        response.body().setNcrOrderStatus(NcrOrderStatus.ReceivedForFulfillment);
        MockitoUtil.mockAsynchronousRetrofitRequest(mPostOrderCheckStatusCall, response);
        assertPlaceOrder(1, true);
    }

    @Test
    public void testPlaceOrderWithInFulfillmentStatus() throws IOException {
        Response<NcrOrderWrappedData> response = Response.success(GsonUtil.readObjectFromClasspath("/test_order_check_status_inprogress_ncr.json", NcrOrderWrappedData.class));
        response.body().setNcrOrderStatus(NcrOrderStatus.InFulfillment);
        MockitoUtil.mockAsynchronousRetrofitRequest(mPostOrderCheckStatusCall, response);
        assertPlaceOrder(1, true);
    }

    @Test
    public void testPlaceOrderWithReadyForPickupStatus() throws IOException {
        Response<NcrOrderWrappedData> response = Response.success(GsonUtil.readObjectFromClasspath("/test_order_check_status_inprogress_ncr.json", NcrOrderWrappedData.class));
        response.body().setNcrOrderStatus(NcrOrderStatus.ReadyForPickup);
        MockitoUtil.mockAsynchronousRetrofitRequest(mPostOrderCheckStatusCall, response);
        assertPlaceOrder(1, true);
    }

    @Test
    public void testPlaceOrderWithFinishedStatus() throws IOException {
        Response<NcrOrderWrappedData> response = Response.success(GsonUtil.readObjectFromClasspath("/test_order_check_status_inprogress_ncr.json", NcrOrderWrappedData.class));
        response.body().setNcrOrderStatus(NcrOrderStatus.Finished);
        MockitoUtil.mockAsynchronousRetrofitRequest(mPostOrderCheckStatusCall, response);
        assertPlaceOrder(1, true);
    }

    @Test
    public void testOrderPickupTimeAsapIsValid() {
        mOrderCheckoutPresenter.placeOrder();
        verify(mOrderCheckoutView, never()).showNotValidSelectedPickupTime();
    }

    @Test
    public void testOrderPickupTimeNotValid() {
        when(mPickUpTimeModel.isAsap()).thenReturn(false);
        when(mPickUpTimeModel.getPickUpTime()).thenReturn(new LocalTime(10, 15));
        mOrderCheckoutPresenter.placeOrder();
        verify(mOrderCheckoutView, times(1)).showNotValidSelectedPickupTime();
    }

    private void assertPlaceOrder(int timesGetOrderWasCalled, boolean checkTracerCalled) {
        try {
            MockitoUtil.mockEnqueueWithObject(mPostOrderCall, mNcrOrderWrappedData);
            mOrderCheckoutPresenter.placeOrder();
            mWaitForBounce.acquire();
            verify(mOrderCheckoutView, times(1)).goToConfirmation(any());
            verify(mUserServices, times(1)).setWallet(any());
            verify(mNcrWrapperApi, times(timesGetOrderWasCalled)).getOrder(anyString());
            verify(mTracerFactory).createTracer(AppConstants.CUSTOM_METRIC_ID_NCR_ORDER_PLACE);
            verify(mTracer).start();
            if (checkTracerCalled) {
                verify(mTracer).end();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private void assurePlaceOrder() throws InterruptedException {
        mWaitForArgumentCaptor = new Semaphore(0);
        assertPlaceOrder(1, true);
        assertTrue(mWaitForArgumentCaptor.tryAcquire(10, TimeUnit.SECONDS));
    }

    private void setMockToCheckoutPresenter() {
        mOrderCheckoutPresenter.setClock(mCustomClock);
        mOrderCheckoutPresenter.setEventLogger(mEventLogger);
        mOrderCheckoutPresenter.setSVmsAPI(mSVmsAPI);
        mOrderCheckoutPresenter.setUserServices(mUserServices);
        mOrderCheckoutPresenter.setTagger(mTagger);
        mOrderCheckoutPresenter.setErrorMessageMapper(mErrorMessageMapper);
        mOrderCheckoutPresenter.setOrderService(mOrderService);
        mOrderCheckoutPresenter.setSettingService(mSettingsServices);
    }

    private void setThreadLogicForNcr() {
        mWaitForBounce = new Semaphore(0);
        doAnswer(invocation -> {
            mWaitForBounce.release();
            return null;
        }).when(mOrderCheckoutView).goToConfirmation(any());
        doAnswer(invocation -> {
            mWaitForBounce.release();
            return null;
        }).when(mOrderCheckoutView).showFailToPlaceOrderDialog();
    }

    private void initOrder() {
        mOrder = new NcrOrder(mStoreLocation);
        mNcrOrderWrappedData = (GsonUtil.defaultGson().fromJson(
                new InputStreamReader(getClass()
                        .getResourceAsStream("/test_order_place_ncr.json")),
                NcrOrderWrappedData.class));
        mOrder.setChosenPickUpTime(mPickUpTimeModel);
        mOrder.setNcrOrderId("10");
    }
}

