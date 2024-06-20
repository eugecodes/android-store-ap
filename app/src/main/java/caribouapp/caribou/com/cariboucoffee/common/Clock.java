package caribouapp.caribou.com.cariboucoffee.common;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * Created by andressegurola on 9/29/17.
 */

public interface Clock {

    LocalTime getCurrentTime();

    DateTime getCurrentDateTime();
}
