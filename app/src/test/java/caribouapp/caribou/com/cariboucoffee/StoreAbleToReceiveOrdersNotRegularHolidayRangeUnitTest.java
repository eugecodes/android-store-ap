package caribouapp.caribou.com.cariboucoffee;

import org.junit.Before;
import org.junit.Test;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreAbleToReceiveOrdersNotRegularHolidayRangeUnitTest {

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
    //                                           <---------------<------>
    @Test
    public void testWhenNotCollidingRanges_WithHolidayAfter_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 17, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(17, 30), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenNotCollidingRange_WithHolidayAfter_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 17, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenNotCollidingRanges_WithHolidayAfter_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 17, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(20, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenNotCollidingRanges_WithHolidayAfter_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 17, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(18, 50), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                                       <---------------<------>
    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayAfter_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 15, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(17, 30), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayAfter_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 15, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayAfter_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 15, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(20, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayAfter_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 15, 0, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(18, 50), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                                    <---------------<------>
    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayAfter_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 50, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayAfter_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 50, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(18, 50), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayAfter_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 50, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(21, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayAfter_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 50, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 40), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                                <---------------<------>
    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_WithHolidayAfter_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 45, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_WithHolidayAfter_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 45, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 40), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_WithHolidayAfter_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 45, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(21, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_WithHolidayAfter_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 14, 45, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(18,50), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                          <---------------<------>
    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayAfter_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 13, 00, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayAfter_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 13, 00, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(10, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayAfter_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 13, 00, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(21, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayAfter_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 13, 00, 19, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(18, 50), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                       <---------------<------>
    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayAfter() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 12, 00, 15, 15);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayAfter_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 12, 0, 15, 15);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(10, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayAfter_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 12, 0, 15, 15);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 20), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayAfter_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 12, 0, 15, 15);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 5), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                    <---------------<------>
    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayAfter_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 10, 0, 15, 5);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 47), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayAfter_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 10, 0, 15, 5);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(9, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayAfter_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 10, 0, 15, 5);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 10), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayAfter_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 10, 0, 15, 5);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 0), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                <---------------<------>
    // Holiday hour
    //                <---------------<------>
    @Test
    public void testWhenEqualPeriods_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 40), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenEqualPeriods_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenEqualPeriods_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 5), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenEqualPeriods_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 8, 0, 15, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                    <---------------<------>
    // Holiday hour
    //                <---------------<------>
    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayBefore_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 5);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(9, 0), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayBefore_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 5);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayBefore_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 5);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 5), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnTotallyMixedPeriods_WithHolidayBefore_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 5);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 55), mStoreLocation, 15)).isFalse();
    }



    // Regular hour
    //                       <---------------<------>
    // Holiday hour
    //                <---------------<------>
    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodTopEdge_RegularTopEdge_WithHolidayBefore_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 15);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(9, 0), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodTopEdge_RegularTopEdge_WithHolidayBefore_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 15);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodTopEdge_RegularTopEdge_WithHolidayBefore_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 15);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 5), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodTopEdge_RegularTopEdge_WithHolidayBefore_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 10, 0, 15, 15);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isFalse();
    }


    // Regular hour
    //                            <---------------<------>
    // Holiday hour
    //                <---------------<------>
    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayBefore_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 12, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(10,0), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayBefore_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 12, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayBefore_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 12, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 20), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnMixedPeriods_WithHolidayBefore_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 12, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14,50), mStoreLocation, 15)).isFalse();
    }

    // Regular hour
    //                                <---------------<------>
    // Holiday hour
    //                <---------------<------>
    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayBefore_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 45, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(12, 0), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayBefore_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 45, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayBefore_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 45, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 10), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriodBottomEdge_RegularTopEdge_WithHolidayBefore_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 45, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isFalse();
    }

    // Regular hour
    //                                   <---------------<------>
    // Holiday hour
    //                <---------------<------>
    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayBefore_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 40, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(12, 0), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayBefore_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 40, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayBefore_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 40, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 5), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnUnavailablePeriod_WithHolidayBefore_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation, 14, 40, 19, 0);
        addHolidayOnFixedDate(mStoreLocation, 8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isFalse();
    }


    // Regular hour
    //                                      <---------------<------>
    // Holiday hour
    //               <---------------<------>
    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayBefore_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,15, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(12, 0), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayBefore_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,15, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayBefore_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,15, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(15, 5), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenCollidingRangesOnEdge_WithHolidayBefore_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,15, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isFalse();
    }


    // Regular hour
    //                                          <---------------<------>
    // Holiday hour
    //               <---------------<------>
    @Test
    public void testWhenNotCollidingRanges_WithHolidayBefore_OnHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,17, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(12, 0), mStoreLocation, 15)).isTrue();
    }

    @Test
    public void testWhenNotCollidingRange_WithHolidayBefore_BeforeHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,17, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(7, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenNotCollidingRanges_WithHolidayBefore_AfterHolidayHour() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,17, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(18, 0), mStoreLocation, 15)).isFalse();
    }

    @Test
    public void testWhenNotCollidingRanges_WithHolidayBefore_InUnavailablePeriod() {
        setRegularScheduleEntryForFixedDate(mStoreLocation,17, 0, 19, 0);
        addHolidayOnFixedDate(mStoreLocation,8, 0, 15, 0);

        assertThat(StoreHoursCheckUtil.isStoreAbleToReceiveOrder(createFixedDateClock(14, 50), mStoreLocation, 15)).isFalse();
    }

    private CustomClock createFixedDateClock(int hour, int minutes) {
        return mStoreHoursTestHelper.createFixedDateClock(hour, minutes);
    }

    private void setRegularScheduleEntryForFixedDate(StoreLocation storeLocation, int startHour, int startMinute, int endHour, int endMinute) {
        mStoreHoursTestHelper.setRegularScheduleEntryForFixedDate(storeLocation, startHour, startMinute, endHour, endMinute);
    }

    private void addHolidayOnFixedDate(StoreLocation storeLocation, int startHour, int startMinute, int endHour, int endMinute) {
        mStoreHoursTestHelper.addHolidayOnFixedDate(storeLocation, startHour, startMinute, endHour, endMinute);
    }
}


