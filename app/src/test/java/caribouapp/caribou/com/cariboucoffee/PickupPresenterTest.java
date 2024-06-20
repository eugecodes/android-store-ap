package caribouapp.caribou.com.cariboucoffee;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static caribouapp.caribou.com.cariboucoffee.util.DefaultStoreHelper.createSchedule;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.GeolocationServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.PickupContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model.DeliveryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model.PickupModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.presenter.PickupPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;

@RunWith(MockitoJUnitRunner.class)
public class PickupPresenterTest {

    PickupContract.Presenter mPickupPresenter;

    @Mock
    UserServices mUserServices;

    @Mock
    PickupContract.View mView;

    @Mock
    GeolocationServices mGeolocationServices;

    @Mock
    StoreLocation mStoreLocation;

    @Mock
    OrderService mOrderService;

    @Mock
    private SettingsServices mSettingsServices;

    @Mock
    Clock mClock;

    private NcrOrder mOrder;

    private PickupModel mModel;

    @Before
    public void setup() {
        when(mView.isActive()).thenReturn(true);
        mockReturnSameBoolean().when(mView).addressLine1ErrorEnabled(anyBoolean());
        mockReturnSameBoolean().when(mView).zipErrorEnabled(anyBoolean());
        mockReturnSameBoolean().when(mView).deliveryPhoneNumberErrorEnabled(anyBoolean());

        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(mView).runOnUiThread(any());
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResultCallback<BigDecimal> resultCallback = (ResultCallback) args[2];
            resultCallback.onSuccess(new BigDecimal(3));
            return null;
        }).when(mGeolocationServices).distanceBetween(any(), any(), any());

        populateStoreLocation();

        mModel = new PickupModel();
        mOrder = new NcrOrder(mStoreLocation);
        mModel.setOrder(mOrder);
        mModel.setDeliveryRadius(new BigDecimal(5));

        PickupPresenter pickupPresenter = new PickupPresenter(mView, mModel);
        pickupPresenter.setUserServices(mUserServices);
        pickupPresenter.setGeolocationServices(mGeolocationServices);
        pickupPresenter.setOrderService(mOrderService);

        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2019, 04, 1, 9, 30));
        when(mClock.getCurrentTime()).thenReturn(new LocalTime(9, 30));
        pickupPresenter.setClock(mClock);

        populateSettingsServices();
        pickupPresenter.setSettingsServices(mSettingsServices);

        mPickupPresenter = pickupPresenter;

        doAnswer(invocation -> {
            ResultCallback<Order> resultCallback = (ResultCallback<Order>) invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(mOrder);
            return null;
        }).when(mOrderService).loadCurrentOrder(any());
    }

    private Stubber mockReturnSameBoolean() {
        return doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0, Boolean.class);
            }
        });
    }

    @Test
    public void testDeliveryRequiredFieldsNoData() {
        mPickupPresenter.setPickupType(YextPickupType.Delivery);
        mPickupPresenter.validateAndContinue();

        verify(mView).addressLine1ErrorEnabled(true);
        verify(mView).zipErrorEnabled(true);
        verify(mView).deliveryPhoneNumberErrorEnabled(true);

        verify(mView, never()).navigateToNextOrderScreen(anyBoolean());
    }

    @Test
    public void testDeliveryRequiredFieldsFullData() {
        populatePickUpModelForDelivery();
        mPickupPresenter.validateAndContinue();

        verify(mView).addressLine1ErrorEnabled(false);
        verify(mView).zipErrorEnabled(false);
        verify(mView).deliveryPhoneNumberErrorEnabled(false);

        verify(mView).navigateToNextOrderScreen(anyBoolean());
    }

    @Test
    public void testRequiresBounceNoWalkIn() {
        mPickupPresenter.setPickupType(YextPickupType.WalkIn);
        mPickupPresenter.validateAndContinue();

        verify(mView, never()).addressLine1ErrorEnabled(false);
        verify(mView, never()).zipErrorEnabled(false);
        verify(mView, never()).deliveryPhoneNumberErrorEnabled(false);

        verify(mView).navigateToNextOrderScreen(false);
    }

    @Test
    public void testRequiresBounceNoDeliveryToDelivery() {
        populatePickUpModelForDelivery();

        mPickupPresenter.validateAndContinue();

        verify(mView).addressLine1ErrorEnabled(false);
        verify(mView).zipErrorEnabled(false);
        verify(mView).deliveryPhoneNumberErrorEnabled(false);
        verify(mView).navigateToNextOrderScreen(true);

        // Update data but remain in Delivery mode

        mPickupPresenter.setPickupType(YextPickupType.Delivery);
        DeliveryModel deliveryModel = mModel.getDeliveryModel();
        deliveryModel.setAddressLine1("456 Main St.");
        deliveryModel.setZipCode("55000");
        deliveryModel.setContactPhoneNumber("555-374-3382");

        mPickupPresenter.validateAndContinue();

        // times (3) cause of the implicit call made by the property observer
        // on the fields after the first validation
        verify(mView, times(3)).addressLine1ErrorEnabled(false);
        verify(mView, times(3)).zipErrorEnabled(false);
        verify(mView, times(3)).deliveryPhoneNumberErrorEnabled(false);
        verify(mView).navigateToNextOrderScreen(false);
    }

    @Test
    public void testRequiresBounceYesFromDelivery() {
        populatePickUpModelForDelivery();

        mPickupPresenter.validateAndContinue();

        verify(mView).addressLine1ErrorEnabled(false);
        verify(mView).zipErrorEnabled(false);
        verify(mView).deliveryPhoneNumberErrorEnabled(false);
        verify(mView).navigateToNextOrderScreen(true);

        // Update data but remain in Delivery mode

        mPickupPresenter.setPickupType(YextPickupType.WalkIn);

        mPickupPresenter.validateAndContinue();

        // times (3) cause of the implicit call made by the property observer
        // on the fields after the first validation
        verify(mView, times(1)).addressLine1ErrorEnabled(false);
        verify(mView, times(1)).zipErrorEnabled(false);
        verify(mView, times(1)).deliveryPhoneNumberErrorEnabled(false);
        verify(mView, times(2)).navigateToNextOrderScreen(true);
    }

    @Test
    public void testRequiresBounceYesToDelivery() {
        mPickupPresenter.setPickupType(YextPickupType.WalkIn);

        mPickupPresenter.validateAndContinue();

        verify(mView, never()).addressLine1ErrorEnabled(anyBoolean());
        verify(mView, never()).zipErrorEnabled(anyBoolean());
        verify(mView, never()).deliveryPhoneNumberErrorEnabled(anyBoolean());
        verify(mView).navigateToNextOrderScreen(false);


        mPickupPresenter.setPickupType(YextPickupType.Delivery);
        populatePickUpModelForDelivery();

        mPickupPresenter.validateAndContinue();

        // times (2) cause of the implicit call made by the property observer
        // on the fields after the first validation
        verify(mView, times(2)).addressLine1ErrorEnabled(false);
        verify(mView, times(2)).zipErrorEnabled(false);
        verify(mView, times(2)).deliveryPhoneNumberErrorEnabled(false);
        verify(mView, times(1)).navigateToNextOrderScreen(true);
    }

    @Test
    public void testDeliveryOutOfStoreRange() {
        populatePickUpModelForDelivery();
        mModel.setDeliveryRadius(new BigDecimal(1));
        mPickupPresenter.validateAndContinue();

        verify(mView).addressLine1ErrorEnabled(false);
        verify(mView).zipErrorEnabled(false);
        verify(mView).deliveryPhoneNumberErrorEnabled(false);

        verify(mView).showOutOfDeliveryRangeDialog(any());
    }

    @Test
    public void testDeliveryOutOfDeliveryHoursSchedule() {
        populatePickUpModelForDelivery();
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2019, 04, 1, 9, 30));
        when(mClock.getCurrentTime()).thenReturn(new LocalTime(14, 30));

        mPickupPresenter.validateAndContinue();

        verify(mView, never()).addressLine1ErrorEnabled(anyBoolean());
        verify(mView, never()).zipErrorEnabled(anyBoolean());
        verify(mView, never()).deliveryPhoneNumberErrorEnabled(anyBoolean());
        verify(mView, never()).navigateToNextOrderScreen(anyBoolean());

        verify(mView).showContinueNotAvailableDialog();
    }

    @Test
    public void testDeliveryIsEnabledOnLoadPickupTypes() {
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2019, 04, 1, 9, 30));
        when(mClock.getCurrentTime()).thenReturn(new LocalTime(9, 30));
        mPickupPresenter.loadOrder();
        assertTrue(mModel.isDeliveryEnabled());
    }

    @Test
    public void testDeliveryIsDisabledOnLoadPickupTypes() {
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2019, 04, 1, 14, 30));
        when(mClock.getCurrentTime()).thenReturn(new LocalTime(14, 30));
        mPickupPresenter.loadOrder();
        assertFalse(mModel.isDeliveryEnabled());
    }

    private void populatePickUpModelForDelivery() {
        DeliveryModel deliveryModel = mModel.getDeliveryModel();
        deliveryModel.setAddressLine1("123 Main St.");
        deliveryModel.setZipCode("55401");
        deliveryModel.setContactPhoneNumber("763-374-3382");
        mModel.setSelectedPickupType(YextPickupType.Delivery);
    }

    private void populateStoreLocation() {

        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.Curbside);
        yextPickupTypes.add(YextPickupType.Delivery);
        yextPickupTypes.add(YextPickupType.DriveThru);
        yextPickupTypes.add(YextPickupType.WalkIn);

        when(mStoreLocation.getPickupTypes()).thenReturn(yextPickupTypes);

        Integer[] deliveryOpenHours = new Integer[]{
                /* dayOfWeek, openHour,openMinutes,closesHour,closesMinutes */
                1, 9, 0, 13, 0, // Monday
                2, 9, 0, 13, 0, // Tuesday
                3, 9, 0, 13, 0, // Wednesday
                4, 9, 0, 13, 0, // Thursday
                5, 9, 0, 13, 0, // Friday
                6, 10, 0, 12, 0, // Saturday
                7, -1, -1, -1, -1 // Sunday
        };

        when(mStoreLocation.getDeliveryHoursSchedule()).thenReturn(createSchedule(deliveryOpenHours));
    }

    private void populateSettingsServices() {
        List<String> carTypes = new ArrayList<>();
        carTypes.add("Car");
        carTypes.add("SUV");
        carTypes.add("Truck");
        carTypes.add("Van");

        List<String> carColors = new ArrayList<>();
        carColors.add("Black");
        carColors.add("White");
        carColors.add("Gray/Silver");
        carColors.add("Blue");
        carColors.add("Red");
        carColors.add("Brown/Tan");
        carColors.add("Green");
        carColors.add("Yellow");
        carColors.add("Orange");

        when(mSettingsServices.getPickupCurbsideCarColors()).thenReturn(carColors);
        when(mSettingsServices.getPickupCurbsideCarTypes()).thenReturn(carTypes);
        when(mSettingsServices.getPickupCurbsideTipMessage()).thenReturn("");
    }
}
