package caribouapp.caribou.com.cariboucoffee.util;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextDate;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationScheduleModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreFeature;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;

public class DefaultStoreHelper {

    public static final String DEFAULT_STORE_ID = "9920";

    public static StoreLocation defaultStore() {
        StoreLocation storeLocation = new StoreLocation();
        storeLocation.setId(DEFAULT_STORE_ID);
        storeLocation.setAddressShort("ShortAddress1");
        storeLocation.setDistanceInMiles(0.01);
        storeLocation.setLatitude(45.04380161);
        storeLocation.setLongitude(-93.33053046);
        storeLocation.setLocatity("Locality");
        storeLocation.setPhone("2836798");
        storeLocation.setName("Physical Lab");
        storeLocation.setState("MN");
        storeLocation.setPreparationTime("5 - 8");
        storeLocation.setZipcode("zipcode1");
        storeLocation.setRestrictedAccessLocation(true);
        storeLocation.setAddress(StringUtils.appendWithNewLine(storeLocation.getAddressShort(),
                storeLocation.getLocatity() + ", " + storeLocation.getState() + " " + storeLocation.getZipcode()
        ));

        Integer[] data = new Integer[]{
                /* dayOfWeek, openHour,openMinutes,closesHour,closesMinutes */
                1, 0, 0, 23, 59,
                2, 0, 0, 23, 59,
                3, 0, 0, 23, 59,
                4, 0, 0, 23, 59,
                5, 0, 0, 23, 59,
                6, 0, 0, 23, 59,
                7, 0, 0, 23, 59
        };

        YextDate holidayDate = new YextDate();
        holidayDate.setDate(new LocalDate(2018, 8, 10));
        holidayDate.setRegularHours(false);
        YextTimePeriod yextTimePeriod = new YextTimePeriod();
        yextTimePeriod.setClosed(true);
        ArrayList<YextTimePeriod> yextTimePeriodsList = new ArrayList<>();
        yextTimePeriodsList.add(yextTimePeriod);
        holidayDate.setTimePeriod(yextTimePeriodsList);
        storeLocation.getHolidayHours().add(holidayDate);

        storeLocation.setOpenHourSchedule(createSchedule(data));

        List<StoreFeature> features = new ArrayList<>();
        features.add(StoreFeature.ORDER_OUT_OF_STORE);
        storeLocation.setFeatures(features);

        return storeLocation;
    }

    public static Map<Integer, LocationScheduleModel> createSchedule(Integer[] scheduleData) {
        Map<Integer, LocationScheduleModel> scheduleModels = new HashMap<>();
        for (int i = 0; i < 7 * 5; i = i + 5) {
            LocationScheduleModel day = new LocationScheduleModel(scheduleData[i]);
            if (scheduleData[i + 1] == -1) {
                // Store is closed
            } else {
                day.setOpens(new LocalTime(scheduleData[i + 1], scheduleData[i + 2]));
                day.setCloses(new LocalTime(scheduleData[i + 3], scheduleData[i + 4]));
            }
            scheduleModels.put(scheduleData[i], day);
        }

        return scheduleModels;
    }
}
