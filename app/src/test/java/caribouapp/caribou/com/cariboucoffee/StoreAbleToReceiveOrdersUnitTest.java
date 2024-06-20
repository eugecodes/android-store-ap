package caribouapp.caribou.com.cariboucoffee;

import org.junit.Before;
import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursTestHelper;
import static org.assertj.core.api.Assertions.assertThat;

public class StoreAbleToReceiveOrdersUnitTest {

    private static int fixedYear = 2017;
    private static int fixedMonth = 10;
    private static int fixedDay = 9;

    private StoreLocation mStoreLocation;
    private StoreHoursTestHelper mStoreHoursTestHelper;

    @Before
    public void setup() {
        mStoreLocation = new StoreLocation();
        mStoreHoursTestHelper = new StoreHoursTestHelper(fixedYear, fixedMonth, fixedDay);
    }

    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                <---------------<------>
    @Test
    public void testWhenEqualPeriods_OnHolidayClosed() {
        mStoreHoursTestHelper.setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        mStoreHoursTestHelper.addClosedHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(mStoreHoursTestHelper.createFixedDateClock(13, 00), mStoreLocation, 15)).isFalse();
    }

    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                                           <---------------<------>
    @Test
    public void testWhenNotCollidingPeriods_WithRegularHoursHoliday_OnHolidayHour() {
        mStoreHoursTestHelper.setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        mStoreHoursTestHelper.addRegularHoursHolidayOnFixedDate(mStoreLocation, 17, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(mStoreHoursTestHelper.createFixedDateClock(17, 30), mStoreLocation, 15)).isFalse();
    }

    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                                           <---------------<------>
    @Test
    public void testWhenNotCollidingPeriods_WithRegularHoursHoliday_OnRegularHour() {
        mStoreHoursTestHelper.setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        mStoreHoursTestHelper.addRegularHoursHolidayOnFixedDate(mStoreLocation, 17, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(mStoreHoursTestHelper.createFixedDateClock(14, 00), mStoreLocation, 15)).isTrue();
    }
}


