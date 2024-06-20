package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Swapnil K on 22/06/22.
 */

public class AmsRequestDeleteAccount {

    @SerializedName("uid")
    private String uid;

    public AmsRequestDeleteAccount(String userId) {
        uid = userId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
