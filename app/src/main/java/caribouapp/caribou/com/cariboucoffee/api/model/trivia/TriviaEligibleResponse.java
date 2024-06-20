package caribouapp.caribou.com.cariboucoffee.api.model.trivia;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TriviaEligibleResponse implements Serializable {

    @SerializedName("isEligible")
    private boolean mEligible;

    public boolean isEligible() {
        return mEligible;
    }

    public void setEligible(boolean eligible) {
        mEligible = eligible;
    }
}
