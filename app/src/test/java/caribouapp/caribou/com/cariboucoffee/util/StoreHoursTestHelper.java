package caribouapp.caribou.com.cariboucoffee.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.CustomClock;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextDate;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationScheduleModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import java8.util.stream.Collectors;
import java8.util.stream.IntStreams;

public class StoreHoursTestHelper {

    private int fixedYear;
    private int fixedMonth;
    private int fixedDay;

    public StoreHoursTestHelper(int fixedYear, int fixedMonth, int fixedDay) {
        this.fixedYear = fixedYear;
        this.fixedMonth = fixedMonth;
        this.fixedDay = fixedDay;
    }

    public CustomClock createFixedDateClock(int hour, int minutes) {
        return new CustomClock(new DateTime(fixedYear, fixedMonth, fixedDay, hour, minutes, 0));
    }

    public void addHolidayOnFixedDate(StoreLocation storeLocation, int startHour, int startMinute, int endHour, int endMinute) {
        storeLocation.getHolidayHours().add(createHolidayOnFixedDate(startHour, startMinute, endHour, endMinute, false, false));
    }

    public void addDeliveryHolidayOnFixedDate(StoreLocation storeLocation, int startHour, int startMinute, int endHour, int endMinute) {
        storeLocation.getDeliveryHolidayHours().add(createHolidayOnFixedDate(startHour, startMinute, endHour, endMinute, false, false));
    }

    public void addRegularHoursHolidayOnFixedDate(StoreLocation storeLocation, int startHour, int startMinute, int endHour, int endMinute) {
        storeLocation.getHolidayHours().add(createHolidayOnFixedDate(startHour, startMinute, endHour, endMinute, false, true));
    }

    public void addClosedHolidayOnFixedDate(StoreLocation storeLocation, int startHour, int startMinute, int endHour, int endMinute) {
        storeLocation.getHolidayHours().add(createHolidayOnFixedDate(startHour, startMinute, endHour, endMinute, true, false));
    }

    public static void setRegularScheduleEntryForFixedDate(StoreLocation storeLocation, int startHour, int startMinute, int endHour, int endMinute) {
        setRegularScheduleEntryForEveryDay(storeLocation.getOpenHourSchedule(), startHour, startMinute, endHour, endMinute);
    }

    private YextDate createHolidayOnFixedDate(int startHour, int startMinute, int endHour, int endMinute, boolean closed, boolean regularHours) {
        return createHoliday(fixedYear, fixedMonth, fixedDay, startHour, startMinute, endHour, endMinute, closed, regularHours);
    }

    private static void setRegularScheduleEntryForEveryDay(Map<Integer, LocationScheduleModel> schedule, int startHour, int startMinute, int endHour, int endMinute) {
        schedule.putAll(IntStreams.range(0, 7)
                .mapToObj(value -> createLocationScheduleModel(value + 1, startHour, startMinute, endHour, endMinute))
                .collect(Collectors.toMap(LocationScheduleModel::getWeekDay, locationSchedule -> locationSchedule)));
    }

    private static LocationScheduleModel createLocationScheduleModel(int dayOfWeek, int startHour, int startMinute, int endHour, int endMinute) {
        LocationScheduleModel model = new LocationScheduleModel(dayOfWeek);

        model.setOpens(new LocalTime(startHour, startMinute, 0));
        model.setCloses(new LocalTime(endHour, endMinute, 0));

        return model;
    }

    private static YextDate createHoliday(int year, int month, int day, int startHour, int startMinute, int endHour, int endMinute, boolean closed,
                                          boolean regularHours) {
        YextDate holiday = new YextDate();

        holiday.setDate(new LocalDate(year, month, day));
        holiday.setRegularHours(regularHours);
        YextTimePeriod period = new YextTimePeriod();
        period.setClosed(closed);
        period.setStartTime(new LocalTime(startHour, startMinute, 0));
        period.setFinishTime(new LocalTime(endHour, endMinute, 0));
        ArrayList<YextTimePeriod> yextTimePeriods = new ArrayList<>();
        yextTimePeriods.add(period);
        holiday.setTimePeriod(yextTimePeriods);

        return holiday;
    }
}
