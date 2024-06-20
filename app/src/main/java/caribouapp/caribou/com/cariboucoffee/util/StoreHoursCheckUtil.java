package caribouapp.caribou.com.cariboucoffee.util;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextDate;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationScheduleModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import java8.util.stream.StreamSupport;

/**
 * Created by jmsmuy on 5/3/18.
 */

public final class StoreHoursCheckUtil {
    private static final String TAG = StoreHoursCheckUtil.class.getSimpleName();

    private StoreHoursCheckUtil() {
    }

    public static boolean isStoreAbleToReceiveOrder(Clock clock, StoreLocation data, int minutesBeforeClosing) {
        LocationScheduleModel schedule = getStoreOpenHoursScheduleForToday(clock, data);

        if (schedule == null || schedule.isClosed()) {
            return false;
        }

        LocalTime openTime = schedule.getOpens();
        LocalTime closingTime = schedule.getCloses().minusMinutes(minutesBeforeClosing);
        LocalTime currentTime = clock.getCurrentTime();

        return DateUtil.timeBetween(currentTime, openTime, closingTime);
    }

    public static LocationScheduleModel getStoreOpenHoursScheduleForToday(Clock clock, StoreLocation storeLocation) {
        return getStoreScheduleForToday(clock, storeLocation, storeLocation.getOpenHourSchedule(), storeLocation.getHolidayHours());
    }

    public static LocationScheduleModel getStoreDeliveryHoursScheduleForToday(Clock clock, StoreLocation storeLocation) {
        return getStoreScheduleForToday(clock, storeLocation, storeLocation.getDeliveryHoursSchedule(), storeLocation.getHolidayHours());
    }

    private static LocationScheduleModel getStoreScheduleForToday(Clock clock, StoreLocation storeLocation,
                                                                  Map<Integer, LocationScheduleModel> locationScheduleModels,
                                                                  List<YextDate> holidayDays) {
        DateTime currentDate = clock.getCurrentDateTime();
        if (locationScheduleModels == null || locationScheduleModels.size() < 7) {
            Log.e(TAG, new LogErrorException("Store Location Schedule malformed for store: " + storeLocation.getId()));
            return null; // As open hours schedule is malformed we can't say if store is open or not!
        }

        LocationScheduleModel schedule;
        try {
            schedule = ObjectCloner.deepCopy(locationScheduleModels.get(currentDate.getDayOfWeek()));
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Error cloning schedule object"));
            return null;
        }

        if (holidayDays == null) {
            return schedule;
        }

        // Override with holiday hours if today is a holiday
        YextDate holiday = findHolidayNoRegularHours(currentDate, holidayDays);

        if (holiday != null) {
            if (holiday.isClosed()) {
                schedule.setOpens(null);
                schedule.setCloses(null);
                return schedule;
            }

            if (holiday.isRegularHours()) {
                return schedule;
            }

            // We only support one time range
            YextTimePeriod holidayHours =
                    holiday.getTimePeriod() == null ? null
                            : holiday.getTimePeriod().isEmpty() ? null
                            : holiday.getTimePeriod().get(0);

            if (holidayHours == null) {
                return schedule;
            } else if (holidayHours.isClosed()) {
                schedule.setOpens(null);
                schedule.setCloses(null);
            } else {
                schedule.setOpens(holidayHours.getStartTime());
                schedule.setCloses(holidayHours.getFinishTime());
            }
        }
        return schedule;
    }

    public static String getStoreOpenStatusString(Context context, StoreLocation storeLocation, Clock clock) {

        if (storeLocation == null) {
            return "";
        }
        StoreHoursCheckUtil.OpenStatus openStatus =
                StoreHoursCheckUtil.getStoreOpenStatus(clock, storeLocation);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("h:mm a");
        if (openStatus == null) {
            return "";
        }

        String openStatusText;
        if (openStatus.isOpen() && openStatus.getNextTime() != null) {
            openStatusText = context.getString(R.string.store_open_status_with_time, formatter.print(openStatus.getNextTime()));
        } else if (!openStatus.isOpen() && openStatus.getNextTime() != null) {
            openStatusText = context.getString(R.string.store_closed_status_with_time, formatter.print(openStatus.getNextTime()));
        } else {
            openStatusText = context.getString(R.string.store_closed_status);
        }
        return openStatusText;
    }

