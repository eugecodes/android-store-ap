package caribouapp.caribou.com.cariboucoffee;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

/**
 * Created by jmsmuy on 4/27/18.
 */
public class DeserializerTest {

    class DateWrapper {
        @SerializedName("dateTime")
        private DateTime mDateTime;

        public DateTime getDateTime() {
            return mDateTime;
        }
    }

    public DateTime getTestDateTime(boolean includeMillis) {
        return new DateTime(2030, 12, 31, 23, 36, 59, includeMillis ? 789 : 0);
    }

    /**
     * Tests "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     */
    @Test
    public void deserializeFirstDateTime() {
        Gson gson = GsonUtil.defaultGson();
        DateWrapper dateTime = gson.fromJson("{\"dateTime\":\"2030-12-31T23:36:59.789Z\"}", DateWrapper.class);
        Assert.assertEquals(dateTime.getDateTime(), getTestDateTime(true));
    }

    /**
     * Tests "yyyy-MM-dd'T'HH:mm:ss"
     */
    @Test
    public void deserializeSecondDateTime() {
        DateWrapper dateTime = GsonUtil.defaultGson().fromJson("{\"dateTime\":\"2030-12-31T23:36:59\"}", DateWrapper.class);
        Assert.assertEquals(dateTime.getDateTime(), getTestDateTime(false));
    }

    /**
     * Tests "yyyyMMdd'T'HHmmss"
     */
    @Test
    public void deserializeThirdDateTime() {
        DateWrapper dateTime = GsonUtil.defaultGson().fromJson("{\"dateTime\":\"20301231T233659\"}", DateWrapper.class);
        Assert.assertEquals(dateTime.getDateTime(), getTestDateTime(false));
    }

    /**
     * "yyyy-MM-dd' 'HH:mm:ss' UTC'"
     */
    @Test
    public void deserializeFourthDateTime() {
        DateWrapper dateTime = GsonUtil.defaultGson().fromJson("{\"dateTime\":\"2030-12-31 23:36:59 UTC\"}", DateWrapper.class);
        Assert.assertEquals(dateTime.getDateTime(), getTestDateTime(false));
    }
}
