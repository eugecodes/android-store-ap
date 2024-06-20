package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.content.Context;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.common.EndingDateFormatter;

public class EndingDateFormatterTest {

    private static String TODAY = "Today";

    private Context mContext;
    private CustomClock mClock;
    private String mDateFormat;
    private String mHoursDateFormat;

    @Before
    public void setup() {
        mContext = mock(Context.class);
        doReturn(TODAY).when(mContext).getString(R.string.today);

        mClock = new CustomClock(new DateTime(2018, 10, 15, 15, 10, 0));

        mDateFormat = "Ends %s";
        mHoursDateFormat = "%d Hours";
    }

    @Test
    public void testToday() {
        EndingDateFormatter endingDateFormatter = new EndingDateFormatter(mClock, mDateFormat, mHoursDateFormat, false);
        Assert.assertEquals("Ends " + TODAY, endingDateFormatter.format(mContext, new DateTime(2018, 10, 15, 20, 10, 0)));
    }

    @Test
    public void testTodayWithHours() {
        EndingDateFormatter endingDateFormatter = new EndingDateFormatter(mClock, mDateFormat, mHoursDateFormat, true);
        Assert.assertEquals("5 Hours", endingDateFormatter.format(mContext, new DateTime(2018, 10, 15, 20, 10, 0)));
    }

    @Test
    public void testTomorrow() {
        EndingDateFormatter endingDateFormatter = new EndingDateFormatter(mClock, mDateFormat, mHoursDateFormat, false);
        Assert.assertEquals("Ends Tue", endingDateFormatter.format(mContext, new DateTime(2018, 10, 16, 20, 10, 0)));
    }

    @Test
    public void test6DaysFromNow() {
        EndingDateFormatter endingDateFormatter = new EndingDateFormatter(mClock, mDateFormat, mHoursDateFormat, false);
        Assert.assertEquals("Ends Sun", endingDateFormatter.format(mContext, new DateTime(2018, 10, 21, 20, 10, 0)));
    }

    @Test
    public void testAWeekFromNow() {
        EndingDateFormatter endingDateFormatter = new EndingDateFormatter(mClock, mDateFormat, mHoursDateFormat, false);
        Assert.assertEquals("Ends Oct 22", endingDateFormatter.format(mContext, new DateTime(2018, 10, 22, 20, 10, 0)));
    }

    @Test
    public void testMoreThanAYear() {
        EndingDateFormatter endingDateFormatter = new EndingDateFormatter(mClock, mDateFormat, mHoursDateFormat, true);
        Assert.assertEquals("Ends Oct 16 2019", endingDateFormatter.format(mContext, new DateTime(2019, 10, 16, 20, 10, 0)));
    }
}
