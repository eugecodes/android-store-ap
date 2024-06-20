package caribouapp.caribou.com.cariboucoffee;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import caribouapp.caribou.com.cariboucoffee.common.Clock;

public class CustomClock implements Clock {
    private DateTime mCustomDateTime;

    public CustomClock(DateTime customDateTime) {
        mCustomDateTime = customDateTime;
    }

    @Override
    public LocalTime getCurrentTime() {
        return mCustomDateTime.toLocalTime();
    }

    @Override
    public DateTime getCurrentDateTime() {
        return mCustomDateTime;
    }
}