package caribouapp.caribou.com.cariboucoffee.util;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import caribouapp.caribou.com.cariboucoffee.CustomClock;

import static org.junit.Assert.assertFalse;

public class DateUtilTest {

    DateTime now = new DateTime(2018, 10, 15, 15, 10, 0);
    CustomClock customClock = new CustomClock(now);

    @Test
    public void whenNow_thenIsBeforeTodayFalse() {
        assertFalse(DateUtil.isBeforeToday(customClock, now.toLocalDate()));
    }

    @Test
    public void whenTodayAtZeroHour_thenIsBeforeTodayFalse() {
        DateTime todayAtZero = new DateTime(2018, 10, 15, 0, 0, 0);
        assertFalse(DateUtil.isBeforeToday(customClock, todayAtZero.toLocalDate()));
    }
}