package caribouapp.caribou.com.cariboucoffee;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextOpenHours;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

public class YextPopulateHoursTest {

    YextOpenHours mYextOpenHours;

    @Before
    public void setup() {

    }

    @Test
    public void testPopulateOpenHoursWithMissingDays() {
        mYextOpenHours = GsonUtil.readObjectFromClasspath("/test_missing_days_open_hours.json", YextOpenHours.class);
        mYextOpenHours.populateOpenHours();
        boolean[] closeStatus = new boolean[]{true, false, true, false, true, false, false};
        checkOpenStatus(closeStatus);
    }

    @Test
    public void testPopulateOpenHoursWithSundayNotMissing() {
        mYextOpenHours = GsonUtil.readObjectFromClasspath("/test_missing_days_open_hours_2.json", YextOpenHours.class);
        mYextOpenHours.populateOpenHours();
        boolean[] closeStatus = new boolean[]{true, true, true, true, true, true, false};
        checkOpenStatus(closeStatus);
    }

    @Test
    public void testPopulateOpenHoursWithAllMissingDays() {
        mYextOpenHours = new YextOpenHours();
        mYextOpenHours.populateOpenHours();
        boolean[] closeStatus = new boolean[]{true, true, true, true, true, true, true};
        checkOpenStatus(closeStatus);
    }

    @Test
    public void testPopulateOpenHoursWithNoMissingDays() {
        mYextOpenHours = GsonUtil.readObjectFromClasspath("/test_no_missing_days_open_hours_3.json", YextOpenHours.class);
        mYextOpenHours.populateOpenHours();
        boolean[] closeStatus = new boolean[]{false, false, false, false, false, false, false};
        checkOpenStatus(closeStatus);
    }

    private void checkOpenStatus(boolean[] closeStatus) {
        Assert.assertEquals(7, mYextOpenHours.getOpenHours().size());
        for (int i = 1; i <= closeStatus.length; i++) {
            Assert.assertEquals(closeStatus[i -1], mYextOpenHours.getOpenHours().get(i).isClosed());
        }
    }
}