    private static YextDate findHolidayNoRegularHours(DateTime currentDateTime, List<YextDate> holidays) {
        LocalDate currentDate = currentDateTime.toLocalDate();

        return StreamSupport.stream(holidays).filter(holidayDate ->
                !holidayDate.isRegularHours() && holidayDate.getDate().equals(currentDate)
        ).findFirst().orElse(null);
    }

    public static OpenStatus getStoreOpenStatus(Clock clock, StoreLocation storeLocation) {
        return getOpenStatus(clock, storeLocation, storeLocation.getOpenHourSchedule(), storeLocation.getHolidayHours());
    }


    public static OpenStatus getDeliveryOpenStatus(Clock clock, StoreLocation storeLocation) {
        return getOpenStatus(clock, storeLocation, storeLocation.getDeliveryHoursSchedule(), storeLocation.getDeliveryHolidayHours());
    }

    private static OpenStatus getOpenStatus(Clock clock,
                                            StoreLocation storeLocation,
                                            Map<Integer, LocationScheduleModel> scheduleModels,
                                            List<YextDate> holidays) {

        LocationScheduleModel schedule = getStoreScheduleForToday(clock, storeLocation, scheduleModels, holidays);
        LocalTime currentTime = clock.getCurrentTime();

        if (schedule == null) {
            return new OpenStatus(false, null);
        }

        if (schedule.getOpens() == null) {
            Log.e(TAG, new LogErrorException("No store schedule for store: " + storeLocation.getId() + " name: " + storeLocation.getName()));
            return new OpenStatus(false, null);
        }

        LocalTime opens = schedule.getOpens();
        LocalTime closes = schedule.getCloses();

        if (opens.compareTo(closes) == 0) {
            // Open all day
            return new OpenStatus(true, null);
        } else if (opens.compareTo(closes) < 0) {
            // Usual case where the open time is lesser than the close time
            //                      +------------------------------------------------------------------+
            //                      |                                                                  |
            //   +                  |                                                                  |                      +
            //   |                  |                                                                  |                      |
            //+----------T1---------------------------------------T2-----------------------------------------------T3-------------------+
            //   |                  +                                                                  +                      |
            //   |                                                                                                            |
            //   +                 Opens                                                              Closes                  +
            //
            //   00:00                                                                                                        00:00

            if (currentTime.compareTo(schedule.getOpens()) < 0) {
                // Still not open. T1 case
                return new OpenStatus(false, schedule.getOpens());
            } else if (opens.compareTo(currentTime) <= 0 && currentTime.compareTo(closes) <= 0) {
                // Store is open. T2 case
                return new OpenStatus(true, schedule.getCloses());
            } else {
                // Store already closed for the day. T3
                return new OpenStatus(false, null);
            }
        } else if (closes.compareTo(opens) < 0) {
            // The store closes after midnight
            //  +------------------+                          +--------------------------------------------------------------------------+
            //                     |                          |
            //    +                |                          |                                                                +
            //    |                |                          |                                                                |
            // +---------T1-------------------T2---------------------------------------------T3--------------------------------------------+
            //    |                +                          +                                                                |
            //    |                                                                                                            |
            //    +              Closes                     Opens                                                              +
            //
            //    00:00                                                                                                        00:00

            if (opens.compareTo(currentTime) <= 0 || currentTime.compareTo(closes) <= 0) {
                // Cases T1 and T3
                return new OpenStatus(true, schedule.getCloses());
            } else {
                // Still not open. Case T2
                return new OpenStatus(false, schedule.getOpens());
            }
        }

        return null;
    }

    public static boolean isStoreOpen(Clock clock, StoreLocation data) {
        return getStoreOpenStatus(clock, data).isOpen();
    }

    public static boolean isDeliveryOpen(Clock clock, StoreLocation data) {
        return getDeliveryOpenStatus(clock, data).isOpen();
    }

    public static class OpenStatus {
        private boolean mOpen;
        private LocalTime mNextTime;

        public OpenStatus(boolean open, LocalTime nextTime) {
            mOpen = open;
            mNextTime = nextTime;
        }

        public boolean isOpen() {
            return mOpen;
        }

        public void setOpen(boolean open) {
            mOpen = open;
        }

        public LocalTime getNextTime() {
            return mNextTime;
        }

        public void setNextTime(LocalTime nextTime) {
            mNextTime = nextTime;
        }
    }
}
