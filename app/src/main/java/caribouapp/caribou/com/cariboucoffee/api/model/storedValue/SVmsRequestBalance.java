package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/8/17.
 */

public class SVmsRequestBalance {

    @SerializedName("checkRegistration")
    private boolean mCheckRegistration;

    public SVmsRequestBalance(boolean checkRegistration) {
        mCheckRegistration = checkRegistration;
    }

    public boolean isCheckRegistration() {
        return mCheckRegistration;
    }

    public void setCheckRegistration(boolean checkRegistration) {
        mCheckRegistration = checkRegistration;
    }

}
