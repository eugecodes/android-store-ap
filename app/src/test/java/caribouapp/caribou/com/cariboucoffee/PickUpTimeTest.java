package caribouapp.caribou.com.cariboucoffee;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static caribouapp.caribou.com.cariboucoffee.util.DefaultStoreHelper.createSchedule;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextDate;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.OrderCheckoutContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter.OrderCheckoutPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;

/**
 * Created by jmsmuy on 16/07/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class PickUpTimeTest {

    private OrderCheckoutPresenter mPresenter;
    private StoreLocation mStoreLocation;

    @Mock
    private OrderService mOrderService;

    @Mock
    private Clock mClock;

    @Mock
    private SettingsServices mSettingsServices;

    private Order mSavedOrder;

    @Before
    public void setup() {

        mStoreLocation = new StoreLocation();

        Integer[] data = new Integer[]{
                /* dayOfWeek, openHour,openMinutes,closesHour,closesMinutes */
                1, 8, 0, 21, 0,
                2, 8, 0, 21, 0,
                3, 8, 0, 18, 30,
                4, 8, 0, 21, 0,
                5, 8, 0, 23, 59,
                6, 6, 0, 15, 0,
                7, -1, -1, -1, -1
        };

        mStoreLocation.setOpenHourSchedule(createSchedule(data));
        mSavedOrder = new NcrOrder(mStoreLocation);
        mSavedOrder = spy(mSavedOrder);

        YextDate tuesdayHoliday = new YextDate();
        tuesdayHoliday.setDate(new LocalDate(2018, 7, 17));
        YextTimePeriod timePeriod = new YextTimePeriod();
        timePeriod.setStartTime(new LocalTime(8, 0));
        timePeriod.setFinishTime(new LocalTime(14, 0));
        tuesdayHoliday.setTimePeriod(new ArrayList<>());
        tuesdayHoliday.getTimePeriod().add(timePeriod);
        mStoreLocation.setHolidayHours(new ArrayList<>());
        mStoreLocation.getHolidayHours().add(tuesdayHoliday);
        when(mSettingsServices.getBulkPrepTimeInMins()).thenReturn(20);
        when(mSavedOrder.isBulkOrder()).thenReturn(false);
        mSavedOrder.getStoreLocation().setDeliveryMinimum(new BigDecimal(0));
        mSavedOrder.setTotal(new BigDecimal(10));
        when(mSettingsServices.isBulkOrderingEnabled()).thenReturn(true);
        when(mSettingsServices.getOrderMinutesBeforeClosing()).thenReturn(10);

        mOrderService = mock(OrderService.class);

        doAnswer(invocation -> {
            ResultCallback<Order> resultCallback = (ResultCallback<Order>) invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(mSavedOrder);
            return null;
        }).when(mOrderService).loadCurrentOrder(any());

    }

    @Test
    public void testPickUpTimeMaxOptions() {
        DateTime startingDate = new DateTime(2018, 7, 16, 10, 40);
        DateTime endDate = new DateTime(2018, 7, 16, 20, 50);
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, new PickUpTimeModel(true));
        testPickupTimes(2018, 7, 16, 10, 40, expectedPickUpTimes);
    }

    @Test
    public void testPickUpTimeNearMidNight() {
        DateTime startingDate = new DateTime(2018, 7, 1, 10, 40);
        DateTime endDate = new DateTime(2018, 7, 1, 23, 40);
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, new PickUpTimeModel(true));
        testPickupTimes(2018, 7, 6, 10, 40, expectedPickUpTimes);
    }

    @Test
    public void testBulkOrderDoNotContainAsapOption() {
        DateTime startingDate = new DateTime(2018, 7, 16, 11, 0);
        DateTime endDate = new DateTime(2018, 7, 16, 20, 30);
        PickUpTimeModel bulkOrderPickUpTime = new PickUpTimeModel(new LocalTime(startingDate).minusMinutes(10));
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, bulkOrderPickUpTime);
        when(mSavedOrder.isBulkOrder()).thenReturn(true);
        testPickupTimes(2018, 7, 16, 10, 22, expectedPickUpTimes);
        testPickupTimes(2018, 7, 16, 10, 29, expectedPickUpTimes);
        testPickupTimes(2018, 7, 16, 10, 21, expectedPickUpTimes);
    }

    @Test
    public void testPickUpTimeNearClosingHour() {
        DateTime startingDate = new DateTime(2018, 7, 16, 20, 30);
        DateTime endDate = new DateTime(2018, 7, 16, 20, 50);
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, new PickUpTimeModel(true));
        testPickupTimes(2018, 7, 16, 20, 25, expectedPickUpTimes);
        testPickupTimes(2018, 7, 16, 20, 27, expectedPickUpTimes);
    }

    @Test
    public void testBulkOrderNearClosingHour() {
        when(mSavedOrder.isBulkOrder()).thenReturn(true);
        DateTime startingDate = new DateTime(2018, 7, 16, 19, 40);
        DateTime endDate = new DateTime(2018, 7, 16, 20, 30);
        PickUpTimeModel bulkOrderPickUpTime = new PickUpTimeModel(new LocalTime(startingDate).minusMinutes(10));
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, bulkOrderPickUpTime);
        testPickupTimes(2018, 7, 16, 19, 5, expectedPickUpTimes);
        testPickupTimes(2018, 7, 16, 19, 2, expectedPickUpTimes);
    }

    @Test
    public void testClosedStore() {
        List<PickUpTimeModel> expectedPickUpTimes = new ArrayList<>();
        testPickupTimes(2018, 7, 15, 20, 16, expectedPickUpTimes);
    }

    @Test
    public void test4() {
        DateTime startingDate = new DateTime(2018, 7, 18, 16, 30);
        DateTime endDate = new DateTime(2018, 7, 18, 18, 20);
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, new PickUpTimeModel(true));
        testPickupTimes(2018, 7, 18, 16, 30, expectedPickUpTimes);
    }

    @Test
    public void testHolidayHours() {
        DateTime startingDate = new DateTime(2018, 7, 17, 13, 20);
        DateTime endDate = new DateTime(2018, 7, 17, 13, 50);
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, new PickUpTimeModel(true));
        testPickupTimes(2018, 7, 17, 13, 16, expectedPickUpTimes);
    }

    @Test
    public void testHolidayHoursBulkOrder() {
        when(mSavedOrder.isBulkOrder()).thenReturn(true);
        DateTime startingDate = new DateTime(2018, 7, 17, 12, 40);
        DateTime endDate = new DateTime(2018, 7, 17, 13, 30);
        PickUpTimeModel bulkOrderPickUpTime = new PickUpTimeModel(new LocalTime(startingDate).minusMinutes(10));
        List<PickUpTimeModel> expectedPickUpTimes = generateHoursRange(startingDate, endDate, bulkOrderPickUpTime);

        testPickupTimes(2018, 7, 17, 12, 6, expectedPickUpTimes);
        testPickupTimes(2018, 7, 17, 12, 3, expectedPickUpTimes);
        testPickupTimes(2018, 7, 17, 12, 7, expectedPickUpTimes);
    }


    private void testPickupTimes(int year, int month, int day, int hour, int minute, List<PickUpTimeModel> expectedPickUpTimes) {
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(year, month, day, hour, minute, 0, 0));
        when(mClock.getCurrentTime()).thenReturn(new LocalTime(hour, minute));

        mOrderService.loadCurrentOrder(new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                CheckoutModel model = new CheckoutModel();
                model.setOrder(data);
                mPresenter = new OrderCheckoutPresenter(new TestView() {
                    @Override
                    public void showPickUpTimesDialog(List<PickUpTimeModel> pickUpTimes) {
                        if (pickUpTimes.isEmpty()) {
                            return;
                        }
                        assertEquals(pickUpTimes, expectedPickUpTimes);
                        assertEquals(expectedPickUpTimes.size(), pickUpTimes.size());
                    }
                }, model);
                mPresenter.setClock(mClock);
                mPresenter.setOrderService(mOrderService);
                mPresenter.setSettingsServices(mSettingsServices);
                mPresenter.editPickupTime();
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

    private List<PickUpTimeModel> generateHoursRange(DateTime startingTime, DateTime finishTime, PickUpTimeModel bulkOrderTime) {
        List<PickUpTimeModel> timeMatrix = new ArrayList<>();
        timeMatrix.add(bulkOrderTime);
        while (startingTime.compareTo(finishTime) <= 0) {
            timeMatrix.add(new PickUpTimeModel(new LocalTime(startingTime)));
            startingTime = startingTime.plusMinutes(10);
        }
        return timeMatrix;
    }

    private class TestView implements OrderCheckoutContract.View {

        @Override
        public void showPickUpTimesDialog(List<PickUpTimeModel> pickUpTimes) {

        }

        @Override
        public void viewOnMap() {

        }

        @Override
        public void showChooseANewPickUpTimeDialog() {

        }

        @Override
        public void hideRewardErrorBanner() {

        }

        @Override
        public void setShowPickupTimeField(boolean show) {

        }

        @Override
        public void setShowPickupLocationField(boolean show) {

        }

        @Override
        public void showAsapNotAvailable() {

        }

        @Override
        public void showStoreNearClosingForBulk() {

        }

        @Override
        public void showPickupTypeScreen() {

        }


        @Override
        public void setDeliveryMessage(String pickupDeliveryPrepMessage) {

        }

        @Override
        public void setPickupCurbsideTipMessage(String pickupCurbsideMessage) {

        }

        @Override
        public void showDeliveryMinimumNotMetDialog(BigDecimal deliveryMinimum) {

        }

        @Override
        public void showDeliveryClosedDialog() {

        }

        @Override
        public void showNotValidSelectedPickupTime() {

        }

        @Override
        public void showCustomTipDialog(TippingOption selectedTipOption, BigDecimal orderTotal) {

        }

        @Override
        public void showTipping() {

        }

        @Override
        public void setupTippingOptions(List<TippingOption> tippingOptions) {

        }

        @Override
        public void showChosenTip(TippingOption tippingOption, BigDecimal tip) {

        }

        @Override
        public void showMessage(int messageResId) {

        }

        @Override
        public void showMessage(String message) {

        }

        @Override
        public void showWarning(int messageResId) {

        }

        @Override
        public void showWarning(int messageResId, Object... args) {

        }

        @Override
        public void showWarning(String message) {

        }

        @Override
        public void showError(Throwable throwable) {

        }

        @Override
        public void showLoadingLayer() {

        }

        @Override
        public void showDebugDialog(String title, String message) {

        }

        @Override
        public void showLoadingLayer(boolean showProcessingText) {

        }

        @Override
        public void hideLoadingLayer() {

        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void runOnUiThread(Runnable runnable) {
            runnable.run();
        }

        @Override
        public void goToDashboard() {

        }

        @Override
        public void anchorViewToBannerOrTop() {

        }

        @Override
        public void anchorViewToReward() {

        }

        @Override
        public void updateMoneyBalance(boolean enoughBalanceMoney, BigDecimal balanceMoney) {

        }

        @Override
        public void showCloseDialog() {

        }

        @Override
        public void goToAddFunds(BigDecimal amountNeededToAdd) {

        }

        @Override
        public void displayOrderData(Order orderData) {

        }

        @Override
        public void goToConfirmation(Order orderData) {

        }

        @Override
        public void showFailToPlaceOrderDialog() {

        }

        @Override
        public void showNationalOutageDialog(String title, String message) {

        }

        @Override
        public void showStoreClosedDialog() {

        }
    }
}
