package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by jmsmuy on 11/8/17.
 */

public class CmsPromo extends CmsPromoOrPerk {

    @SerializedName("Start Date")
    private DateTime mStartDate;

    @SerializedName("End Date")
    private DateTime mEndDate;

    public DateTime getStartDate() {
        return mStartDate;
    }

    public void setStartDate(DateTime startDate) {
        mStartDate = startDate;
    }

    public DateTime getEndDate() {
        return mEndDate;
    }

    public void setEndDate(DateTime endDate) {
        mEndDate = endDate;
    }

}
