package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/8/17.
 */

public class CmsPerk extends CmsPromoOrPerk {

    @SerializedName("GiveXPromoID")
    private String mPerkId;

    public String getPerkId() {
        return mPerkId;
    }

    public void setPerkId(String perkId) {
        mPerkId = perkId;
    }

}
