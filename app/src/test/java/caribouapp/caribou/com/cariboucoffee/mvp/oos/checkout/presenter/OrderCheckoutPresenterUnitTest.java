package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
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

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.OrderCheckoutContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;

@RunWith(MockitoJUnitRunner.class)
public class OrderCheckoutPresenterUnitTest {

    private OrderCheckoutPresenter subject;

    @Mock
    private OrderCheckoutContract.View mView;
    @Mock
    private CheckoutModel mCheckoutModel;
    @Mock
    private SVmsAPI mSVmsAPI;
    @Mock
    private Tagger mTagger;
    @Mock
    private UserServices mUserServices;
    @Mock
    private OrderService mOrderService;
    @Mock
    private AppDataStorage mAppDataStorage;
    @Mock
    private Clock mClock;
    @Mock
    private EventLogger mEventLogger;
    @Mock
    private ErrorMessageMapper mErrorMessageMapper;
    @Mock
    private SettingsServices mSettingsServices;
    @Mock
    private OrderNavHelper mOrderNavHelper;

    @Before
    public void setUp() {
        subject = new OrderCheckoutPresenter(mView, mCheckoutModel);
        subject.setClock(mClock);
        subject.setUserServices(mUserServices);
        subject.setSVmsAPI(mSVmsAPI);
        subject.setTagger(mTagger);
        subject.setUserServices(mUserServices);
        subject.setEventLogger(mEventLogger);
        subject.setOrderService(mOrderService);
        subject.setSettingService(mSettingsServices);
        subject.setAppDataStorage(mAppDataStorage);
        subject.setErrorMessageMapper(mErrorMessageMapper);
        subject.setOrderNavHelper(mOrderNavHelper);

        when(mOrderService.isPickupTimeSupported()).thenReturn(true);
        when(mSettingsServices.getPickupCurbsideTipMessage()).thenReturn("Tip Message");
        when(mSettingsServices.getPickupDeliveryPrepMessage()).thenReturn("Prep Message");
    }

    @After
    public void tearDown() {
        subject = null;
    }

    @Test
    public void test_givenInit_whenNationalShortageIsMotEnabled_thenNationalShortageIsNotShown() {
        when(mUserServices.isUserLoggedIn()).thenReturn(true);
        when(mSettingsServices.isNationalOutageDialogEnabled()).thenReturn(false);

        subject.init();

        verify(mView, never()).showNationalOutageDialog(eq("Title"), eq("Message"));
    }

    @Test
    public void test_givenInit_whenUserNotLogged_thenNationalShortageNotIsShown() {
        when(mUserServices.isUserLoggedIn()).thenReturn(false);

        subject.init();

        verify(mView, never()).showNationalOutageDialog(eq("Title"), eq("Message"));
    }

    @Test
    public void test_givenInit_whenNationalShortageIsEnabled_thenNationalShortageIsShown() {
        when(mUserServices.isUserLoggedIn()).thenReturn(true);
        when(mSettingsServices.isNationalOutageDialogEnabled()).thenReturn(true);

        when(mSettingsServices.getNationalOutageDialogTitle()).thenReturn("Title");
        when(mSettingsServices.getNationalOutageDialogMessage()).thenReturn("Message");

        subject.init();

        verify(mView).showNationalOutageDialog(eq("Title"), eq("Message"));
    }

    @Test
    public void test_givenGuestCheckoutFlags_whenDisplaying_thenOnlyShowWhenAllEnabled() {
        setGuestCheckoutFlags(false, false);
        assertFalse(subject.isContinueAsGuestCheckEnabled());

        setGuestCheckoutFlags(true, false);
        assertFalse(subject.isContinueAsGuestCheckEnabled());

        setGuestCheckoutFlags(false, true);
        assertFalse(subject.isContinueAsGuestCheckEnabled());

        setGuestCheckoutFlags(true, true);
        assertTrue(subject.isContinueAsGuestCheckEnabled());
    }

    private void setGuestCheckoutFlags(boolean isContinueAsGuestEnabled, boolean isStoreGuestCheckoutEnabled) {
        when(mSettingsServices.isContinueAsGuestCheckEnabled()).thenReturn(isContinueAsGuestEnabled);

        StoreLocation storeLocation = new StoreLocation();
        storeLocation.setGuestCheckoutEnabled(isStoreGuestCheckoutEnabled);
        when(mCheckoutModel.getOrder()).thenReturn(new NcrOrder(storeLocation));
    }
}