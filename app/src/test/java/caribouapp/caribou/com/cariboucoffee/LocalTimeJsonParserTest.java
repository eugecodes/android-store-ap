package caribouapp.caribou.com.cariboucoffee;

import org.junit.Assert;

import org.joda.time.LocalTime;
import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

public class LocalTimeJsonParserTest {

    @Test
    public void testParseBeforeMidnight() {
        LocalTime localTime = GsonUtil.defaultGson().fromJson("\"23:59\"", LocalTime.class);
        Assert.assertEquals(new LocalTime(23, 59), localTime);
    }

    @Test
    public void testParseMorning() {
        LocalTime localTime = GsonUtil.defaultGson().fromJson("\"10:30\"", LocalTime.class);
        Assert.assertEquals(new LocalTime(10, 30), localTime);
    }

    @Test
    public void testParseBeforeAfternoon() {
        LocalTime localTime = GsonUtil.defaultGson().fromJson("\"15:30\"", LocalTime.class);
        Assert.assertEquals(new LocalTime(15, 30), localTime);
    }

    @Test
    public void testParseMidnight() {
        LocalTime localTime = GsonUtil.defaultGson().fromJson("\"00:00\"", LocalTime.class);
        Assert.assertEquals(new LocalTime(0, 0), localTime);
    }
}
