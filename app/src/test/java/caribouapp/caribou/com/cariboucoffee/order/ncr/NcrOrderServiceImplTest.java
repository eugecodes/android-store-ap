package caribouapp.caribou.com.cariboucoffee.order.ncr;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import caribouapp.caribou.com.cariboucoffee.analytics.TracerFactory;
import caribouapp.caribou.com.cariboucoffee.api.NcrWrapperApi;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import caribouapp.caribou.com.cariboucoffee.util.TestResultCallback;
import retrofit2.Call;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class NcrOrderServiceImplTest {

    @Mock
    private Clock clock;
    @Mock
    private UserServices userServices;
    @Mock
    private NcrMenuDataServiceImpl ncrMenuDataService;
    @Mock
    private NcrWrapperApi mNcrWrapperApi;
    @Mock
    private SettingsServices settingsServices;
    @Mock
    private TracerFactory tracerFactory;
    @Mock
    private Call<NcrOrderWrappedData> mCheckOrderStatusCall;

    private static NcrOrderWrappedData mockResponse;

    private NcrOrderServiceImpl subject;

    @BeforeClass
    public static void setUpClass() {
        mockResponse = GsonUtil.readObjectFromClasspath("/test_ncr_order_ahead_check_status_null_status.json", NcrOrderWrappedData.class);
    }

    @AfterClass
    public static void tearDownClass() {
        mockResponse = null;
    }

    @Before
    public void setUp() {
        subject = new NcrOrderServiceImpl(clock, userServices, ncrMenuDataService,
                mNcrWrapperApi, settingsServices, tracerFactory);
    }

    @After
    public void tearDown() {
        subject = null;
    }

    @Test
    public void test_givenWaitForCurbsideFinished_whenStatusIsNull_thenAppDoesNotCrash() throws IOException, InterruptedException {
        Response<NcrOrderWrappedData> response = Response.success(mockResponse);
        response.body().setNcrOrderStatus(null);
        MockitoUtil.mockAsynchronousRetrofitRequest(mCheckOrderStatusCall, response);

        when(settingsServices.getCurbsideCheckFinishedAttempts()).thenReturn(5);
        when(mNcrWrapperApi.getOrder(anyString())).thenReturn(mCheckOrderStatusCall);

        NcrOrder ncrOrder = new NcrOrder(null);
        ncrOrder.setNcrOrderId("09327432765729801129");

        Semaphore waitForCheckout = new Semaphore(0);
        subject.waitForCurbsideFinished(ncrOrder, new TestResultCallback<Order>() {
            @Override
            public void onError(Throwable error) {
                assertTrue(error instanceof SocketTimeoutException);
                waitForCheckout.release();
            }
        });
        assertTrue(waitForCheckout.tryAcquire(15, TimeUnit.SECONDS));
        verify(mNcrWrapperApi, times(6)).getOrder(anyString());
    }

    @Test
    public void test_givenWaitForOrderCommandFinish_whenStatusIsNull_thenAppDoesNotCrash() throws IOException, InterruptedException {
        Response<NcrOrderWrappedData> response = Response.success(mockResponse);
        response.body().setNcrOrderStatus(null);
        MockitoUtil.mockAsynchronousRetrofitRequest(mCheckOrderStatusCall, response);

        when(settingsServices.getOrderAheadCheckStatusMaxAttempts()).thenReturn(4);
        when(mNcrWrapperApi.getOrder(anyString())).thenReturn(mCheckOrderStatusCall);

        NcrOrder ncrOrder = new NcrOrder(null);
        ncrOrder.setNcrOrderId("09327432765729801129");

        subject.setOrder(ncrOrder);

        Semaphore waitForCheckout = new Semaphore(0);
        subject.waitForOrderCommandFinish(new TestResultCallback<Order>() {
            @Override
            public void onError(Throwable error) {
                assertTrue(error instanceof SocketTimeoutException);
                waitForCheckout.release();
            }
        });
        assertTrue(waitForCheckout.tryAcquire(15, TimeUnit.SECONDS));
        verify(mNcrWrapperApi, times(4)).getOrder(anyString());
    }
}
