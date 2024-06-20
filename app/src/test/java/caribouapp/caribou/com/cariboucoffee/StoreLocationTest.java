package caribouapp.caribou.com.cariboucoffee;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTimeConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationScheduleModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

public class StoreLocationTest {

    @Test
    public void testLoadOpenHoursScheduleForClosedDays() {
        LatLng caribouHqLatLng = new LatLng(45.0441802, -93.3339986);
        YextLocation yextLocation = GsonUtil.readObjectFromClasspath("/test_store_location_closed_days.json", YextLocation.class);
        yextLocation.populateStoreHours();

        StoreLocation storeLocation = new StoreLocation(yextLocation, caribouHqLatLng);

        Map<Integer, LocationScheduleModel> openHoursSchedule = storeLocation.getOpenHourSchedule();

        Assert.assertEquals(8, storeLocation.getOpenHourSchedule().size());
        for (int weekDay = DateTimeConstants.MONDAY; weekDay < DateTimeConstants.SUNDAY; weekDay++) {
            Assert.assertTrue(openHoursSchedule.containsKey(weekDay));
        }
        Assert.assertTrue(openHoursSchedule.containsKey(LocationScheduleModel.WEEK_DAY_HOLIDAYS));

        Assert.assertTrue(openHoursSchedule.get(DateTimeConstants.SATURDAY).isClosed());
        Assert.assertTrue(openHoursSchedule.get(DateTimeConstants.SUNDAY).isClosed());
    }


    @Test
    public void testLoadDeliveryHoursScheduleForClosedDays() {
        LatLng caribouHqLatLng = new LatLng(45.0441802, -93.3339986);
        YextLocation yextLocation = GsonUtil.readObjectFromClasspath("/test_store_location_closed_days.json", YextLocation.class);
        yextLocation.populateStoreHours();

        StoreLocation storeLocation = new StoreLocation(yextLocation, caribouHqLatLng);

        Map<Integer, LocationScheduleModel> deliveryHoursSchedule = storeLocation.getDeliveryHoursSchedule();

        Assert.assertEquals(8, storeLocation.getOpenHourSchedule().size());
        for (int weekDay = DateTimeConstants.MONDAY; weekDay < DateTimeConstants.SUNDAY; weekDay++) {
            Assert.assertTrue(deliveryHoursSchedule.containsKey(weekDay));
        }
        Assert.assertTrue(storeLocation.getDeliveryHoursSchedule().containsKey(LocationScheduleModel.WEEK_DAY_HOLIDAYS));

        Assert.assertTrue(deliveryHoursSchedule.get(DateTimeConstants.FRIDAY).isClosed());
        Assert.assertTrue(deliveryHoursSchedule.get(DateTimeConstants.SATURDAY).isClosed());
        Assert.assertTrue(deliveryHoursSchedule.get(DateTimeConstants.SUNDAY).isClosed());
    }
}
