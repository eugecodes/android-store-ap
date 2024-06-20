package caribouapp.caribou.com.cariboucoffee;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextGetLocationResult;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursTestHelper;

import static caribouapp.caribou.com.cariboucoffee.util.DefaultStoreHelper.createSchedule;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class StoreOpenStatusUnitTest {

    private StoreLocation mStoreLocation;

    @Before
    public void setup() {
        mStoreLocation = new StoreLocation();

        Integer[] storeOpenHours = new Integer[]{
                /* dayOfWeek, openHour,openMinutes,closesHour,closesMinutes */
                1, 8, 0, 21, 0, // Monday      20171009
                2, 8, 0, 21, 0, // Tuesday     20171010
                3, 8, 0, 21, 0, // Wednesday   20171011
                4, 8, 0, 21, 0, // Thursday    20171012
                5, 8, 0, 21, 0, // Friday      20171013
                6, 6, 0, 15, 0, // Saturday    20171014
                7, -1, -1, -1, -1 // Sunday    20171015
        };

        mStoreLocation.setOpenHourSchedule(createSchedule(storeOpenHours));

        Integer[] deliveryOpenHours = new Integer[]{
                /* dayOfWeek, openHour,openMinutes,closesHour,closesMinutes */
                1, 9, 0, 13, 0, // Monday      20171009
                2, 9, 0, 13, 0, // Tuesday     20171010
                3, 9, 0, 13, 0, // Wednesday   20171011
                4, 9, 0, 13, 0, // Thursday    20171012
                5, 9, 0, 13, 0, // Friday      20171013
                6, 10, 0, 12, 0, // Saturday   20171014
                7, -1, -1, -1, -1 // Sunday    20171015
        };

        mStoreLocation.setDeliveryHoursSchedule(createSchedule(deliveryOpenHours));

        StoreHoursTestHelper storeHoursTestHelper = new StoreHoursTestHelper(2017, 10, 13);
        storeHoursTestHelper.addClosedHolidayOnFixedDate(mStoreLocation, 10, 0, 18, 0);

        storeHoursTestHelper = new StoreHoursTestHelper(2017, 10, 12);
        storeHoursTestHelper.addRegularHoursHolidayOnFixedDate(mStoreLocation, 10, 0, 18, 0);

        storeHoursTestHelper = new StoreHoursTestHelper(2017, 10, 11);
        storeHoursTestHelper.addHolidayOnFixedDate(mStoreLocation, 10, 0, 18, 0);
        storeHoursTestHelper.addDeliveryHolidayOnFixedDate(mStoreLocation, 9, 30, 11, 30);
    }

    @Test
    public void testStoreOpenHoursRegularHoursSaturday20171014() {
        StoreHoursTestHelper helper = new StoreHoursTestHelper(2017, 10, 14);

        CustomClock customClock = helper.createFixedDateClock(10, 0);
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(15, 0), openStatus.getNextTime());

        customClock = helper.createFixedDateClock(6, 0);
        openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(15, 0), openStatus.getNextTime());

        customClock = helper.createFixedDateClock(5, 0);
        openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertFalse(openStatus.isOpen());
        assertEquals(new LocalTime(6, 0), openStatus.getNextTime());

        customClock = helper.createFixedDateClock(16, 0);
        openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertFalse(openStatus.isOpen());
        assertNull(openStatus.getNextTime());

    }

    @Test
    public void testStoreOpenHoursHolidaysFriday20171013() {
        StoreHoursTestHelper helper = new StoreHoursTestHelper(2017, 10, 13);

        CustomClock customClock = helper.createFixedDateClock(9, 0);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(9, 50);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(10, 0);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(18, 0);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(19, 0);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));
    }


    @Test
    public void testStoreOpenHoursHolidaysTuesday20171010() {
        CustomClock customClock = new CustomClock(new DateTime(2017, 10, 10, 10, 0));
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));
    }

    @Test
    public void testStoreOpenHoursHolidaysThursday20171012() {
        StoreHoursTestHelper helper = new StoreHoursTestHelper(2017, 10, 12);

        CustomClock customClock = helper.createFixedDateClock(9, 0);
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(9, 50);
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(10, 0);
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(18, 0);
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(19, 0);
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

    }

    @Test
    public void testStoreOpenHoursHolidaysWednesday20171011() {
        StoreHoursTestHelper helper = new StoreHoursTestHelper(2017, 10, 11);

        CustomClock customClock = helper.createFixedDateClock(9, 0);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(9, 50);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(10, 0);
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(18, 0);
        assertTrue(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(18, 1);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));

        customClock = helper.createFixedDateClock(19, 0);
        assertFalse(StoreHoursCheckUtil.isStoreOpen(customClock, mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(customClock, mStoreLocation, 15));
    }

    @Test
    public void testDeliveryOpenHoursMonday20171009() {
        StoreHoursTestHelper helper = new StoreHoursTestHelper(2017, 10, 9);

        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(8, 0), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(9, 0), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(9, 40), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(12, 0), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(13, 0), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(15, 0), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(22, 0), mStoreLocation));
    }

    @Test
    public void testDeliveryOpenHoursSaturday20171014() {
        StoreHoursTestHelper helper = new StoreHoursTestHelper(2017, 10, 14);

        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(8, 0), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(9, 0), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(10, 10), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(11, 0), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(12, 0), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(15, 0), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(22, 0), mStoreLocation));
    }

    @Test
    public void testDeliveryOpenHoursHolidayWednesday20171011() {
        StoreHoursTestHelper helper = new StoreHoursTestHelper(2017, 10, 11);

        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(8, 0), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(9, 0), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(9, 40), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(11, 0), mStoreLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(11, 20), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(12, 0), mStoreLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helper.createFixedDateClock(22, 0), mStoreLocation));
    }

    @Test
    public void testDeliveryOpenHoursWithMissingDays() {
        YextGetLocationResult yextLocationResult = GsonUtil.readObjectFromClasspath("/test_yext_store_missing_open_hours_get_location.json", YextGetLocationResult.class);
        YextLocation yextLocation = yextLocationResult.getResponse();
        yextLocation.getHours().populateOpenHours();
        yextLocation.getDeliveryHours().populateOpenHours();
        StoreLocation storeLocation = new StoreLocation(yextLocation, null);
        StoreHoursTestHelper helperOnMissingDay = new StoreHoursTestHelper(2017, 10, 2);
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnMissingDay.createFixedDateClock(8, 0), storeLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnMissingDay.createFixedDateClock(9, 0), storeLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnMissingDay.createFixedDateClock(9, 40), storeLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnMissingDay.createFixedDateClock(10, 0), storeLocation));
        assertNull(StoreHoursCheckUtil.getStoreOpenStatus(helperOnMissingDay.createFixedDateClock(12, 0), storeLocation).getNextTime());
        StoreHoursTestHelper helperOnDayWithData = new StoreHoursTestHelper(2017, 10, 1);
        assertNotNull(StoreHoursCheckUtil.getStoreOpenStatus(helperOnDayWithData.createFixedDateClock(12, 0), storeLocation).getNextTime());
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnDayWithData.createFixedDateClock(8, 0), storeLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnDayWithData.createFixedDateClock(9, 0), storeLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helperOnDayWithData.createFixedDateClock(9, 40), storeLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helperOnDayWithData.createFixedDateClock(11, 0), storeLocation));
        assertTrue(StoreHoursCheckUtil.isDeliveryOpen(helperOnDayWithData.createFixedDateClock(11, 20), storeLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnDayWithData.createFixedDateClock(14, 0), storeLocation));
        assertFalse(StoreHoursCheckUtil.isDeliveryOpen(helperOnDayWithData.createFixedDateClock(22, 0), storeLocation));
    }
}
