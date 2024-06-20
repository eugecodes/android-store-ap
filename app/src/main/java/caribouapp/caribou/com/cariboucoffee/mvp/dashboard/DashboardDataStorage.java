package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;

/**
 * Created by andressegurola on 10/18/17.
 */

public interface DashboardDataStorage {

    void loadTimeOfDayRanges(ResultCallback<TimeOfDayTimeRanges> callback);

}
