package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.common.CurbsideStatusEnum;
import caribouapp.caribou.com.cariboucoffee.common.MainLooperResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.OrderConfirmationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.ConfirmationModel;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationPresenterTest {

    private OrderConfirmationPresenter subject;
    @Mock
    private OrderConfirmationContract.View mView;
    @Mock
    private ConfirmationModel confirmationModel;
    @Mock
    private NcrOrder mNcrOrder;
    @Mock
    private PickUpTimeModel mPickUpTimeModel;
    @Mock
    private EventLogger mEventLogger;
    @Mock
    private OrderService mOrderService;
    @Mock
    private SettingsServices mSettingsServices;
    @Mock
    private UserServices mUserServices;

    @Before
    public void setUp() {
        when(confirmationModel.getOrder()).thenReturn(mNcrOrder);
        when(mNcrOrder.getChosenPickUpTime()).thenReturn(mPickUpTimeModel);

        subject = new OrderConfirmationPresenter(mView, confirmationModel);
        subject.setEventLogger(mEventLogger);
        subject.setOrderService(mOrderService);
        subject.setSettingsServices(mSettingsServices);
        subject.setUserServices(mUserServices);

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
    public void test_givenInit_whenFirebaseIsOff_thenImHereSignalNotShown() {
        when(mNcrOrder.isDelivery()).thenReturn(false);
        when(mNcrOrder.isCurbside()).thenReturn(true);

        when(mPickUpTimeModel.isAsap()).thenReturn(true);
        when(mSettingsServices.isOrderAhead()).thenReturn(true);
        when(mSettingsServices.getPickupASAPMessage()).thenReturn("ASAP Message");

        when(mSettingsServices.isImHereCurbSideFeatureEnabled()).thenReturn(false);

        subject.init();

        verify(mOrderService, never()).waitForCurbsideFinished(any(), any());
        verify(confirmationModel, never()).setCurbsideIamHereState(CurbsideStatusEnum.IM_HERE);
    }

    @Test
    public void test_givenInit_whenFirebaseIsOn_thenImHereSignalShown() {
        when(confirmationModel.getCurbsideIamHereState()).thenReturn(CurbsideStatusEnum.NONE);
        when(mNcrOrder.isDelivery()).thenReturn(false);
        when(mNcrOrder.isCurbside()).thenReturn(true);
        when(mNcrOrder.getStatus()).thenReturn(NcrOrderStatus.ReceivedForFulfillment);

        when(mPickUpTimeModel.isAsap()).thenReturn(true);
        when(mSettingsServices.isOrderAhead()).thenReturn(true);
        when(mSettingsServices.getPickupASAPMessage()).thenReturn("ASAP Message");

        when(mUserServices.getCurbSideImHereTeachingCounter()).thenReturn(1);
        when(mSettingsServices.getCurbSideImHereTeachingMessageMaxAttempts()).thenReturn(1);
        when(mOrderService.shouldDisplayCurbsideIamHere()).thenReturn(true);

        when(mSettingsServices.isImHereCurbSideFeatureEnabled()).thenReturn(true);

        doAnswer(invocation -> {
            MainLooperResultCallback resultCallback = invocation.getArgument(1, MainLooperResultCallback.class);
            resultCallback.onMainSuccess(null);
            return null;
        }).when(mOrderService).waitForCurbsideFinished(any(), any());

        subject.init();

        verify(mOrderService, atLeastOnce()).waitForCurbsideFinished(any(), any());
        verify(confirmationModel, atLeastOnce()).setCurbsideIamHereState(CurbsideStatusEnum.IM_HERE);
    }

}