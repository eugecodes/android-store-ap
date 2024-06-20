package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

/**
 * Created by jmsmuy on 1/22/18.
 */

public class ResponseClaimedRewardsExpirations {

    @SerializedName("date")
    private LocalDate mDate;

    @SerializedName("amount")
    private int mAmount;

    public LocalDate getDate() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        mDate = date;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }
}
