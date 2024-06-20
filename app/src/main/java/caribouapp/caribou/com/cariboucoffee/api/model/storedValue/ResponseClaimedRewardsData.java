package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by jmsmuy on 1/22/18.
 */

public class ResponseClaimedRewardsData {

    @SerializedName("id")
    private Integer mId;

    @SerializedName("balance")
    private int mBalance;

    @SerializedName("expirationDate")
    private DateTime mExpirationDate;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public int getBalance() {
        return mBalance;
    }

    public void setBalance(int balance) {
        mBalance = balance;
    }

    public DateTime getExpirationDate() {
        return mExpirationDate;
    }

    public void setExpirationDate(DateTime expirationDate) {
        mExpirationDate = expirationDate;
    }
}
