package caribouapp.caribou.com.cariboucoffee;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextDate;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;

import static caribouapp.caribou.com.cariboucoffee.util.DefaultStoreHelper.createSchedule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class StoreOpenStatusMidnightUnitTest {

    private StoreLocation mStoreLocation;

    @Before
    public void setup() {
        mStoreLocation = new StoreLocation();

        Integer[] data = new Integer[]{
                /* dayOfWeek, openHour,openMinutes,closesHour,closesMinutes */
                1, 5, 0, 0, 0,
                2, 5, 0, 0, 0,
                3, 5, 0, 0, 0,
                4, 5, 0, 2, 0,
                5, 0, 0, 0, 0,
                6, 0, 0, 0, 0,
                7, -1, -1, -1, -1
        };

        mStoreLocation.setOpenHourSchedule(createSchedule(data));

        List<YextDate> holidayHours = new ArrayList<>();
        YextDate holiday = new YextDate();
        holiday.setDate(new LocalDate(2018, 10, 13));
        holiday.setRegularHours(false);
        YextTimePeriod period = new YextTimePeriod();
        period.setClosed(true);
        period.setStartTime(new LocalTime(10, 0, 0));
        period.setFinishTime(new LocalTime(18, 0, 0));
        ArrayList<YextTimePeriod> yextTimePeriods = new ArrayList<>();
        yextTimePeriods.add(period);
        holiday.setTimePeriod(yextTimePeriods);
        holidayHours.add(holiday);

        holiday = new YextDate();
        holiday.setDate(new LocalDate(2018, 10, 12));
        holiday.setRegularHours(true);
        period = new YextTimePeriod();
        period.setClosed(false);
        period.setStartTime(new LocalTime(10, 0, 0));
        period.setFinishTime(new LocalTime(18, 0, 0));
        ArrayList<YextTimePeriod> yextHolidayTimePeriods = new ArrayList<>();
        yextHolidayTimePeriods.add(period);
        holiday.setTimePeriod(yextHolidayTimePeriods);
        holidayHours.add(holiday);

        holiday = new YextDate();
        holiday.setDate(new LocalDate(2018, 10, 11));
        holiday.setRegularHours(false);
        period = new YextTimePeriod();
        period.setClosed(false);
        period.setStartTime(new LocalTime(10, 0, 0));
        period.setFinishTime(new LocalTime(18, 0, 0));
        ArrayList<YextTimePeriod> yextHoliday2TimePeriods = new ArrayList<>();
        yextHoliday2TimePeriods.add(period);
        holiday.setTimePeriod(yextHoliday2TimePeriods);
        holidayHours.add(holiday);

        mStoreLocation.getHolidayHours().addAll(holidayHours);
    }

    @Test
    public void testStoreOpenMonday() {
        // Monday from 5am to 24
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 1, 10, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(0, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenMonday2() {
        // Monday from 5am to 24
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 1, 5, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(0, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenMonday3() {
        // Monday from 5am to 24
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 1, 6, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(0, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenTuesday() {
        // Tuesday from 5am to 0am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 2, 10, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(0, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenTuesday2() {
        // Tuesday from 5am to 0am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 2, 5, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(0, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenTuesday3() {
        // Tuesday from 5am to 0am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 2, 6, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(0, 0), openStatus.getNextTime());
    }


    @Test
    public void testStoreOpenThursday() {
        // Thursday from 5am to 2am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 4, 6, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(2, 0), openStatus.getNextTime());
    }


    @Test
    public void testStoreOpenThursday2() {
        // Thursday from 5am to 2am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 4, 15, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(2, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenThursday3() {
        // Thursday from 5am to 2am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 4, 0, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(2, 0), openStatus.getNextTime());

    }

    @Test
    public void testStoreOpenThursday4() {
        // Thursday from 5am to 2am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 4, 1, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertEquals(new LocalTime(2, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenFriday() {
        // Friday all day
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 5, 0, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertNull(openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenFriday2() {
        // Friday all day
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 5, 10, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertNull(openStatus.getNextTime());
    }

    @Test
    public void testStoreOpenFriday3() {
        // Friday all day
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 5, 20, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(openStatus.isOpen());
        assertNull(openStatus.getNextTime());
    }

    @Test
    public void testStoreClosedMonday() {
        // Monday from 5am to 24
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 1, 4, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(!openStatus.isOpen());
        assertEquals(new LocalTime(5, 0), openStatus.getNextTime());

    }

    @Test
    public void testStoreClosedTuesday() {
        // Tuesday from 5am to 0am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 2, 3, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(!openStatus.isOpen());
        assertEquals(new LocalTime(5, 0), openStatus.getNextTime());
    }

    @Test
    public void testStoreClosedThursday() {
        // Thursday from 5am to 2am
        CustomClock customClock = new CustomClock(new DateTime(2018, 10, 4, 4, 0));
        StoreHoursCheckUtil.OpenStatus openStatus = StoreHoursCheckUtil.getStoreOpenStatus(customClock, mStoreLocation);
        assertTrue(!openStatus.isOpen());
        assertEquals(new LocalTime(5, 0), openStatus.getNextTime());
    }
}
