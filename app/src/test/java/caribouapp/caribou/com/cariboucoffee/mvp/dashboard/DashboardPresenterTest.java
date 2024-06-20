package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.common.CurbsideStatusEnum;
import caribouapp.caribou.com.cariboucoffee.common.MainLooperResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsideOrderData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;

@RunWith(MockitoJUnitRunner.class)
public class DashboardPresenterTest {

    private DashboardPresenter subject;
    @Mock
    private DashboardContract.View mView;
    @Mock
    private DashboardModel dashboardModel;
    @Mock
    private EventLogger mEventLogger;
    @Mock
    private OrderService mOrderService;
    @Mock
    private SettingsServices mSettingsServices;
    @Mock
    private UserServices mUserServices;
    @Mock
    private AppDataStorage mAppDataStorage;
    @Mock
    private Tagger mTagger;
    @Mock
    private PushManager mPushManager;
    @Mock
    private CurbsideOrderData mCurbsideOrderData;

    @Before
    public void setUp() {
        subject = new DashboardPresenter(mView, dashboardModel);
        subject.setRandom(new Random());
        subject.setUserServices(mUserServices);
        subject.setAppDataStorage(mAppDataStorage);
        subject.setEventLogger(mEventLogger);
        subject.setOrderService(mOrderService);
        subject.setTagger(mTagger);
        subject.setPushManager(mPushManager);
        subject.setSettings(mSettingsServices);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0, Runnable.class);
            runnable.run();
            return null;
        }).when(mView).runOnUiThread(any());
    }

    @After
    public void tearDown() {
        subject = null;
    }


    @Test
    public void test_givenOnResume_whenFirebaseIsOff_thenImHereSignalNotShown() {
        when(mCurbsideOrderData.isAsap()).thenReturn(true);
        when(mCurbsideOrderData.getOrderId()).thenReturn("12525281824483242937");
        when(mCurbsideOrderData.getStatus()).thenReturn(NcrOrderStatus.InFulfillment);
        when(mUserServices.getCurbsideOrderData()).thenReturn(mCurbsideOrderData);

        when(mSettingsServices.isOrderAhead()).thenReturn(true);
        when(mSettingsServices.isImHereCurbSideFeatureEnabled()).thenReturn(false);

        doAnswer(invocation -> {
            MainLooperResultCallback resultCallback = invocation.getArgument(1, MainLooperResultCallback.class);
            resultCallback.onMainSuccess(null);
            return null;
        }).when(mOrderService).waitForCurbsideFinished(any(), any());

        subject.onResume();

        verify(mOrderService, times(1)).waitForCurbsideFinished(any(), any());
        verify(dashboardModel, never()).setCurbsideIamHereState(any());
        verify(dashboardModel, never()).setCurbsidePickupTime(any());
        verify(dashboardModel, never()).setCurbsideIamHereMessage(any());
    }

    @Test
    public void test_givenOnResume_whenFirebaseIsOn_thenImHereSignalShown() {
        when(mCurbsideOrderData.isAsap()).thenReturn(true);
        when(mCurbsideOrderData.getOrderId()).thenReturn("12525281824483242937");
        when(mCurbsideOrderData.getStatus()).thenReturn(NcrOrderStatus.InFulfillment);
        when(mUserServices.getCurbsideOrderData()).thenReturn(mCurbsideOrderData);

        when(mOrderService.shouldDisplayCurbsideIamHere()).thenReturn(true);
        when(mSettingsServices.isOrderAhead()).thenReturn(true);
        when(mSettingsServices.isImHereCurbSideFeatureEnabled()).thenReturn(true);

        doAnswer(invocation -> {
            MainLooperResultCallback resultCallback = invocation.getArgument(1, MainLooperResultCallback.class);
            resultCallback.onMainSuccess(null);
            return null;
        }).when(mOrderService).waitForCurbsideFinished(any(), any());

        subject.onResume();

        verify(mOrderService, times(1)).waitForCurbsideFinished(any(), any());

        verify(dashboardModel, atLeastOnce()).setCurbsideIamHereState(CurbsideStatusEnum.IM_HERE);
        verify(dashboardModel, atLeastOnce()).setCurbsidePickupTime(any());
        verify(dashboardModel, atLeastOnce()).setCurbsideIamHereMessage(any());
    }

    @Test
    public void test_givenOnResume_whenNationalShortageWasAlreadyShown_thenNationalShortageIsNotShown() {
        when(mUserServices.isUserLoggedIn()).thenReturn(true);
        when(mSettingsServices.isNationalOutageDialogEnabled()).thenReturn(true);
        when(mUserServices.getNationalShortageDialogCounter()).thenReturn(1);
        when(mSettingsServices.getNationalOutageDialogMaxAttempts()).thenReturn(1);

        subject.onResume();

        verify(mView, never()).showNationalOutageDialog(eq("Title"), eq("Message"));
        verify(mUserServices, never()).incrementNationalShortageDialogCounter();
    }

    @Test
    public void test_givenOnResume_whenNationalShortageIsMotEnabled_thenNationalShortageIsNotShown() {
        when(mUserServices.isUserLoggedIn()).thenReturn(true);
        when(mSettingsServices.isNationalOutageDialogEnabled()).thenReturn(false);

        subject.onResume();

        verify(mView, never()).showNationalOutageDialog(eq("Title"), eq("Message"));
        verify(mUserServices, never()).incrementNationalShortageDialogCounter();
    }

    @Test
    public void test_givenOnResume_whenUserNotLogged_thenNationalShortageNotIsShown() {
        when(mUserServices.isUserLoggedIn()).thenReturn(false);

        subject.onResume();

        verify(mView, never()).showNationalOutageDialog(eq("Title"), eq("Message"));
        verify(mUserServices, never()).incrementNationalShortageDialogCounter();
    }

    @Test
    public void test_givenOnResume_whenNationalShortageIsEnabled_thenNationalShortageIsShown() {
        when(mUserServices.isUserLoggedIn()).thenReturn(true);
        when(mSettingsServices.isNationalOutageDialogEnabled()).thenReturn(true);
        when(mUserServices.getNationalShortageDialogCounter()).thenReturn(0);
        when(mSettingsServices.getNationalOutageDialogMaxAttempts()).thenReturn(1);

        when(mSettingsServices.getNationalOutageDialogTitle()).thenReturn("Title");
        when(mSettingsServices.getNationalOutageDialogMessage()).thenReturn("Message");

        subject.onResume();

        verify(mView).showNationalOutageDialog(eq("Title"), eq("Message"));
        verify(mUserServices).incrementNationalShortageDialogCounter();
    }

}